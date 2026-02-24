package com.lsam.visualruntime.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RuntimeExecutor {
    private final ExecutorService exec;

    public RuntimeExecutor(int threads) {
        this.exec = Executors.newFixedThreadPool(Math.max(1, threads));
    }

    public void execute(Runnable r) {
        exec.execute(r);
    }

    public void shutdown() {
        exec.shutdown();
    }
}
