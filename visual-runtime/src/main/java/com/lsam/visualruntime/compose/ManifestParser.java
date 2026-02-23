package com.lsam.visualruntime.compose;

import com.lsam.visualruntime.error.DecodeError;
import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Expected JSON (Edge canonical):
 * {
 *   "manifest_sha256": "...",   // optional
 *   "width": 512,
 *   "height": 512,
 *   "layers": [
 *     {
 *       "uri": "asset-character/..png",   // or "url"
 *       "z_index": 5,                    // or "zIndex"
 *       "x": 256,
 *       "y": 256,
 *       "scale": 1.0,
 *       "rotation": 0,
 *       "alpha": 1.0,
 *       "blend_mode": "normal",
 *       "anchor_x": 0.5,                 // optional
 *       "anchor_y": 0.5                  // optional
 *     }
 *   ]
 * }
 */
public class ManifestParser {

    public ComposeManifest parse(String layersJson) throws Exception {

        if (layersJson == null || layersJson.trim().isEmpty()) {
            throw new DecodeError("layers_json empty");
        }

        JSONObject root = new JSONObject(layersJson);

        String sha = optStringAny(root, "manifest_sha256", "manifestSha256", "sha256", "manifest");
        int width = optIntAny(root, 512, "width", "w");
        int height = optIntAny(root, 512, "height", "h");

        JSONArray arr = root.optJSONArray("layers");
        if (arr == null) throw new DecodeError("layers missing");
        if (arr.length() <= 0) throw new DecodeError("layers empty");

        final float cx = width / 2.0f;
        final float cy = height / 2.0f;

        List<LayerSpec> layers = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;

            String uri = optStringAny(o, "uri", "url");
            if (uri == null || uri.trim().isEmpty()) throw new DecodeError("layer uri empty");

            int z = optIntAny(o, 0, "z_index", "zIndex", "z");

            float x = optFloatAny(o, cx, "x");
            float y = optFloatAny(o, cy, "y");

            float scale = optFloatAny(o, 1.0f, "scale");
            float rotation = optFloatAny(o, 0.0f, "rotation");

            float alpha = optFloatAny(o, 1.0f, "alpha");
            String blend = optStringAny(o, "blend_mode", "blendMode");
            if (blend == null || blend.trim().isEmpty()) blend = "normal";

            float anchorX = optFloatAny(o, 0.5f, "anchor_x", "anchorX");
            float anchorY = optFloatAny(o, 0.5f, "anchor_y", "anchorY");

            layers.add(new LayerSpec(
                    uri,
                    z,
                    x,
                    y,
                    scale,
                    rotation,
                    anchorX,
                    anchorY,
                    alpha,
                    blend
            ));
        }

        return new ComposeManifest(sha, width, height, layers);
    }

    private static String optStringAny(JSONObject o, String... keys) {
        for (String k : keys) {
            if (o.has(k)) {
                String v = o.optString(k, null);
                if (v != null) return v;
            }
        }
        return null;
    }

    private static int optIntAny(JSONObject o, int def, String... keys) {
        for (String k : keys) {
            if (o.has(k)) {
                try { return o.getInt(k); } catch (Exception ignored) {}
                try { return (int) Math.round(o.getDouble(k)); } catch (Exception ignored) {}
            }
        }
        return def;
    }

    private static float optFloatAny(JSONObject o, float def, String... keys) {
        for (String k : keys) {
            if (o.has(k)) {
                try { return (float) o.getDouble(k); } catch (Exception ignored) {}
            }
        }
        return def;
    }
}
