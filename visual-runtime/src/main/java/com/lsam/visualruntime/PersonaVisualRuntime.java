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
