package com.lsam.visualruntime.trace;

public interface MetricsSink {

    void record(String name, long elapsedMs);

    void recordKV(String key, String value);
}
