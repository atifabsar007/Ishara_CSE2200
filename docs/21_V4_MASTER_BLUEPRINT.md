# Ishara 4.0 Master Development Blueprint

**Developer:** Md. Atif Absar  
**Native Android:** Java, XML, Android Studio, Gradle 8.11.1, Android Gradle Plugin 8.9.2, Android minSdk 24.  
**Brand:** Ishara teal, mint and deep-ink palette; user-supplied hand/speech logo. Course identifiers belong in lab documentation, not user-facing UI.

## 1. Vision, audiences and accessibility

Ishara helps people express or understand communication using more than one modality. Users include people with speech difficulties, deaf or hard-of-hearing students, teachers, friends, families and caregivers. Live lecture captions convert compatible speech recognition results into text. Gesture Studio translates *supported general handshapes* to custom demonstration messages and can compose a sentence. Text-to-speech offers the reverse path. Picture Board and quick phrases are immediately usable without a camera or microphone.

Ensure large touch targets, readable text (minimum 14sp for main content), adequate color contrast, understandable status messages, alternative text inputs, and immediate guest access without registration.

## 2. App journeys

1. **New user:** Welcome -> Create account -> unique local username + name + password -> Home. Login credentials remain on the device.
2. **Returning user:** Current device session skips welcome until logout. Logout opens Welcome; Login checks a salted, iterated password verifier.
3. **Urgent/simple communication:** Welcome -> Guest -> Picture Board / Quick Phrases / Type to Speak; no account required.
4. **Student lecture:** Home -> Live Captions -> grant mic permission -> record recognized captions -> save session -> History. Speech service may return delayed/chunked results.
5. **Gesture:** Home -> Gesture Studio -> grant camera, download official MediaPipe model, select context -> detect supported handshape -> add phrase -> speak/save/share.
6. **Practice:** Home -> Ishara Quest -> Meaning Match or Sentence Quest -> local score/points. Separately, the Guided Practice dashboard measures *actual labeled detection trials*, never confusing quiz points with model accuracy.

## 3. Screens

| Screen | Implementation | Purpose |
|---|---|---|
| Welcome | `WelcomeActivity`, `activity_welcome.xml` | Clear app value, login/register/guest entry, logo and developer |
| Offline local auth | `AuthActivity`, `activity_auth.xml` | Register, login, password check, local session, error feedback |
| Home dashboard | `MainActivity`, `activity_main.xml` | Personalized greeting, shortcut ranking, featured captions and quests |
| Ishara Quest | `QuestActivity`, `activity_quest.xml` | Meaning Match, Bangla word order, XP and badges |
| Live Captions | `CaptionActivity`, `CaptionController` | Classroom and conversation voice-to-text |
| Two-way Conversation | `ConversationActivity`, `ChatAdapter` | Speak-to-text conversation and text-to-speech response |
| Gesture Studio | `GestureActivity` | CameraX + MediaPipe recognition of seven handshapes |
| Sentence Builder | `SentenceActivity` | Build phrases and full sentences |
| Picture Board | `BoardActivity` | Large ready-made communication buttons |
| Gesture Learning | `LearnActivity` | Examples, accessible educational explanation |
| Recognition Progress | `ProgressActivity` | Confidence/attempt history from real guided detection |
| Quick Phrases | `PhraseActivity` | Frequently used phrases and personalized shortcuts |
| History | `HistoryActivity`, `HistoryAdapter` | SQLite ContentProvider lists |
| Settings / API | `SettingsActivity` | Battery receiver, JSON networking, optional Firebase demo |
| About | `AboutActivity` | What/why/who/how/developer |

## 4. Java architecture and storage

Package `com.kuet.ishara`: Java model/logic separation from Activities, XML ViewBinding, Adapters/ViewHolders, custom views and Service/Receiver/Provider. The new `PasswordHasher` is Android-independent pure Java. `AccountDb` stores only a salted PBKDF2 verifier, never plaintext passwords. `SessionManager` stores the current local account ID/display name in SharedPreferences. Passwords are **not** sent online. `QuestEngine` supplies deterministic testable question/answer/scoring logic. `QuestDb` stores attempts/points per account. Old `MessageDbHelper` and `IsharaDataStore` are now scoped by local ID, while the original unsuffixed databases remain guest-only and preserve previous v3 data.

### Data tables
* `ishara_accounts_local.db / accounts`: `_id`, username, display name, salt, password verifier, created_at.
* Scoped `ishara_quest[_account_ID].db / attempts`: game mode, question, correct and chosen response, correct flag, points, timestamp.
* Scoped `ishara[_account_ID].db`: original history and saved phrases through ContentProvider.
* Scoped `ishara_v3[_account_ID].db`: caption sessions, recognition trials and most-used phrases.

For a real public release, implement fully reviewed identity, secure remote login/password recovery, data deletion/export, encryption at rest where appropriate, a consent/privacy policy, and accessibility testing. This is an educational prototype.

## 5. Ishara Quest game design

**Meaning Match:** display one of seven supported general handshape labels and four phrase options. The user chooses the correct *Ishara demo meaning*, earns 10 points for a correct answer, and receives feedback plus the correct response on an incorrect answer. Its result is not a MediaPipe accuracy measurement.

**Sentence Quest:** present a useful Bangla sentence broken into shuffled word tiles. User taps words in order, can undo, checks the completed sentence, and earns 15 points if correct. Includes classroom help requests, rest, needs and polite communication. Completion records to SQLite. Badge labels (`Explorer`, `Bridge Maker`, `Word Builder`, `Communication Champion`) are earned by point milestones. These are educational rewards and not real sign-language certifications.

## 6. Direct lab syllabus coverage

| Week | Syllabus topic | Testable implementation |
|---|---|---|
| 8 | Android architecture / Gradle / manifest | Native project, applicationId, Java package and manifest registrations |
| 8 | Activity lifecycle | Login/onboarding navigation; gesture and caption pause/resume |
| 8 | XML, ViewBinding, listeners | Welcome/Auth/Home/Quest/Caption XML and Java event handlers |
| 8 | Explicit/implicit intents | Screen navigation and share captions/text |
| 9 | RecyclerView / adapter / ViewHolder | History, conversation messages, gesture lessons, phrase lists |
| 9 | Custom Views / UX | Gesture confidence drawing, adaptive shortcuts, tactile button layouts |
| 10 | Services / receivers / provider | Bound TTS service, battery receiver, SQLite message ContentProvider |
| 10 | Permissions | CAMERA, RECORD_AUDIO, runtime consent UX |
| 11 | HTTP + JSON parsing | JSON client for optional phrase pack demo |
| 11 | SQLite | Account storage, per-user communication history, caption sessions and quest scores |
| 11 | Background work / APIs | Camera image analysis, speech callbacks, model installer, background network tasks |
| 11 | Firebase | Existing opt-in demonstration module; requires separately configured project |
| 12 | Quiz study | Explain lifecycle, cursor/CRUD, salted hashing, adapters, recognizer callbacks |
| 13 | Final demo | Account -> lecture caption -> sentence -> recognition -> quest -> SQLite history |

## 7. Engineering priorities

1. Test project sync and manifest/build on a physical Android phone. Fix all compile/runtime errors.
2. Verify Welcome, register, logout, login, guest and account-specific storage (must pass before final demo).
3. Verify caption microphone permission and service on Android phone; demonstrate actual partial/final text and save.
4. Verify CameraX with model and real camera. Existing seven shapes only; additional classes need trained model and dataset evaluation.
5. Test all game questions, persistence, accessibility with TalkBack and large text, screen rotation and permission denial.
6. Add optional Firebase/cloud account only after user requests a real online account, tests, and privacy design.

## 8. Known limitations

No complete Android Gradle build, emulator install or device testing could be performed in the generation environment. Source-level validation checks XML and Java-only logic; see `docs/22_V4_TEST_PLAN.md`. Google speech recognition may depend on service/network/language. Do not promise guaranteed continuous or medical-grade transcription. Explicit class presentation should identify that video gesture meanings are custom mappings rather than authentic sign-language signs.
