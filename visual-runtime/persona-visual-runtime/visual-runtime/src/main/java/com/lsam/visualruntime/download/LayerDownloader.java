package com.lsam.visualruntime.download;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LayerDownloader {

    private final OkHttpClient client;

    public LayerDownloader(OkHttpClient client) {
        this.client = client;
    }

    public byte[] download(String url) throws IOException {
        try {
            return downloadOnce(url);
        } catch (IOException first) {
            return downloadOnce(url); // Retry 1回のみ
        }
    }

    private byte[] downloadOnce(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code());
            }
            if (response.body() == null) {
                throw new IOException("Empty body");
            }
            return response.body().bytes();
        }
    }
}
