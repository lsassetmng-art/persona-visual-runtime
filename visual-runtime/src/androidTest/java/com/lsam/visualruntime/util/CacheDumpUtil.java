package com.lsam.visualruntime.util;

import android.content.Context;

import java.io.File;

public final class CacheDumpUtil {

    private CacheDumpUtil() {}

    public static void dumpCache(Context ctx) {
        File dir = new File(ctx.getCacheDir(), "persona-runtime");
        if (!dir.exists()) return;

        logRecursive(dir, "");
    }

    private static void logRecursive(File f, String indent) {
        if (f.isDirectory()) {
            System.out.println(indent + "[DIR] " + f.getName());
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) {
                    logRecursive(c, indent + "  ");
                }
            }
        } else {
            System.out.println(indent + "[FILE] " + f.getName() + " (" + f.length() + " bytes)");
        }
    }
}
