package com.lsam.visualruntime.anim;

import android.os.Handler;
import android.os.HandlerThread;

public final class FrameLoop {

    public interface Listener {
        void onFrame(AnimationFrame frame);
    }

    private final HandlerThread thread;
    private final Handler handler;
    private final int fps;
    private final Listener listener;
    private final AudioAmplitudeProvider amp;

    private boolean running = false;
    private long frameIndex = 0;

    public FrameLoop(int fps, Listener listener, AudioAmplitudeProvider amp) {
        this.fps = Math.max(1, fps);
        this.listener = listener;
        this.amp = amp;
        thread = new HandlerThread("visualruntime-loop");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public void start() {
        if (running) return;
        running = true;
        tick();
    }

    public void stop() {
        running = false;
        handler.removeCallbacksAndMessages(null);
    }

    public void shutdown() {
        stop();
        thread.quitSafely();
    }

    private void tick() {
        if (!running) return;

        long now = System.currentTimeMillis();
        float blink = SimpleAnimators.blink(now);
        float mouth = SimpleAnimators.mouth(now, amp);

        if (listener != null) {
            listener.onFrame(new AnimationFrame(frameIndex++, now, mouth, blink));
        }

        handler.postDelayed(this::tick, 1000L / fps);
    }
}
