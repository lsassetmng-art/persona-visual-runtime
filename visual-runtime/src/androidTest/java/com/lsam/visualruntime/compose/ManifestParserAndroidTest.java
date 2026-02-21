package com.lsam.visualruntime.compose;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lsam.visualruntime.model.ComposeManifest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ManifestParserAndroidTest {

    @Test
    public void testParseBasic() throws Exception {

        String json = "{"
                + "\"manifest_sha256\":\"abc\","
                + "\"width\":512,"
                + "\"height\":512,"
                + "\"layers\":["
                + "{\"url\":\"https://example.com/a.png\",\"z_index\":0}"
                + "]"
                + "}";

        ManifestParser parser = new ManifestParser();
        ComposeManifest m = parser.parse(json);

        assertEquals(512, m.getWidth());
        assertEquals(1, m.getLayers().size());
    }
}
