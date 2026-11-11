# Open Issues, Known Constraints and Risk Register

| Priority | Issue | Impact | Mitigation |
|---|---|---|---|
| HIGH | Android build not executed here | Compile/runtime issues may remain | Run Gradle build on Mac; fix first error |
| HIGH | Real camera gesture testing incomplete | Demo may show emulator VirtualScene | Use physical phone and test 7 gestures |
| HIGH | Android SpeechRecognizer variability | Live captions may stop / lag / fail | Segment retry, fallback to typing, session save |
| MEDIUM | Bengali ASR/TTS availability varies | Some devices cannot caption/speak Bangla | Language selection, explicit user notification |
| MEDIUM | Long lecture sessions / partial results | Repeated or missing lines | Human proofread, periodic save, editable transcript future |
| MEDIUM | SQLite UI-thread use in simple prototype | May stall for large datasets | Move DAO queries to ExecutorService before production |
| MEDIUM | Recognition stats are not true accuracy | Misleading claims risk | Guided-ground-truth validation in future |
| MEDIUM | Same seven physical signs despite context expansion | User may expect true sign language | Clear limitations + future custom model roadmap |
| LOW | Gradle wrapper binary not packaged | User needs repaired Mac wrapper files | Copy from already trusted working source; verify checksum |
| LOW | Firebase credentials not configured | Cloud backup optional/inactive | Follow original `FIREBASE_SETUP.md` |
| LOW | Old V2 optional features may not be regression-tested | Hidden bugs | Full lab test checklist |
