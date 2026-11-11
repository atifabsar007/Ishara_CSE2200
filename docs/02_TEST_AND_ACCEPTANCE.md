# Acceptance Test Plan & Regression Checklist

Status field is intentionally **UNTESTED** until verified by a human on a real phone. Do not label the result as passed based on code presence.

| ID | Module | Steps | Expected output | Status |
|---|---|---|---|---|
| B01 | Android build | `./gradlew :app:assembleDebug` | BUILD SUCCESSFUL + debug APK | UNTESTED |
| B02 | Installation | Install on Android 11+ and Android API 24 device/emulator | Opens without crash | UNTESTED |
| U01 | First launch | Clear app data and open | Brand logo, mission, audience, developer; Get Started works | UNTESTED |
| U02 | Relaunch | Close and relaunch after Get Started | Main dashboard (no repeating onboarding) | UNTESTED |
| U03 | Attribution | Open About | Developer Md. Atif Absar and truthful capability notes | UNTESTED |
| U04 | App branding | Inspect visible screens | No CSE 2200 title or lab course badge | UNTESTED |
| C01 | Caption permissions | Tap Start; deny microphone | Helpful failure state; no crash | UNTESTED |
| C02 | Bangla recognition | Start; speak a few short Bangla lines on working ASR phone | Partial and final speech text shown | UNTESTED |
| C03 | English recognition | Change language to English; speak sentence | New language recognized where ASR available | UNTESTED |
| C04 | Segments | Read for 90 seconds with pauses | Multiple finalized segments append in order | UNTESTED |
| C05 | Caption save | Give session title and tap Save | Persisted in `ishara_v3.db` and legacy history | UNTESTED |
| C06 | Lifecycle | Press Home while listening | Microphone stops; no background audio capture | UNTESTED |
| C07 | No speech service | Test unsupported Android emulator | Clear unavailable status, typed fallback possible | UNTESTED |
| T01 | Two-way chat | Start listening then type and Speak reply | Incoming text bubble + spoken outgoing text | UNTESTED |
| T02 | TTS offline | Disable network, speak Bangla | Output if device language installed, else honest notice | UNTESTED |
| G01 | AI setup | Open Gesture Studio with missing model | Download/retry or error explanation, no fabricated detections | UNTESTED |
| G02 | Real camera | Show hand on real Android phone | Camera preview displays your actual hand | UNTESTED |
| G03 | Thumbs-up | Hold thumbs up steadily | Label/score shown, only once until hand removed | UNTESTED |
| G04 | Other classes | Test seven supported handshapes | Valid classes only with unknown/none handling | UNTESTED |
| G05 | Contexts | Change among 5 contexts | Different *demo* phrase mappings | UNTESTED |
| G06 | Guided practice | Select expected gesture, intentionally show right/wrong | Expected vs detected saved with match/fail | UNTESTED |
| G07 | Performance | Navigate out of camera and back | No camera lockup or runaway analyzer threads | UNTESTED |
| L01 | Practice dashboard | Perform 5 guided trials, view stats | Totals, success/fail and mean confidence update | UNTESTED |
| L02 | Stats validity | Perform 3 free detections | Marked unverified; not counted as success | UNTESTED |
| A01 | Adaptive home | Select same phrase repeatedly; return Home | Phrase appears in top shortcuts | UNTESTED |
| S01 | Word builder | Tap words, Undo, Clear, Speak, Save | Correct sentence string and controls | UNTESTED |
| P01 | Picture board | Select 3 boards, Speak | Selected phrase voiced; usage recorded | UNTESTED |
| H01 | History | Send messages / save phrase / delete | RecyclerView shows changes and database persistence | UNTESTED |
| L10 | Intent | Share caption or sentence | Android share sheet opens with expected text | UNTESTED |
| L11 | Service | Speak phrase, switch between activities | Bound TTS service works and unbinds | UNTESTED |
| L12 | Receiver | Open Settings and view battery | Updates without receiver leak or crash | UNTESTED |
| L13 | Provider | Save and inspect local message history | ContentProvider supports internal CRUD | UNTESTED |
| L14 | Networking | Tap JSON demo with internet and with airplane mode | Parses response / shows error without blocking UI | UNTESTED |
| L15 | Firebase | Only when intentionally configured + approved | One explicit action uploads only intended text | UNTESTED |
| X01 | Stress | 20 screen switches, camera toggles, microphone starts | No crash / ANR / resource leak | UNTESTED |
| X02 | Orientation | Rotate during caption/session | Does not crash; session text preserved where specified | UNTESTED |

## Report data only after actual field tests

**Gesture accuracy measurement:** For each ground-truth gesture class, collect 20–30 labeled trials (different backgrounds, lighting, hand distance, users). Record predicted class, confidence, no-detection and incorrect detection; report per-class precision/recall and confusion matrix. Current `ProgressActivity` is a **practice activity dashboard**, not a validated model-accuracy report.

**Caption quality:** Use a small authorized set of spoken sentences and manually compare reference text to final captions, with attention to Bangla accents, background noise and speaker distance. Record whether cloud/network or offline service was used. Never claim 100% accuracy.

**QA evidence to retain:** build screenshot, app version, test phone make/Android version, camera photo proof, example captions with permission, test spreadsheet, Logcat exceptions, known issues list and re-test status. These make a stronger viva submission.
