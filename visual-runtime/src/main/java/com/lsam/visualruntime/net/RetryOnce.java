package com.lsam.visualruntime.net;

import java.io.IOException;

public final class RetryOnce {

    private RetryOnce(){}

    public interface IoSupplier<T> {
        T get() throws IOException;
    }

    public static <T> T run(IoSupplier<T> op) throws IOException {
        try {
            return op.get();
        } catch (IOException first) {
            return op.get();
        }
    }
}
