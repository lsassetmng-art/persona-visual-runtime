package com.lsam.visualruntime.render;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

public final class PngWriter {

    private PngWriter() {}

    public static void writePng(Bitmap bitmap, File outFile) throws Exception {
        if (bitmap == null) throw new IllegalArgumentException("bitmap is null");
        if (outFile == null) throw new IllegalArgumentException("outFile is null");

        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            boolean ok = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            if (!ok) {
                throw new IllegalStateException("PNG compress failed");
            }
        }
    }
}
