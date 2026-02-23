package com.lsam.visualruntime.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {

    public static void safeReplace(File tmp, File dst) throws Exception {
        if (tmp == null || dst == null) throw new IllegalArgumentException("tmp/dst null");
        File parent = dst.getParentFile();
        if (parent != null) parent.mkdirs();

        if (dst.exists() && !dst.delete()) {
            throw new IllegalStateException("failed to delete dst: " + dst.getAbsolutePath());
        }
        if (!tmp.renameTo(dst)) {
            // fallback copy
            copy(tmp, dst);
            tmp.delete();
        }
    }

    public static void copy(File src, File dst) throws Exception {
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dst)) {
            byte[] buf = new byte[64 * 1024];
            int r;
            while ((r = fis.read(buf)) > 0) fos.write(buf, 0, r);
        }
    }

    public static void writePng(Bitmap bmp, File out) throws Exception {
        File parent = out.getParentFile();
        if (parent != null) parent.mkdirs();
        try (FileOutputStream fos = new FileOutputStream(out)) {
            if (!bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                throw new IllegalStateException("png compress failed");
            }
        }
    }
}
