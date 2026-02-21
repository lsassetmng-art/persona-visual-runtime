package com.lsam.visualruntime.compose;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.lsam.visualruntime.log.Logger;
import com.lsam.visualruntime.render.PngWriter;
import com.lsam.visualruntime.security.Sha256;

import java.io.File;

public class FallbackGenerator {

    // ライブラリ側resource（visual-runtime内）
    public static int fallbackDrawableResId(Context context) {
        // Rクラス参照はモジュール内で解決される想定
        return context.getResources().getIdentifier("persona_fallback", "drawable", context.getPackageName());
    }

    public static Result writeFallbackPng(Context context, File outFile, int w, int h) throws Exception {

        int resId = fallbackDrawableResId(context);
        if (resId == 0) {
            throw new IllegalStateException("fallback drawable not found");
        }

        Drawable d = ContextCompat.getDrawable(context, resId);
        if (d == null) {
            throw new IllegalStateException("fallback drawable load failed");
        }

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        d.setBounds(0, 0, w, h);
        d.draw(c);

        try {
            PngWriter.writePng(bmp, outFile);
        } finally {
            if (!bmp.isRecycled()) bmp.recycle();
        }

        String sha = Sha256.hexOfFile(outFile);
        Logger.w("fallback png generated: " + outFile.getName());
        return new Result(outFile, sha);
    }

    public static class Result {
        public final File file;
        public final String sha256;
        public Result(File file, String sha256) {
            this.file = file;
            this.sha256 = sha256;
        }
    }
}
