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
    // Decode the gain map at a moderate sample size; the gain map layer is already
    // low resolution and this keeps the cached bitmap small.
    private static final int GAIN_MAP_SAMPLE_SIZE = 4;

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
     * Extract the gain map image itself from an Ultra HDR photo and convert it to a visible
     * grayscale image. Decodes a small sample only, so this is safe to call off the UI thread.
     *
     * <p>The raw gain map contents are a single-channel enhancement layer whose values are not
     * meaningful as a directly displayable image (rendering them yields black). We normalise the
     * channel values across the image into the full 0..255 range so the gain distribution becomes
     * visible as a greyscale map.</p>
     *
     * @return a visible greyscale bitmap, or {@code null} if the image has no gain map.
     */
    public static Bitmap extractGainMap(final File imageFile) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE || imageFile == null || !imageFile.exists()) {
            return null;
        }
        Bitmap sample = null;
        try {
            // BitmapFactory.decodeFile is the same path hasGainMap() uses and is proven to keep
            // the gain map attached. ImageDecoder with a target sample size silently strips it.
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = GAIN_MAP_SAMPLE_SIZE;
            sample = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            if (sample == null || !sample.hasGainmap() || sample.getGainmap() == null) {
                return null;
            }
            final Bitmap contents = sample.getGainmap().getGainmapContents();
            return contents == null ? null : toVisibleGrayscale(contents);
        } catch (final Exception ex) {
            return null;
        } finally {
            if (sample != null) {
                sample.recycle();
            }
        }
    }

    /**
     * Convert the enhancement-layer bitmap into a displayable greyscale image by reading each
     * pixel's strongest channel and stretching the min..max range to 0..255.
     */
    private static Bitmap toVisibleGrayscale(final Bitmap contents) {
        final int width = contents.getWidth();
        final int height = contents.getHeight();
        final int pixelCount = width * height;
        final int[] pixels = new int[pixelCount];
        contents.getPixels(pixels, 0, width, 0, 0, width, height);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (final int pixel : pixels) {
            final int r = (pixel >> 16) & 0xFF;
            final int g = (pixel >> 8) & 0xFF;
            final int b = pixel & 0xFF;
            final int alpha = (pixel >>> 24) & 0xFF;
            // Single-channel maps may store the value in alpha or in one colour channel.
            final int value = Math.max(Math.max(r, g), Math.max(b, alpha));
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        final int range = Math.max(1, max - min);
        final Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final int[] out = new int[pixelCount];
        for (int i = 0; i < pixelCount; i++) {
            final int pixel = pixels[i];
            final int r = (pixel >> 16) & 0xFF;
            final int g = (pixel >> 8) & 0xFF;
            final int b = pixel & 0xFF;
            final int alpha = (pixel >>> 24) & 0xFF;
            final int value = Math.max(Math.max(r, g), Math.max(b, alpha));
            final int gray = (value - min) * 255 / range;
            out[i] = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
        }
        result.setPixels(out, 0, width, 0, 0, width, height);
        return result;
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
