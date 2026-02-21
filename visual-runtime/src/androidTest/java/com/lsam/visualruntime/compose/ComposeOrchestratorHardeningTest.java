package com.lsam.visualruntime.compose;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.lsam.visualruntime.cache.DiskCacheManager;
import com.lsam.visualruntime.model.ComposeRequest;
import com.lsam.visualruntime.security.PngValidator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ComposeOrchestratorHardeningTest {

    private Context ctx;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        server = new MockWebServer();
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) server.shutdown();
    }

    private static byte[] tinyPngBytes() {
        // 1x1 PNG (base64 decoded) minimal valid
        return new byte[] {
                (byte)0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A,
                0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52,
                0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x01,
                0x08,0x06,0x00,0x00,0x00,(byte)0x1F,(byte)0x15,(byte)0xC4,(byte)0x89,
                0x00,0x00,0x00,0x0A,0x49,0x44,0x41,0x54,
                0x78,(byte)0x9C,0x63,0x00,0x01,0x00,0x00,0x05,0x00,0x01,
                0x0D,0x0A,0x2D,(byte)0xB4,
                0x00,0x00,0x00,0x00,0x49,0x45,0x4E,0x44,
                (byte)0xAE,0x42,0x60,(byte)0x82
        };
    }

    @Test
    public void testETag304CacheHit() throws Exception {

        // 1回目：200 + ETag
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("ETag", "\"v1\"")
                .setBody(new okio.Buffer().write(tinyPngBytes()))
        );

        // 2回目：304 Not Modified
        server.enqueue(new MockResponse()
                .setResponseCode(304)
        );

        String url = server.url("/layer.png").toString();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m1\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"" + url + "\",\"z_index\":0,\"blend_mode\":\"normal\",\"alpha\":1.0,\"scale_mode\":\"fit_center\"}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        ComposeOrchestrator.Config cfg = new ComposeOrchestrator.Config();
        cfg.enableCache = true;
        cfg.enableFallback = true;

        ComposeOrchestrator orch = new ComposeOrchestrator(cfg);

        // 1st compose
        ComposeOrchestrator.Result r1 = orch.composeBlocking(ctx, req);
        assertTrue(r1.outputFile.exists());
        assertTrue(PngValidator.isValidPng(r1.outputFile));

        // 2nd compose (should hit cache quickly OR 304 path)
        ComposeOrchestrator.Result r2 = orch.composeBlocking(ctx, req);
        assertTrue(r2.outputFile.exists());
        assertTrue(PngValidator.isValidPng(r2.outputFile));

        // サーバーへのリクエスト2回分が消化されている（200 + 304）
        assertEquals(0, server.getRequestCount() - 2);
    }

    @Test
    public void testFallbackOnBrokenImage() throws Exception {

        // 壊れたデータ（非PNG/JPEG/WebP）
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("NOT_AN_IMAGE")
        );

        String url = server.url("/broken.bin").toString();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m_broken\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"" + url + "\",\"z_index\":0,\"blend_mode\":\"normal\",\"alpha\":1.0}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        ComposeOrchestrator.Config cfg = new ComposeOrchestrator.Config();
        cfg.enableCache = false;
        cfg.enableFallback = true;
        cfg.fallbackWidth = 32;
        cfg.fallbackHeight = 32;

        ComposeOrchestrator orch = new ComposeOrchestrator(cfg);

        ComposeOrchestrator.Result r = orch.composeBlocking(ctx, req);

        // fallbackは fromFallback=true では返してない実装なので「PNG valid」で確認
        assertTrue(r.outputFile.exists());
        assertTrue(PngValidator.isValidPng(r.outputFile));
    }

    @Test
    public void testBlendModesDoNotCrash() throws Exception {

        // 2 layers (both tiny png)
        server.enqueue(new MockResponse().setResponseCode(200).setBody(new okio.Buffer().write(tinyPngBytes())));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(new okio.Buffer().write(tinyPngBytes())));

        String u1 = server.url("/a.png").toString();
        String u2 = server.url("/b.png").toString();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m_blend\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"" + u1 + "\",\"z_index\":0,\"blend_mode\":\"normal\",\"alpha\":1.0},"
                + "{\"url\":\"" + u2 + "\",\"z_index\":1,\"blend_mode\":\"multiply\",\"alpha\":0.8}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        ComposeOrchestrator orch = new ComposeOrchestrator(new ComposeOrchestrator.Config());
        ComposeOrchestrator.Result r = orch.composeBlocking(ctx, req);

        assertTrue(r.outputFile.exists());
        assertTrue(PngValidator.isValidPng(r.outputFile));
    }

    @Test
    public void testParallelSameKeyDoesNotCrash() throws Exception {

        // 1 layer (tiny png) - serve enough responses for concurrency
        server.enqueue(new MockResponse().setResponseCode(200).setBody(new okio.Buffer().write(tinyPngBytes())));
        server.enqueue(new MockResponse().setResponseCode(304));
        server.enqueue(new MockResponse().setResponseCode(304));

        String url = server.url("/p.png").toString();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m_parallel\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"" + url + "\",\"z_index\":0}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        final ComposeOrchestrator orch = new ComposeOrchestrator(new ComposeOrchestrator.Config());

        final CountDownLatch latch = new CountDownLatch(3);
        final Throwable[] err = new Throwable[1];

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    ComposeOrchestrator.Result r = orch.composeBlocking(ctx, req);
                    if (!PngValidator.isValidPng(r.outputFile)) throw new AssertionError("png invalid");
                } catch (Throwable t) {
                    err[0] = t;
                } finally {
                    latch.countDown();
                }
            }
        };

        new Thread(task).start();
        new Thread(task).start();
        new Thread(task).start();

        assertTrue(latch.await(20, TimeUnit.SECONDS));
        assertNull(err[0]);
    }
}
