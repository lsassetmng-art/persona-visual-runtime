package com.lsam.visualruntime.model;

public class LayerSpec {

    public static final String SCALE_FIT_CENTER = "fit_center";
    public static final String SCALE_CENTER_CROP = "center_crop";

    private final String url;
    private final int zIndex;
    private final float alpha;
    private final String blendMode;
    private final String scaleMode;

    // 旧互換コンストラクタ（既存コードを壊さない）
    public LayerSpec(String url, int zIndex, float alpha, String blendMode) {
        this(url, zIndex, alpha, blendMode, SCALE_FIT_CENTER);
    }

    public LayerSpec(String url, int zIndex, float alpha, String blendMode, String scaleMode) {
        this.url = url;
        this.zIndex = zIndex;
        this.alpha = alpha;
        this.blendMode = blendMode;
        this.scaleMode = (scaleMode == null || scaleMode.isEmpty()) ? SCALE_FIT_CENTER : scaleMode;
    }

    public String getUrl() {
        return url;
    }

    public int getZIndex() {
        return zIndex;
    }

    public float getAlpha() {
        return alpha;
    }

    public String getBlendMode() {
        return blendMode;
    }

    public String getScaleMode() {
        return scaleMode;
    }
}
