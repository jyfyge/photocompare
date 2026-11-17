package ch.want.imagecompare.ui.compareimages;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;
import ch.want.imagecompare.domain.PhotoComparePreferences;
import ch.want.imagecompare.domain.PhotoViewMediator;
import ch.want.imagecompare.domain.UltraHdrSupport;

/**
 * @link https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * @link https://blog.fossasia.org/how-to-create-a-basic-gallery-application/
 */
public class CompareImagesActivity extends AppCompatActivity {

    private FileImageMediaResolver mediaResolver;
    private PhotoViewMediator photoViewMediator;
    private SwitchMaterial syncToggle;
    private ExecutorService imageLoaderExecutor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_images);
        photoViewMediator = buildPhotoViewMediator();
        photoViewMediator.setOnImageChangeListener(image -> updateHdrColorMode());
        initInitialState(savedInstanceState);
        initToolbar();
    }

    private PhotoViewMediator buildPhotoViewMediator() {
        final PhotoViewMediator mediator = new PhotoViewMediator(new ImageDetailViewImpl(findViewById(R.id.upperImage)), new ImageDetailViewImpl(findViewById(R.id.bottomImage)), this);
        syncToggle = findViewById(R.id.toggleZoomPanSync);
        syncToggle.setOnCheckedChangeListener((buttonView, isChecked) -> mediator.setSyncZoomAndPan(isChecked));
        syncToggle.setChecked(false);
        //
        final ImageButton resetMatrixButton = findViewById(R.id.resetMatrix);
        resetMatrixButton.setOnClickListener(v -> photoViewMediator.resetState());
        return mediator;
    }

    private void initInitialState(final Bundle savedInstanceState) {
        final int topImageIndex;
        final int bottomIndex;
        final ArrayList<ImageBean> selectedBeansFromState;
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            selectedBeansFromState = intent.getParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION);
            topImageIndex = intent.getIntExtra(BundleKeys.KEY_TOPIMAGE_INDEX, 0);
            bottomIndex = intent.getIntExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
        } else {
            selectedBeansFromState = savedInstanceState.getParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION);
            topImageIndex = savedInstanceState.getInt(BundleKeys.KEY_TOPIMAGE_INDEX);
            bottomIndex = savedInstanceState.getInt(BundleKeys.KEY_BOTTOMIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
        }
        mediaResolver = FileImageMediaResolver.create(this, savedInstanceState);
        // Querying MediaStore checks File.exists() for every image. On large libraries this can
        // take several seconds and must not run on the main thread, otherwise the system kills
        // the app with an ANR before the compare view ever becomes responsive.
        imageLoaderExecutor = Executors.newSingleThreadExecutor();
        imageLoaderExecutor.execute(() -> {
            final List<ImageBean> allImageBeans = mediaResolver.execute();
            if (selectedBeansFromState != null && !selectedBeansFromState.isEmpty()) {
                ImageBean.copySelectedState(selectedBeansFromState, allImageBeans);
            }
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                photoViewMediator.initGalleryImageList(allImageBeans, topImageIndex, bottomIndex);
                syncToggle.setChecked(true);
                updateHdrColorMode();
                Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setTitle(R.string.app_name));
            });
        });
    }

    /**
     * Enable HDR rendering while at least one of the compared images is an Ultra HDR image.
     * Gain map detection decodes a small sample only, and always runs off the UI thread.
     */
    private void updateHdrColorMode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                || imageLoaderExecutor == null || imageLoaderExecutor.isShutdown()) {
            return;
        }
        final List<ImageBean> images = photoViewMediator.getGalleryImageList();
        final int topIndex = photoViewMediator.getTopIndex();
        final int bottomIndex = photoViewMediator.getBottomIndex();
        imageLoaderExecutor.execute(() -> {
            final boolean hdr = hasGainMapAt(images, topIndex) || hasGainMapAt(images, bottomIndex);
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    UltraHdrSupport.applyWindowColorMode(this, hdr);
                }
            });
        });
    }

    private static boolean hasGainMapAt(final List<ImageBean> images, final int index) {
        if (index < 0 || index >= images.size()) {
            return false;
        }
        return UltraHdrSupport.hasGainMap(images.get(index).getImageFile());
    }

    @Override
    protected void onDestroy() {
        if (imageLoaderExecutor != null) {
            imageLoaderExecutor.shutdownNow();
        }
        super.onDestroy();
    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.action_loading_images);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.compare_image_menu, menu);
        MenuItem checkBox = menu.findItem(R.id.checkboxStyleDark);
        checkBox.setChecked(photoViewMediator.isCheckboxesDarkMode());
        checkBox = menu.findItem(R.id.showExifDetails);
        checkBox.setChecked(photoViewMediator.isExifDisplay());
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mediaResolver.saveInstanceState(savedInstanceState);
        savedInstanceState.putInt(BundleKeys.KEY_TOPIMAGE_INDEX, photoViewMediator.getTopIndex());
        savedInstanceState.putInt(BundleKeys.KEY_BOTTOMIMAGE_INDEX, photoViewMediator.getBottomIndex());
        savedInstanceState.putParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(photoViewMediator.getGalleryImageList())));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showSelection:
                new ShowSelectedImagesTransition(this, mediaResolver, photoViewMediator).execute();
                return true;
            case R.id.checkboxStyleDark:
                final boolean isNowDarkMode = photoViewMediator.toggleCheckboxStyleDark();
                item.setChecked(isNowDarkMode);
                new PhotoComparePreferences(this).setCheckboxStyleDark(isNowDarkMode);
                return true;
            case R.id.showExifDetails:
                final boolean isNowShowingExif = photoViewMediator.toggleExifDisplay();
                item.setChecked(isNowShowingExif);
                new PhotoComparePreferences(this).setShowExifDetails(isNowShowingExif);
                return true;
            case android.R.id.home:
                new BackToImageListTransition(this, mediaResolver, photoViewMediator).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
