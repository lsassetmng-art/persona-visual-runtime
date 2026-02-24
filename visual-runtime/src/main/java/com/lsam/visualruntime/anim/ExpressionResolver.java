package com.lsam.visualruntime.anim;

public interface ExpressionResolver {

    /**
     * フレーム情報から manifestSha を決定する。
     */
    String resolve(long frameIndex, long nowMs, float mouthOpen, float blink);
}
