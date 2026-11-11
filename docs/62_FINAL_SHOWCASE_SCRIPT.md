# Ishara — Five-Minute Video and Live Showcase Script

**Project title:** ISHARA – Sign-to-Speech Communication  
**Target length:** 4:50–5:00  
**Recording:** screen capture with clear narration; show the actual phone UI and Firebase Console evidence. Do not use mock records.

## 0:00–0:25 — Introduction

"Assalamu Alaikum. My project is Ishara, a Java-based Android communication support application. Its main purpose is to support communication through camera-based handshape recognition, readable captions and speech output. The two main modules are Gesture Studio and Live Conversation Captions."

## 0:25–1:15 — Gesture Studio

Open Gesture Studio. Show the live camera, model readiness, one supported handshape, detected label, confidence, phrase mapping and sentence composition. Add a phrase, undo it, then speak it.

Say: "CameraX supplies frames and MediaPipe classifies a limited set of general handshapes. Java handles confidence checks, stable detection, context-based phrase mapping, sentence building, history and TextToSpeech. This is a gesture demonstration prototype, not a complete Bangla Sign Language translator."

## 1:15–1:55 — Live Captions

Open Live Captions. Choose a supported language, start the microphone, speak one sentence, show partial/final captions, save and copy/share.

Say: "Android SpeechRecognizer converts speech into text. Java manages the microphone permission, recognition callbacks, transcript, SQLite save and share actions. Recognition depends on the phone's installed speech service and language support."

## 1:55–2:30 — Play, Learn and Connect

Show Gesture Dictionary or Ishara Quest briefly, then open Connect. Create a room and send one short message. If possible, show the second device receiving it live.

Say: "The supporting Play and Learn tools provide gesture practice. Connect uses Firebase Realtime Database for room-based live messages."

## 2:30–3:35 — Week 10 Android components

Open Project Lab & System Tools. Show the battery event, toggle Airplane mode and show the status change. Open Picture Board, select an image from Photo Picker, show it in the app. Return to Lab Showcase and show the bound SpeechService and internal SQLite/ContentProvider CRUD example.

Say: "I use a bound Android Service for TextToSpeech, a BroadcastReceiver for system battery and airplane-mode events, and ContentResolver to read a selected image from Android's media content provider. The selected picture is not uploaded."

## 3:35–4:35 — Week 11 API, JSON, SQLite and Firebase

Run the HTTPS JSON request. Show parsed fields. Open Firebase Console briefly: Authentication user, Firestore profile and progress summary, Realtime Database room message. Show progress sync queued/completed.

Say: "The app demonstrates HTTP GET and JSON parsing on a background executor, SQLite CRUD, WorkManager for user-triggered background progress sync, Firebase Authentication, Realtime Database and Cloud Firestore."

## 4:35–5:00 — Conclusion

Return to home screen.

Say: "Ishara combines camera-based gesture communication and live captions with Java-driven Android components and Firebase services. Thank you."

## Recording checklist

- Use a real phone for gesture recognition and captions if possible.
- Keep each feature demonstration short; do not spend time scrolling through every screen.
- Do not claim accuracy percentages unless you have measured them with a labeled test set.
- Show actual Firebase Console data, not a static screenshot alone.
- Use a public/unlisted video link only if the teacher can open it without requesting access.
