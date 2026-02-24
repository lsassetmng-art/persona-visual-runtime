package com.lsam.visualruntime.util;

import androidx.annotation.NonNull;

public interface Callback<T> {
    void onSuccess(@NonNull T value);
    void onError(@NonNull Throwable error);
}
