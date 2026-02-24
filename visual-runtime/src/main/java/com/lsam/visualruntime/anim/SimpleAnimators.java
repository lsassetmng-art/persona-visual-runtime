package com.lsam.visualruntime.anim;

public final class SimpleAnimators {

    private SimpleAnimators(){}

    public static float blink(long timeMs) {
        long period = 4000L;
        long closed = 120L;
        long t = timeMs % period;
        return t < closed ? 1f : 0f;
    }

    public static float mouth(long timeMs, AudioAmplitudeProvider amp) {
        if (amp != null) {
            float a = amp.getAmplitude01();
            if (a < 0f) a = 0f;
            if (a > 1f) a = 1f;
            return a;
        }
        double s = Math.sin(timeMs / 120.0);
        return (float)((s + 1.0) * 0.5);
    }
}
