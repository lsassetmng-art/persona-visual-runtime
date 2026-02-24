package com.lsam.visualruntime.rig;

import com.lsam.visualruntime.model.ParametricState;
import com.lsam.visualruntime.render.RenderInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * ParametricState → RenderInstruction[]
 * Live2D簡易版（transform2Dのみ）
 */
public final class RigSolver {

    public List<RenderInstruction> solve(ParametricState state) {

        List<RenderInstruction> list = new ArrayList<>();

        // mouth layer
        list.add(new RenderInstruction(
                "mouth",
                0f,
                0f,
                1f,
                1f + state.mouthOpen * 0.8f,
                0f,
                1f,
                0.5f,
                0.5f
        ));

        // eye layer (blink)
        float eyeScale = 1f - state.blink;
        list.add(new RenderInstruction(
                "eye",
                0f,
                0f,
                1f,
                eyeScale,
                0f,
                1f,
                0.5f,
                0.5f
        ));

        // body layer (breath)
        list.add(new RenderInstruction(
                "body",
                0f,
                state.breath * 4f,
                1f,
                1f,
                0f,
                1f,
                0.5f,
                0.5f
        ));

        // face layer (headYaw / Roll)
        list.add(new RenderInstruction(
                "face",
                state.headYaw * 3f,
                state.headPitch * 2f,
                1f,
                1f,
                state.headRoll * 5f,
                1f,
                0.5f,
                0.5f
        ));

        return list;
    }
}
