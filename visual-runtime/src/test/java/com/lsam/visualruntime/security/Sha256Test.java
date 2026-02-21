package com.lsam.visualruntime.security;

import org.junit.Test;
import static org.junit.Assert.*;

public class Sha256Test {

    @Test
    public void testHexOfBytes() throws Exception {
        byte[] data = "hello".getBytes();
        String sha = Sha256.hexOfBytes(data);
        assertEquals("2cf24dba5fb0a030e...", sha.substring(0, 20)); // prefix match
    }
}
