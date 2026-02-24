package com.lsam.visualruntime.anim;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 毎フレームのcomposeが重ならないよう直列化。
 */
public final class RenderSerialExecutor {

    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    public void execute(Runnable r) {
        exec.execute(r);
    }

    public void shutdown() {
        exec.shutdown();
    }
}
