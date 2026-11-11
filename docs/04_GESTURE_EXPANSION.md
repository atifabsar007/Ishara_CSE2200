# Gesture Expansion: Honest Architecture and Future ML Work

## Current recognizer

Google MediaPipe's supplied Gesture Recognizer understands seven built-in **general handshapes**: `Closed_Fist`, `Open_Palm`, `Pointing_Up`, `Thumb_Down`, `Thumb_Up`, `Victory`, and `ILoveYou`. `None`/Unknown is not an eighth useful gesture. These labels are handshapes, not Bangla Sign Language words. The code maps each handshape to one phrase per selected context (Everyday, Classroom, Travel, Home, Healthcare).

**Thus:** seven detectable physical handshapes × five user-selectable contexts = up to 35 *phrase shortcuts*. **This is not 35 physical gestures, a sign language translator, or a trained Bangla dictionary.**

## How to extend physical gestures properly (future phase)

1. Consult local sign-language users and interpreters rather than assigning meaning to arbitrary handshapes. Create an opt-in concept list and permission/consent plan.
2. Choose 10–15 distinct candidate classes. Check whether single-hand landmark features can distinguish them or if motion/two hands/face features are required.
3. Record diverse, consented, labeled examples from multiple participants, lighting levels and devices; protect and delete raw video where possible.
4. Split by participant (not random frames only) into train, validation and test sets to avoid data leakage.
5. Train a compatible custom MediaPipe gesture model or a separate TensorFlow Lite landmark classifier with a reproducible Python training workflow. Java stays the Android app language; model training is a separate ML pipeline.
6. Integrate model file as an asset and parse labels in Java. Keep `UNKNOWN` and low confidence states, temporal smoothing, deduplication and cancel/undo gestures.
7. Report confusion matrix, precision, recall and latency on held-out users. Do not conflate confidence with accuracy.
8. Add visual sign tutorials and meaningful words to Ishara only after user testing.

## Java software design for extended classifier

- `GestureRecognizer` model adapter: translates model results into label+score.
- `GestureDictionary`: maps label + mode to words or phrases.
- `TemporalStabilizer`: requires repeated confident frames and neutral-reset before accepting a new token.
- `SentenceComposer`: appends tokens with clear spacing and Undo.
- `IsharaDataStore`: logs practice records with ground-truth expected label.

## Important user test cases

- One hand outside frame -> no detection.
- Same gesture held for 2 seconds -> one token, not 12 duplicates.
- Right and left hand / skin color / distance / cluttered background -> record how recognition varies.
- Gesture held with low confidence -> visible uncertainty, never silently invent a word.
- No network -> previously cached model works; no model -> useful explanation.

References: https://developers.google.com/edge/mediapipe/solutions/vision/gesture_recognizer and https://developers.google.com/edge/mediapipe/solutions/vision/gesture_recognizer/android
