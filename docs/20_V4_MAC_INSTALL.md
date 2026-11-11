# Ishara 4.0.1: Mac upgrade and run guide

## Recommended path: Apply the small patch to your currently working Ishara 3.0.1

Your project was at `~/Desktop/Java Android Project/Ishara_CSE2200` and your Gradle wrapper already worked on your Mac. The patch **does not replace the entire app directory**; it preserves your `gradlew`, `gradle-wrapper.jar`, model file, Android SDK configuration and existing SQLite history.

1. Quit Android Studio or close the Ishara project.
2. Extract `Ishara_4_0_1_Mac_Corrected_Patch.zip` (it contains `app/`, `docs/` and `apply_patch_macos.sh`).
3. Open **Terminal**, navigate to the *extracted patch folder* by typing `cd ` and dragging that folder into Terminal, then press Enter.
4. Run: `bash apply_patch_macos.sh`
5. Reopen `~/Desktop/Java Android Project/Ishara_CSE2200` in Android Studio; trust it and **Sync Project with Gradle Files**.
6. Select your Android phone or already configured emulator and press **Run**. The new UI should display Ishara 4.0.1 after installation.

The script validates the target path before copying, backs up the older Java, layouts and manifest in a timestamped folder (without copying the big model binary), and uses `rsync` to **merge** changed files. It must be launched from the extracted patch folder. To choose a different project location: `bash apply_patch_macos.sh "/full/path/to/Ishara_CSE2200"`.

## Full project ZIP path

Extract `Ishara_4_0_1_Full_Corrected_Project.zip`, open `Ishara_CSE2200` in Android Studio. As in earlier packages, official Gradle wrapper scripts/JAR and Google MediaPipe model binary may need to be copied from your existing working Ishara installation. The full source ZIP contains Gradle settings but does not embed third-party binary wrappers or the model. The patch is easier when you have Ishara already working.

## New account smoke test

* Welcome -> Continue as guest -> Home -> Live Captions (check it still works).
* Home -> Log out -> Create account -> full name, username, password (8+ characters), confirm -> Home.
* Log out -> Log in with the same username/password -> Home with the correct display name.
* Test wrong password; it should reject login. After five failures, login temporarily pauses on that screen.
* Open Ishara Quest -> Meaning Match, answer, Next; switch to Sentence Quest, tap tiles, Check, Next. Close/reopen app, confirm game points are saved.
* Switch between users and confirm their saved messages and quiz points are not mixed. Guest history remains guest-only.

## Microphone / camera troubleshooting

* **Emulator captions**: In emulator Extended Controls (three dots), Microphone -> enable host microphone input. Give mic permission when asked. Language must be supported by the installed speech service. Try English first, then Bangla.
* **Gesture camera**: Use a real Android phone with Camera permission. Download `gesture_recognizer.task` through the app or place the official file under `app/src/main/assets/` as instructed in the existing README. The emulator's virtual scene is not a webcam image.
* **Emulator failed: no space**: Free disk space before downloading additional system images. Prefer testing on an existing device.
* **BUILD FAILED**: View > Tool Windows > Build > copy the first *actual* error and file/line rather than guessing.
