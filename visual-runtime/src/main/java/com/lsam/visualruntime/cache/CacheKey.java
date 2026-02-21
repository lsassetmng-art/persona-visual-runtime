package com.lsam.visualruntime.cache;

public class CacheKey {

    private final String personaId;
    private final String manifestSha256;
    private final int width;
    private final int height;

    public CacheKey(String personaId, String manifestSha256, int width, int height) {
        this.personaId = personaId;
        this.manifestSha256 = manifestSha256;
        this.width = width;
        this.height = height;
    }

    public String getPersonaId() { return personaId; }
    public String getManifestSha256() { return manifestSha256; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public String fileNamePng() {
        return manifestSha256 + "_" + width + "x" + height + ".png";
    }
}
