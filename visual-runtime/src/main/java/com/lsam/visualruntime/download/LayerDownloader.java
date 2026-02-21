package com.lsam.visualruntime.download;

import com.lsam.visualruntime.error.NetworkError;
import com.lsam.visualruntime.error.SecurityError;
import com.lsam.visualruntime.log.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LayerDownloader {

    public static class Config {
        public int connectTimeoutMs = 8000;
        public int readTimeoutMs = 15000;
        public int retryCount = 2;
        public long maxBytesPerLayer = 8L * 1024L * 1024L; // 8MB
    }

    private final Config config;

    public LayerDownloader(Config config) {
        this.config = (config == null) ? new Config() : config;
    }

    /**
     * 条件付きDL:
     * - 既存ファイル + meta があれば If-None-Match / If-Modified-Since を付ける
     * - 304なら既存をそのまま利用
     */
    public void downloadToFile(String url, File outFile) throws Exception {

        validateUrl(url);

        int attempts = 0;
        Exception last = null;

        while (attempts <= config.retryCount) {
            attempts++;
            try {
                doDownloadConditional(url, outFile);
                return;
            } catch (SecurityError se) {
                throw se;
            } catch (Exception e) {
                last = e;
                Logger.w("download failed attempt=" + attempts + " url=" + Logger.mask(url));
                try { Thread.sleep(250L * attempts); } catch (InterruptedException ignore) {}
            }
        }

        throw new NetworkError("download failed after retries", last);
    }

    private void doDownloadConditional(String urlStr, File outFile) throws Exception {

        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }

        LayerCacheMeta meta = new LayerCacheMeta(outFile);

        HttpURLConnection conn = null;
        BufferedInputStream in = null;
        FileOutputStream out = null;

        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(config.connectTimeoutMs);
            conn.setReadTimeout(config.readTimeoutMs);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");

            // conditional headers
            if (outFile.exists() && outFile.length() > 0) {
                String etag = meta.getEtag();
                String lm = meta.getLastModified();
                if (etag != null) conn.setRequestProperty("If-None-Match", etag);
                if (lm != null) conn.setRequestProperty("If-Modified-Since", lm);
            }

            int code = conn.getResponseCode();

            if (code == 304) {
                // not modified
                if (!outFile.exists() || outFile.length() <= 0) {
                    throw new NetworkError("304 but cache file missing");
                }
                Logger.d("304 not modified: " + Logger.mask(outFile.getName()));
                return;
            }

            if (code < 200 || code >= 300) {
                throw new NetworkError("http status=" + code);
            }

            long contentLen = conn.getContentLengthLong();
            if (contentLen > 0 && contentLen > config.maxBytesPerLayer) {
                throw new NetworkError("layer too large by header: " + contentLen);
            }

            in = new BufferedInputStream(conn.getInputStream());

            // atomic-ish write: temp then rename
            File tmp = new File(outFile.getAbsolutePath() + ".tmp");
            out = new FileOutputStream(tmp);

            byte[] buf = new byte[64 * 1024];
            long total = 0;

            int r;
            while ((r = in.read(buf)) != -1) {
                total += r;
                if (total > config.maxBytesPerLayer) {
                    throw new NetworkError("layer too large by stream: " + total);
                }
                out.write(buf, 0, r);
            }
            out.flush();

            if (tmp.length() <= 0) {
                throw new NetworkError("downloaded file empty");
            }

            // replace
            if (outFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                outFile.delete();
            }
            if (!tmp.renameTo(outFile)) {
                // fallback: copy
                throw new NetworkError("rename tmp failed");
            }

            // save meta from response
            String etagNew = conn.getHeaderField("ETag");
            String lmNew = conn.getHeaderField("Last-Modified");
            meta.set(etagNew, lmNew);

        } finally {
            try { if (out != null) out.close(); } catch (Exception ignore) {}
            try { if (in != null) in.close(); } catch (Exception ignore) {}
            if (conn != null) conn.disconnect();
        }
    }

    private void validateUrl(String url) throws SecurityError {
        if (url == null) throw new SecurityError("url is null");
        if (!(url.startsWith("https://") || url.startsWith("http://"))) {
            throw new SecurityError("invalid url scheme");
        }
        if (url.startsWith("file://") || url.startsWith("content://") || url.startsWith("data:") || url.startsWith("javascript:")) {
            throw new SecurityError("forbidden url scheme");
        }
    }
}
