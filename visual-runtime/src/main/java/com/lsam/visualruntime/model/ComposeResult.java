package com.lsam.visualruntime.model;

import java.io.File;

public class ComposeResult {

    private final File outputFile;
    private final String sha256;
    private final boolean fromCache;
    private final boolean fromFallback;
    private final long elapsedMsTotal;

    public ComposeResult(File outputFile, String sha256, boolean fromCache, boolean fromFallback, long elapsedMsTotal) {
        this.outputFile = outputFile;
        this.sha256 = sha256;
        this.fromCache = fromCache;
        this.fromFallback = fromFallback;
        this.elapsedMsTotal = elapsedMsTotal;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public String getSha256() {
        return sha256;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public boolean isFromFallback() {
        return fromFallback;
    }

    public long getElapsedMsTotal() {
        return elapsedMsTotal;
    }
}
