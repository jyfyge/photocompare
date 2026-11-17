<div align="center">

# Photo Compare

An Android app for comparing two photos side by side (jyfyge fork).

[简体中文](README.md) · [English](README_EN.md)

[GitHub](https://github.com/jyfyge/photocompare) · [Coolapk](https://www.coolapk.com/u/3222917)

</div>

## Features

- Pick a folder or a capture date, then display two photos from the set side by side.
- Zoom and pan can be synchronized or disabled; synchronization compensates for different image dimensions.
- Mark photos as candidates, then share them or delete the unselected ones.
- Sort by modification time or file name.
- Ultra HDR support on Android 14+: gain maps are preserved by the platform decoder, and the window switches to HDR mode when comparing Ultra HDR photos (actual HDR output depends on the device display).
- Fixed an ANR that killed the app and returned to the home screen when opening the compare view on large photo libraries.

## Compatibility state

- Minimum Android 7.0 (API 24), target Android 17 (API 37).
- Ultra HDR display requires Android 14+ and an HDR-capable screen.
- This fork uses application ID `com.jyfyge.photocompare` and a different signing key from the Play Store build. It can be installed as a separate app; data is not shared.
- APK Signature Scheme v2 + v3 are enabled; v1 is disabled.

## Usage

### Installation

Download `photocompare-jyfyge-v<versionName>_<versionCode>-release.apk` from the Releases page and install it. Because this fork uses a different package name, it appears as a separate app.

### Build

Use JDK 21 and Android SDK with API 37 platform and Build Tools 37.0.0:

```bash
./gradlew assembleRelease
./gradlew testDebugUnitTest
```

`assembleDebug` produces a `-debug.apk`. The `<versionCode>` in the file name comes from `version.properties` in the project root.

### Versioning

- `versionCode`: stored in `version.properties`. It is incremented only when app source or `app/build.gradle` has changed **and** the task produces an APK. Documentation edits and unit tests do not change it, and rebuilding unchanged code does not consume a number.
- `versionName`: semantic `MAJOR.MINOR.PATCH`. MAJOR is for incompatible changes, MINOR for new features, PATCH for bug fixes.
- Pre-release builds use a beta suffix, for example `1.1.0-beta.1`; the final release drops the suffix.

## Language

This repository keeps the Chinese README as `README.md`. This file is the English version. Release notes are provided in both languages.

## Discussion

- Report issues and suggestions through this repository's Issues page.
- Upstream project: [sniederb/photocompare](https://github.com/sniederb/photocompare).

## Security and privacy

- The app reads gallery images locally only. It does not access the network or upload photos.
- Reading images requires the system photo permission.
- The release keystore `photocompare-release.jks` and `keystore.properties` are excluded from Git. If the key is lost, updates to the installed app can no longer be signed; keep a separate backup.

## License

This project is based on [sniederb/photocompare](https://github.com/sniederb/photocompare) and keeps the upstream **Non-Profit Open Software License 3.0**. The full text is in `LICENSE.txt`.

As required by the license:

- All copyright, patent, trademark and license notices from the original source are retained.
- This file and `README.md` together serve as the attribution notice required by section 6: the Original Work has been modified in this fork, including the ANR fix, Android 17 adaptation and Ultra HDR support.
- The names and trademarks of the original author and contributors may not be used to endorse or promote this fork.
- Recipients must be informed of the license terms when copies are distributed.

Section 17(d) states that distribution under the Non-Profit OSL 3.0 name is only allowed if you declare that you are a not-for-profit organization deriving no revenue from distribution, support or services. Otherwise the ordinary OSL 3.0 must be used with clear notices. This fork currently follows the former path; publishers should confirm their own status. This is not legal advice.

## Contributing

- Build and run basic tests locally before committing and pushing.
- `app/src/test` contains JVM unit tests; `app/src/androidTest` contains instrumented tests that need a device or emulator. Both should be kept.
- Follow the versioning rules above. Do not manually edit the `versionCode` of an already published release.
- Never commit signing files or passwords.

## Credits

- Upstream author Simon Niederberger and the [photocompare](https://github.com/sniederb/photocompare) project.
- [Glide](https://github.com/bumptech/glide) and [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view).
- The structure of this README follows [KernelSU](https://github.com/tiann/KernelSU).
