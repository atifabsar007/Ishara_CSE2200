# ISHARA – Sign-to-Speech Communication

**Developer:** Md. Atif Absar  
**Course:** CSE 2200 – Advanced Programming Laboratory  
**Department:** Computer Science and Engineering  
**University:** Khulna University of Engineering & Technology (KUET)  
**Platform:** Android Studio, Java 17 and XML  
**Release:** 6.1.0

## Abstract

Ishara is a Java-centered Android communication support prototype. It focuses on two main communication workflows: camera-based gesture-to-message composition and live speech captions. The Gesture Studio uses CameraX to capture frames and MediaPipe Gesture Recognizer to classify a limited set of general handshape categories. Java applies confidence and stability checks, maps recognized categories to illustrative Bangla phrases, builds editable sentences and uses Android TextToSpeech to speak a completed message. Live Captions uses Android SpeechRecognizer to convert speech into readable text, which users can review, save locally and share.

Supporting features include two-way conversation, a picture communication board, gesture dictionary, calibration, practice games, progress statistics, message history and Firebase room communication. The application also demonstrates Android Activities, XML layouts, ViewBinding, Intents, RecyclerView, Adapters, ViewHolders, a bound Service, BroadcastReceiver, ContentProvider access, permissions, HTTP/JSON, SQLite, WorkManager and Firebase Authentication, Realtime Database and Cloud Firestore.

## Problem statement and objectives

Communication can become difficult when people use different communication methods. Ishara explores a low-cost mobile interface that supports gesture-assisted messages, speech captions and spoken replies. The objectives are to:

1. Build an accessible Android application primarily in Java.
2. Implement a real CameraX and MediaPipe gesture-recognition pipeline.
3. Convert supported general handshapes into readable, editable demonstration messages.
4. Provide Bangla text-to-speech and Bangla/English live captions where device services support them.
5. Store local communication and learning data using SQLite.
6. Demonstrate the Android programming concepts covered in CSE 2200 Lab Weeks 8–11.
7. Integrate Firebase Authentication, Realtime Database and Cloud Firestore.

## Main features

### Gesture-to-Text Studio

- CameraX live camera preview.
- On-device MediaPipe Gesture Recognizer integration.
- Seven supported general handshape categories.
- Confidence display, stability checks and optional landmark overlay.
- Context-specific illustrative phrase mapping.
- Sentence composition, add, undo, clear, save, share and speak.

### Live Conversation Captions

- Bangla and English language selection, subject to device speech-service support.
- Partial and finalized recognition text.
- Session transcript, sentence count, save, copy and share.
- Runtime microphone permission and lifecycle-safe stop behaviour.

### Supporting modules

- Two-way conversation with speech recognition and spoken replies.
- Picture Board with ready-made communication tiles and optional local image preview.
- Gesture Dictionary, camera practice and calibration.
- Ishara Quest and practice progress.
- Sentence Builder, Quick Phrases and Message History.
- Firebase Connect rooms for live messages between devices.

## System architecture

**Gesture path:** CameraX → ImageAnalysis executor → MediaPipe Gesture Recognizer → Java confidence/stability logic → GestureDictionary → SentenceComposer → TextToSpeech / SQLite / Share Intent.

**Caption path:** Microphone permission → Android SpeechRecognizer → CaptionController callbacks → Java transcript state → SQLite / Copy / Share.

**Cloud path:** Firebase Authentication → Realtime Database room messages; Cloud Firestore stores account profile and user-triggered aggregate practice progress. WorkManager performs the progress upload in the background when a network connection is available.

**Media path:** Android Photo Picker → content URI → ContentResolver / ContentProvider → ImageView preview. Selected pictures are not copied to app storage or uploaded to Firebase.

## Lab 8–11 coverage

| Week | Required topic | Ishara implementation |
|---|---|---|
| 8 | Android Studio architecture, Gradle, Manifest | Native Java project, Gradle configuration and AndroidManifest |
| 8 | Activity lifecycle | Activity callbacks and lifecycle logging in Lab Showcase |
| 8 | XML layouts and ViewBinding | XML screen layouts and generated Activity bindings |
| 8 | Event handling | Java click listeners and ActivityResult APIs |
| 8 | Explicit and implicit Intents | Screen navigation, share chooser and app settings navigation |
| 9 | Layouts and reusable UI | LinearLayout, ScrollView, Material Components and custom Views |
| 9 | RecyclerView, Adapters, ViewHolders | Chat, History, Dictionary, Lesson and Phrase adapters |
| 10 | Services | Bound SpeechService for TextToSpeech |
| 10 | BroadcastReceiver | BatteryReceiver for battery and airplane-mode system events |
| 10 | ContentProvider | Internal MessageProvider and Android media content URI access through ContentResolver |
| 10 | Permissions | Runtime camera/microphone permissions and scoped Photo Picker access |
| 11 | HTTP and JSON | HTTPS GET and JSONObject parsing in a background executor |
| 11 | SQLite | MessageDbHelper, IsharaDataStore, QuestDb and LabDemoDb |
| 11 | Background task | WorkManager progress-sync worker |
| 11 | Firebase Authentication | Email/password account and anonymous guest identity |
| 11 | Firebase Realtime Database | Room-based live message exchange |
| 11 | Cloud Firestore | User profile and aggregate practice progress documents |

## Data management and privacy

- Message history, captions and practice records are stored locally in SQLite by default.
- Camera frames and landmark coordinates are not uploaded.
- Live room messages are sent only when the user presses Send.
- Aggregate learning progress is uploaded only after the user presses the sync button.
- The WorkManager upload contains counts and average confidence only. It does not contain captions, chat transcripts, camera frames or pictures.
- Selected pictures are previewed from a content URI and are not permanently stored or uploaded. Firebase Storage is not used.
- Firebase access depends on the published security rules. No Firebase Admin credentials are embedded in the Android app.

## Limitations

The stock MediaPipe model recognizes seven general handshape classes. The phrase mapping is illustrative and is not a complete or validated Bangla Sign Language translation system. Model confidence is not the same as measured recognition accuracy. Actual performance must be tested on a physical phone under different lighting and hand positions. Live caption language availability and quality depend on the Android speech-recognition service installed on the device. The Firebase demonstration is an academic prototype and is not end-to-end encrypted.

## Testing and evidence

Complete the acceptance checklist in `docs/61_V6_1_0_TEST_AND_ACCEPTANCE.md`. Insert actual screenshots for the home screen, Gesture Studio, Live Captions, Photo Picker, BroadcastReceiver, HTTP/JSON response, Firebase Authentication, Realtime Database and Firestore. Record only test results that were observed on the target device. Do not claim a full Android build or Firebase test passed until it has been performed.

## Conclusion

Ishara demonstrates how Java-based Android development can combine camera processing, speech recognition, accessible UI, local databases, Android system components, background work and Firebase services in one focused communication application. Its main value is the integration of gesture-assisted message composition and live captions, supported by learning and connection tools.

## References

- Android CameraX: https://developer.android.com/media/camera/camerax
- Android Activity Result APIs: https://developer.android.com/training/basics/intents/result
- Android Photo Picker: https://developer.android.com/training/data-storage/shared/photopicker
- Android Content Providers: https://developer.android.com/guide/topics/providers/content-providers
- Android WorkManager: https://developer.android.com/topic/libraries/architecture/workmanager
- Android TextToSpeech: https://developer.android.com/reference/android/speech/tts/TextToSpeech
- MediaPipe Gesture Recognizer: https://developers.google.com/edge/mediapipe/solutions/vision/gesture_recognizer/android
- Firebase Authentication: https://firebase.google.com/docs/auth/android/start
- Firebase Realtime Database: https://firebase.google.com/docs/database/android/start
- Cloud Firestore: https://firebase.google.com/docs/firestore/quickstart
