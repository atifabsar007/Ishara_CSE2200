# Ishara 6.0.1 — Mobile readability correction

## Root cause
Earlier versions combined `Theme.MaterialComponents.DayNight.NoActionBar` with permanently light (white/mint) custom card backgrounds. On phones running dark mode, inherited text/placeholder/spinner colors could become white or near-white and disappear against these backgrounds. Some individual views also omitted an explicit hint color.

## Changes
- Unified **Light** MaterialComponents theme with `colorOnSurface`, `textColorPrimary`, `textColorSecondary`, and dark input foregrounds.
- Disabled Android forced darkening to preserve the branded teal/white palette.
- Set status bar icon mode for a light background.
- Explicit text, hint, floating-label, outline and helper colors for all TextInputLayout / TextInputEditText fields.
- Explicit dark foreground and visible hint colors on ordinary EditText and TextView hints.
- Dark-teal text in custom spinner choices and selected items, including Bangla/English captions settings, gesture context, calibration and simulator.
- Darkened secondary (`muted`) text so it remains legible on white and mint.
- Two-way chat history now wraps its messages rather than leaving a fixed 300dp blank region.
- Preserved gestures, captions, SQLite data structures, login behavior, games, network Peer Link and developer tools.

## Verification on your real phone
Test BOTH phone appearance modes: Settings → Display → Light / Dark (brand remains Light by design). Check:
1. Log in: Username, Password, show/hide typing, hint and errors readable.
2. Register: Name, Username, Password, Confirm password all visible.
3. Gesture Studio: phrase and **Your message** visible before and after composition, with readable placeholder when empty.
4. Two-way Conversation: language spinner, transcription text, reply textbox and messages readable.
5. Live Captions: language selected item/dropdown, title and caption text readable.
6. Quick phrases: message entry text and hint readable.
7. Visual gesture dictionary search / gesture context / calibration spinner options readable.
8. Peer Link IP, code and message fields readable.
9. Ishara Quest, sentence building and remaining home-screen controls readable.
10. Check at small screen width and Android font size 100% and 130%; scrollable screens should remain navigable.

## Mac upgrade (source overwrite only)
Use the separate patch archive. It keeps Gradle wrapper, `gesture_recognizer.task`, and unrelated source assets intact. **Back up your working project** before applying any patch.

### Installation
```
cd /path/to/extracted/Ishara_6_0_1_Mac_UI_Patch
bash apply_patch_macos.sh
```
Alternatively: `bash apply_patch_macos.sh "$HOME/Desktop/Java Android Project/Ishara_CSE2200"`

Then Android Studio → Sync → Build → Rebuild Project → Run. If an old app is already installed, Run normally upgrades it in place; avoid clearing app storage if you want to preserve local accounts and history.

## Scope and limits
This ZIP contains Android Studio source, not an APK. These static checks do not replace a real Android build and UI test on your phone. The UI is built for Android phones and tablet-size emulator windows, **not** as a native Mac/Windows desktop app. The standard MediaPipe gesture model has its original limited classes.
