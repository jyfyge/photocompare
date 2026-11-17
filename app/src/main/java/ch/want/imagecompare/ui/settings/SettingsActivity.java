package ch.want.imagecompare.ui.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ch.want.imagecompare.R;
import ch.want.imagecompare.domain.PhotoComparePreferences;

/**
 * Settings screen: display preferences and project information about this fork.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initSizeFormatSetting();
        initAboutSection();
    }

    private void initSizeFormatSetting() {
        final PhotoComparePreferences preferences = new PhotoComparePreferences(this);
        final RadioGroup group = findViewById(R.id.sizeFormatGroup);
        group.check(preferences.isShowResolution() ? R.id.sizeFormatResolution : R.id.sizeFormatMegapixel);
        group.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            final boolean showResolution = checkedId == R.id.sizeFormatResolution;
            preferences.setShowResolution(showResolution);
        });
    }

    private void initAboutSection() {
        ((TextView) findViewById(R.id.aboutVersion)).setText(
                getString(R.string.settings_about_version) + ": " + versionName());
        ((TextView) findViewById(R.id.aboutAuthor)).setText(
                getString(R.string.settings_about_author) + ": jyfyge (https://www.coolapk.com/u/3222917)");
        ((TextView) findViewById(R.id.aboutRepository)).setText(
                getString(R.string.settings_about_repository) + ": https://github.com/jyfyge/photocompare");
    }

    private String versionName() {
        try {
            final PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName + " (" + info.versionCode + ")";
        } catch (final PackageManager.NameNotFoundException e) {
            Log.w("SettingsActivity", "Version not available: " + e.getMessage());
            return "?";
        }
    }
}
