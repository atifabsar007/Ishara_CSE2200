# Ishara 6.1.0 — Release status at source handoff

## Completed in this source update

- Reorganized the home screen around Gesture Studio and Live Captions.
- Grouped secondary features into Play, Learn, Connect, communication tools, progress and project tools.
- Preserved offline local accounts and guest access while adding Firebase email/password accounts.
- Added Firebase Authentication, Realtime Database room messaging and Cloud Firestore profile/progress code.
- Added a user-triggered WorkManager progress sync that uploads aggregate counts only.
- Added Photo Picker and ContentResolver image preview to Picture Board. Images are not stored or uploaded.
- Expanded system BroadcastReceiver handling for battery and airplane-mode events.
- Removed the unencrypted same-Wi-Fi socket messaging implementation from the active source and replaced it with Firebase rooms.
- Added Firebase rules/setup, test checklist, GitHub workflow, Word report template and five-minute showcase script.

## Checks completed here

- XML parsing and ViewBinding reference checker: PASS.
- UI contrast checker: PASS.
- Three platform-independent Java logic smoke tests: PASS.
- 6.1.0 structural feature checker: PASS.
- Word report template opens and contains the supplied home-screen image: PASS.
- ZIP integrity test: PASS.

## Still required before submission

- **Android Gradle build:** NOT RUN in this environment. No Android SDK or Gradle installation was available. The uploaded source archive also did not contain `gradle-wrapper.jar`. Install/configure Gradle 8.11.1 in Android Studio, or add the official wrapper JAR for Gradle 8.11.1.
- **Firebase runtime:** NOT TESTED. The four Firebase client values in `firebase_config.xml` are intentionally blank. Create the Firebase project, enable Email/Password and Anonymous Authentication, create Realtime Database and Firestore, fill the values and publish the security rules.
- **Physical-device tests:** NOT RUN here. Test camera recognition, speech services, airplane/battery broadcasts, Photo Picker, two-device live rooms and WorkManager sync on Android hardware.
- **Final report:** The DOCX is a template. Replace placeholders with actual screenshots and test outcomes.
- **GitHub:** The source is repository-ready and includes a build workflow, but it has not been pushed to a remote repository. Create the public repository and follow `docs/64_GITHUB_RELEASE_GUIDE.md`.

Do not describe the project as fully built, Firebase-verified or device-tested until these remaining checks pass.
