package com.lsam.visualruntime.anim;

public final class SimpleAnimators {

    private SimpleAnimators(){}

    public static float blink(long nowMs) {
        long cycle = nowMs % 3000L;
        if (cycle < 120) {
            return 1f;
        }
        return 0f;
    }

    public static float mouth(long nowMs, AudioAmplitudeProvider amp) {
        if (amp != null) {
            return amp.getAmplitude();
        }
        return (float)Math.abs(Math.sin(nowMs / 120.0));
    }
}
