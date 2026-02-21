package com.lsam.visualruntime.compose;

import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;
import com.lsam.visualruntime.log.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManifestParser {

    private static final int MAX_LAYERS = 20;
    private static final int MAX_WIDTH = 4096;
    private static final int MAX_HEIGHT = 4096;

    public ComposeManifest parse(String layersJson) throws Exception {

        if (layersJson == null || layersJson.trim().isEmpty()) {
            throw new IllegalArgumentException("layersJson is empty");
        }

        JSONObject root = new JSONObject(layersJson);

        String manifestSha = root.optString("manifest_sha256", null);
        if (manifestSha == null) {
            throw new IllegalArgumentException("manifest_sha256 missing");
        }

        int width = root.optInt("width", 1080);
        int height = root.optInt("height", 1920);

        if (width <= 0 || width > MAX_WIDTH) {
            throw new IllegalArgumentException("width out of range");
        }
        if (height <= 0 || height > MAX_HEIGHT) {
            throw new IllegalArgumentException("height out of range");
        }

        JSONArray layersArray = root.optJSONArray("layers");
        if (layersArray == null || layersArray.length() == 0) {
            throw new IllegalArgumentException("layers missing or empty");
        }
        if (layersArray.length() > MAX_LAYERS) {
            throw new IllegalArgumentException("too many layers");
        }

        List<LayerSpec> layers = new ArrayList<>();

        for (int i = 0; i < layersArray.length(); i++) {
            JSONObject obj = layersArray.getJSONObject(i);

            String url = obj.optString("url", null);
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("layer url missing");
            }
            validateUrl(url);

            int zIndex = obj.optInt("z_index", 0);
            float alpha = (float) obj.optDouble("alpha", 1.0);
            String blend = obj.optString("blend_mode", "normal");
            String scaleMode = obj.optString("scale_mode", LayerSpec.SCALE_FIT_CENTER);

            if (alpha < 0f || alpha > 1f) {
                throw new IllegalArgumentException("alpha out of range");
            }

            layers.add(new LayerSpec(url, zIndex, alpha, blend, scaleMode));
        }

        Logger.d("Manifest parsed: layers=" + layers.size());
        return new ComposeManifest(manifestSha, width, height, layers);
    }

    private void validateUrl(String url) {
        if (!(url.startsWith("https://") || url.startsWith("http://"))) {
            throw new IllegalArgumentException("invalid url scheme");
        }
        if (url.startsWith("file://") || url.startsWith("content://") || url.startsWith("data:") || url.startsWith("javascript:")) {
            throw new IllegalArgumentException("forbidden url scheme");
        }
    }
}
