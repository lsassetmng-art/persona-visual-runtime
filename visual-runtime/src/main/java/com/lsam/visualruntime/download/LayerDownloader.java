package com.lsam.visualruntime.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Supabase Storage public object fetcher.
 * URL:
 *   https://{PROJECT_REF}.supabase.co/storage/v1/object/public/{bucket}/{path}
 */
public class LayerDownloader {

    // 固定（ユーザー環境の正本）
    private static final String PROJECT_REF = "bkvycodiojbwcomnylqa";
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public void downloadToFile(String bucketName, String assetPath, File outFile) throws IOException {

        if (bucketName == null || bucketName.isEmpty()) throw new IOException("bucketName empty");
        if (assetPath == null || assetPath.isEmpty()) throw new IOException("assetPath empty");
        if (outFile == null) throw new IOException("outFile null");

        String url = buildPublicUrl(bucketName, assetPath);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response resp = CLIENT.newCall(request).execute();
        try {
            if (!resp.isSuccessful()) {
                throw new IOException("HTTP " + resp.code() + " : " + url);
            }
            if (resp.body() == null) {
                throw new IOException("Empty body : " + url);
            }
            outFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                fos.write(resp.body().bytes());
            }
        } finally {
            resp.close();
        }
    }

    private String buildPublicUrl(String bucket, String path) {
        return "https://" + PROJECT_REF
                + ".supabase.co/storage/v1/object/public/"
                + bucket + "/" + path;
    }
}
