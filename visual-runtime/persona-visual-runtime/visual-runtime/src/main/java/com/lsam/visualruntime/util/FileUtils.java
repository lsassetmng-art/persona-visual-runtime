package com.lsam.visualruntime.util;

import java.io.File;

public final class FileUtils {

    private FileUtils() {}

    public static boolean isExpired(File file, long maxAgeMs) {
        long age = System.currentTimeMillis() - file.lastModified();
        return age > maxAgeMs;
    }

    public static int purgeExpired(File dir, long maxAgeMs) {
        int removed = 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;

        for (File f : files) {
            if (f.isFile() && isExpired(f, maxAgeMs)) {
                if (f.delete()) {
                    removed++;
                }
            }
        }
        return removed;
    }
}
