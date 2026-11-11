# Beyond Prototype: Planned Improvements (NOT claimed as implemented)

**Round A: Stabilization** — compile on Mac, fix build/manifest issues, test on real Android phone, turn test matrix from UNTESTED into actual PASS/FAIL with logs.

**Round B: Lecture productivity** — transcript list UI, searchable captions, edit wrongly recognized segments, timestamps, mark important lines, speaker labels (only if actually implemented and tested), export as text with SAF/file provider.

**Round C: More real gestures** — consult users, collect consented gesture samples, train custom model, package labels into Java, create held-out benchmark. Do not imply that multiple context mappings are multiple learned signs.

**Round D: Personalization** — context shortcuts by profile, user-created phrase board, sentence grammar templates, option to pin frequent phrases, delete history and consent controls.

**Round E: Accessibility QA** — TalkBack audit, color contrast review, font scaling, one-handed support, device language availability, support for multiple Android API levels.

**Round F: Performance/quality** — baseline detection FPS/latency on budget phone, memory-leak inspection, SQLite queries on dedicated executor, rotation/resume state testing, battery comparisons.

**Round G: Release** — remove unused provider/network features, data retention controls, privacy disclosure of third-party speech services, model license review, signed APK and store compliance. No release without consent checks and real tester feedback.
