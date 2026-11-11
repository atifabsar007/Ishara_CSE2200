# Ishara 5.0 Quality and Regression Matrix

These are *required device tests*, not claims that they have all been run.

| ID | Test | Expected outcome |
|---|---|---|
| H01 | Open app after account login | Gesture camera first; captions second; quest third |
| H02 | Tap each new home card | Peer Link, Everyday Simulator, Inside Ishara open |
| P01 | Pair on same Wi-Fi with correct code | Both screens report Connected |
| P02 | Wrong code | Host rejects, other phone reports pairing rejected |
| P03 | Send Bangla/English text | Correct UTF-8 content appears on receiving phone |
| P04 | 401-character message | Rejected before send |
| P05 | Stop and exit Peer Link | Socket resources close; reconnect via new session |
| P06 | Start with public IP | Rejected; no external socket attempt |
| P07 | Latest saved phrase → send | Draft populates, transmits and appears on peer |
| P08 | Gesture Studio → Peer Link | Composed phrase is passed to Peer Link through Intent |
| S01 | Start each of four scenarios | Appropriate first question and choices appear |
| S02 | Explore every branch | No invalid or missing next-node references |
| S03 | Complete / restart scenario | Progress persisted; current score reset on restart |
| S04 | Speech / share latest response | Existing bound TTS/implicit Intent work |
| L01 | Exit/return lifecycle screen | Activity callbacks visible in log and Logcat |
| L02 | SQLite Create/Read/Update/Delete | Operations affect demo table, not real history |
| L03 | Parse bundled JSON | Bangla and English messages are parsed correctly |
| L04 | Battery broadcast | Battery percentage or graceful unavailable response |
| L05 | TTS, implicit share, peer navigation | Each button performs advertised action |
| R01 | Original Gesture Studio | Camera available on real phone; support seven pretrained handshapes |
| R02 | Existing Live Captions | With supported speech service + microphone permission, transcripts appear |
| R03 | Login, local SQLite history, Quest | No regression or data loss |
| A01 | TalkBack and font scaling | Accessible labels, readable content, no clipping |

Static validation: `python3 tools/check_source.py`, `bash tools/check_v5_logic.sh`; Android build: `./gradlew :app:assembleDebug :app:testDebugUnitTest` in configured Android Studio environment.
