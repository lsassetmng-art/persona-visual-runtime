package com.lsam.visualruntime.compose;

import java.io.File;

public interface ComposeCallback {

    void onSuccess(File outputFile, String sha256);

    void onError(Exception exception);
}
