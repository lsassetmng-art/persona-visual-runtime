package com.lsam.visualruntime.security;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public final class Sha256 {

    private Sha256() {}

    public static String hexOfFile(File file) throws Exception {
        if (file == null) throw new IllegalArgumentException("file is null");
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] buf = new byte[64 * 1024];
        int read;
        try (FileInputStream fis = new FileInputStream(file)) {
            while ((read = fis.read(buf)) != -1) {
                md.update(buf, 0, read);
            }
        }
        return toHex(md.digest());
    }

    public static String hexOfBytes(byte[] bytes) throws Exception {
        if (bytes == null) throw new IllegalArgumentException("bytes is null");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes);
        return toHex(md.digest());
    }

    private static String toHex(byte[] digest) {
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit((b & 0xF), 16));
        }
        return sb.toString();
    }
}
