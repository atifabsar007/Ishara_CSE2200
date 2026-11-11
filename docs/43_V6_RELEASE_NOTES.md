# Release notes 6.0

Source base: Ishara 5.0, preserving all earlier Java/XML modules.
New files: CalibrationActivity, GestureCalibration, GestureFeedbackDb, DictionaryActivity + Adapter, HandLandmarkOverlay, SentenceRules, XML layouts, illustrated assets, report and tests.
Changed files: GestureActivity, SentenceActivity, activity_gesture.xml, activity_sentence.xml, MainActivity, activity_main.xml, Manifest, app/build.gradle, README.
QA: XML/viewBinding validation, pure-Java logic smoke test, PDF render inspection, ZIP integrity and patch dry-run. Full Android build is pending Mac SDK.
Limitations: seven general stock gestures; hand overlay approximation over cropped previews; feedback logs do not train the AI; local link is unencrypted demonstration.
