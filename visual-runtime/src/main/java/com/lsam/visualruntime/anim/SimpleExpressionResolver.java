package com.lsam.visualruntime.anim;

/**
 * 最小実装：
 * blink > 0.5 → BLINK
 * mouthOpen > threshold → TALK
 * それ以外 → IDLE
 */
public final class SimpleExpressionResolver implements ExpressionResolver {

    private final String idleSha;
    private final String talkSha;
    private final String blinkSha;
    private final float talkThreshold;

    public SimpleExpressionResolver(
            String idleSha,
            String talkSha,
            String blinkSha,
            float talkThreshold
    ) {
        this.idleSha = idleSha;
        this.talkSha = talkSha;
        this.blinkSha = blinkSha;
        this.talkThreshold = talkThreshold;
    }

    @Override
    public String resolve(long frameIndex, long nowMs, float mouthOpen, float blink) {

        if (blink > 0.5f && blinkSha != null) {
            return blinkSha;
        }

        if (mouthOpen > talkThreshold && talkSha != null) {
            return talkSha;
        }

        return idleSha;
    }
}
