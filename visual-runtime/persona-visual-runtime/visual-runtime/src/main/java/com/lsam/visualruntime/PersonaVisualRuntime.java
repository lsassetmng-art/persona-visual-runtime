package com.lsam.visualruntime;

import java.io.File;
import com.lsam.visualruntime.util.RuntimeExecutor;
import com.lsam.visualruntime.util.Callback;

public class PersonaVisualRuntime {

    private final RuntimeExecutor executor = new RuntimeExecutor(2);

    public PersonaVisualRuntime() {}

    // 既存同期メソッドを呼ぶ前提
    public File composeSync(String personaId, String manifestSha, int width, int height) throws Exception {
        throw new UnsupportedOperationException("composeSync must be implemented (existing Phase B logic)");
    }

    // Phase C Asyncラッパー
    public void composeAsync(
            final String personaId,
            final String manifestSha,
            final int width,
            final int height,
            final Callback<File> callback
    ) {
        executor.execute(() -> {
            try {
                File file = composeSync(personaId, manifestSha, width, height);
                callback.onSuccess(file);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
