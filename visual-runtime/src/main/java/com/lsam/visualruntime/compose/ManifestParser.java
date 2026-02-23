package com.lsam.visualruntime.compose;

import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManifestParser {

    public ComposeManifest parse(String layersJson) throws Exception {
        if (layersJson == null || layersJson.trim().isEmpty()) {
            throw new IllegalArgumentException("layersJson empty");
        }

        JSONObject root = new JSONObject(layersJson);

        String sha = root.optString("manifest_sha256", "");
        if (sha.isEmpty()) sha = root.optString("manifestSha256", ""); // fallback

        int width = root.optInt("width", 512);
        int height = root.optInt("height", 512);

        JSONArray arr = root.optJSONArray("layers");
        if (arr == null || arr.length() == 0) throw new IllegalArgumentException("manifest layers empty");

        List<LayerSpec> layers = new ArrayList<>(arr.length());

        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);

            String bucket = o.optString("bucket_name", "");
            String path = o.optString("asset_path", "");
            if (bucket.isEmpty()) bucket = o.optString("bucket", "");
            if (path.isEmpty()) path = o.optString("path", "");
            if (bucket.isEmpty() || path.isEmpty()) {
                // 旧仕様互換: uri = "bucket/path" みたいなケース（最終仕様では使わないが保険）
                String uri = o.optString("uri", "");
                if (!uri.isEmpty() && uri.contains("/")) {
                    int p = uri.indexOf('/');
                    bucket = uri.substring(0, p);
                    path = uri.substring(p + 1);
                }
            }
            if (bucket.isEmpty() || path.isEmpty()) {
                throw new IllegalArgumentException("layer missing bucket_name/asset_path at index=" + i);
            }

            int z = o.optInt("z_index", 0);
            float x = (float) o.optDouble("x", width / 2.0);
            float y = (float) o.optDouble("y", height / 2.0);
            float scale = (float) o.optDouble("scale", 1.0);
            float rot = (float) o.optDouble("rotation", 0.0);
            float alpha = (float) o.optDouble("alpha", 1.0);
            String blend = o.optString("blend_mode", "normal");

            layers.add(new LayerSpec(bucket, path, z, x, y, scale, rot, alpha, blend));
        }

        return new ComposeManifest(sha, width, height, layers);
    }
}
