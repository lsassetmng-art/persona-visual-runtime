package com.lsam.visualruntime.trace;

import android.os.SystemClock;

public final class Trace {

    private static volatile boolean enabled = true;

    private Trace() {}

    public static void setEnabled(boolean on) {
        enabled = on;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static long now() {
        return SystemClock.elapsedRealtime();
    }

    public static long elapsed(long startMs) {
        return SystemClock.elapsedRealtime() - startMs;
    }

    public static void record(MetricsSink sink, String name, long startMs) {
        if (!enabled || sink == null) return;
        sink.record(name, elapsed(startMs));
    }
}
