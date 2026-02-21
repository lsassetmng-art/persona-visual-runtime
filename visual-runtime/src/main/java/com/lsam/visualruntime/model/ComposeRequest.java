package com.lsam.visualruntime.model;

import androidx.annotation.NonNull;

public class ComposeRequest {

    private final String personaId;
    private final String layersJson;
    private final int outputWidth;
    private final int outputHeight;
    private final String outputFileName;

    public ComposeRequest(
            @NonNull String personaId,
            @NonNull String layersJson,
            int outputWidth,
            int outputHeight,
            @NonNull String outputFileName
    ) {
        this.personaId = personaId;
        this.layersJson = layersJson;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
        this.outputFileName = outputFileName;
    }

    public String getPersonaId() {
        return personaId;
    }

    public String getLayersJson() {
        return layersJson;
    }

    public int getOutputWidth() {
        return outputWidth;
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public String getOutputFileName() {
        return outputFileName;
    }
}
