# Ishara 2.0 — changes from the first CSE 2200 prototype

1. First-use model download directly inside the camera screen from Google's official MediaPipe model URL. Model cached privately after download; errors show a retry control. Manual model installation remains supported via macOS helper script.
2. Redesigned home and camera screens: polished teal UI, high-contrast camera area, live recognition status, confidence bars, and clearer communication controls.
3. MediaPipe `RunningMode.VIDEO` with consecutive-frame stability gating, confidence filtering and camera frame throttling for faster tracking on supported devices.
4. 7 supported general handshapes mapped into three different educational demo scenarios: Everyday, Classroom and Travel. These are NOT linguistic sign-language translations.
5. Auto-add toggle for composing sentences by moving between recognized gestures; whole-phrase Undo, Clear, Speak, Save, Share.
6. Front/rear lens switch, scanning pause/resume, preserved sentence state on rotation.
7. Updated macOS upgrade guide and test plan.

**Compatibility:** Java + Android XML, same project application ID (`com.kuet.ishara`), Gradle plugin/dependency list unchanged. To preserve the working Gradle wrapper from your earlier install, replace **only** the `app/` directory in your existing project rather than its entire root folder.

**Validation limits:** XML parse, view-binding ID consistency and pure-Java rules passed. Android Gradle build and device inference could not be executed without an Android SDK and model download in the authoring environment. **Please test before your lab demo.**
