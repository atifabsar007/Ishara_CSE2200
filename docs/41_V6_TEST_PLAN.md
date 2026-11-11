# Ishara 6.0 | Manual Acceptance Tests
1. Open Ishara and check first three home cards: Gesture Studio, Live Captions, Quest.
2. Open Dictionary; all seven cards show gesture illustrations and correct demo Bangla meanings. Type English and Bangla search terms; tap each card to open practice Camera.
3. Use Device Manager camera/real Android phone and load MediaPipe model. Show one gesture for at least three sampled frames. Change threshold in Calibration and check saved values on return.
4. Show known gesture; tap Calibration from Camera; select corrected label and save. Check feedback count increments after app restart.
5. Check skeleton overlay draws 21 points with connections. Toggle it off and on. Confirm camera preview remains smooth on real device. Alignment may vary with device orientation.
6. In Sentence Builder choose `আমি`, `পানি`, `চাই`; tap Suggest; expect `আমি পানি চাই।`. Tap Use and Speak. Test Undo and Clear.
7. Test Live Captions with mic access enabled. For emulator, use Extended Controls > Microphone > Enable host microphone access.
8. Verify existing Quest, Simulator, Peer Link, JSON, History, login and logout.
9. Verify offline startup except one-time AI model download; do not claim online authentication.
10. Check font size, landscape behavior, permission denial, and no camera resource leak on exit.

## Build on Mac
- Keep your working Gradle wrapper scripts and JAR from the previous local installation.
- Android Studio: Open project root, Gradle JDK 21, SDK 35, Sync, Build > Rebuild Project.
- No APK or full Android build has been verified in this environment.
