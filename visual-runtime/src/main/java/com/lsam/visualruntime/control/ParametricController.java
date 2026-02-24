package com.lsam.visualruntime.control;

import com.lsam.visualruntime.model.ParametricState;

/**
 * Phase 4: 外部制御レイヤー
 * スレッド安全な ParametricState 管理
 */
public final class ParametricController {

    private final ParametricState state = new ParametricState();

    public synchronized ParametricState snapshot() {
        ParametricState s = new ParametricState();
        s.mouthOpen = state.mouthOpen;
        s.eyeBlink = state.eyeBlink;
        s.headTilt = state.headTilt;
        s.bodyShift = state.bodyShift;
        return s;
    }

    public synchronized void setMouth(float v) {
        state.mouthOpen = clamp01(v);
    }

    public synchronized void setBlink(float v) {
        state.eyeBlink = clamp01(v);
    }

    public synchronized void setHeadTilt(float v) {
        state.headTilt = clampSigned(v);
    }

    public synchronized void setBodyShift(float v) {
        state.bodyShift = clampSigned(v);
    }

    private float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    private float clampSigned(float v) {
        if (v < -1f) return -1f;
        if (v > 1f) return 1f;
        return v;
    }
}
