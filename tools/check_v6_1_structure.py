from pathlib import Path
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
checks = {
    "Firebase Auth": root / "app/src/main/java/com/kuet/ishara/cloud/FirebaseServices.java",
    "WorkManager worker": root / "app/src/main/java/com/kuet/ishara/cloud/CloudProgressSyncWorker.java",
    "Realtime room": root / "app/src/main/java/com/kuet/ishara/ui/PeerActivity.java",
    "Media ContentResolver": root / "app/src/main/java/com/kuet/ishara/ui/BoardActivity.java",
}
missing = [name for name, path in checks.items() if not path.exists()]
if missing:
    raise SystemExit("Missing implementation: " + ", ".join(missing))

gradle = (root / "app/build.gradle").read_text()
for dep in ("firebase-auth", "firebase-database", "firebase-firestore", "work-runtime"):
    if dep not in gradle:
        raise SystemExit("Missing dependency: " + dep)

manifest = ET.parse(root / "app/src/main/AndroidManifest.xml").getroot()
permissions = {node.attrib.get("{http://schemas.android.com/apk/res/android}name") for node in manifest.findall("uses-permission")}
for permission in ("android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"):
    if permission not in permissions:
        raise SystemExit("Missing permission: " + permission)

for name, path in checks.items():
    source = path.read_text()
    if not source.strip():
        raise SystemExit("Empty source: " + name)

print("PASS: Firebase Authentication, Realtime Database, Firestore, WorkManager, Photo Picker/ContentResolver and required permissions are present.")
print("NOTE: This is structural validation only. Firebase Console setup, Android build and device tests are still required.")
