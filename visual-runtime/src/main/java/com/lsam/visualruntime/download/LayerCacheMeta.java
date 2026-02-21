package com.lsam.visualruntime.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class LayerCacheMeta {

    private final File metaFile;

    public LayerCacheMeta(File layerFile) {
        this.metaFile = new File(layerFile.getAbsolutePath() + ".meta");
    }

    public String getEtag() {
        Properties p = load();
        return p.getProperty("etag", null);
    }

    public String getLastModified() {
        Properties p = load();
        return p.getProperty("last_modified", null);
    }

    public void set(String etag, String lastModified) {
        Properties p = new Properties();
        if (etag != null && !etag.trim().isEmpty()) p.setProperty("etag", etag.trim());
        if (lastModified != null && !lastModified.trim().isEmpty()) p.setProperty("last_modified", lastModified.trim());

        try (FileOutputStream fos = new FileOutputStream(metaFile)) {
            p.store(fos, "layer cache meta");
            fos.flush();
        } catch (Exception ignore) {
        }
    }

    private Properties load() {
        Properties p = new Properties();
        if (!metaFile.exists()) return p;

        try (FileInputStream fis = new FileInputStream(metaFile)) {
            p.load(fis);
        } catch (Exception ignore) {
        }
        return p;
    }
}
