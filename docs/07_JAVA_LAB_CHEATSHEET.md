# Java + Android Syllabus: Source-Level Guide

| Core concept | Where to show it | Demonstration |
|---|---|---|
| OOP classes | `CaptionController`, `IsharaDataStore` | Explain fields, methods, private state |
| Interfaces | `CaptionController.Callback`, `PhraseGridAdapter.OnChoose` | Callback pattern decouples recognizer and UI |
| Inheritance | `BaseSpeechActivity extends AppCompatActivity` | Base lifecycle/service behavior reused |
| Method overrides | Activity lifecycle, `RecognitionListener` | Compare Android callbacks in Logcat |
| Collections | `ArrayList<String>` in Conversation, Sentence Builder | Append and remove words/messages |
| String processing | `StringBuilder` transcript and `SentenceComposer` | Compose output without excessive concatenation |
| Listeners | `setOnClickListener` in Activities | Tap -> lambda -> Java method -> UI change |
| ViewBinding | `ActivityCaptionBinding.inflate(...)` | Avoid manual findViewById and use typed widget IDs |
| Adapters / ViewHolders | `ChatAdapter`, `LessonAdapter` | Recycle row XML for variable data |
| SQLite CRUD | `IsharaDataStore`, `MessageDbHelper` | INSERT / query / saved history |
| Transactions | `IsharaDataStore.usedPhrase` | Update counts consistently |
| Runtime permissions | `RequestPermission` in caption and gesture | Explicit camera/mic request, denial state |
| Activity lifecycle | `onStop` caption / gesture | Stop resources when app becomes invisible |
| Service | `SpeechService` + `BaseSpeechActivity` | Bound TextToSpeech speech engine |
| Receiver | `BatteryReceiver` + `SettingsActivity` | Android battery broadcast registered/unregistered |
| ContentProvider | `MessageProvider` | Internal shared data interface |
| Networking | `JsonDemoClient` | HTTPS/JSON, error handling, off-main-thread executor |
| Background thread | `ExecutorService` in camera and Settings | Avoid blocking UI with costly work |
| Advanced API | CameraX / MediaPipe and Android SpeechRecognizer | Live input, callbacks, stable frame processing |
| Gradle | `app/build.gradle` | Explain dependencies, build types, `compileSdk` |

**Key clarification:** This is Java for Android. Java isn't C++, and the syllabus screenshot focuses on Android Java APIs, so no native C++ implementation is necessary.
