package com.lsam.visualruntime.anim;

public interface ExpressionResolver {
    String resolve(
            long frameIndex,
            long nowMs,
            float mouthAmplitude,
            float blinkValue
    );
}
