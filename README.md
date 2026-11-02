# FX NOW — JARVIS-style Android assistant

FX NOW now uses a redesigned native Android assistant screen with an animated orb, chat UI, voice input through Android speech recognition, spoken replies through Android Text-to-Speech, and provider selection.

## Build APK
1. Open the **Actions** tab.
2. Select **Build FX NOW APK**.
3. Open the latest successful run and download the **FX-NOW-debug-APK** artifact.
4. Extract the ZIP and install `app-debug.apk` on your Android device.

## Connect real AI
The app uses a secure backend proxy; provider API keys must never be placed inside the APK.
1. Deploy the Node backend in the `fxnow-upgraded.zip` source package to an HTTPS host.
2. Set a server-side provider key such as `GEMINI_API_KEY` (free-tier availability and quotas vary), `OPENAI_API_KEY`, or `ANTHROPIC_API_KEY`.
3. Optionally configure `FXNOW_ACCESS_TOKEN` on the server.
4. Open FX NOW → Settings and enter the deployed HTTPS backend URL and optional access token.
5. Select Automatic, Gemini, OpenAI-compatible, or Claude and send a test message.

## Current limitations
- The Android app does not itself host the backend. Real AI chat requires the backend to be deployed and configured.
- Provider API keys are server-side only; never paste them into the app.
- Voice input uses Android's installed speech-recognition service; availability depends on the device and language settings.
- This is a debug APK for testing, not a production-signed release.
- Calendar, reminders, broad device automation, and file operations are not implemented yet.

## Source
The secure Node backend and browser/PWA version are included in the separately generated `fxnow-upgraded.zip`. The native Android UI calls its `/api/chat` endpoint.
