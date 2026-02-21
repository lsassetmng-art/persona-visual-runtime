package com.lsam.visualruntime.security;

import java.io.File;
import java.io.FileInputStream;

public final class ImageFormatDetector {

    public enum Format {
        PNG("png"),
        JPEG("jpg"),
        WEBP("webp"),
        UNKNOWN("bin");

        private final String ext;
        Format(String ext) { this.ext = ext; }
        public String ext() { return ext; }
    }

    private ImageFormatDetector() {}

    public static Format detect(File file) {
        if (file == null || !file.exists() || file.length() < 12) return Format.UNKNOWN;

        byte[] head = new byte[16];
        int r = 0;

        try (FileInputStream fis = new FileInputStream(file)) {
            r = fis.read(head);
        } catch (Exception e) {
            return Format.UNKNOWN;
        }

        if (r < 12) return Format.UNKNOWN;

        // PNG signature: 89 50 4E 47 0D 0A 1A 0A
        if ((head[0] & 0xFF) == 0x89 &&
            (head[1] & 0xFF) == 0x50 &&
            (head[2] & 0xFF) == 0x4E &&
            (head[3] & 0xFF) == 0x47 &&
            (head[4] & 0xFF) == 0x0D &&
            (head[5] & 0xFF) == 0x0A &&
            (head[6] & 0xFF) == 0x1A &&
            (head[7] & 0xFF) == 0x0A) {
            return Format.PNG;
        }

        // JPEG: FF D8 FF
        if ((head[0] & 0xFF) == 0xFF &&
            (head[1] & 0xFF) == 0xD8 &&
            (head[2] & 0xFF) == 0xFF) {
            return Format.JPEG;
        }

        // WebP: "RIFF"...."WEBP"
        if ((head[0] & 0xFF) == 0x52 && // R
            (head[1] & 0xFF) == 0x49 && // I
            (head[2] & 0xFF) == 0x46 && // F
            (head[3] & 0xFF) == 0x46 && // F
            (head[8] & 0xFF) == 0x57 && // W
            (head[9] & 0xFF) == 0x45 && // E
            (head[10] & 0xFF) == 0x42 && // B
            (head[11] & 0xFF) == 0x50) { // P
            return Format.WEBP;
        }

        return Format.UNKNOWN;
    }

    public static void requireSupported(File file) {
        Format f = detect(file);
        if (f == Format.UNKNOWN) {
            throw new IllegalStateException("unsupported image format");
        }
    }
}
