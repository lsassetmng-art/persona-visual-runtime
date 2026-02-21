package com.lsam.visualruntime.cache;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class DiskCacheCleaner {

    private final long maxBytes;

    public DiskCacheCleaner(long maxBytes) {
        this.maxBytes = maxBytes;
    }

    public void clean(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        long total = 0;
        for (File f : files) {
            if (f.isFile()) total += f.length();
        }

        if (total <= maxBytes) return;

        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        for (File f : files) {
            if (total <= maxBytes) break;
            if (f.isFile()) {
                long len = f.length();
                if (f.delete()) {
                    total -= len;
                }
            }
        }
    }
}
