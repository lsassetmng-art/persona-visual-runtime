package com.lsam.visualruntime.compose;

import android.os.Looper;

public final class StrictModeGuard {

    private final boolean requireBackgroundThread;

    public StrictModeGuard(boolean requireBackgroundThread) {
        this.requireBackgroundThread = requireBackgroundThread;
    }

    public static StrictModeGuard strict() {
        return new StrictModeGuard(true);
    }

    public static StrictModeGuard relaxed() {
        return new StrictModeGuard(false);
    }

    public void check() {
        if (!requireBackgroundThread) return;

        if (Looper.getMainLooper() != null && Looper.getMainLooper().isCurrentThread()) {
            throw new IllegalStateException("compose() must not run on main thread (StrictModeGuard)");
        }
    }

    public boolean isRequireBackgroundThread() {
        return requireBackgroundThread;
    }
}
