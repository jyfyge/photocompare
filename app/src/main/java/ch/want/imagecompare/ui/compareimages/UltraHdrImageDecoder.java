package ch.want.imagecompare.ui.compareimages;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import com.davemorrissey.labs.subscaleview.decoder.SkiaImageDecoder;

import java.io.File;

/**
 * Bitmap decoder that keeps the Ultra HDR gain map on Android 14+ by using the platform
 * {@link android.graphics.ImageDecoder} instead of BitmapFactory. Older devices keep the
 * original Skia decoder behaviour.
 */
public class UltraHdrImageDecoder implements com.davemorrissey.labs.subscaleview.decoder.ImageDecoder {

    private final SkiaImageDecoder fallbackDecoder = new SkiaImageDecoder();

    @Override
    public Bitmap decode(final Context context, final Uri uri) throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return fallbackDecoder.decode(context, uri);
        }
        final android.graphics.ImageDecoder.Source source;
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            source = android.graphics.ImageDecoder.createSource(new File(uri.getPath()));
        } else {
            source = android.graphics.ImageDecoder.createSource(context.getContentResolver(), uri);
        }
        return android.graphics.ImageDecoder.decodeBitmap(source);
    }
}
