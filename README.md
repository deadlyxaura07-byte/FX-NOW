# FX NOW

FX NOW is an Android launcher MVP built with Kotlin and Jetpack Compose.

## Included
- Premium dark dashboard
- Installed-app search and launch
- Local task board saved on device
- Built-in command shortcuts
- Settings shortcut and launcher Home intent
- GitHub Actions workflow to build a debug APK

## Build
Open this repository in Android Studio with JDK 17 and Gradle 8.9, then run `gradle assembleDebug`. GitHub Actions also builds the debug APK on pushes to `main` and allows manual runs from the Actions tab.

## Current limits
FX AI is an integration placeholder: no AI provider, online research, voice assistant, or autonomous coding service is connected yet. Tasks remain local to this device. This is an MVP and has not yet been verified on a physical device.