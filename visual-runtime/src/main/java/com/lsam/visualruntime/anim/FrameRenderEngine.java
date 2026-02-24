package com.lsam.visualruntime.anim;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.lsam.visualruntime.PersonaVisualRuntime;

import java.io.File;
import java.io.FileInputStream;

public final class FrameRenderEngine {

    public interface Listener {
        void onBitmap(long frameIndex, Bitmap bitmap);
        void onError(Throwable error);
    }

    private final PersonaVisualRuntime runtime;
    private final RenderSerialExecutor renderExec;
    private final FrameTicker ticker;
    private final Handler mainHandler;

    private final String personaId;
    private final String manifestSha;
    private final int width;
    private final int height;
    private final Listener listener;

    public FrameRenderEngine(
            PersonaVisualRuntime runtime,
            int fps,
            String personaId,
            String manifestSha,
            int width,
            int height,
            Listener listener
    ) {
        this.runtime = runtime;
        this.renderExec = new RenderSerialExecutor();
        this.personaId = personaId;
        this.manifestSha = manifestSha;
        this.width = width;
        this.height = height;
        this.listener = listener;
        this.mainHandler = new Handler(Looper.getMainLooper());

        this.ticker = new FrameTicker(fps, this::onTick);
    }

    public void start() {
        ticker.start();
    }

    public void stop() {
        ticker.stop();
        renderExec.shutdown();
    }

    public void shutdown() {
        ticker.shutdown();
        renderExec.shutdown();
    }

    private void onTick(long frameIndex, long nowMs) {

        renderExec.execute(() -> {
            try {
                File file = runtime.composeSync(personaId, manifestSha, width, height);

                try (FileInputStream in = new FileInputStream(file)) {
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    if (bmp == null) throw new RuntimeException("Bitmap decode failed");

                    mainHandler.post(() -> {
                        if (listener != null) {
                            listener.onBitmap(frameIndex, bmp);
                        }
                    });
                }

            } catch (Throwable t) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(t);
                    }
                });
            }
        });
    }
}
