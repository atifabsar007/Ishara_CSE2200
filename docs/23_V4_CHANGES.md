# Ishara change log, version 4.0.0

## Added
* Local sign-up/login and continuing as guest, login form validation, salted PBKDF2 password verifiers, 60-second screen-level cooldown after five failures.
* Session persistence, account switch/logout, user-specific local SQLite filenames for messages, captions, phrases and practice, preserving legacy guest data.
* Ishara Quest mini-games, sentence tiles, language/meaning matching, persistent scoring and badge milestones.
* A redesigned branded welcome and home experience with the creator name Md. Atif Absar.
* Master Blueprint, installation guide and acceptance checklist.

## Preserved
* CaptionController microphone diagnostics, existing Java SpeechRecognizer, CameraX/MediaPipe model loader, TTS, SQLite history, ContentProvider, BatteryReceiver, networking, Firebase demo, lesson and practice features.
* Original Ishara logo in `drawable-nodpi` and branding folder.

## Deferred / limitations
* Cloud login, password reset, remote database and account sharing are **not** included.
* Additional physical hand gestures require a tested custom model. Ishara Quest meanings are custom demonstration mappings.
* Android runtime and emulator/phone verification still required.

## Version 4.0.1: Quest compile correction

- `QuestActivity.java`, line 29: replaced nonexistent `MaterialButton.setTextAllCaps(false)` with `MaterialButton.setAllCaps(false)`.
- App version changed to `4.0.1` (`versionCode 6`) to distinguish the corrected build.
- No account, caption, game, or database schema changes. Existing application data is intended to remain intact when updating the same application ID. Always keep a backup before changing the project.
- Source sanity checks are not a replacement for the Android Gradle build and emulator/phone tests.
