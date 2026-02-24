package com.lsam.visualruntime.util;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public final class OkHttpProvider {

    private OkHttpProvider() {}

    public static OkHttpClient create() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .callTimeout(25, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }
}
