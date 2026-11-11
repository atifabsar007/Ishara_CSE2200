# Ishara 5.0 | Master Development Blueprint

**Developed by:** Md. Atif Absar  
**Main purpose:** Use camera-recognized hand gestures to create readable and spoken messages.  
**Secondary accessibility tools:** Live captions, two-way speech/text, saved phrases, pictures, education, practice, and local device communication.  
**Stack:** Native Android, Java 17, XML, Gradle, CameraX, MediaPipe, SQLite, Services, BroadcastReceiver, ContentProvider, RecyclerView, HTTP, JSON, Firebase optional.  
**Release type:** Coursework prototype, not a certified assistive, sign-language, or secure messaging product.

## 1. Approved Ishara home-screen order

1. **Gesture-to-Text Studio** is the first prominent dark-teal hero card. Recognition, composing a sentence and speaking it are the primary flows.
2. **Live Conversation Captions** is the second card, supporting broader communication without becoming the app's main purpose.
3. **Ishara Quest** is the third card.
4. Adaptive shortcuts, Peer Link, Everyday Simulator, two-way speech, sentence builder, picture board, phrases, learning, history, and optional Inside Ishara follow.

No CSE course number is displayed in the normal app UI. Academic evidence stays in `docs/`. Keep the official Ishara hand-wave logo and user credit.

## 2. Added module A: Ishara Peer Link

**Concept:** An opt-in, foreground **local TCP socket** exchange between two *physical* Android phones on the same private Wi-Fi network. This is not an internet messenger; no Firebase configuration is needed. The session uses a six-digit code and a local IPv4 address. It is a learning demonstration, **unencrypted and not safe for sensitive/private content**.

### User journey
1. Both devices join the same Wi-Fi network; router must allow device-to-device connections.
2. Phone A: Home → Peer Link → **Host a session**. The app shows Phone A's local IP and generated six-digit session code.
3. Phone B: Home → Peer Link → enter Phone A's IP and code → **Join**.
4. After `Connected`, either phone types and sends a phrase; incoming text appears as a chat bubble. **Speak latest incoming message** uses the existing bound TTS Service.
5. Open Gesture Studio or Sentence Builder **before** pairing, compose a sentence and tap **Send message via Peer Link**. That text opens Peer Link as a prefilled draft. Pair and send.
6. Stop session when finished. The connection closes when PeerActivity is destroyed. Chat entries may also be saved to the existing local history ContentProvider.

### Technical architecture
* `network/LocalAddress.java`: local IPv4 discovery and allowlist, validation of six-digit pairing code, 1–400 character message policy.
* `network/PeerSession.java`: `ServerSocket` host, `Socket` client, JSON-lines handshake, unencrypted LAN message transfer, executor-managed I/O, Handler marshals events to main thread, proper resource shutdown.
* `ui/PeerActivity.java`: host/join, status errors, RecyclerView adapter, send, load latest history phrase, TTS, stop.
* Permissions: existing `android.permission.INTERNET`, no new dangerous permissions.
* Limitations: only private IPv4 LAN; IPv6, Wi-Fi Direct, internet relay, TLS, discovery via QR, multi-peer and background reconnect are out of scope. A 6-digit code is **not encryption**. Some routers isolate clients. An emulator is not an appropriate substitute for testing two real devices.

## 3. Added module B: Everyday Conversation Simulator

**Concept:** A branching, text-first communication practice app. It never suggests that only one communication method is valid. Users explore four situations: café, clinic reception, bus stop, meeting someone new.

**Implementation:** `logic/ScenarioEngine.java` provides immutable Scenario → Node → Choice state graphs. Every option carries a response, next node ID, and practice points. `ui/SimulatorActivity.java` renders choice buttons in a MaterialCard-like design. The path is recorded to `data/ScenarioDb.java`, a user-scoped SQLite database separate from Quest's quiz scores. Existing TTS can speak the most recent response; implicit Intents share it. The teaching goal is OOP, collections/maps, graph-like branching, event handling, CRUD, lifecycle, and accessible UI. UI scores measure practice completion, not intelligence or sign-language proficiency.

## 4. Added module C: Inside Ishara (Developer Demonstration)

**Concept:** An optional technical panel reachable near the bottom of the home screen, *without* showing lab/course labels as the app's public-facing identity.

* **Lifecycle:** the Activity logs `onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy` in real time using Java callbacks and Logcat.
* **SQLite CRUD:** Create → Read → Update → Delete a row in its **own demonstration SQLite database**, never modifying real conversation history.
* **JSON:** parse a small Bangla/English `JSONObject` and `JSONArray` in Java.
* **BroadcastReceiver:** query Android's sticky battery-change broadcast for current battery percentage (existing Settings also dynamically registers a `BatteryReceiver`).
* **TTS Service:** speak a demo Bangla sentence using the existing bound `SpeechService`.
* **Implicit Intent:** invoke Android's native sharing chooser.
* **Real networking:** direct link to Peer Link for actual socket communication.
* **Other source modules:** gesture CameraX/MediaPipe, history RecyclerView and ViewHolders, existing JSON HTTP request in Settings, optional Firebase module, runtime microphone/camera permissions.

## 5. Lab syllabus mapping

| Week | Required concept | Ishara feature/evidence |
|---|---|---|
| 8 | Project structure, Gradle, manifest | Java packages, activity declarations, versioning, permission manifest |
| 8 | Activity lifecycle | Inside Ishara callback log, camera/caption pause/resume |
| 8 | XML, ViewBinding and click listeners | 3 new layouts + updated Home/Gesture/Sentence and Java listeners |
| 8 | Explicit / implicit Intents | Navigate from Home; compose gesture → Peer Link; share |
| 9 | RecyclerView, ViewHolder, adapters | ChatAdapter (Peer Link), HistoryAdapter, LessonAdapter, phrase cards |
| 9 | Custom Views, UX | GestureStatusView, large text, clear status feedback, high contrast controls |
| 10 | Services | SpeechService (bound), input/output examples |
| 10 | BroadcastReceiver | BatteryReceiver in Settings; demo battery sticky broadcast |
| 10 | ContentProvider | Persist Peer Link messages via MessageProvider; history screens |
| 10 | Permissions | CAMERA, RECORD_AUDIO and existing INTERNET permission |
| 11 | Networking, API, HTTP | Real private Wi-Fi TCP sockets; optional HTTPS JSON demo |
| 11 | JSON parsing | JSONObject wire format, Inside Ishara sample, JsonDemoClient |
| 11 | Database manipulation | SQLite CRUD in dedicated LabDemoDb, SimulatorDb progress, existing history/user account DBs |
| 11 | Background operations | ExecutorService for socket I/O, CameraX analyzer, model downloader |
| 11 | Firebase database | Existing optional Firebase sync (requires separate configuration) |

## 6. Preserved functionality

Ishara 4.0.1 local login / register / guest, official logo, developer credit, camera AI, gesture phrase contexts, language speech output, captions, conversation, Quest game, accuracy learning dashboard, SQLite history, adaptive phrase shortcuts, picture board and preferences remain part of this source. Existing Java source is not intentionally deleted.

## 7. Acceptance criteria

1. **Build:** Android Studio Gradle `:app:assembleDebug` succeeds after installing official wrapper and Android SDK 35.
2. **Login:** Existing guest and local accounts behave as before.
3. **Home:** Gesture first, Captions second, Quest third. All buttons navigate correctly.
4. **Peer:** Phone A hosts; Phone B joins using the matching IP and six-digit code; invalid code rejected; text sent and received both ways; incoming text spoken; stop closes connection; incorrect Wi-Fi shows helpful error.
5. **Simulator:** All four scenarios complete along all branches and save completed paths to SQLite; replay starts afresh; TTS and sharing work.
6. **Lab:** Lifecycle entries appear, all CRUD buttons affect only demo data, JSON parses, battery works where available, implicit chooser opens, and Peer Link navigation works.
7. **Regression:** Gesture model initializes on a real device, camera frames are available, sentence builder remains functional, captions continue with microphone permissions, existing histories and Quest remain intact.
8. **Safety and accessibility:** Remote LAN messages explicitly labeled as unencrypted, sender/recipient choose to start/stop, full app usable in Guest mode, usable touch targets and readable contrast.

## 8. Next potential improvements (not claimed implemented)

* Replace LAN text with encrypted authenticated transport and consent-reviewed privacy controls before real-world use.
* Test a custom gesture model trained with representative consenting users; the shipped MediaPipe general recognizer does **not** translate Bangla Sign Language.
* Add proper session restore and multi-peer support with robust networking tests.
* Optimize speech recognition for language switching through on-device testing; Android recognition capabilities differ by device.
* Accessibility audit with TalkBack, larger fonts, touch targets and contrast checks.

## 9. Verification status

The included validation scripts check XML, binding identifiers and platform-independent Java logic. **They are not an Android emulator build or physical phone acceptance test.** Complete Gradle and two-physical-phone testing are essential before evaluation.
