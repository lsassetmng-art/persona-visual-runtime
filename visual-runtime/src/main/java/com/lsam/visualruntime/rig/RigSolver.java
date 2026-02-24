package com.lsam.visualruntime.rig;

import androidx.annotation.NonNull;

import com.lsam.visualruntime.model.ParametricState;
import com.lsam.visualruntime.render.LayerTransformProvider;
import com.lsam.visualruntime.render.TransformSpec;

/**
 * ParametricState → LayerTransformProvider
 * 非破壊・軽量・Live2D思想の入口
 */
public final class RigSolver {

    @NonNull
    public LayerTransformProvider solve(@NonNull ParametricState s) {

        return layerKey -> {

            // 例: レイヤー名で簡易振り分け
            if (layerKey.contains("head")) {
                return new TransformSpec(
                        s.bodyShift * 10f,
                        0f,
                        1f,
                        s.headTilt * 5f
                );
            }

            if (layerKey.contains("mouth")) {
                return new TransformSpec(
                        0f,
                        s.mouthOpen * 5f,
                        1f + (s.mouthOpen * 0.1f),
                        0f
                );
            }

            if (layerKey.contains("eye")) {
                return new TransformSpec(
                        0f,
                        s.eyeBlink * 3f,
                        1f - (s.eyeBlink * 0.3f),
                        0f
                );
            }

            return TransformSpec.identity();
        };
    }
}
