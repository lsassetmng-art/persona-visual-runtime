package com.lsam.visualruntime.model;

public class LayerSpec {

    private final String url;
    private final int zIndex;

    private final float x;
    private final float y;

    private final float scale;
    private final float rotation;

    private final float anchorX;
    private final float anchorY;

    private final float alpha;
    private final String blendMode;

    public LayerSpec(
            String url,
            int zIndex,
            float x,
            float y,
            float scale,
            float rotation,
            float anchorX,
            float anchorY,
            float alpha,
            String blendMode
    ) {
        this.url = url;
        this.zIndex = zIndex;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = rotation;
        this.anchorX = clamp01(anchorX);
        this.anchorY = clamp01(anchorY);
        this.alpha = clamp01(alpha);
        this.blendMode = (blendMode == null) ? "normal" : blendMode.toLowerCase();
    }

    public String getUrl() { return url; }
    public int getZIndex() { return zIndex; }

    public float getX() { return x; }
    public float getY() { return y; }

    public float getScale() { return scale; }
    public float getRotation() { return rotation; }

    public float getAnchorX() { return anchorX; }
    public float getAnchorY() { return anchorY; }

    public float getAlpha() { return alpha; }
    public String getBlendMode() { return blendMode; }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
