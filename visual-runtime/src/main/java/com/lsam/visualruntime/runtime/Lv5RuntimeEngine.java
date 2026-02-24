package com.lsam.visualruntime.runtime;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.lsam.visualruntime.PersonaVisualRuntime;
import com.lsam.visualruntime.anim.FrameTicker;
import com.lsam.visualruntime.model.ParametricState;
import com.lsam.visualruntime.render.InstructionRenderer;
import com.lsam.visualruntime.render.RenderInstruction;
import com.lsam.visualruntime.rig.RigSolver;

import java.util.List;

/**
 * Lv5統合エンジン
 */
public final class Lv5RuntimeEngine {

    public interface Listener {
        void onFrame(Bitmap bitmap);
        void onError(Throwable error);
    }

    private final PersonaVisualRuntime runtime;
    private final RigSolver solver = new RigSolver();
    private final InstructionRenderer renderer = new InstructionRenderer();

    private final FrameTicker ticker;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final String personaId;
    private final String manifestSha;
    private final int width;
    private final int height;
    private final Listener listener;

    private long startTime;

    public Lv5RuntimeEngine(
            PersonaVisualRuntime runtime,
            int fps,
            String personaId,
            String manifestSha,
            int width,
            int height,
            Listener listener
    ) {
        this.runtime = runtime;
        this.personaId = personaId;
        this.manifestSha = manifestSha;
        this.width = width;
        this.height = height;
        this.listener = listener;

        this.ticker = new FrameTicker(fps, this::onTick);
    }

    public void start() {
        startTime = System.currentTimeMillis();
        ticker.start();
    }

    public void stop() {
        ticker.stop();
    }

    public void shutdown() {
        ticker.shutdown();
    }

    private void onTick(long frameIndex, long nowMs) {

        try {

            // ① ParametricState生成
            ParametricState state = new ParametricState();

            long t = nowMs - startTime;

            state.mouthOpen = (float)((Math.sin(t / 120.0) + 1.0) * 0.5);
            state.blink = (t % 4000 < 120) ? 1f : 0f;
            state.breath = (float)((Math.sin(t / 800.0) + 1.0) * 0.5);
            state.headYaw = (float)Math.sin(t / 2000.0) * 0.3f;
            state.headPitch = (float)Math.sin(t / 1800.0) * 0.2f;
            state.headRoll = (float)Math.sin(t / 2200.0) * 0.2f;

            // ② RigSolver
            List<RenderInstruction> instructions = solver.solve(state);

            // ③ 既存composeSyncでベース生成
            java.io.File file =
                    runtime.composeSync(personaId, manifestSha, width, height);

            android.graphics.Bitmap base =
                    android.graphics.BitmapFactory.decodeFile(file.getAbsolutePath());

            if (base == null) throw new RuntimeException("Base bitmap decode failed");

            // ⚠ ここではレイヤービットマップ再取得が必要
            // 今回は簡易版：ベースのみ返す（構造確認用）
            Bitmap result = base;

            mainHandler.post(() -> listener.onFrame(result));

        } catch (Throwable t) {
            mainHandler.post(() -> listener.onError(t));
        }
    }
}
