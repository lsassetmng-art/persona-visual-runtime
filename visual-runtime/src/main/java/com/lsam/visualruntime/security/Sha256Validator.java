package com.lsam.visualruntime.security;

public final class Sha256Validator {

    private Sha256Validator() {}

    public static boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

    public static void requireMatch(String expectedHex, String actualHex) {
        if (expectedHex == null || expectedHex.trim().isEmpty()) {
            return; // expected not provided -> skip
        }
        if (!equalsIgnoreCase(expectedHex.trim(), actualHex)) {
            throw new IllegalStateException("sha256 mismatch");
        }
    }
}
