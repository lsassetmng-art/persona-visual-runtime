package com.lsam.visualruntime.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RuntimeExecutor {

    private final ExecutorService executor;

    public RuntimeExecutor(int threads) {
        this.executor = Executors.newFixedThreadPool(Math.max(1, threads));
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
