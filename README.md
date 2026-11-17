# Photo Compare

一个用于并排对比两张照片的 Android 应用（jyfyge fork）。

## 主要功能

- 从图库文件夹或拍摄日期中选取一组照片，按顺序取两张并排显示。
- 缩放与平移可同步，也可手动关闭；同步时会按两张图的尺寸比例校正。
- 支持标记候选照片，之后统一分享，或删除未选中的照片。
- 支持按修改时间或文件名排序。
- Android 14+ 支持 Ultra HDR：使用系统解码器保留 gain map，并在对比 Ultra HDR 照片时切换 HDR 显示模式（实际效果取决于设备屏幕）。
- 修复了在图片数量较多的图库中进入对比页时，主线程扫描媒体库触发 ANR、应用被系统杀掉并闪回首页的问题。

## 兼容性状态

- 最低支持 Android 7.0（API 24），目标版本为 Android 17（API 37）。
- Ultra HDR 显示需要 Android 14+ 和支持 HDR 的屏幕。
- 本分支包名为 `com.jyfyge.photocompare`，与 Play 商店版签名不同，可作为独立应用同时安装，数据不共享。
- 签名方案为 APK Signature Scheme v2 + v3，不启用 v1。

## 使用方式

### 安装

从本项目 Releases 页面下载 `photocompare-jyfyge-v<versionName>_<versionCode>-release.apk` 并安装。由于是新包名，安装后会出现一个新的应用图标。

### 构建

使用 JDK 21 和 Android SDK（含 API 37 平台与 Build Tools 37.0.0）：

```bash
./gradlew assembleRelease
./gradlew testDebugUnitTest
```

`assembleDebug` 会产出 `-debug.apk`。APK 文件名中的 `<versionCode>` 来自项目根目录的 `version.properties`。

### 版本号约定

- `versionCode`：保存在 `version.properties` 中。只有当**应用源码或 `app/build.gradle` 有改动**，并且本次任务会产出 APK 时才会 +1；仅修改文档或运行单元测试不会改变它，重复构建同一份代码也不会重复涨号。
- `versionName`：采用 `主版本.次版本.修订号`。主版本用于不兼容的大改，次版本用于新增功能，修订号用于仅修复问题。
- 发布前版本追加 beta 后缀，例如 `1.1.0-beta.1`，正式版去掉后缀。

## 语言

本 README 默认使用中文。Release Notes 同时提供中文和英文版本。

## 讨论与反馈

- 本分支的问题与建议：通过本仓库的 Issues 提交。
- 上游项目：[sniederb/photocompare](https://github.com/sniederb/photocompare)。

## 安全与隐私

- 应用只在本地读取图库图片，不联网、不上传照片。
- 读取图片需要系统授予照片访问权限。
- 发布密钥 `photocompare-release.jks` 和 `keystore.properties` 不进入仓库；密钥丢失后将无法为现有应用发布更新，请单独备份。

## 许可证

本项目基于 [sniederb/photocompare](https://github.com/sniederb/photocompare) 修改，沿用上游的 **Non-Profit Open Software License 3.0**，全文见 `LICENSE.txt`。

依许可证要求：

- 保留上游源码中的版权、专利、商标及许可声明。
- 本 README 同时作为许可证第 6 条要求的修改声明：原作品在本分支中已被修改，修改内容包括 ANR 修复、Android 17 适配和 Ultra HDR 支持。
- 不得使用原作者或贡献者的名称、商标为衍生作品背书。
- 分发衍生作品时必须保留许可条款，并确保接收方知悉这些条款。

许可证第 17(d) 条规定：只有在你声明自己是“不从分发、支持或服务中获取任何收入的非营利组织”时，才可以 Non-Profit OSL 3.0 名义分发；否则必须改用普通 OSL 3.0 并发布清晰声明。本分支目前按前者发布，发布者应自行确认是否符合条件。本说明不构成法律意见。

## 贡献

- 修改代码后，先在本地完成构建和基本测试，确认通过后再提交和推送。
- `app/src/test` 是 JVM 单元测试，`app/src/androidTest` 是需要设备或模拟器的仪器化测试，两者都应保留。
- 版本号按上面的约定执行，不要手动修改已发布版本对应的 `versionCode`。
- 签名文件和密码不要提交到仓库。

## 致谢

- 上游作者 Simon Niederberger 及 [photocompare](https://github.com/sniederb/photocompare) 项目。
- [Glide](https://github.com/bumptech/glide) 与 [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view)。
- 本 README 的排版结构参考 [KernelSU](https://github.com/tiann/KernelSU)。
