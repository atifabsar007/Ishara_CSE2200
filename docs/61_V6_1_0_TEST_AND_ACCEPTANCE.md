# Ishara 6.1.0 — Test and Acceptance Checklist

Do not mark a test PASS until it has been performed on the stated device. Record phone model, Android version, date and evidence screenshot/video.

## Build and launch

- [ ] Open the `Ishara_CSE2200` root in Android Studio.
- [ ] Sync Gradle 8.11.1 / Android Gradle Plugin 8.9.2.
- [ ] Build `app:assembleDebug` successfully.
- [ ] Install on a physical Android device.
- [ ] Launch, guest entry, logout and relaunch.
- [ ] Register with Firebase email/password after configuring Firebase.
- [ ] Confirm Firebase Auth user appears in Console.

## Week 8 — Activities and lifecycle

- [ ] Navigate from Welcome to MainActivity.
- [ ] Open Gesture Studio and return; verify lifecycle logs.
- [ ] Open Live Captions and return; verify microphone stops on screen exit.
- [ ] Confirm ViewBinding-backed buttons work.
- [ ] Test explicit navigation and implicit share chooser.
- [ ] Rotate supported screens and check no clipped controls.

## Week 9 — UI and lists

- [ ] Check text and controls at normal and large font sizes.
- [ ] Confirm Dictionary RecyclerView search/list behaviour.
- [ ] Confirm History RecyclerView displays saved messages.
- [ ] Confirm Conversation RecyclerView receives new chat rows.
- [ ] Confirm Phrase and Lesson adapters render and respond.
- [ ] Confirm landmark overlay is aligned with camera preview on the target phone.

## Gesture Studio

- [ ] Grant camera permission; deny once and confirm helpful recovery.
- [ ] Verify MediaPipe model downloads or loads from cache.
- [ ] Test all seven supported general handshape categories in good lighting.
- [ ] Test no hand / unsupported hand and confirm no false phrase is added.
- [ ] Verify confidence threshold and stable-frame acceptance.
- [ ] Add, undo, clear, save, share and speak a composed message.
- [ ] Confirm camera frames are not uploaded.

## Live Captions and conversation

- [ ] Grant microphone permission; deny once and confirm helpful recovery.
- [ ] Test English captions on a device with English recognition support.
- [ ] Test Bangla captions on a device with Bangla recognition support.
- [ ] Confirm partial and final captions are distinguishable.
- [ ] Save, copy, share and clear a transcript.
- [ ] Verify captions stop when the screen stops.
- [ ] Test typed reply and Bangla TextToSpeech where the voice is installed.

## Week 10 — Android components

- [ ] In Settings, confirm `ACTION_BATTERY_CHANGED` is displayed.
- [ ] Toggle Airplane mode and confirm ON/OFF event text updates.
- [ ] Use emulator battery controls or a test device to verify low/okay events if available.
- [ ] In Picture Board, open Photo Picker and choose an image.
- [ ] Confirm the app reads the image content URI and displays the selected image.
- [ ] Confirm no broad media permission is requested and the image is not uploaded.
- [ ] In Lab Showcase, demonstrate the internal MessageProvider / SQLite CRUD table.
- [ ] Demonstrate bound SpeechService by speaking a phrase.

## Week 11 — API, JSON, database and Firebase

- [ ] Run the HTTPS GET test and show the parsed JSON title/completed fields.
- [ ] Switch off network and confirm the app reports a network error without freezing.
- [ ] Create/read/update/delete sample LabDemoDb rows.
- [ ] Register an email/password Firebase account.
- [ ] Confirm profile document appears at `users/{uid}` in Firestore.
- [ ] Create a Firebase room and join from a second device/emulator.
- [ ] Send a message and confirm both clients update live.
- [ ] Confirm the message appears under `rooms/{roomCode}/messages` in Realtime Database.
- [ ] Complete guided practice and Quest rounds.
- [ ] Tap cloud progress sync and confirm WorkManager completes.
- [ ] Confirm `users/{uid}/progress/summary` appears in Firestore.
- [ ] Confirm only aggregate statistics are uploaded by the progress worker.

## Release and submission

- [ ] Confirm Firebase Realtime Database and Firestore rules are published and restrictive.
- [ ] Confirm no Admin SDK key, signing key or password is in GitHub.
- [ ] Confirm GitHub Actions Android build passes.
- [ ] Confirm repository visibility is suitable for the teacher's link.
- [ ] Capture final app screenshots for the Word report.
- [ ] Record a video no longer than five minutes and test the public link in a signed-out browser.
- [ ] Ensure title matches the previously submitted title exactly.
