package com.lsam.visualruntime.anim;

/**
 * Lv4: 単純状態切替
 * idle / talk / blink
 */
public final class SimpleExpressionResolver implements ExpressionResolver {

    private final String idleJson;
    private final String talkJson;
    private final String blinkJson;
    private final float talkThreshold;

    public SimpleExpressionResolver(
            String idleJson,
            String talkJson,
            String blinkJson,
            float talkThreshold
    ) {
        this.idleJson = idleJson;
        this.talkJson = talkJson;
        this.blinkJson = blinkJson;
        this.talkThreshold = talkThreshold;
    }

    @Override
    public String resolve(
            long frameIndex,
            long nowMs,
            float mouthAmplitude,
            float blinkValue
    ) {
        if (blinkValue > 0.8f) {
            return blinkJson;
        }
        if (mouthAmplitude > talkThreshold) {
            return talkJson;
        }
        return idleJson;
    }
}
