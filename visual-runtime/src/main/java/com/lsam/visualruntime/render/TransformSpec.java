package com.lsam.visualruntime.render;

/**
 * 各レイヤーに適用する変形
 */
public final class TransformSpec {

    public final float dx;
    public final float dy;
    public final float scale;
    public final float rotation;

    public TransformSpec(float dx, float dy, float scale, float rotation) {
        this.dx = dx;
        this.dy = dy;
        this.scale = scale;
        this.rotation = rotation;
    }

    public static TransformSpec identity() {
        return new TransformSpec(0f, 0f, 1f, 0f);
    }
}
