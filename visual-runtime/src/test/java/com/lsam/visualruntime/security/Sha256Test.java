package com.lsam.visualruntime.security;

import org.junit.Test;
import static org.junit.Assert.*;

public class Sha256Test {

    @Test
    public void testHexOfBytes() throws Exception {
        byte[] data = "hello".getBytes();
        String sha = Sha256.hexOfBytes(data);

        // SHA-256("hello") =
        // 2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824
        assertEquals(64, sha.length());
        assertTrue(sha.startsWith("2cf24dba5fb0a30e26e83b2ac5b9e29e"));
    }
}
