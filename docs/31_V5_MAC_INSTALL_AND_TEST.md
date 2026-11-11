# Ishara 5.0: macOS Upgrade & Testing

## Recommended: update existing working Ishara 4.0.1

1. Quit Android Studio (keep your emulator data). Make a Finder duplicate of your folder `~/Desktop/Java Android Project/Ishara_CSE2200`.
2. Extract **Ishara_5_0_Mac_Upgrade_Patch.zip**.
3. In Terminal, navigate to the extracted patch folder and run: `bash apply_patch_macos.sh`.
4. The installer checks the target project's `settings.gradle`, creates a timestamped backup and merges only modified/new Java, XML, manifest and Gradle files; it never overwrites your working `gradlew`, Gradle wrapper JAR or MediaPipe `.task` model.
5. Reopen your existing Ishara folder in Android Studio; Gradle Sync; then run **Build > Rebuild Project**, choose your Android phone or emulator, and tap Run.
6. If Android Studio reports a build failure, copy the **first red compiler error** from the Build tab rather than the whole warning list.
7. You can uninstall/roll back the update using the backup and re-sync, but don't uninstall the app unless you have backed up your on-device data.

## Fresh full ZIP install

The full archive is source-only; the official Gradle wrapper JAR and executable scripts are not redistributed. If opening it as a new project, copy those wrapper files from your already-working Ishara 4.0.1 folder. You may also need to copy/install `gesture_recognizer.task` into `app/src/main/assets` or use Ishara's model download function. Use Android SDK Platform 35 and Gradle JDK 17/21. Exact Android Gradle build must be done in Android Studio.

## Test Peer Link using TWO physical phones

* **Important:** This version uses unencrypted local TCP; do not send private or sensitive content.
* Connect two Android phones to the **same trusted Wi-Fi**, without guest network isolation, router AP isolation, or restrictive VPN.
* Install Ishara on both. On Phone A → Peer Link → Host. Copy its displayed local IPv4 and six-digit code.
* On Phone B → Peer Link → type IP and code → Join. The host should show Connected too.
* Send Bangla and English sample sentences both directions; tap Speak latest incoming message.
* On Phone A, open Gesture Studio, build a sentence, select Send via Peer Link; pair and send it.
* Try wrong code, Wi-Fi off, stop, and rotate screens. When connection drops, reopen Peer Link to start a new session.
* If the host address is unavailable or connection fails, test real devices (emulators are NAT-isolated) and confirm your Wi-Fi permits local device-to-device TCP traffic (port 38347).

## Test Simulator and Inside Ishara

* Home → Everyday Simulator → pick one of the four scenarios → start → select response → complete → observe SQLite saved count, restart another route.
* Tap Speak response and Share response. Android TTS voice availability varies; install a Bengali voice if needed.
* Home → Inside Ishara → inspect callback log → Create/Read/Update/Delete sample → Parse JSON → Battery → Speak → Share → Real Wi-Fi networking.
* Verify the rest of Ishara: local login, gesture camera, sentence builder, captions, Quest game, saved history and adaptive shortcuts.

## Troubleshooting

* **Mac disk almost full:** Free space before installing new emulator images. Reuse the working emulator or test physical phones.
* **Mic captions empty:** Emulator Extended Controls → Microphone → enable Host Microphone Access; allow Mac microphone permission.
* **Camera shows geometric virtual scene:** Emulator uses virtual camera. Configure host webcam or test a real Android camera.
* **AI model missing:** Download required `gesture_recognizer.task` using app setup or tools script.
* **Peer Connection refused/timed out:** IP/code/router isolation/VPN issue or host screen not active.
* **No Bangla voice:** Android TTS voice must support Bengali; test English fallback separately.
