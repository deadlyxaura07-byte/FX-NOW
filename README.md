# FX NOW

FX NOW is an Android launcher MVP built with Kotlin and Jetpack Compose.

## Included in the Android app
- Premium dark dashboard
- Installed-app search and launch
- Local task board saved on device
- Built-in command shortcuts
- Settings shortcut and launcher Home intent
- GitHub Actions workflow to build a debug APK

## Build an APK
The GitHub Actions workflow at `.github/workflows/android.yml` runs on pushes to `main` and can also be started manually:
1. Open the repository's **Actions** tab.
2. Select **Build FX NOW APK**.
3. Choose **Run workflow** on the `main` branch.
4. Open the completed run and download the `FX-NOW-debug-APK` artifact.
5. Extract the ZIP and install the APK on Android.

## AI assistant upgrade status
A separate FX NOW web/backend upgrade source package has been prepared. It includes a Node.js server, provider adapters (Gemini-first, OpenAI-compatible, and Claude), an animated web interface, and automated tests. The package requires a deployed HTTPS backend and server-side provider API key before the Android app can deliver real AI responses. It has not yet been merged into this Kotlin Android application.

## Current limitations
The Kotlin Android app is still an MVP: an actual AI provider, native voice assistant, online research, reminders, and autonomous coding are not connected in this Android app. The debug APK is unsigned and should be treated as a test build until verified on a physical device.
