package com.lsam.visualruntime.util;

import android.util.Log;

public final class TestTraceLogger {

    private static final String TAG = "VisualRuntimeTest";

    public static void log(String msg) {
        Log.i(TAG, msg);
    }

    public static void section(String name) {
        Log.i(TAG, "========== " + name + " ==========");
    }
}
