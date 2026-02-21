package com.lsam.visualruntime.log;

public enum LogLevel {
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    TRACE(5);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }

    public boolean allows(LogLevel other) {
        return other != null && other.priority() <= this.priority;
    }
}
