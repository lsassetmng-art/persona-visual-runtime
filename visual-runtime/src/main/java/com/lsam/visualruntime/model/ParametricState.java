package com.lsam.visualruntime.model;

/**
 * Lv5: Live2D思想の簡易パラメータ状態
 */
public final class ParametricState {

    public float mouthOpen = 0f;   // 0..1
    public float eyeBlink = 0f;    // 0..1
    public float headTilt = 0f;    // -1..1
    public float bodyShift = 0f;   // -1..1

    public static ParametricState idle() {
        return new ParametricState();
    }
}
