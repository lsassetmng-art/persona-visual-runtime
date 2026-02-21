package com.lsam.visualruntime.render;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.lsam.visualruntime.model.ComposeManifest;
import com.lsam.visualruntime.model.LayerSpec;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BitmapComposerTest {

    @Test
    public void testComposeSingleLayer() throws Exception {

        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // create tiny PNG
        Bitmap bmp = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        File f = new File(ctx.getCacheDir(), "test_layer.png");
        try (FileOutputStream fos = new FileOutputStream(f)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        bmp.recycle();

        ComposeManifest manifest = new ComposeManifest(
                "abc",
                64,
                64,
                Collections.singletonList(
                        new LayerSpec("dummy", 0, 1f, "normal")
                )
        );

        BitmapComposer composer = new BitmapComposer();
        Bitmap result = composer.composeToBitmap(manifest, Collections.singletonList(f));

        assertNotNull(result);
        assertEquals(64, result.getWidth());
        result.recycle();
    }
}
