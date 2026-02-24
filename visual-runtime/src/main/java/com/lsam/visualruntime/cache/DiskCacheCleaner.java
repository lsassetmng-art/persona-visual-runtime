package com.lsam.visualruntime.cache;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public final class DiskCacheCleaner {

    private DiskCacheCleaner(){}

    public static void clean(File personaDir, DiskCachePolicy policy) {
        if (personaDir == null || !personaDir.exists()) return;

        File[] files = personaDir.listFiles();
        if (files == null) return;

        long now = System.currentTimeMillis();

        // TTL削除
        for (File f : files) {
            if (!f.isFile()) continue;
            if (now - f.lastModified() > policy.ttlMs) {
                f.delete();
            }
        }

        files = personaDir.listFiles();
        if (files == null) return;

        File[] only = Arrays.stream(files)
                .filter(File::isFile)
                .toArray(File[]::new);

        if (only.length <= policy.maxFiles) return;

        Arrays.sort(only, Comparator.comparingLong(File::lastModified));
        int over = only.length - policy.maxFiles;
        for (int i = 0; i < over; i++) {
            only[i].delete();
        }
    }
}
