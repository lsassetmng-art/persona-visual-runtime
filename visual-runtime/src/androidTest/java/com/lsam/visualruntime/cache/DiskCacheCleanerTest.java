package com.lsam.visualruntime.cache;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DiskCacheCleanerTest {

    @Test
    public void testLruClean() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File dir = new File(ctx.getCacheDir(), "persona-runtime/test-lru");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        // create 5 files ~1MB each
        for (int i = 0; i < 5; i++) {
            File f = new File(dir, "f" + i + ".bin");
            try (FileOutputStream fos = new FileOutputStream(f)) {
                byte[] buf = new byte[256 * 1024];
                for (int k = 0; k < 4; k++) fos.write(buf); // ~1MB
            }
            f.setLastModified(System.currentTimeMillis() - (10_000L * (5 - i)));
        }

        long max = 2L * 1024L * 1024L; // 2MB
        DiskCacheCleaner cleaner = new DiskCacheCleaner(max);
        cleaner.clean(dir);

        long total = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) if (f.isFile()) total += f.length();
        }

        assertTrue(total <= max);
    }
}
