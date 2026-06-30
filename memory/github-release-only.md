---
name: github-release-only
description: 只使用 Actions 生成 Release，不发布到应用商店
---

修复了视频播放时的屏幕方向问题：

- **问题**: 打开视频后自动横屏显示，无法根据手机方向自动切换横竖屏
- **原因**: PlayerPreferences.kt 中默认值为 VIDEO_ORIENTATION，会根据视频内容强制旋转屏幕
- **修复**: 将默认值改为 AUTOMATIC，让播放器跟随手机物理方向，而非根据视频内容强制旋转
- **版本**: 1.0.1 (versionCode=2)

**修改的文件**:
- `core/model/src/main/java/com/easyplayer/app/core/model/PlayerPreferences.kt` (第 12 行)
- `app/build.gradle.kts` (版本号更新)

**使用说明**:
- 修改后的默认行为是跟随手机物理方向
- 用户可以在设置中手动切换屏幕方向
- 其他选项：AUTOMATIC、LANDSCAPE、PORTRAIT、VIDEO_ORIENTATION 等
