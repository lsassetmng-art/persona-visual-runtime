package com.lsam.visualruntime.anim;

public final class AnimationFrame {

    public final long frameIndex;
    public final long timeMs;
    public final float mouthOpen; // 0..1
    public final float blink;     // 0..1

    public AnimationFrame(long frameIndex, long timeMs, float mouthOpen, float blink) {
        this.frameIndex = frameIndex;
        this.timeMs = timeMs;
        this.mouthOpen = clamp(mouthOpen);
        this.blink = clamp(blink);
    }

    private float clamp(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
