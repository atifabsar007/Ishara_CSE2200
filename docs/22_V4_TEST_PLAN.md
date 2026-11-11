# Ishara 4.0 Acceptance & Regression Plan

Test on Android Studio with a real Android phone if possible. For every failed test capture Android Studio Build or Logcat output, expected vs actual behavior, then fix and retest.

## Authentication / profile
- [ ] Welcome logo displays; About opens without authentication.
- [ ] Login screen and Register screen switch cleanly.
- [ ] Create account with unique username, name, 8+ char password.
- [ ] Wrong confirm password rejected; weak password rejected.
- [ ] Duplicate username rejected, no crash.
- [ ] Sign out and sign back in; greeted with correct display name.
- [ ] Wrong password rejected; transient repeated attempts limited.
- [ ] Guest can open Live Captions, Picture Board and Gesture Studio without an account.
- [ ] Existing v3 guest message history remains available in Guest mode.
- [ ] Register two accounts, save different phrases and quiz points; account A cannot see account B's local data.
- [ ] App reopening with active session navigates directly to Home; logout clears the active session.

## Ishara Quest
- [ ] Meaning Match shows four unique options and expected answer.
- [ ] Wrong choice shows correct explanation and grants zero points.
- [ ] Right choice grants ten points only once; multiple taps not rewarded.
- [ ] Sentence Quest scrambled words select in order, Undo works, Check requires all words.
- [ ] Correct sentence grants fifteen points; incorrect grants zero.
- [ ] Results persist after closing/reopening and stay within current account.
- [ ] Game controls readable on small phone and at larger system font scale.
- [ ] Educational disclaimer appears; no claim of translating official Bangla signs.

## Preserve earlier features
- [ ] Camera screen requests permission, uses front/back camera, and downloads/initializes `.task` model.
- [ ] On emulator, camera may show a virtual scene; test with real phone camera to evaluate recognition.
- [ ] Live Captions: grant microphone permission; enable emulator host microphone input; test English then Bangla separately.
- [ ] Save, copy, share captions; no crash on network or recognition-service errors.
- [ ] Two-way conversation uses SpeechRecognizer and TextToSpeech.
- [ ] Picture Board/Quick Phrases/Sentence Builder/History/Favorites respond as before.
- [ ] Practice Dashboard shows separately measured detection statistics, not quiz points.
- [ ] RecyclerViews update after saved/deleted messages; ContentProvider CRUD still functions.
- [ ] BatteryReceiver / JSON demo / optional Firebase do not crash when their external services are unavailable.
- [ ] Lifecycle: navigate away and return; pause/resume permission workflows safely.

## Local test results performed in generation environment
- XML parse checks for Android Manifest and all resource XML.
- Static checks for Java ViewBinding IDs for new layouts.
- `javac` compile for `PasswordHasher` and `QuestEngine`.
- Pure Java smoke check: good/bad passwords, 200 generated puzzles and scoring/badge mapping.

**Not performed:** complete Android Gradle sync/build, instrumented tests or live device/emulator verification. The developer should complete the above list before claiming the app is fully working.
