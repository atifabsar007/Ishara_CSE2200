# Ishara 3.0 — Six-to-Eight-Minute Demonstration and Viva Guide

**Opening (45s):** “Ishara means gesture, but communication is bigger than gestures. I built Ishara as a Java-based Android communication tool to help users communicate with hands, voice and text. The app is designed for users with hearing or speech difficulties, students, teachers and caregivers. Developer: Md. Atif Absar.”

**Onboarding and navigation (45s):** Show official logo, how it works and target users. Point out XML activity screens, click listeners and explicit intents. Explain why the course name is not on the consumer UI.

**Lecture mode (90s):** In Live Captions, ask a colleague to say two short sentences. Show partial text first and finalized text afterward. Save a session with a title; copy/share. Explain `SpeechRecognizer`, `RecognitionListener`, permission and lifecycle stop. Have a typed fallback if ASR unavailable.

**Two-way conversation (60s):** Ask a colleague to speak, observe a text bubble. Type a Bangla reply and tap Speak. Explain TTS bound Service, RecyclerView Adapter and Android permissions.

**Gesture / sentence (90s):** Present a real hand to the phone camera, show confidence score and context-specific phrase, add to sentence, undo, speak. Explain CameraX analyzer, MediaPipe pretrained model, frame throttling, Java collections, deduplication and model asset. State clearly that only seven *physical* classes are currently detected, not 35 signs.

**Learning + adaptive analytics (60s):** Choose a practice gesture and show right/wrong result log. Open Progress and explain practice metrics versus real-world accuracy. Tap the same quick phrase several times; return Home and show it rank higher through SQLite usage counts.

**Week 10/11 spotlight (30s):** Open Settings to show the battery BroadcastReceiver, HTTP+JSON example, optional Firebase. Show saved messages via ContentProvider + SQLite.

**Close (20s):** “My next step is real field testing with users and a custom, consented gesture dataset. The current prototype works as a Java laboratory architecture and avoids making unsupported sign-language translation claims.”

## Common viva questions

1. **Why Java?** Java OOP, event listeners, RecyclerView adapters, SDK callbacks, SQLite, services and exception handling align directly with the lab.
2. **What is an Activity?** Android screen component with lifecycle hooks. Compare `onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy` using Logcat.
3. **Why RecyclerView?** Efficiently reuses ViewHolders for changing conversation/gesture lists.
4. **Why a service for TTS?** It centralizes TextToSpeech engine state across screens; SpeechService is bound while needed.
5. **What is a BroadcastReceiver?** Callback reacting to Android broadcasts such as battery state while registered.
6. **Why ContentProvider?** Standard controlled interface for accessing message storage; ours is `exported=false`.
7. **Why SQLite?** Efficient local structured persistence and deterministic queries without internet.
8. **How does live captioning work?** `SpeechRecognizer` gives partial/final callbacks from an installed ASR service, often in limited-duration segments; app restarts segments while visible.
9. **Does Ishara translate Bangla Sign Language?** No. It maps seven general recognized handshapes to sample messages; custom sign model is future research.
10. **What does confidence mean?** Model score for its predicted class, not proven overall accuracy. Guided practice compares against known expected class.
11. **What if network is off?** Phrase boards, SQLite and typed composition still work. Recognition model works once cached. Speech recognition/TTS depend on installed services and languages.
12. **What was hardest?** Lifecycle-safe camera and microphone use, resource constraints, permission handling, meaningful analytics and avoiding misleading recognition claims.
