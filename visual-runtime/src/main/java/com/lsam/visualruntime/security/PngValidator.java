package com.lsam.visualruntime.security;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;

public final class PngValidator {

    private static final byte[] PNG_SIG = new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private PngValidator() {}

    public static boolean isValidPng(File file) {
        if (file == null || !file.exists() || file.length() < 8) return false;

        // signature
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] head = new byte[8];
            int r = fis.read(head);
            if (r != 8) return false;
            for (int i = 0; i < 8; i++) {
                if (head[i] != PNG_SIG[i]) return false;
            }
        } catch (Exception e) {
            return false;
        }

        // decode check (軽量に bounds だけでも良いが、破損検知を強くするためdecode)
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), o);
        if (o.outWidth <= 0 || o.outHeight <= 0) return false;

        // 可能なら軽く実decode（小さい場合のみ）
        if ((long)o.outWidth * (long)o.outHeight <= 512L * 512L) {
            Bitmap b = null;
            try {
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
                b = BitmapFactory.decodeFile(file.getAbsolutePath(), o2);
                return b != null;
            } catch (Throwable t) {
                return false;
            } finally {
                if (b != null && !b.isRecycled()) b.recycle();
            }
        }

        return true;
    }
}
