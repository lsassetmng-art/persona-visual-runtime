package com.lsam.visualruntime.util;

public interface Callback<T> {
    void onSuccess(T value);
    void onError(Throwable error);
}
