package com.lsam.visualruntime.render;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.lsam.visualruntime.error.DecodeError;
import com.lsam.visualruntime.error.OutOfMemoryComposeError;
import com.lsam.visualruntime.log.Logger;
import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;
import com.lsam.visualruntime.security.ImageFormatDetector;

import java.io.File;
import java.util.List;

public class BitmapComposer {

    private static final int MAX_PIXELS = 4096 * 4096;
    private static final int MAX_LAYER_FILE_BYTES = 8 * 1024 * 1024;
    private static final float[] SCALE_STEPS = new float[] { 1.0f, 0.75f, 0.5f };

    public Bitmap composeToBitmap(ComposeManifest manifest, List<File> layerFiles) throws Exception {
        Exception last = null;
        for (float scale : SCALE_STEPS) {
            try {
                return composeInternal(manifest, layerFiles, scale);
            } catch (OutOfMemoryError oom) {
                last = new OutOfMemoryComposeError("OOM during compose (scale=" + scale + ")", oom);
                Logger.w("OOM during compose, retry with smaller scale: " + scale);
            }
        }
        if (last != null) throw last;
        throw new IllegalStateException("compose failed");
    }

    private Bitmap composeInternal(ComposeManifest manifest, List<File> layerFiles, float scale) throws Exception {

        if (manifest == null) throw new IllegalArgumentException("manifest is null");
        if (layerFiles == null) throw new IllegalArgumentException("layerFiles is null");

        int w0 = manifest.getWidth();
        int h0 = manifest.getHeight();
        int w = Math.max(1, Math.round(w0 * scale));
        int h = Math.max(1, Math.round(h0 * scale));

        long pixels = (long) w * (long) h;
        if (pixels <= 0 || pixels > MAX_PIXELS) throw new IllegalArgumentException("output size too large");

        Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setDither(true);

        List<LayerSpec> layers = manifest.getLayers();
        if (layers == null || layers.isEmpty()) throw new IllegalArgumentException("manifest layers empty");
        if (layers.size() != layerFiles.size()) throw new IllegalArgumentException("layers count mismatch");

        for (int i = 0; i < layers.size(); i++) {

            LayerSpec spec = layers.get(i);
            File file = layerFiles.get(i);

            if (file == null || !file.exists()) throw new DecodeError("layer file missing");
            long len = file.length();
            if (len <= 0) throw new DecodeError("layer file empty");
            if (len > MAX_LAYER_FILE_BYTES) Logger.w("Layer file too large (may OOM). len=" + len + " file=" + Logger.mask(file.getName()));

            // format check (PNG/JPEG/WebP only)
            ImageFormatDetector.requireSupported(file);

            Bitmap layer = null;
            try {
                layer = decodeForTarget(file, w, h);
                if (layer == null) throw new DecodeError("decode returned null");

                float alpha = spec.getAlpha();
                if (alpha < 0f) alpha = 0f;
                if (alpha > 1f) alpha = 1f;
                paint.setAlpha((int) (alpha * 255f));

                String blend = safeLower(spec.getBlendMode(), "normal");
                String scaleMode = safeLower(spec.getScaleMode(), LayerSpec.SCALE_FIT_CENTER);

                if ("normal".equals(blend)) {
                    paint.setXfermode(null);
                    drawScaled(canvas, paint, layer, w, h, scaleMode);
                } else if ("multiply".equals(blend)) {
                    drawWithXfermode(canvas, paint, layer, w, h, scaleMode, PorterDuff.Mode.MULTIPLY);
                } else if ("screen".equals(blend)) {
                    drawWithXfermode(canvas, paint, layer, w, h, scaleMode, PorterDuff.Mode.SCREEN);
                } else {
                    Logger.w("blend_mode not supported yet: " + blend + " -> fallback normal");
                    paint.setXfermode(null);
                    drawScaled(canvas, paint, layer, w, h, scaleMode);
                }

            } finally {
                paint.setXfermode(null);
                if (layer != null && !layer.isRecycled()) layer.recycle();
            }
        }

        paint.setAlpha(255);
        return out;
    }

    private void drawWithXfermode(Canvas canvas, Paint paint, Bitmap layer, int outW, int outH, String scaleMode, PorterDuff.Mode mode) {
        int save = canvas.saveLayer(0f, 0f, outW, outH, null);
        try {
            paint.setXfermode(null);
            // base already exists on canvas; we apply xfer on top draw
            paint.setXfermode(new PorterDuffXfermode(mode));
            drawScaled(canvas, paint, layer, outW, outH, scaleMode);
        } finally {
            paint.setXfermode(null);
            canvas.restoreToCount(save);
        }
    }

    private void drawScaled(Canvas canvas, Paint paint, Bitmap layer, int outW, int outH, String scaleMode) {
        int lw = layer.getWidth();
        int lh = layer.getHeight();

        if (lw == outW && lh == outH) {
            canvas.drawBitmap(layer, 0f, 0f, paint);
            return;
        }

        Matrix m = new Matrix();

        float sx = (float) outW / (float) lw;
        float sy = (float) outH / (float) lh;

        float scale;
        if (LayerSpec.SCALE_CENTER_CROP.equals(scaleMode)) {
            scale = Math.max(sx, sy);
        } else {
            // fit_center
            scale = Math.min(sx, sy);
        }

        float dx = (outW - lw * scale) / 2f;
        float dy = (outH - lh * scale) / 2f;

        m.postScale(scale, scale);
        m.postTranslate(dx, dy);

        canvas.drawBitmap(layer, m, paint);
    }

    private Bitmap decodeForTarget(File file, int targetW, int targetH) throws Exception {
        BitmapFactory.Options o1 = new BitmapFactory.Options();
        o1.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), o1);

        int srcW = o1.outWidth;
        int srcH = o1.outHeight;
        if (srcW <= 0 || srcH <= 0) throw new DecodeError("invalid image bounds");

        int sample = calcInSampleSize(srcW, srcH, targetW, targetH);

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inJustDecodeBounds = false;
        o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
        o2.inDither = true;
        o2.inScaled = false;
        o2.inSampleSize = sample;

        return BitmapFactory.decodeFile(file.getAbsolutePath(), o2);
    }

    private int calcInSampleSize(int srcW, int srcH, int reqW, int reqH) {
        int inSampleSize = 1;
        if (srcH > reqH || srcW > reqW) {
            final int halfH = srcH / 2;
            final int halfW = srcW / 2;
            while ((halfH / inSampleSize) >= reqH && (halfW / inSampleSize) >= reqW) {
                inSampleSize *= 2;
                if (inSampleSize >= 16) break;
            }
        }
        if (inSampleSize < 1) inSampleSize = 1;
        return inSampleSize;
    }

    private String safeLower(String s, String def) {
        if (s == null || s.trim().isEmpty()) return def;
        return s.trim().toLowerCase();
    }
}
