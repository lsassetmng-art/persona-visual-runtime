package com.lsam.visualruntime.cache;

public final class DiskCachePolicy {

    public final long ttlMs;
    public final int maxFiles;

    public DiskCachePolicy(long ttlMs, int maxFiles) {
        this.ttlMs = ttlMs;
        this.maxFiles = maxFiles;
    }

    public static DiskCachePolicy safeDefault() {
        return new DiskCachePolicy(24L * 60L * 60L * 1000L, 80);
    }
}
