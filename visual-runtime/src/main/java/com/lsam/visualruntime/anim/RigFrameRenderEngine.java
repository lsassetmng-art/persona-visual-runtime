package com.lsam.visualruntime.anim;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.lsam.visualruntime.compose.ComposeOrchestrator;
import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.ParametricState;
import com.lsam.visualruntime.render.BitmapComposer;
import com.lsam.visualruntime.render.LayerTransformProvider;
import com.lsam.visualruntime.rig.RigSolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class RigFrameRenderEngine {

    public interface Listener {
        void onBitmap(long frameIndex, @NonNull Bitmap bitmap);
        void onError(@NonNull Throwable error);
    }

    private final Context appContext;
    private final String personaId;
    private final int fps;
    private final String layersJson;
    private final Listener listener;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private FrameLoop loop;

    public RigFrameRenderEngine(
            @NonNull Context context,
            @NonNull String personaId,
            int fps,
            @NonNull String layersJson,
            @NonNull Listener listener
    ) {
        this.appContext = context.getApplicationContext();
        this.personaId = personaId;
        this.fps = Math.max(1, fps);
        this.layersJson = layersJson;
        this.listener = listener;
    }

    public void start() {

        loop = new FrameLoop(fps, (frameIndex, nowMs) -> {

            try {

                // ===== ParametricState自動更新 =====
                ParametricState state = new ParametricState();

                state.mouthOpen = (float)Math.abs(Math.sin(nowMs / 120.0));
                state.eyeBlink = (nowMs % 3000L < 120) ? 1f : 0f;
                state.headTilt = (float)Math.sin(nowMs / 1000.0);
                state.bodyShift = (float)Math.sin(nowMs / 1500.0);

                // ===== Rig解決 =====
                RigSolver solver = new RigSolver();
                LayerTransformProvider rigProvider = solver.solve(state);

                // ===== 既存Compose =====
                ComposeOrchestrator orch = new ComposeOrchestrator();
                ComposeOrchestrator.Result r =
                        orch.composeBlocking(appContext, personaId, layersJson);

                ComposeManifest manifest = r.manifest;
                List<File> files = r.layerFiles;

                BitmapComposer composer = new BitmapComposer();

                Bitmap bmp =
                        composer.composeToBitmapWithRig(
                                manifest,
                                files,
                                rigProvider
                        );

                mainHandler.post(() -> listener.onBitmap(frameIndex, bmp));

            } catch (Throwable t) {
                mainHandler.post(() -> listener.onError(t));
            }

        });

        loop.start();
    }

    public void stop() {
        if (loop != null) loop.stop();
    }
}
