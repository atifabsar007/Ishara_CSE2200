# ISHARA 3.0 — MASTER DEVELOPMENT BLUEPRINT

**Project:** Ishara (ইশারা) | Communication Beyond Words  
**Developer / app credit:** Md. Atif Absar  
**Platform:** Native Android; Java 17 compatible source; XML layouts  
**Course context (DOCUMENTATION ONLY):** CSE 2200, Advanced Programming Laboratory, KUET  
**Blueprint revision:** 3.0, 18 October 2026  
**Deliverable scope:** A runnable-source prototype plus architecture and implementation/test roadmap; the full Android build and assistive-accessibility evaluation are pending.

---

## 1. Product mission, users and positioning

Ishara is a communication assistant that provides multiple paths for a message to be understood: hands -> sample text -> speech; voice -> live displayed text; text / picture -> speech; words -> sentences. It is designed to help reduce communication barriers without requiring everyone to use the same method. Its purpose is supportive communication, not medical diagnosis or sign-language certification.

**Primary groups:** (1) people who have difficulty speaking and prefer words, picture boards, or supported gestures; (2) people who are deaf or hard of hearing, including students who wish to read captions while an instructor speaks; (3) teachers and classroom aides; (4) family members and caregivers; (5) anyone wanting a quick phrase or large-text communication aid.

**Top use cases:**
- **Lecture use case:** A student opens Live Captions, chooses a language, taps Start while the instructor speaks, views partial text quickly and finalized segments in a scrollable transcript, saves a session with a title, reviews prior saved output via history, or shares it with permission.
- **Face-to-face use case:** Another person speaks and sees a caption bubble; the Ishara user types an answer, taps Speak, and can save or share the interaction.
- **Gesture use case:** A user launches Gesture Studio, confirms the camera is really capturing their hand, makes one of seven *general* supported handshapes, sees the current context's *demo* phrase, adds phrases to a sentence, and plays the sentence through Bangla TTS.
- **No camera / no mic use case:** A user picks a picture board tile or phrase button, assembles words and uses large text / speech.

**Product principles:** user control over microphone and camera; high contrast and large touch targets; clear error states and no fake “AI success” feedback; preserve offline core functions; never label arbitrary MediaPipe handshapes as official Bangla Sign Language words; never claim untested recognition accuracy.

## 2. Brand and visual design

**Name and visible credit:** Ishara / ইশারা; developed by Md. Atif Absar. Course numbers and student-submission labels are intentionally not displayed inside the app.  
**Logo:** Use the user-provided, full-color Ishara hand + speech bubble mark unaltered as the brand image. A crop of the same symbol is provided for the Android app icon. Use the exact supplied artwork in onboarding and About.  
**Colors:** dark teal `#003E46`, teal `#087B74`, soft mint `#DDF4EE`, near-white `#F7FAF9`, ink `#143D3A`, secondary `#66817E`, accent `#D6F46B`.  
**Components:** 20–23dp rounded rectangles, 15–17sp body copy, 23–30sp major titles, at least 48dp tall buttons, generous vertical spacing, visible scanning/mic state, readable Bangla text and controls that are easy to explain.  
**Navigation:** First-launch Welcome -> Home; from Home: Gesture, Captions, Conversation, Sentence Builder, Picture Board, Quick Phrases, Learn, Practice Stats, History, Preferences, About.

**Screen inventory**

| Screen | Main content | Primary action | Persisted data |
|---|---|---|---|
| Welcome | Identity; why built; audiences; how it works; maker | Get started | onboarded preference |
| Home | Intro; lecture hero; module cards; adaptive phrases | Choose module | phrase frequency |
| Gesture Studio | Live preview; camera flip; model readiness; confidence; contexts | Recognize/add/speak | gesture trial, phrase usage, history |
| Live Captions | Live partial + final text; session name; language | Start / pause / save | captions and history |
| Two-way | Speaker messages + typed reply bubbles | Listen / speak reply | history |
| Sentence Builder | Bangla word tiles; composed sentence | Undo / speak / save | saved phrases |
| Picture Board | 12 need-oriented communication tiles | Select / speak | phrase usage, history |
| Quick Phrases | Reusable phrase cards, free text | Compose / save / speak | history, saved phrases, usage |
| Gesture Learning | Seven illustrated-by-name practice cards | Practice on camera | trials |
| Practice Dashboard | Total / successes / errors / confidence | Refresh | summary from trials |
| History | Last messages and saves | Reuse / delete | original SQLite database |
| Preferences | Voice/cloud/network demo/battery status | Check / optional sync | permissions / config |
| About | Mission, audience, usage, privacy, developer credit | Read | none |

## 3. User journeys and acceptance criteria

### 3.1 Lecture captions

**Journey:** Teacher speaks -> Android speech service delivers partial text -> show partial text -> final segment callback -> append to a readable transcript -> user chooses Save -> SQLite stores title, language, body and timestamp. The user may copy or share text with Android intents.

**Acceptance gate:** microphone permission explicitly requested; Start begins a recognizer session; interim and finalized results look different; Pause switches mic off; app stops recognition in the background; saved transcript can be located; languages unsupported by the recognizer get a meaningful error rather than fabricated captions. No promise of uninterrupted long recordings.

**Implementation:** `CaptionActivity` + `CaptionController` + `IsharaDataStore`; `SpeechRecognizer`, `RecognitionListener`, `RecognizerIntent`, `ActivityResultContracts.RequestPermission`, `StringBuilder`, SQLite. Retry only after a delay and stop after repeated engine-level failure. Run on main thread as required for Android `SpeechRecognizer`.

### 3.2 Two-way conversation

**Journey:** person speaks -> finalized text appears in chat RecyclerView -> user types an answer -> SpeechService reads Bangla text -> messages saved to old message DB. Provide typing fallback if speech recognition is absent.

**Acceptance gate:** reversible microphone state; chats appear in order, TTS doesn't crash when language unavailable, text persists in history, mic release on exit.

### 3.3 Gesture learning + sentence composition

**Journey:** choose gesture card -> camera opens with `practice_expected` -> user presents physical hand -> MediaPipe identifies a class -> record expected, observed, confidence and pass/fail -> show result in practice stats. In free scanning, selected phrase maps to context and can be added to a sentence. Controls: flip, pause, clear, undo, speak, save and share.

**Acceptance gate:** model download missing state is explicit; recognizes actual physical hand only when a camera feed is present; a fixed virtual scene does not count; de-duplicate repeated detections; an incorrect guided gesture is counted as failure; unverified free scanning is **not** counted as an accuracy success. Protect from background processing and camera lifecycle problems.

**Actual capability:** **7 pretrained handshapes**. Five context mappings yield up to 35 *message shortcuts*, not 35 distinct recognized hand signs. Expansion to 15/30 *actual* signs is future research with a new labeled dataset and custom model. Further specification in `04_GESTURE_EXPANSION.md`.

### 3.4 Adaptive dashboard

**Journey:** choosing a phrase stores/updates `phrase_usage` -> `MainActivity.onResume` queries the three highest-frequency shortcuts -> shows them without needing complex cloud AI.

**Acceptance gate:** first launch has a helpful empty state; frequent phrases rise in order; queries run after screen return; no exposure of the private database to other apps. Later optimization: move DB work off UI thread when dataset grows.

## 4. Architecture: Java-centered

```
Android Activity + XML (ViewBinding / click listeners)
    |
    +--- Speech path: CaptionController -> Android SpeechRecognizer -> UI
    |                                                 |
    |                                           IsharaDataStore
    +--- Gesture path: CameraX -> ImageAnalysis -> MediaPipe recognizer
    |                            |             |
    |                            +----- GestureDictionary / SentenceComposer
    |                                           |
    |                                    Practice logging + saved history
    +--- Phrase / Board / Sentence UI -> Java events + SQLite usage ranking
    +--- BaseSpeechActivity -> bound SpeechService -> Android TextToSpeech
    +--- History -> MessageProvider -> MessageDbHelper
    +--- Settings -> BroadcastReceiver + HTTPS/JSON + Firebase opt-in
```

Java modules and their intent:
- `ui/`: Activities + RecyclerView Adapters / ViewHolders, Back navigation, lifecycle.
- `logic/`: platform-independent gesture mapping and sentence composition; speech controller wraps Android SDK.
- `data/`: the original `MessageDbHelper` / `MessageProvider`; new `IsharaDataStore` stores V3 usage, practice and captions in a separate SQLite DB to avoid overwriting version 2 user data.
- `services/`: local bound service and battery broadcast receiver.
- `network/` and `cloud/`: HTTP JSON example and opt-in Firebase sample.

**Core Java concepts you should explain in viva:** encapsulation in each helper, interfaces (`CaptionController.Callback`, adapter listeners), inheritance (`BaseSpeechActivity extends AppCompatActivity`), overrides (`onCreate`, lifecycle, `RecognitionListener`), anonymous callbacks and lambda expressions, `ArrayList` and maps, `StringBuilder` for text composition, SQLite transactions, executors for network operations, lifecycle ownership, exception handling and null checking.

## 5. SQLite schema

**Legacy DB:** `ishara.db` remains untouched with `messages` and `saved_phrases` to protect V2 data. MessageProvider remains non-exported and controls access inside the app.

**V3 new DB:** `ishara_v3.db` (version 1) contains:

```sql
CREATE TABLE captions (
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL, body TEXT NOT NULL,
  language TEXT NOT NULL, created_at INTEGER NOT NULL
);
CREATE TABLE gesture_trials (
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  expected TEXT, detected TEXT NOT NULL,
  confidence REAL NOT NULL, outcome TEXT NOT NULL,
  created_at INTEGER NOT NULL
);
CREATE TABLE phrase_usage (
  phrase TEXT PRIMARY KEY, category TEXT NOT NULL,
  use_count INTEGER NOT NULL DEFAULT 0, last_used INTEGER NOT NULL
);
```

**Rules:** free scanning trials have `expected=NULL`, `outcome=uncertain` (meaning no ground-truth answer, not low confidence). Guided trials have `expected` label and `success`/`failed` based on matching the expected detected class. Mean model confidence is the mean score, **not a statistically validated accuracy**. A future labeled test dataset is required for that claim. The phrase ranking uses `ORDER BY use_count DESC, last_used DESC`. History data is user-owned; consider delete-all and export in next phase.

## 6. Lab syllabus mapping / demonstration

| Week | Required concept | Ishara source / live demo |
|---|---|---|
| 8 | Android Studio structure, Gradle, Manifest | `settings.gradle`, `app/build.gradle`, `AndroidManifest.xml` |
| 8 | Activity lifecycle | `MainActivity`, camera pause/unbind, `CaptionActivity.onStop()` stops mic |
| 8 | XML layout and ViewBinding | 30 XML resources checked, generated `Activity...Binding` |
| 8 | Event listeners | Every Start, Speak, Pause, Save and Camera action |
| 8 | Explicit / implicit intents | Navigation to Activities; ACTION_SEND sharing |
| 9 | RecyclerView | history, quick phrases, conversation, gesture lessons, picture board |
| 9 | Adapters & ViewHolders | `PhraseGridAdapter`, `HistoryAdapter`, `ChatAdapter`, `LessonAdapter` |
| 9 | Custom views and UX | `GestureStatusView`, confidence visualization, large readable captions |
| 10 | Services | `SpeechService` bound to `BaseSpeechActivity` |
| 10 | BroadcastReceiver | `BatteryReceiver` registered while Settings is visible |
| 10 | ContentProvider | `MessageProvider` reads/writes local history |
| 10 | Android permissions | CAMERA, RECORD_AUDIO + internet connectivity declaration |
| 11 | HTTP networking and JSON | `JsonDemoClient` works in an executor / Settings button |
| 11 | SQLite CRUD | old messages, new `IsharaDataStore` stats and caption persistence |
| 11 | Background data work | existing JSON executor, MediaPipe analysis executor, model download |
| 11 | Firebase database | explicit opt-in `FirebaseSync` with user setup and authorization |
| 11 | Advanced Android APIs | CameraX, MediaPipe Tasks, SpeechRecognizer, TextToSpeech |
| 12 | Practical quiz | reproduce lifecycle, SQLite and service / receiver behavior |
| 13 | Demonstration + report | `02_TEST_AND_ACCEPTANCE.md` + `05_DEMO_VIVA.md` |

**Note:** This is a Java Android project, not a C++ project. C++ is not required to demonstrate your supplied Android Java syllabus. Native C++ would add complexity without helping the core evaluation.

## 7. Engineering priorities and dependency chain

**P0: Build and baseline (must pass).** Source integrity -> verified Gradle wrapper and Java 17/21 -> sync -> debug build -> onboarding / home, message storage and sharing. Device tests and storage inspection.

**P1: Working core (must pass).** Quick Phrases + Sentence Builder -> SpeechService -> Picture Board -> History + search (original list; broader filtering later) -> first-launch About and logo. Focus on reliable Java / SQLite behavior.

**P2: Accessible live communication (must pass for standout).** RECORD_AUDIO permission + availability error -> CaptionController partial/final -> auto retry with backoff -> save / share -> Two-way chat -> app-stop microphone release -> test Bangla and English on real hardware.

**P3: Camera & learning (must pass before claiming gesture feature).** Camera permission -> real camera feed -> stock model available -> stable-frame debounce -> speak / sentence compose -> practice expected class -> SQLite practice dashboard. Verify with at least two human testers and two Android devices if available. No unsupported claims of continuous recognition.

**P4: Data and lab extras.** Adaptive ranking -> JSON fetch and parsing -> BatteryReceiver -> ContentProvider queries -> optional Firebase setup -> offline and no-permission tests.

**P5: Future enhancements, not falsely labelled as complete.** Custom 15+ gesture classes / Bangla Sign Language corpus, sentence grammar correction, searchable transcript viewer, language pack import/export, custom user phrase packs, in-app voice changer, accessibility audit and formal quantitative evaluation.

## 8. Reliability, usability, accessibility and ethics

1. Permissions are requested only on relevant screens. Camera and mic status must be visible.
2. Capture stops when the relevant activity leaves the foreground. No hidden microphone use.
3. Always include offline / typed fallback when ASR or MediaPipe is unavailable.
4. Never send video frames to Firebase or another server as part of this prototype. Camera inference stays local after model installation.
5. Speech recognition may use a third-party device service and network; explain that to users and ask permission in sensitive settings (such as classrooms).
6. No claim of certified translation or emergency-system reliability. Avoid medical or safety-critical reliance on gestures.
7. Ensure large text, 48dp+ targets, color contrast, accessible labels and support for screen readers in a later UX audit.
8. Watch resource use: limit camera analysis FPS, close `ImageProxy`, release TTS service/recognizer, end executor threads on Activity destruction, and avoid long DB operations on UI thread.
9. Protect privacy: messages and transcripts are local by default; Firebase is opt-in, no general external export without a clear user action.

## 9. Definition of Done for Ishara 3.0

- [ ] Debug build succeeds on macOS Android Studio.
- [ ] Splash/onboarding shows official logo, user audience, purpose and Md. Atif Absar.
- [ ] No old project/course branding visible in the app interface.
- [ ] Live captions produce real finalized output on at least one test phone with a working ASR service.
- [ ] Pausing or navigating away stops microphone use promptly.
- [ ] Two-way reply speaks typed Bangla text (where TTS language installed).
- [ ] Camera displays a *real* hand and recognizes at least several of the seven physical gesture classes.
- [ ] Built sentences can be saved/shared; repeated frames do not create duplicate words.
- [ ] Practice attempts persist and dashboard counts survive app restart.
- [ ] Adaptive shortcuts reflect actual phrase usage.
- [ ] JSON fetch / SQLite / BroadcastReceiver / Service / ContentProvider can be demonstrated.
- [ ] No crash on denied camera, denied mic, absent ASR, absent network, unavailable TTS or missing model.
- [ ] All documentation updated to reflect verified functionality, screenshots and known bugs.

See `02_TEST_AND_ACCEPTANCE.md` for pass/fail steps, and `03_BUILD_STATUS.md` for what is and isn't verified here.

## 10. External documentation (for development)

- Android RecognitionListener: https://developer.android.com/reference/android/speech/RecognitionListener
- Android RecognizerIntent: https://developer.android.com/reference/android/speech/RecognizerIntent
- MediaPipe gesture recognition: https://developers.google.com/edge/mediapipe/solutions/vision/gesture_recognizer
- MediaPipe Android guide: https://developers.google.com/edge/mediapipe/solutions/vision/gesture_recognizer/android
- Android microphone/foreground service constraints: https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start
- Gradle wrapper manual: https://docs.gradle.org/current/userguide/gradle_wrapper.html

**This is a specification, source prototype and verification plan, not an evidence-backed certification of recognition accuracy or production readiness.**
