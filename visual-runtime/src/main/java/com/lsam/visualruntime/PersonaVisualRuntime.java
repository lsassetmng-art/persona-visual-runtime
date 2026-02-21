package com.lsam.visualruntime;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lsam.visualruntime.compose.ComposeCallback;
import com.lsam.visualruntime.compose.ComposeOrchestrator;
import com.lsam.visualruntime.compose.StrictModeGuard;
import com.lsam.visualruntime.log.LogLevel;
import com.lsam.visualruntime.log.Logger;
import com.lsam.visualruntime.model.ComposeRequest;
import com.lsam.visualruntime.trace.LoggerMetricsSink;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PersonaVisualRuntime {

    private static final ExecutorService EXEC = Executors.newFixedThreadPool(2);

    private PersonaVisualRuntime() {}

    public static void compose(
            @NonNull final Context context,
            @NonNull final ComposeRequest request,
            @NonNull final ComposeCallback callback
    ) {

        // 本番デフォルト：INFO
        Logger.setLevel(LogLevel.INFO);

        final ComposeOrchestrator.Config cfg = new ComposeOrchestrator.Config();
        cfg.strictModeGuard = StrictModeGuard.strict();
        cfg.metricsSink = new LoggerMetricsSink();
        cfg.enableCache = true;
        cfg.enableFallback = true;

        final ComposeOrchestrator orchestrator = new ComposeOrchestrator(cfg);

        EXEC.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ComposeOrchestrator.Result r = orchestrator.composeBlocking(context, request);
                    callback.onSuccess(r.outputFile, r.sha256);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
}
