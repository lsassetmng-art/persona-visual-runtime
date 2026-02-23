package com.lsam.visualruntime.model;

public class LayerSpec {

    private final String bucketName;
    private final String assetPath;

    private final int zIndex;

    // coordinate system: canvas pixels, origin = (0,0) top-left
    // (x,y) is the draw center position in output canvas
    private final float x;
    private final float y;

    private final float scale;
    private final float rotation; // degrees
    private final float alpha;    // 0..1
    private final String blendMode; // "normal" only (others ignore)

    public LayerSpec(
            String bucketName,
            String assetPath,
            int zIndex,
            float x,
            float y,
            float scale,
            float rotation,
            float alpha,
            String blendMode
    ) {
        this.bucketName = bucketName;
        this.assetPath = assetPath;
        this.zIndex = zIndex;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = rotation;
        this.alpha = alpha;
        this.blendMode = blendMode;
    }

    public String getBucketName() { return bucketName; }
    public String getAssetPath() { return assetPath; }

    public int getZIndex() { return zIndex; }

    public float getX() { return x; }
    public float getY() { return y; }

    public float getScale() { return scale; }
    public float getRotation() { return rotation; }

    public float getAlpha() { return alpha; }
    public String getBlendMode() { return blendMode; }
}
