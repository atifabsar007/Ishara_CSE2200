# Ishara 2.0 upgrade on macOS — follow this exact sequence

You already have the first Ishara Android Studio project working on your Mac. **Keep its Gradle wrapper files.** This ZIP includes a replacement app folder plus docs and tools.

1. Stop the app (red Stop button), then close the Ishara project in Android Studio.
2. In Finder open `Desktop/Java Android Project/Ishara_CSE2200`. Duplicate this folder to `Ishara_CSE2200_backup` (the backup contains the existing, working Gradle wrapper).
3. Unzip `Ishara_Android_CSE2200_V2.zip` in Finder.
4. Open the *newly unzipped* `Ishara_CSE2200`, **copy its entire `app` folder**, then open your **original** `Desktop/Java Android Project/Ishara_CSE2200` and paste. When macOS asks, choose **Replace** the old `app` folder. Do not copy the root `gradle` folder or remove existing `gradlew` / wrapper files.
5. Optionally also copy the new `docs` and `tools` folders to your old project.
6. Reopen your *original* `Desktop/Java Android Project/Ishara_CSE2200` in Android Studio, click **Sync Project with Gradle Files** and **Run**.
7. On the Gesture Camera screen, the app will **automatically attempt to download the official MediaPipe model once**, store it in the app's private files folder, and then switch to `AI READY`. A retry button appears if the internet request fails. The model remains cached for subsequent runs.
8. Optional offline alternative on Mac: in Terminal, in the original project, run `bash tools/install_model_macos.sh` to place the model inside `app/src/main/assets`, then **Rebuild Project** and Run. This is useful if the emulator cannot download the model itself.

## Why you previously saw a pixelated green camera

That was your Android emulator's software camera, not your actual MacBook camera. Your screenshot also said `Gesture model missing/unavailable`. These are **two separate problems**:

* **AI missing:** upgraded app will download the official MediaPipe model or offer a Retry button.
* **No real hand image:** use a real Android phone connected over USB/Wi-Fi, or configure your emulator's **Front Camera = Webcam0** through Android Studio > Tools > Device Manager > Edit the `Medium Phone API 37.0` device > Advanced settings > Camera. Stop/restart the virtual device. Grant macOS Camera permission to the relevant app when asked. Webcam availability varies by emulator build.

The emulator's software-generated picture cannot detect your actual hands. If possible, use your Android phone for the final demo.

## How to verify

- Home screen displays a new gradient hero panel and a large "Open live camera" button.
- Gesture screen displays the preview first, a FRONT/BACK indicator, model status, and a recognition card.
- After AI is ready: show a thumbs-up steadily for half a second → "হ্যাঁ" in Everyday mode.
- Switch context to Classroom then make the thumbs-up gesture again → "আমি বুঝতে পেরেছি" (contextual *demo* mapping).
- Toggle Auto-add, remove your hand, show different supported gestures, then tap Speak or Save.
- Tap Undo to delete the most recently added whole phrase.

## Troubleshooting

- **Model download fails:** check Wi-Fi on the *emulator/phone* and tap "Download / retry gesture AI". If that fails, use `bash tools/install_model_macos.sh` and rebuild.
- **AI READY, but no result:** your camera may still be showing an emulated scene. Switch to physical device or Mac webcam in AVD configuration; provide light and one visible hand.
- **Unrecognized signs:** the pretrained model detects seven general handshapes, **not** complete Bangla Sign Language. It has a finite label set; face/full-body signs are not handled.
- **Speech sounds wrong:** enable/install Bangla Text-to-Speech voice on device. The app does not generate speech with cloud AI.
- **Build failure:** copy the first actual red Gradle error and share it. This version was not Android-compiled in the source authoring environment.
