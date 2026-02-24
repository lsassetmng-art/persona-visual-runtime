package com.lsam.visualruntime;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lsam.visualruntime.compose.ComposeOrchestrator;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PersonaVisualRuntime {

    private static final ExecutorService EXEC = Executors.newFixedThreadPool(2);

    private PersonaVisualRuntime() {}

    public interface Callback {
        void onSuccess(@NonNull File outputPng, boolean fromCache);
        void onError(@NonNull Exception e);
    }

    public static void compose(
            @NonNull final Context context,
            @NonNull final String personaId,
            @NonNull final String layersJson,
            @NonNull final Callback callback
    ) {
        EXEC.execute(new Runnable() {
            @Override public void run() {
                try {
                    ComposeOrchestrator orch = new ComposeOrchestrator();
                    ComposeOrchestrator.Result r = orch.composeBlocking(context, personaId, layersJson);
                    callback.onSuccess(r.outputFile, r.fromCache);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
}

    // ===== Phase C Async追加 =====
    private final com.lsam.visualruntime.util.RuntimeExecutor __executor =
            new com.lsam.visualruntime.util.RuntimeExecutor(2);

    public void composeAsync(
            final String personaId,
            final String manifestSha,
            final int width,
            final int height,
            final com.lsam.visualruntime.util.Callback<java.io.File> cb
    ) {
        __executor.execute(() -> {
            try {
                java.io.File f = composeSync(personaId, manifestSha, width, height);
                cb.onSuccess(f);
            } catch (Throwable t) {
                cb.onError(t);
            }
        });
    }

    // ===== Phase D FrameLoop開始 =====
    public com.lsam.visualruntime.anim.FrameLoop startFrameLoop(
            int fps,
            com.lsam.visualruntime.anim.AudioAmplitudeProvider amp,
            com.lsam.visualruntime.anim.FrameLoop.Listener listener
    ) {
        com.lsam.visualruntime.anim.FrameLoop loop =
                new com.lsam.visualruntime.anim.FrameLoop(fps, listener, amp);
        loop.start();
        return loop;
    }
