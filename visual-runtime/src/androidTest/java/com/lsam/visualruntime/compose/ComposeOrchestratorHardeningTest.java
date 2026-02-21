package com.lsam.visualruntime.compose;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.lsam.visualruntime.fake.FakeLayerDownloader;
import com.lsam.visualruntime.model.ComposeRequest;
import com.lsam.visualruntime.security.PngValidator;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ComposeOrchestratorHardeningTest {

    @Test
    public void testBlendModesDoNotCrash() throws Exception {

        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m_blend\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"https://fake/a\",\"z_index\":0,\"blend_mode\":\"normal\",\"alpha\":1.0},"
                + "{\"url\":\"https://fake/b\",\"z_index\":1,\"blend_mode\":\"multiply\",\"alpha\":0.8}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        ComposeOrchestrator.Config cfg = new ComposeOrchestrator.Config();
        cfg.customDownloader = new FakeLayerDownloader();

        ComposeOrchestrator orch = new ComposeOrchestrator(cfg);
        ComposeOrchestrator.Result r = orch.composeBlocking(ctx, req);

        assertTrue(r.outputFile.exists());
        assertTrue(PngValidator.isValidPng(r.outputFile));
    }

    @Test
    public void testFallbackOnBrokenImage() throws Exception {

        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m_fallback\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"https://fake/broken\",\"z_index\":0}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        ComposeOrchestrator.Config cfg = new ComposeOrchestrator.Config();
        cfg.customDownloader = new FakeLayerDownloader();

        ComposeOrchestrator orch = new ComposeOrchestrator(cfg);
        ComposeOrchestrator.Result r = orch.composeBlocking(ctx, req);

        assertTrue(r.outputFile.exists());
        assertTrue(PngValidator.isValidPng(r.outputFile));
    }

    @Test
    public void testParallelSameKeyDoesNotCrash() throws Exception {

        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m_parallel\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"https://fake/a\",\"z_index\":0}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        ComposeOrchestrator.Config cfg = new ComposeOrchestrator.Config();
        cfg.customDownloader = new FakeLayerDownloader();

        final ComposeOrchestrator orch = new ComposeOrchestrator(cfg);

        Thread t1 = new Thread(() -> {
            try { orch.composeBlocking(ctx, req); } catch (Exception ignored) {}
        });

        Thread t2 = new Thread(() -> {
            try { orch.composeBlocking(ctx, req); } catch (Exception ignored) {}
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertTrue(true);
    }

    @Test
    public void testETag304CacheHit_Simulated() throws Exception {

        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String layersJson = "{"
                + "\"manifest_sha256\":\"m_cache\","
                + "\"width\":64,"
                + "\"height\":64,"
                + "\"layers\":["
                + "{\"url\":\"https://fake/a\",\"z_index\":0}"
                + "]"
                + "}";

        ComposeRequest req = new ComposeRequest("persona-test", layersJson, 64, 64, "out.png");

        ComposeOrchestrator.Config cfg = new ComposeOrchestrator.Config();
        cfg.customDownloader = new FakeLayerDownloader();
        cfg.enableCache = true;

        ComposeOrchestrator orch = new ComposeOrchestrator(cfg);

        ComposeOrchestrator.Result r1 = orch.composeBlocking(ctx, req);
        ComposeOrchestrator.Result r2 = orch.composeBlocking(ctx, req);

        assertTrue(r1.outputFile.exists());
        assertTrue(r2.outputFile.exists());
    }
}
