# Ishara 6.0 | Mac Upgrade and Build

## Recommended patch method
1. Keep a backup of your working `~/Desktop/Java Android Project/Ishara_CSE2200` folder.
2. Download and unzip the Ishara 6.0 Mac Upgrade Patch.
3. Close Android Studio and run `bash apply_patch_macos.sh` from the extracted patch folder.
4. The script saves previous versions of replaced files under a timestamped backup folder. It does not delete the Android model file or Gradle wrapper scripts.
5. Reopen Ishara, select Gradle JDK 21, Sync, `Build > Rebuild Project`, then Run on a real Android phone.

## New activities
- **Dictionary**: search handshapes and practice each with the camera.
- **Calibration**: customize confidence, reset, and save explicit correction labels.
- **Gesture Studio**: optional 21-point overlay, camera practice, and rule-based sentence suggestions.

## Important
- MediaPipe still needs `gesture_recognizer.task` in `app/src/main/assets/`, or use Ishara's existing first-use model download. Google pretrained model detects only 7 general handshapes.
- For the emulator camera, set Webcam0 in AVD configuration, or use a real Android phone. The emulator's virtual scene is not a camera feed of your real hand.
- For emulator live captions: Extended Controls > Microphone > enable host microphone access; allow mic permissions.
- V6 output has been statically validated; it has NOT been compiled by the complete Android SDK or device-tested here.
