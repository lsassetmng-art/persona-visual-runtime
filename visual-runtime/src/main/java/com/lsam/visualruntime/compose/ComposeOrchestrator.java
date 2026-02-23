package com.lsam.visualruntime.compose;

import android.content.Context;

import com.lsam.visualruntime.download.LayerDownloader;
import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;
import com.lsam.visualruntime.render.BitmapComposer;
import com.lsam.visualruntime.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComposeOrchestrator {

    public static class Result {
        public final File outputFile;
        public final boolean fromCache;

        public Result(File outputFile, boolean fromCache) {
            this.outputFile = outputFile;
            this.fromCache = fromCache;
        }
    }

    private final ManifestParser parser = new ManifestParser();
    private final LayerDownloader downloader = new LayerDownloader();
    private final BitmapComposer composer = new BitmapComposer();

    public Result composeBlocking(Context context, String personaId, String layersJson) throws Exception {

        if (context == null) throw new IllegalArgumentException("context null");
        if (personaId == null || personaId.trim().isEmpty()) throw new IllegalArgumentException("personaId empty");

        ComposeManifest manifest = parser.parse(layersJson);

        List<LayerSpec> layers = new ArrayList<>(manifest.getLayers());
        Collections.sort(layers, new Comparator<LayerSpec>() {
            @Override public int compare(LayerSpec a, LayerSpec b) {
                return a.getZIndex() - b.getZIndex();
            }
        });

        int w = manifest.getWidth();
        int h = manifest.getHeight();

        String sha = manifest.getManifestSha256();
        if (sha == null || sha.isEmpty()) sha = "nohash";

        File outPng = getOutputFile(context, personaId, sha, w, h);

        if (outPng.exists() && outPng.length() > 0) {
            return new Result(outPng, true);
        }

        // download layers
        List<File> files = new ArrayList<>(layers.size());
        File dir = outPng.getParentFile();
        if (dir != null) dir.mkdirs();

        for (int i = 0; i < layers.size(); i++) {
            LayerSpec spec = layers.get(i);

            File lf = new File(dir, "layer_" + i + ".bin");
            downloader.downloadToFile(spec.getBucketName(), spec.getAssetPath(), lf);
            files.add(lf);
        }

        // compose
        ComposeManifest sorted = new ComposeManifest(sha, w, h, layers);
        android.graphics.Bitmap bmp = composer.composeToBitmap(sorted, files);

        File tmp = new File(outPng.getAbsolutePath() + ".tmp");
        FileUtils.writePng(bmp, tmp);
        if (bmp != null && !bmp.isRecycled()) bmp.recycle();

        FileUtils.safeReplace(tmp, outPng);

        return new Result(outPng, false);
    }

    private File getOutputFile(Context context, String personaId, String sha, int w, int h) {
        File base = new File(context.getCacheDir(), "visualruntime");
        File pdir = new File(base, personaId);
        String name = sha + "_" + w + "x" + h + ".png";
        return new File(pdir, name);
    }
}
