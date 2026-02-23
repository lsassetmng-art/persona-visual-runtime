package com.lsam.visualruntime.render;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.lsam.visualruntime.error.DecodeError;
import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;

import java.io.File;
import java.util.List;

public class BitmapComposer {

    public Bitmap composeToBitmap(ComposeManifest manifest, List<File> layerFiles) throws Exception {

        int w = manifest.getWidth();
        int h = manifest.getHeight();

        Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        List<LayerSpec> layers = manifest.getLayers();

        for (int i = 0; i < layers.size(); i++) {

            LayerSpec spec = layers.get(i);
            File file = layerFiles.get(i);

            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bmp == null) throw new DecodeError("decode failed");

            float alpha = spec.getAlpha();
            paint.setAlpha((int)(alpha * 255f));

            String blend = spec.getBlendMode();
            if ("multiply".equals(blend)) {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            } else if ("screen".equals(blend)) {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            } else {
                paint.setXfermode(null);
            }

            drawLayer(canvas, paint, bmp, spec);

            bmp.recycle();
        }

        paint.setXfermode(null);
        paint.setAlpha(255);

        return out;
    }

    private void drawLayer(Canvas canvas, Paint paint, Bitmap bmp, LayerSpec spec) {

        int bw = bmp.getWidth();
        int bh = bmp.getHeight();

        float ax = spec.getAnchorX();
        float ay = spec.getAnchorY();

        float anchorPx = bw * ax;
        float anchorPy = bh * ay;

        Matrix m = new Matrix();

        m.postTranslate(-anchorPx, -anchorPy);
        m.postScale(spec.getScale(), spec.getScale());
        m.postRotate(spec.getRotation());
        m.postTranslate(spec.getX(), spec.getY());

        canvas.drawBitmap(bmp, m, paint);
    }
}
