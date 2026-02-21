package com.lsam.visualruntime.compose;

import java.util.concurrent.ConcurrentHashMap;

public class ComposeLockRegistry {

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public Object acquire(String key) {
        Object lock = new Object();
        Object existing = locks.putIfAbsent(key, lock);
        return existing != null ? existing : lock;
    }

    public void release(String key, Object lock) {
        // 同一lockの時だけ外す（安全側）
        locks.remove(key, lock);
    }
}
