package com.lsam.visualruntime.compose;

import android.content.Context;

import com.lsam.visualruntime.cache.*;
import com.lsam.visualruntime.download.LayerDownloader;
import com.lsam.visualruntime.error.*;
import com.lsam.visualruntime.log.Logger;
import com.lsam.visualruntime.model.*;
import com.lsam.visualruntime.render.*;
import com.lsam.visualruntime.security.*;
import com.lsam.visualruntime.trace.*;
import com.lsam.visualruntime.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComposeOrchestrator {

    public static class Config {
        public StrictModeGuard strictModeGuard = StrictModeGuard.strict();
        public MetricsSink metricsSink = null;
        public boolean enableCache = true;
        public boolean enableFallback = true;
        public int fallbackWidth = 512;
        public int fallbackHeight = 512;
        public long maxCacheBytes = 200L * 1024L * 1024L; // 200MB
    }

    private final Config config;
    private final DiskCacheManager cache;
    private final DiskCacheCleaner cleaner;
    private final ComposeLockRegistry lockRegistry;
    private final ManifestParser parser;
    private final LayerSorter sorter;
    private final LayerDownloader downloader;
    private final BitmapComposer composer;

    public ComposeOrchestrator(Config config) {
        this.config = (config == null) ? new Config() : config;
        this.cache = new DiskCacheManager();
        this.cleaner = new DiskCacheCleaner(this.config.maxCacheBytes);
        this.lockRegistry = new ComposeLockRegistry();
        this.parser = new ManifestParser();
        this.sorter = new LayerSorter();
        this.downloader = new LayerDownloader(new LayerDownloader.Config());
        this.composer = new BitmapComposer();
    }

    public Result composeBlocking(Context context, ComposeRequest request) throws Exception {

        long t0 = Trace.now();
        if (config.strictModeGuard != null) config.strictModeGuard.check();

        ComposeManifest manifest = parser.parse(request.getLayersJson());
        List<LayerSpec> layers = new ArrayList<>(manifest.getLayers());
        sorter.sortByZIndex(layers);

        int w = request.getOutputWidth() > 0 ? request.getOutputWidth() : manifest.getWidth();
        int h = request.getOutputHeight() > 0 ? request.getOutputHeight() : manifest.getHeight();

        CacheKey key = new CacheKey(request.getPersonaId(), manifest.getManifestSha256(), w, h);
        String lockKey = request.getPersonaId() + ":" + key.fileNamePng();

        Object lock = lockRegistry.acquire(lockKey);

        synchronized (lock) {
            try {
                File outPng = cache.getOutputPngFile(context, key);
                cache.ensureParent(outPng);

                if (config.enableCache && outPng.exists() && PngValidator.isValidPng(outPng)) {
                    String sha = Sha256.hexOfFile(outPng);
                    return new Result(outPng, sha, true, false, Trace.elapsed(t0));
                }

                List<File> layerFiles = new ArrayList<>();

                for (int i = 0; i < layers.size(); i++) {
                    LayerSpec spec = layers.get(i);
                    File lf = cache.getLayerFile(context, key, i);

                    downloader.downloadToFile(spec.getUrl(), lf);

                    try {
                        ImageFormatDetector.requireSupported(lf);
                    } catch (Exception e) {
                        // 再DL1回
                        downloader.downloadToFile(spec.getUrl(), lf);
                        ImageFormatDetector.requireSupported(lf);
                    }

                    layerFiles.add(lf);
                }

                android.graphics.Bitmap bmp = composer.composeToBitmap(
                        new ComposeManifest(manifest.getManifestSha256(), w, h, layers),
                        layerFiles
                );

                File tmp = new File(outPng.getAbsolutePath() + ".tmp");
                PngWriter.writePng(bmp, tmp);

                if (bmp != null && !bmp.isRecycled()) bmp.recycle();

                FileUtils.safeReplace(tmp, outPng);

                if (!PngValidator.isValidPng(outPng)) {
                    throw new DecodeError("output png invalid");
                }

                String sha = Sha256.hexOfFile(outPng);

                ComposeMetaWriter.writeMeta(outPng, sha, layers.size(), Trace.elapsed(t0));

                // cache clean
                cleaner.clean(outPng.getParentFile());

                return new Result(outPng, sha, false, false, Trace.elapsed(t0));

            } finally {
                lockRegistry.release(lockKey, lock);
                lock.notifyAll();
            }
        }
    }

    public static class Result {
        public final File outputFile;
        public final String sha256;
        public final boolean fromCache;
        public final boolean fromFallback;
        public final long elapsedMsTotal;

        public Result(File outputFile, String sha256, boolean fromCache, boolean fromFallback, long elapsedMsTotal) {
            this.outputFile = outputFile;
            this.sha256 = sha256;
            this.fromCache = fromCache;
            this.fromFallback = fromFallback;
            this.elapsedMsTotal = elapsedMsTotal;
        }
    }
}
