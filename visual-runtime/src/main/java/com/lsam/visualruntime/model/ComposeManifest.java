package com.lsam.visualruntime.model;

import java.util.List;

public class ComposeManifest {

    private final String manifestSha256;
    private final int width;
    private final int height;
    private final List<LayerSpec> layers;

    public ComposeManifest(String manifestSha256, int width, int height, List<LayerSpec> layers) {
        this.manifestSha256 = manifestSha256;
        this.width = width;
        this.height = height;
        this.layers = layers;
    }

    public String getManifestSha256() { return manifestSha256; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<LayerSpec> getLayers() { return layers; }
}
