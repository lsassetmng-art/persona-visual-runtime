package com.lsam.visualruntime.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public final class FileUtils {

    private FileUtils() {}

    public static void safeReplace(File tmp, File target) throws Exception {

        if (target.exists()) {
            //noinspection ResultOfMethodCallIgnored
            target.delete();
        }

        if (tmp.renameTo(target)) {
            return;
        }

        // fallback copy
        try (FileInputStream in = new FileInputStream(tmp);
             FileOutputStream out = new FileOutputStream(target)) {

            byte[] buf = new byte[64 * 1024];
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            out.flush();
        }

        //noinspection ResultOfMethodCallIgnored
        tmp.delete();
    }
}
