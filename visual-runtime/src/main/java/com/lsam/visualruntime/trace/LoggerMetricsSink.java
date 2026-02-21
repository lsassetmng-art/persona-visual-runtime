package com.lsam.visualruntime.trace;

import com.lsam.visualruntime.log.Logger;

public class LoggerMetricsSink implements MetricsSink {

    @Override
    public void record(String name, long elapsedMs) {
        Logger.d("[metric] " + name + "=" + elapsedMs + "ms");
    }

    @Override
    public void recordKV(String key, String value) {
        Logger.d("[metric] " + key + "=" + value);
    }
}
