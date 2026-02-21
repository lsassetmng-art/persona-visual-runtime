package com.lsam.visualruntime.cache;

import android.content.Context;

import java.io.File;

public class DiskCacheManager {

    private static final String ROOT_DIR = "persona-runtime";

    public File getPersonaDir(Context context, String personaId) {
        File root = new File(context.getCacheDir(), ROOT_DIR);
        return new File(root, safe(personaId));
    }

    public File getOutputPngFile(Context context, CacheKey key) {
        File dir = getPersonaDir(context, key.getPersonaId());
        return new File(dir, key.fileNamePng());
    }

    public File getLayerFile(Context context, CacheKey key, int layerIndex) {
        File dir = getPersonaDir(context, key.getPersonaId());
        return new File(dir, key.getManifestSha256() + "_layer_" + layerIndex + ".img");
    }

    public void ensureParent(File f) {
        File p = f.getParentFile();
        if (p != null && !p.exists()) {
            //noinspection ResultOfMethodCallIgnored
            p.mkdirs();
        }
    }

    private String safe(String s) {
        if (s == null) return "null";
        // 超簡易サニタイズ（ファイル名安全）
        return s.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");
    }
}
