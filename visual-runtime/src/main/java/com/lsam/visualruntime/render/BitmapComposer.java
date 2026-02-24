package com.lsam.visualruntime.render;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;

import java.io.File;
import java.util.List;

public class BitmapComposer {

    private static final int MAX_PIXELS = 4096 * 4096;

    public Bitmap composeToBitmap(ComposeManifest manifest, List<File> layerFiles) throws Exception {

        if (manifest == null) throw new IllegalArgumentException("manifest null");
        if (layerFiles == null) throw new IllegalArgumentException("layerFiles null");

        int w = manifest.getWidth();
        int h = manifest.getHeight();

        long px = (long) w * (long) h;
        if (px <= 0 || px > MAX_PIXELS) throw new IllegalArgumentException("output too large");

        List<LayerSpec> layers = manifest.getLayers();
        if (layers == null || layers.isEmpty()) throw new IllegalArgumentException("layers empty");
        if (layers.size() != layerFiles.size()) throw new IllegalArgumentException("layers/files mismatch");

        Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setDither(true);

        for (int i = 0; i < layers.size(); i++) {

            LayerSpec spec = layers.get(i);
            File f = layerFiles.get(i);

            if (f == null || !f.exists() || f.length() <= 0) {
                throw new IllegalStateException("layer file missing/empty at index=" + i);
            }

            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                if (bmp == null) throw new IllegalStateException("decode failed at index=" + i);

                float alpha = spec.getAlpha();
                if (alpha < 0f) alpha = 0f;
                if (alpha > 1f) alpha = 1f;
                paint.setAlpha((int) (alpha * 255f));

                drawAt(canvas, paint, bmp, spec.getX(), spec.getY(), spec.getScale(), spec.getRotation());

            } finally {
                paint.setAlpha(255);
                if (bmp != null && !bmp.isRecycled()) bmp.recycle();
            }
        }

        return out;
    }

    private void drawAt(Canvas canvas, Paint paint, Bitmap bmp, float cx, float cy, float scale, float rotationDeg) {

        float s = scale;
        if (s <= 0f) s = 1f;

        float bw = bmp.getWidth();
        float bh = bmp.getHeight();

        // place bitmap center at (cx,cy)
        Matrix m = new Matrix();
        m.postTranslate(-bw / 2f, -bh / 2f);
        m.postScale(s, s);
        if (rotationDeg != 0f) m.postRotate(rotationDeg);
        m.postTranslate(cx, cy);

        canvas.drawBitmap(bmp, m, paint);
    }

    // =========================================================
    // Lv5: Rig対応合成（既存composeToBitmapは変更しない）
    // =========================================================
    public Bitmap composeToBitmapWithRig(
            com.lsam.visualruntime.model.ComposeManifest manifest,
            java.util.List<java.io.File> layerFiles,
            com.lsam.visualruntime.render.LayerTransformProvider rigProvider
    ) throws Exception {

        if (manifest == null) throw new IllegalArgumentException("manifest null");
        if (layerFiles == null) throw new IllegalArgumentException("layerFiles null");

        int w = manifest.getWidth();
        int h = manifest.getHeight();

        long px = (long) w * (long) h;
        if (px <= 0 || px > MAX_PIXELS) throw new IllegalArgumentException("output too large");

        java.util.List<com.lsam.visualruntime.model.LayerSpec> layers = manifest.getLayers();
        if (layers == null || layers.isEmpty()) throw new IllegalArgumentException("layers empty");
        if (layers.size() != layerFiles.size()) throw new IllegalArgumentException("layers/files mismatch");

        android.graphics.Bitmap out =
                android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(out);

        android.graphics.Paint paint =
                new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG | android.graphics.Paint.FILTER_BITMAP_FLAG);
        paint.setDither(true);

        for (int i = 0; i < layers.size(); i++) {

            com.lsam.visualruntime.model.LayerSpec spec = layers.get(i);
            java.io.File f = layerFiles.get(i);

            if (f == null || !f.exists() || f.length() <= 0) {
                throw new IllegalStateException("layer file missing/empty at index=" + i);
            }

            android.graphics.Bitmap bmp = null;
            try {
                bmp = android.graphics.BitmapFactory.decodeFile(f.getAbsolutePath());
                if (bmp == null) throw new IllegalStateException("decode failed at index=" + i);

                float alpha = spec.getAlpha();
                if (alpha < 0f) alpha = 0f;
                if (alpha > 1f) alpha = 1f;
                paint.setAlpha((int) (alpha * 255f));

                com.lsam.visualruntime.render.TransformSpec rig =
                        (rigProvider != null)
                                ? rigProvider.get(spec.getAssetPath())
                                : com.lsam.visualruntime.render.TransformSpec.identity();

                drawAtWithRig(
                        canvas,
                        paint,
                        bmp,
                        spec.getX(),
                        spec.getY(),
                        spec.getScale(),
                        spec.getRotation(),
                        rig
                );

            } finally {
                paint.setAlpha(255);
                if (bmp != null && !bmp.isRecycled()) bmp.recycle();
            }
        }

        return out;
    }

    private void drawAtWithRig(
            android.graphics.Canvas canvas,
            android.graphics.Paint paint,
            android.graphics.Bitmap bmp,
            float cx,
            float cy,
            float scale,
            float rotationDeg,
            com.lsam.visualruntime.render.TransformSpec rig
    ) {

        float s = scale;
        if (s <= 0f) s = 1f;

        float bw = bmp.getWidth();
        float bh = bmp.getHeight();

        android.graphics.Matrix m = new android.graphics.Matrix();
        m.postTranslate(-bw / 2f, -bh / 2f);
        m.postScale(s * rig.scale, s * rig.scale);
        if (rotationDeg != 0f || rig.rotation != 0f) {
            m.postRotate(rotationDeg + rig.rotation);
        }
        m.postTranslate(cx + rig.dx, cy + rig.dy);

        canvas.drawBitmap(bmp, m, paint);
    }

}
