# Ishara 6.0 | Master Development Blueprint

**Developer:** Md. Atif Absar | **Product:** Ishara | **Stack:** Java 17, XML, Android SDK 35, CameraX, MediaPipe, SQLite

## Main mission
Gesture Camera → supported handshape → Bangla demo meaning → suggested sentence → speech. Live captions and Ishara Quest remain supporting home-screen features (second and third respectively).

## New v6 features implemented
1. Visual Dictionary: searchable seven demo handshapes. Illustrated emoji pictograms; not a verified sign-language database. Tap any card to open camera practice.
2. Personal Calibration: save per-handshape confidence thresholds between 50-95%; defaults to 65%; resets supported. Threshold personalization does not retrain model.
3. Recognition correction: explicitly log predicted vs. corrected class and confidence to account-scoped SQLite feedback database. Errors are kept locally; no model-learning claim.
4. Hand overlay: draws 21 MediaPipe landmarks and skeleton connections over CameraX preview using a custom Java View. In fill-center and rotated scenes positions may be approximate; verify on physical phone.
5. Java sentence suggestions: deterministic templates and punctuation, user-confirmed and fully editable; no invented live translation.

## Preserved v5 modules
- Welcome, login/register/guest, adaptive dashboard, Gesture Studio, speech output, Sentence Builder, Quick Phrases, Live Captions, Two-way Conversation, History, Picture Board, Quest, Practice, Accuracy Dashboard, Peer Link (unencrypted same-Wi-Fi demo), Real-Life Simulator, Inside Ishara, Firebase sample and HTTP/JSON exercise.

## Lab syllabus evidence
- Week 8: ViewBinding and XML layouts, explicit intents among Dictionary / Calibration / Gesture, lifecycle and click listeners.
- Week 9: RecyclerView DictionaryAdapter + ViewHolder, `HandLandmarkOverlay` Canvas drawing, search UX, Material cards.
- Week 10: CAMERA permission and existing SpeechService, BatteryReceiver, MessageProvider, speech/mic permissions.
- Week 11: account-scoped SQLite corrections, existing HTTP JSON, background model download and optional Firebase sample.
- Java: OOP, custom classes, arrays, List, Map, String processing, validation, event listeners, inheritance, background executors.

## Data handling and limitations
- Existing databases retained without destructive migrations. New `gesture_feedback_v6{session}.db` stores correction events separately.
- MediaPipe bundled pretrained model recognizes only seven general handshapes. Custom phrase mappings are NOT official Bangla Sign Language; no additional gesture-class recognizer is provided.
- The model asset is not distributed here; use the in-app one-time download or `tools/install_model_macos.sh`.
- Face-to-face, webcam, and live captions require working device camera/mic and installed recognition services.
- Offline Peer Link is a local prototype; messages are not encrypted. Do not use for sensitive communication.
- Both calibration and feedback are *honest* threshold tuning/logging, not model retraining.

## Future extension
- Research ethical consent-based Bangla Sign Language gesture corpus with Deaf community signers; train/evaluate custom classes, report precision/recall/confusion. Never claim official translation based on arbitrary general handshapes.

## Package verification
- XML structure + ViewBinding source checks + Java-only logic tests, zip archive integrity. Full Android Gradle build and hardware QA pending.
