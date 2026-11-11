# Ishara Acceptance, QA and Test Plan

Use this checklist after Gradle sync and on at least one Android handset with Bangla TextToSpeech support. Mark outcomes yourself; tests have **not** been claimed as device-passed in this package.

## Build gates

- [ ] Open root project in Android Studio; Gradle sync successful.
- [ ] `:app:assembleDebug` successful.
- [ ] `:app:testDebugUnitTest` successful (gesture dictionary, unknown labels, sentence edge cases).
- [ ] `:app:connectedDebugAndroidTest` successful with connected device (SQLite read/write).
- [ ] Run and inspect `IsharaLifecycle` tags in Logcat while changing activities and rotating device.

## Functional demo gates

- [ ] Week 8: all four navigation buttons open their correct Activity; Back returns to home.
- [ ] Week 8: sharing a sentence opens Android's chooser without crashing.
- [ ] Week 8/10: Camera permission accepted and denied; denial does not crash the app.
- [ ] Week 9: tap a quick phrase twice, see two appended phrases, clear and type a different phrase.
- [ ] Week 9: history/saved lists scroll; text remains readable with larger system font.
- [ ] Week 10: speak a phrase; opening History shows it through ContentProvider query.
- [ ] Week 10: save favourite, retrieve it in Saved tab, delete it and confirm removal.
- [ ] Week 10: battery percentage appears when Settings opens and changes via BroadcastReceiver.
- [ ] Week 11: offline phrase builder and history still work with Airplane Mode enabled.
- [ ] Week 11: API button returns valid JSON title when internet is working; no ANR on timeout.
- [ ] Week 11: Firebase disabled shows a clear explanation, not an exception.
- [ ] Week 11: with configured Firebase, explicitly sync one test message and inspect the secured database user path.
- [ ] Open Gesture screen on a fresh installation: test in-app model download, progress and `AI READY` status.
- [ ] Block internet and confirm Retry appears; reconnect and retry.
- [ ] Gesture: model available; test each of seven gestures under clear light on a **real camera**.
- [ ] Context switch Everyday / Classroom / Travel changes demo phrase, not recognized handshape.
- [ ] Auto-add only records accepted stable gesture, and Undo removes an entire phrase.
- [ ] Scanning Pause and Resume work.
- [ ] Emulator "emulated" scene is not mistaken for a real hand feed; test Webcam0 or a physical phone.
- [ ] Gesture: no hand in view should not invent new words; unsupported gestures must be ignored.
- [ ] Gesture: no repeated message on every video frame while hand remains held.
- [ ] Gesture: rotate the handset, flip camera, leave app and return without crashes.
- [ ] TTS: Bangla engine available; speak user-typed Bangla text aloud.
- [ ] TTS: if Bangla voice unavailable, app warns instead of silently claiming success.

## Risks to discuss in viva

1. Incorrect classification from different angles, occlusion, varying skin/background or low light.
2. Not a verified language translator; custom mapping only, no model trained on Bengali Sign Language.
3. Speech support depends on Android TTS engine and installed Bangla voice.
4. Small screen and TalkBack accessibility require additional user testing.
5. Firebase data security requires private rules; anonymous accounts are for testing.
6. Network may be unavailable; users must still have offline communication.

## Minimum measured quality evidence

For every supported gesture, record: gesture name, test attempts (suggest 20 each), correct predictions, unknown/no predictions, wrong predictions, and ambient conditions. Compute accuracy as correct / total attempts. Include actual results, not invented measurements.
