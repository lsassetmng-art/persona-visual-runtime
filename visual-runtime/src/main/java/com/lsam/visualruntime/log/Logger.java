package com.lsam.visualruntime.log;

import android.util.Log;

public final class Logger {

    private static volatile LogLevel level = LogLevel.INFO;
    private static final String TAG = "PersonaVisualRuntime";

    private Logger() {}

    public static void setLevel(LogLevel newLevel) {
        if (newLevel != null) {
            level = newLevel;
        }
    }

    public static LogLevel getLevel() {
        return level;
    }

    public static void e(String msg, Throwable t) {
        if (level.allows(LogLevel.ERROR)) {
            Log.e(TAG, msg, t);
        }
    }

    public static void w(String msg) {
        if (level.allows(LogLevel.WARN)) {
            Log.w(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (level.allows(LogLevel.INFO)) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (level.allows(LogLevel.DEBUG)) {
            Log.d(TAG, msg);
        }
    }

    public static void t(String msg) {
        if (level.allows(LogLevel.TRACE)) {
            Log.v(TAG, msg);
        }
    }

    // URLやトークンなどを直接ログに出したくない時の最低限マスク
    public static String mask(String raw) {
        if (raw == null) return "null";
        int n = raw.length();
        if (n <= 8) return "****";
        return raw.substring(0, 4) + "****" + raw.substring(n - 4);
    }
}
