# ISHARA – Sign-to-Speech Communication

**Release:** 6.1.0  
**Developer:** Md. Atif Absar  
**Platform:** Native Android, Java 17 + XML  
**Academic context:** KUET, CSE 2200 Advanced Programming Laboratory

Ishara is a Java-centered Android communication support prototype. Its two main workflows are camera-based gesture-to-message composition and live speech captions. Supporting tools include two-way conversation, picture communication, learning, practice progress and live Firebase rooms.

> **Scope statement:** The bundled MediaPipe model recognizes seven general handshape categories. Ishara maps those categories to illustrative Bangla demonstration phrases. It is not a complete or validated Bangla Sign Language translator. Recognition performance must be evaluated on actual devices and should not be described as proven accuracy without a labeled test set.

## Main user experience

1. **Gesture Studio:** CameraX preview → MediaPipe Gesture Recognizer → confidence/stability check → Java phrase mapping → editable sentence → Bangla TextToSpeech.
2. **Live Captions:** Android SpeechRecognizer → partial/final text → readable transcript → local save/share.
3. **Two-way Conversation:** speech-to-text input and typed/speech reply.
4. **Play, Learn, Connect:** Ishara Quest, gesture learning and Firebase Realtime Database rooms.
5. **More tools:** Sentence Builder, Picture Board, quick phrases, dictionary, calibration, practice progress and history.

The home screen prioritizes Gesture Studio and Live Captions. Secondary modules are grouped so the core experience is easier to explain and demonstrate.

## Lab 8–11 coverage

| Week | Demonstrable implementation |
|---|---|
| 8 | Java Activities, Gradle, Manifest, lifecycle callbacks, XML, ViewBinding, event listeners, explicit and implicit Intents |
| 9 | Linear layouts, Material components, RecyclerView, Adapters, ViewHolders and custom landmark/status Views |
| 10 | Bound `SpeechService`, dynamic `BatteryReceiver` for battery/airplane events, custom `MessageProvider`, Android Photo Picker and `ContentResolver` |
| 11 | HTTPS GET, JSON parsing, SQLite CRUD, background executors, WorkManager, Firebase Authentication, Realtime Database and Cloud Firestore |

## Build prerequisites

- Android Studio with Android SDK Platform 35 and Build Tools 35.0.0.
- JDK 17 or 21.
- Gradle 8.11.1 with Android Gradle Plugin 8.9.2.
- Internet for dependency resolution and the first MediaPipe model download.
- A physical Android phone is recommended for camera gesture testing and two-device Firebase room testing.

Open the `Ishara_CSE2200` folder in Android Studio. The source archive does not include the binary Gradle wrapper JAR. Use a local Gradle 8.11.1 installation in Android Studio, or install Gradle 8.11.1 and run `./gradlew assembleDebug`. GitHub Actions is configured to build with Gradle 8.11.1 directly.

## Firebase setup is required for cloud demonstrations

Follow [`docs/FIREBASE_SETUP.md`](docs/FIREBASE_SETUP.md). Enable Email/Password and Anonymous Authentication, create Realtime Database and Firestore, then fill the four client configuration values in `app/src/main/res/values/firebase_config.xml`.

The app does not use Firebase Storage because selected pictures are read locally through Android's content provider and are not uploaded. No service-account key belongs in the app or repository.

## Testing

Run the static source checks and Java logic smoke tests:

```bash
python3 tools/check_source.py
python3 tools/check_ui_contrast.py
bash tools/run_java_logic_smoke.sh
python3 tools/check_v6_1_structure.py
```

These checks do not replace an Android Gradle build, Firebase Console verification or physical-device testing. Complete [`docs/61_V6_1_0_TEST_AND_ACCEPTANCE.md`](docs/61_V6_1_0_TEST_AND_ACCEPTANCE.md) before submission.

## GitHub submission

1. Create a GitHub repository named `ishara-android` (or the exact name you prefer).
2. Keep the repository public if your teacher must open the link without signing in. Do not upload passwords, signing keys or Firebase Admin credentials.
3. Add the contents of this project root, commit the 6.1.0 source and push to `main`.
4. Confirm the **Android build** GitHub Actions workflow passes.
5. Add the repository URL to the teacher's submission form. Include the project report and video separately as required.

A starter GitHub Actions workflow is included at `.github/workflows/android-build.yml`.

## Project documents

- [`docs/60_V6_1_0_MASTER_UPDATE.md`](docs/60_V6_1_0_MASTER_UPDATE.md): architecture and feature decisions.
- [`docs/FIREBASE_SETUP.md`](docs/FIREBASE_SETUP.md): Firebase Console configuration and rules.
- [`docs/61_V6_1_0_TEST_AND_ACCEPTANCE.md`](docs/61_V6_1_0_TEST_AND_ACCEPTANCE.md): device and acceptance test plan.
- [`docs/62_FINAL_SHOWCASE_SCRIPT.md`](docs/62_FINAL_SHOWCASE_SCRIPT.md): five-minute video/live demonstration script.
- [`docs/63_RELEASE_CHECKLIST.md`](docs/63_RELEASE_CHECKLIST.md): final verification checklist.
- [`docs/64_GITHUB_RELEASE_GUIDE.md`](docs/64_GITHUB_RELEASE_GUIDE.md): public repository setup and push instructions.
- [`docs/65_RELEASE_STATUS.md`](docs/65_RELEASE_STATUS.md): what is complete and what still needs real-device/Firebase verification.
- [`docs/PROJECT_REPORT.md`](docs/PROJECT_REPORT.md): report content outline.

The Word report template is `docs/ISHARA_Final_Project_Report_Template.docx`. Replace screenshot placeholders and fill actual test outcomes before submitting.
