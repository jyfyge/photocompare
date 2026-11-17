# Photo Compare（jyfyge fork）

本项目是 [sniederb/photocompare](https://github.com/sniederb/photocompare) 的修改版分支，包名改为 `com.jyfyge.photocompare`，可与 Play 商店版同时安装。

## 一、主要功能和特点

- **并排对比两张照片**：上下两栏同时显示，从同一个文件夹或同一拍摄日期中按顺序取图。
- **缩放/平移同步**：两图可同步缩放和平移，也可手动关闭同步；支持按尺寸比例校正两图之间的差异。
- **图片选择与后续操作**：可标记候选照片，之后统一分享，或删除未选中的照片。
- **按文件夹或日期筛选**：以图库文件夹或拍摄日期作为对比范围，并支持排序方式切换。
- **Ultra HDR 支持**：Android 14+ 使用系统 `ImageDecoder` 保留 gain map，在对比 Ultra HDR 照片时自动切换 HDR 显示模式（最终效果取决于设备屏幕）。
- **Android 17 适配**：`targetSdkVersion` 为 37。
- **稳定性修复**：修复了在图片数量较多的图库中点击进入对比页时，主线程扫描媒体库触发 ANR、应用被系统杀掉并闪回首页的问题。

技术栈：Java、Android View 体系、`SubsamplingScaleImageView`、`Glide`、`RecyclerView` / `ViewPager`。

## 二、LICENSE 相关政策

本项目沿用上游的 **Non-Profit Open Software License 3.0**（简称 Non-Profit OSL 3.0），许可证全文见 `LICENSE.txt`。

依据该许可证及本分支的实际情况：

- 保留了上游源码中的版权、专利、商标及许可声明。
- 本文件同时作为许可证第 6 条要求的**修改声明**：原作品在本分支中已被修改，修改内容包括上述 ANR 修复、Android 17 适配和 Ultra HDR 支持。
- 不得使用原作者或贡献者的名称、商标为衍生作品背书。
- 分发衍生作品时必须保留许可条款，并确保接收方知悉这些条款。

**重要限制（许可证第 17(d) 条）**：只有在你声明自己是"不从分发、支持或服务中获取任何收入的非营利组织"时，才可以 Non-Profit OSL 3.0 名义分发。否则必须改用普通 OSL 3.0 并发布清晰的许可声明。本分支目前按前者发布，发布者应自行确认是否符合该条件；本说明不构成法律意见。

## 三、贡献项目

### 构建

使用 JDK 21 和 Android SDK（含 API 37 平台与 Build Tools 37.0.0），在项目根目录执行：

```bash
./gradlew assembleRelease
```

单元测试：

```bash
./gradlew testDebugUnitTest
```

发布包统一命名为 `photocompare-<versionName>-v<versionCode>.apk`，例如 `photocompare-1.0.0-v1794900000.apk`，便于区分版本。

### 版本号约定

- `versionCode`：每次构建都会自动 +1，并写入项目根目录的 `version.properties`，不需要手动修改，也不会重复。
- `versionName`：采用 `主版本.次版本.修订号` 的语义化写法。
  - 主版本：不兼容的大改，很少变动。
  - 次版本：新增功能，例如 `1.0.0` → `1.1.0`。
  - 修订号：仅修复问题，例如 `1.0.0` → `1.0.1`。
  - 发布前版本追加 beta 后缀，例如 `1.1.0-beta.1`，正式版去掉后缀。

### 签名

发布包使用位于项目根目录的 `photocompare-release.jks` 签名，相关配置在 `keystore.properties` 中。这两个文件均已被 `.gitignore` 排除，**不会进入仓库**。密钥一旦丢失，将无法为现有应用发布更新，请务必单独备份。当前证书指纹（SHA-256）为：

```text
AA:4C:3A:EE:40:36:A6:42:60:18:75:20:3E:32:7F:32:BE:F3:AA:12:AB:44:FF:34:B0:BE:1E:7C:B2:87:F0:9D
```

签名方案为 v2 + v3，不启用 v1（最低支持 Android 7.0 / API 24）。

### 提交流程

修改代码后先在本地完成构建和基本测试，确认通过后再提交和推送。测试目录 `app/src/test` 与 `app/src/androidTest` 保留，不要删除。
