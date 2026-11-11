#!/bin/bash
# Optional manual offline-install path. The app also downloads this model on first use.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MODEL="$ROOT/app/src/main/assets/gesture_recognizer.task"
URL="https://storage.googleapis.com/mediapipe-models/gesture_recognizer/gesture_recognizer/float16/1/gesture_recognizer.task"
mkdir -p "$(dirname "$MODEL")"
echo 'Downloading official MediaPipe task model...'
curl --fail --location --retry 3 --connect-timeout 15 --output "$MODEL.partial" "$URL"
BYTES=$(wc -c < "$MODEL.partial" | tr -d ' ')
if [ "$BYTES" -lt 100000 ]; then rm -f "$MODEL.partial"; echo 'Model file too small' >&2; exit 1; fi
mv "$MODEL.partial" "$MODEL"
echo "Installed $MODEL ($BYTES bytes). Now rebuild Ishara in Android Studio."
