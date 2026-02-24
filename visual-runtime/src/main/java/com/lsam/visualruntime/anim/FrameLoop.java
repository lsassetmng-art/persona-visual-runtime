package com.lsam.visualruntime.anim;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Lv3: 時間軸の基礎
 * - fpsでtick
 * - frameIndexとnowMsを通知
 */
public final class FrameLoop {

    public interface Listener {
        void onTick(long frameIndex, long nowMs);
        default void onError(Throwable t) {}
    }

    private final int fps;
    private final Listener listener;

    private ScheduledExecutorService scheduler;
    private volatile boolean running = false;
    private long frameIndex = 0;

    public FrameLoop(int fps, Listener listener) {
        this.fps = Math.max(1, fps);
        this.listener = listener;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        frameIndex = 0;

        long periodMs = Math.max(1, 1000L / fps);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                long now = System.currentTimeMillis();
                listener.onTick(frameIndex++, now);
            } catch (Throwable t) {
                try { listener.onError(t); } catch (Throwable ignore) {}
            }
        }, 0, periodMs, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    public boolean isRunning() {
        return running;
    }
}
