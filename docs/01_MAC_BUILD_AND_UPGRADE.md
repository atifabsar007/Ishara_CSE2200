# macOS / MacBook Air M4 — Installation and Upgrade Guide

Your known environment: MacBook Air M4, Android Studio Rabbit, existing Ishara V2 project on the Desktop inside `Java Android Project`, previously repaired Gradle wrapper, JDK 21 option accepted. The V3 package does not contain a random wrapper binary.

## A. Safe upgrade (recommended)

1. Quit the old Ishara app on the device. In Finder, duplicate your existing `~/Desktop/Java Android Project/Ishara_CSE2200` as a dated backup (contains any project-specific modifications). Keep backup untouched.
2. Extract V3 ZIP to a **new project directory**, e.g. `~/AndroidProjects/Ishara_3_0/Ishara_CSE2200`. Do not extract over the old project blindly.
3. From the *existing trusted, working* `Ishara_CSE2200` root folder, copy `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` to the corresponding paths of the new V3 directory. **Do not overwrite V3's `gradle-wrapper.properties`** (Gradle 8.11.1).
4. Open Terminal:

```bash
cd ~/AndroidProjects/Ishara_3_0/Ishara_CSE2200
chmod +x gradlew
ls -l gradlew gradlew.bat gradle/wrapper/gradle-wrapper.jar
```

5. Android Studio -> File -> Open -> select the V3 root folder (the one containing `settings.gradle`) -> Trust Project.
6. Choose Gradle JVM **17 or 21**, not JDK 27, and ensure Android SDK Platform **35** is installed.
7. File -> Sync Project with Gradle Files. Look in Build for sync errors.
8. From Terminal run `./gradlew :app:assembleDebug` or Android Studio -> Build Project; inspect the **first actual error** if build fails. This build has not been executed in the authoring environment.
9. Use a physical Android phone over USB Debugging where possible; test the camera only with an actual live hand, or configure Android Emulator webcam explicitly. Emulator `VirtualScene` graphics are not your hand.

## B. Gesture model

V3 retains the V2 model installer. A trained model **must exist** before AI detection can work. The app attempts an initial model download if not bundled/cached. You may manually download via the included script:

```bash
cd ~/AndroidProjects/Ishara_3_0/Ishara_CSE2200
bash tools/install_model_macos.sh
```

It installs `app/src/main/assets/gesture_recognizer.task`. The source zip doesn't bundle this external model binary. Rebuild and reinstall after manually placing the model into assets.

## C. Microphone and Bangla captions

Open Live Captions, tap Start, grant microphone permission, choose Bangla or English; the phone must have a speech recognition service supporting that language, and online recognition may be required. If it says unavailable, check the device's speech service or use typed responses. Ishara does not run background recording while Activity is closed.

## D. What if Mac storage is low?

Your earlier Android Studio screenshots showed only ~7.5GB free space and "not enough space to create userdata partition". That is an emulator disk-space problem, not proof that Java compiled incorrectly. Safely remove old unused AVDs and system images via Android Studio Device Manager / SDK Manager, or test directly on your Android phone. A practical working-space target is at least 20GB free. Do not delete the project, Gradle cache indiscriminately, or documents you need.

## E. Important database migration note

When V3 is installed as an upgrade of the same application ID (`com.kuet.ishara`), original `ishara.db` can remain, and new V3 data is stored separately in `ishara_v3.db`. Uninstalling the old app before testing will normally erase its local app data. Back up important local messages before uninstalling.

## F. No-internet build fallback

The first clean Gradle build and V3 dependencies require internet or an already cached set of dependencies. Firebase features remain optional. When Android Studio reports a version or dependency failure, capture the exact Build Output error and fix it rather than downgrading randomly.
