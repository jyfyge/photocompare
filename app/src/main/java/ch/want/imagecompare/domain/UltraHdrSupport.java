package ch.want.imagecompare.domain;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.File;

/**
 * Helpers for Ultra HDR (JPEG gain map) images. Ultra HDR is only available from API 34 onwards;
 * on older versions these methods are no-ops so existing behaviour is preserved.
 */
public final class UltraHdrSupport {

    private static final int SAMPLE_SIZE = 16;

    private UltraHdrSupport() {
    }

    /**
     * Decode a small thumbnail and check whether the image carries a gain map. This avoids
     * decoding the full resolution image just to detect Ultra HDR.
     */
    public static boolean hasGainMap(final File imageFile) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE || imageFile == null || !imageFile.exists()) {
            return false;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = SAMPLE_SIZE;
        Bitmap sample = null;
        try {
            sample = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            return sample != null && sample.hasGainmap();
        } catch (final RuntimeException ex) {
            return false;
        } finally {
            if (sample != null) {
                sample.recycle();
            }
        }
    }

    /**
     * Switch the window colour mode so a gain map can be rendered at full dynamic range.
     */
    public static void applyWindowColorMode(final Activity activity, final boolean hdr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity != null && !activity.isFinishing()) {
            activity.getWindow().setColorMode(hdr ? ActivityInfo.COLOR_MODE_HDR : ActivityInfo.COLOR_MODE_DEFAULT);
        }
    }
}
