package com.lsam.visualruntime.anim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.lsam.visualruntime.PersonaVisualRuntime;
import com.lsam.visualruntime.compose.ComposeOrchestrator;
import com.lsam.visualruntime.util.RuntimeExecutor;

import java.io.File;

/**
 * Lv3: フレームレンダリング（Phase B composeBlocking を時間軸で回す）
 * - tick: layersJson生成 → composeBlocking → Bitmap decode → mainThread通知
 */
public final class FrameRenderEngine {

    public interface Listener {
        void onBitmap(long frameIndex, @NonNull Bitmap bitmap);
        void onError(@NonNull Throwable error);
    }

    private final Context appContext;
    private final String personaId;
    private final int fps;
    private final LayersJsonProvider provider;
    private final Listener listener;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final RuntimeExecutor renderExec = new RuntimeExecutor(1);

    private FrameLoop loop;

    public FrameRenderEngine(
            @NonNull Context context,
            @NonNull String personaId,
            int fps,
            @NonNull LayersJsonProvider provider,
            @NonNull Listener listener
    ) {
        this.appContext = context.getApplicationContext();
        this.personaId = personaId;
        this.fps = Math.max(1, fps);
        this.provider = provider;
        this.listener = listener;
    }

    public void start() {
        if (loop != null && loop.isRunning()) return;

        loop = new FrameLoop(fps, new FrameLoop.Listener() {
            @Override
            public void onTick(long frameIndex, long nowMs) {
                final String layersJson;
                try {
                    layersJson = provider.provide(frameIndex, nowMs);
                } catch (Throwable t) {
                    postError(t);
                    return;
                }

                renderExec.execute(() -> {
                    try {
                        // Phase B 正規ルート：composeBlocking
                        ComposeOrchestrator orch = new ComposeOrchestrator();
                        ComposeOrchestrator.Result r =
                                orch.composeBlocking(appContext, personaId, layersJson);

                        File out = r.outputFile;
                        Bitmap bmp = BitmapFactory.decodeFile(out.getAbsolutePath());
                        if (bmp == null) throw new IllegalStateException("Bitmap decode failed: " + out);

                        final Bitmap finalBmp = bmp;
                        mainHandler.post(() -> listener.onBitmap(frameIndex, finalBmp));
                    } catch (Throwable t) {
                        postError(t);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                postError(t);
            }
        });

        loop.start();
    }

    public void stop() {
        if (loop != null) loop.stop();
    }

    public void shutdown() {
        stop();
        renderExec.shutdown();
    }

    private void postError(Throwable t) {
        mainHandler.post(() -> listener.onError(t));
    }
}
