# Ishara 6.1.0 — Master Update and Academic Alignment

## Product decision

Ishara remains a Java-centered Android communication application. No music detector or unrelated media-recognition feature is added. The strongest project story is the real camera-to-message workflow plus live conversation captions. More independent features would increase testing risk without improving the teacher's Lab 8–11 assessment coverage.

## Main user journeys

### A. Gesture Studio — primary feature

CameraX supplies frames to MediaPipe Gesture Recognizer on a background analyzer. Java handles confidence thresholds, temporal stability, phrase mapping, context selection, sentence composition, history, save/share and TextToSpeech. Camera frames and landmarks are not uploaded.

### B. Live Captions — primary feature

Android SpeechRecognizer produces partial and final results. Java manages language selection, lifecycle, transcript assembly, save to SQLite, copy and share. Microphone permission is requested at runtime. Speech service and language support vary by device.

### C. Play, Learn, Connect

- Play: Ishara Quest.
- Learn: Gesture Dictionary, camera practice, calibration and progress.
- Connect: two-way conversation and Firebase Realtime Database live rooms.

## Home screen information architecture

The home screen now prioritizes Gesture Studio and Live Captions. It then groups Play/Learn/Connect, secondary communication tools, learning/progress, message history and project showcase/settings. The less central scenario simulator remains available but is no longer promoted above the two primary workflows.

## Lab 8–11 implementation map

| Week | Topic | Ishara source / demo |
|---|---|---|
| 8 | Android Studio, Gradle, Manifest | `app/build.gradle`, `AndroidManifest.xml`, project root |
| 8 | Activity lifecycle | `MainActivity`, `CaptionActivity`, `LabActivity` callbacks and logs |
| 8 | XML, ViewBinding, event handling | `Activity*Binding`, layout XML and Java click listeners |
| 8 | Explicit/implicit Intents | Activity navigation, Android share chooser, app settings Intent |
| 9 | Layouts and responsive UI | XML LinearLayout/ScrollView/ConstraintLayout use across screens |
| 9 | RecyclerView, adapters, ViewHolders | `ChatAdapter`, `HistoryAdapter`, `DictionaryAdapter`, `LessonAdapter`, `PhraseGridAdapter` |
| 9 | Custom Views | `GestureStatusView`, `HandLandmarkOverlay` |
| 10 | Service | Bound `SpeechService` for Android TextToSpeech |
| 10 | BroadcastReceiver | `BatteryReceiver` receives battery changed/low/okay and airplane mode state while settings is visible |
| 10 | ContentProvider | Internal `MessageProvider`; Android Photo Picker content URI read with `ContentResolver` in Picture Board |
| 10 | Permissions | Camera and microphone runtime permissions; scoped Photo Picker access |
| 11 | HTTP and JSON | `JsonDemoClient` HTTPS GET and JSONObject parsing on an executor |
| 11 | SQLite | `MessageDbHelper`, `IsharaDataStore`, `QuestDb`, `LabDemoDb` |
| 11 | Background work | `CloudProgressSyncWorker` via WorkManager with connected-network constraint |
| 11 | Firebase Authentication | Email/password account creation and login; anonymous guest room identity |
| 11 | Firebase Realtime Database | Room-based live message exchange |
| 11 | Cloud Firestore | User profile and opt-in aggregate practice progress |

## Data and privacy decisions

- Camera frames and landmark coordinates remain local.
- SpeechRecognizer uses the Android speech service; actual network processing depends on the device's recognition provider. Ask permission and consent before captioning another person's speech.
- Conversation history and practice records remain in local SQLite by default.
- Firebase room messages are sent only when the user taps Send.
- Cloud progress is uploaded only after the user taps Sync. Only aggregate counts and confidence are sent, not message transcripts or images.
- Selected images are previewed from Android content URIs and are not copied to app storage or uploaded. Firebase Storage is therefore not used.
- Firebase security depends on the published database rules. Never ship service-account credentials.

## Scope and limitations

The stock MediaPipe model recognizes seven general handshape classes. Phrase mappings are illustrative and context-dependent. They are not validated Bangla Sign Language vocabulary. The confidence score is a model score, not an accuracy measurement. Do not claim production accessibility readiness until evaluated with intended users and a labeled test set.

## Version

- Version name: 6.1.0
- Version code: 610
- Java source compatibility: 17
- Compile/target SDK: 35
- Minimum SDK: 24
