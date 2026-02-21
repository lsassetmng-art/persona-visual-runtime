package com.lsam.visualruntime.cache;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public final class ComposeMetaWriter {

    private ComposeMetaWriter() {}

    public static void writeMeta(File pngFile, String sha256, int layers, long elapsedMs) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("sha256", sha256);
            obj.put("layers", layers);
            obj.put("elapsed_ms", elapsedMs);
            obj.put("generated_at", System.currentTimeMillis());

            File meta = new File(pngFile.getAbsolutePath() + ".json");

            try (FileOutputStream fos = new FileOutputStream(meta)) {
                fos.write(obj.toString(2).getBytes());
                fos.flush();
            }
        } catch (Exception ignore) {
        }
    }
}
