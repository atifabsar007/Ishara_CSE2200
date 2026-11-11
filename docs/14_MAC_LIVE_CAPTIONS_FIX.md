# Ishara 3.0.1: Live Captions repair for macOS + Android Emulator

**Developer:** Md. Atif Absar  
**Stack:** Android Studio, Java, XML, CameraX, MediaPipe, SQLite, Android SpeechRecognizer, RecyclerView  
**Scope:** Improved diagnostic messages, clearer partial results, experimental Bangla/English switching, and refreshed Smart Shortcuts description. Existing modules remain.

## Start here: this setting is usually the immediate issue

When using Android Studio's Pixel emulator on a Mac, host microphone input is **disabled by default** for privacy. If you can see the app's status **Hearing speech...** or **Microphone ready** but you get no words, do not assume voice recognition is working.

1. Start your existing Android emulator.
2. In its toolbar choose the **three dots (More / Extended Controls)**.
3. Open **Microphone**.
4. Enable **Enable Host Microphone Access**, sometimes called **Virtual microphone uses host audio input**.
5. macOS **System Settings -> Privacy & Security -> Microphone**: if macOS asks, allow access to Android Studio / Android Emulator.
6. In Android, allow Ishara's **Microphone** permission. You can verify in Android **Settings -> Apps -> Ishara -> Permissions -> Microphone**.
7. In Ishara **Live Captions**, choose **English (United States)** and say "Hello, can you hear me?". Finish the sentence and pause briefly.
8. If recognized, try **বাংলা (বাংলাদেশ)** and say a full Bangla sentence. Make sure the installed recognizer supports Bangla.
9. Once both work separately, try **Auto: বাংলা + English (experimental)**. Auto switching requires Android 14+ and a supporting speech engine; it might NOT work even though each language works separately.

**Important:** The emulator often turns host microphone input off again when restarted. Re-enable it after each emulator restart. A physical Android phone avoids this emulator-specific audio forwarding setting and is strongly recommended for realistic testing.

## Install the fix into your existing project (recommended)

1. Close Ishara in Android Studio. Stop the running emulator app or press Stop.
2. Finder -> Desktop -> Java Android Project -> `Ishara_CSE2200`. Make a BACKUP copy of this folder.
3. Extract `Ishara_3_0_1_Captions_Mac_Patch.zip`.
4. From the patch, copy the `app` folder and replace/merge it with the `app` folder in your current `Ishara_CSE2200` project. For clean results, replace `app` after backing up; this retains your existing Gradle wrapper in the root folder. (Project code is replaced; app data on the emulator remains unless you uninstall it.)
5. Keep your existing root-level `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle-wrapper.properties`, and `local.properties` where they are. NEVER overwrite the root Gradle wrapper with this patch.
6. Android Studio -> **File -> Open** -> your existing Ishara root folder, then **File -> Sync Project with Gradle Files**.
7. **Build -> Rebuild Project**, select your Android device or emulator, and press **Run**.
8. Tap **Live Captions** -> **Microphone help & setup**. Follow its checklist.

Alternatively, extract the full corrected source ZIP as a separate project folder and copy your previously working Gradle wrapper scripts and JAR into it as previously instructed. Do not delete the original project.

## What the patch changes

| Feature | Before | After |
| --- | --- | --- |
| Language | Bangla or English | Bangla, English, optional experimental auto switch |
| Recognition errors | Generic "retrying" | Specific network, microphone, permission, language, busy, or timeout explanation |
| Live text | Partial callback sometimes invisible | Clearly labeled partial preview when provided, plus finalized transcript |
| Diagnostics | Little guidance | Microphone help + last error + approximate audio signal |
| Continuous captions | Restarted segments | Safer segment restart logic with retry intervals and limits |
| Conversation Mode | Bangla input only | Incoming speech-language selector and diagnostic link |
| Smart Shortcuts | "Most used context" (confusing) | Explained as rankings of the phrases you actually used |

## Recognizer limitations and what counts as working

- This project calls Android's `SpeechRecognizer` in Java, not a continuously streaming custom speech model.
- Most Android recognition providers transcribe short *segments*. A service MAY return intermediate words, but the system explicitly permits ignoring the partial-results request.
- The auto Bangla/English option requests Android language detection and switching on Android 14+. It requires supported recognizer models and may fail on emulator system images.
- No result does not always mean a Java bug. Possible causes include emulator host mic off, app microphone permission missing, recognizer unavailable, unsupported Bangla model, or network errors.
- **Do not fake captions with demonstration text**. The app displays real recognition callbacks or explanatory error messages.
- Caption transcription can have errors. Have a teacher/speaker's permission when appropriate.

## If still nothing is transcribed

1. Use the in-app **Microphone help & setup** button.
2. See the `Speech service:` diagnostic text with an Android error code. Share that error with the developer.
3. Try English with a single sentence first. If English works but Bangla does not, it is a Bangla model or provider issue.
4. If the recognizer is absent, install or enable a compatible Android speech-recognition service using Android device settings or the provider's official app (if available for that device). A model download may be required for offline use. **Do not download an arbitrary third-party APK.**
5. On the Android emulator, ensure a Google Play-enabled emulator image and appropriate speech-recognition provider if you choose to depend on Google services. A real Android phone is often easier.
6. In Android Studio, open **Logcat**, filter package `com.kuet.ishara`, and capture errors if necessary.

## Verification status

- XML layout parsing and view-binding ID checks pass.
- Platform-independent Java smoke tests pass.
- The CaptionController passes an additional Java compile test against minimal Android API-shaped stubs (NOT a full Android SDK build).
- The project was **not** compiled with the full Android SDK in this environment, and live emulator/phone speech recognition has **not** been tested here. Run an Android Studio Gradle build and real audio tests before relying on it.

## References

- Android emulator Extended Controls microphone: https://developer.android.com/studio/run/emulator-extended-controls
- Android `SpeechRecognizer`: https://developer.android.com/reference/android/speech/SpeechRecognizer
- Android `RecognizerIntent`: https://developer.android.com/reference/android/speech/RecognizerIntent
