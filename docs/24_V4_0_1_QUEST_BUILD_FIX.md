# Ishara 4.0.1: Quest build correction

## Reported Android Studio failure

`app/src/main/java/com/kuet/ishara/ui/QuestActivity.java:29`

`error: cannot find symbol method setTextAllCaps(boolean)`

## Fix in this release

**Wrong**: `button.setTextAllCaps(false);`

**Correct**: `button.setAllCaps(false);`

`MaterialButton` inherits the correct capitalization setter. The invalid method has been removed from all source copies packaged in this release.

## Android Studio verification

1. Back up the working project and apply the corrected patch, or open the full source ZIP.
2. **Sync Project with Gradle Files**.
3. **Build > Rebuild Project**. There should be no `setTextAllCaps(boolean)` compilation error.
4. Run Ishara. Test Welcome -> Register -> Login and Ishara Quest -> Meaning Match and Sentence Quest.
5. Test Live Captions to ensure microphone and recognizer continue working.

**Honest validation**: Source checks and standalone Java tests were run during packaging. An actual Gradle Android build and a device test could not be run here, so any further compile errors must be addressed from Android Studio Build Output.
