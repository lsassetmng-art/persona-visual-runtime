package com.lsam.visualruntime.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComposeManifest {

    private final String manifestSha256;
    private final int width;
    private final int height;
    private final List<LayerSpec> layers;

    public ComposeManifest(String manifestSha256, int width, int height, List<LayerSpec> layers) {
        this.manifestSha256 = (manifestSha256 == null) ? "" : manifestSha256;
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        if (layers == null) {
            this.layers = Collections.emptyList();
        } else {
            this.layers = Collections.unmodifiableList(new ArrayList<>(layers));
        }
    }

    public String getManifestSha256() { return manifestSha256; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<LayerSpec> getLayers() { return layers; }
}
