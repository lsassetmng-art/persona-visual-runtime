package com.lsam.visualruntime.model;

public final class ParametricState {

    public float mouthOpen;   // 0..1
    public float blink;       // 0..1
    public float breath;      // 0..1
    public float headYaw;     // -1..1
    public float headPitch;   // -1..1
    public float headRoll;    // -1..1

    public ParametricState() {}

    public static ParametricState idle() {
        return new ParametricState();
    }
}
