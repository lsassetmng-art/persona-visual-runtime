package com.lsam.visualruntime.anim;

import android.os.Handler;
import android.os.HandlerThread;

public final class FrameTicker {

    public interface Listener {
        void onTick(long frameIndex, long nowMs);
    }

    private final HandlerThread thread;
    private final Handler handler;
    private final int fps;
    private final Listener listener;

    private volatile boolean running = false;
    private long frameIndex = 0;

    public FrameTicker(int fps, Listener listener) {
        this.fps = Math.max(1, fps);
        this.listener = listener;
        this.thread = new HandlerThread("visualruntime-ticker");
        this.thread.start();
        this.handler = new Handler(thread.getLooper());
    }

    public void start() {
        if (running) return;
        running = true;
        frameIndex = 0;
        scheduleNext();
    }

    public void stop() {
        running = false;
        handler.removeCallbacksAndMessages(null);
    }

    public void shutdown() {
        stop();
        thread.quitSafely();
    }

    private void scheduleNext() {
        if (!running) return;

        long now = System.currentTimeMillis();
        if (listener != null) {
            listener.onTick(frameIndex++, now);
        }

        long delay = 1000L / fps;
        handler.postDelayed(this::scheduleNext, delay);
    }
}
