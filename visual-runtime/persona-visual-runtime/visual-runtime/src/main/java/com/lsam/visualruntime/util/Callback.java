package com.lsam.visualruntime.util;

public interface Callback<T> {
    void onSuccess(T result);
    void onError(Throwable error);
}
