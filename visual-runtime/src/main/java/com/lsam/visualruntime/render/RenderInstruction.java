package com.lsam.visualruntime.render;

public final class RenderInstruction {

    public final String layerKey;

    public final float dx;
    public final float dy;
    public final float scaleX;
    public final float scaleY;
    public final float rotation;
    public final float alpha;
    public final float pivotX;
    public final float pivotY;

    public RenderInstruction(
            String layerKey,
            float dx,
            float dy,
            float scaleX,
            float scaleY,
            float rotation,
            float alpha,
            float pivotX,
            float pivotY
    ) {
        this.layerKey = layerKey;
        this.dx = dx;
        this.dy = dy;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.rotation = rotation;
        this.alpha = alpha;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
    }

    public static RenderInstruction identity(String layerKey) {
        return new RenderInstruction(
                layerKey,
                0f, 0f,
                1f, 1f,
                0f,
                1f,
                0.5f, 0.5f
        );
    }
}
