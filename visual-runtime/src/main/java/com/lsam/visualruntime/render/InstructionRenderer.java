package com.lsam.visualruntime.render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IRをCanvas描画へ変換
 */
public final class InstructionRenderer {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Bitmap render(
            Bitmap baseBitmap,
            ComposeManifest manifest,
            Map<String, Bitmap> layerBitmaps,
            List<RenderInstruction> instructions
    ) {

        Bitmap result = baseBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);

        Map<String, RenderInstruction> instMap = new HashMap<>();
        for (RenderInstruction i : instructions) {
            instMap.put(i.layerKey, i);
        }

        for (LayerSpec layer : manifest.layers) {

            Bitmap bmp = layerBitmaps.get(layer.layer_key);
            if (bmp == null) continue;

            RenderInstruction inst =
                    instMap.getOrDefault(layer.layer_key,
                            RenderInstruction.identity(layer.layer_key));

            Matrix m = new Matrix();

            float px = bmp.getWidth() * inst.pivotX;
            float py = bmp.getHeight() * inst.pivotY;

            m.postTranslate(-px, -py);
            m.postScale(inst.scaleX, inst.scaleY);
            m.postRotate(inst.rotation);
            m.postTranslate(layer.x + inst.dx + px,
                            layer.y + inst.dy + py);

            paint.setAlpha((int)(inst.alpha * 255f));
            canvas.drawBitmap(bmp, m, paint);
        }

        return result;
    }
}
