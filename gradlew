#!/usr/bin/env sh
# Lightweight fallback for this source archive, which does not bundle the
# binary Gradle wrapper JAR. Use a local Gradle 8.11.1 installation.
set -eu
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle 8.11.1 is not installed. Install it or configure Android Studio to use a local Gradle 8.11.1 distribution." >&2
  exit 1
fi
if ! gradle --version 2>/dev/null | grep -q 'Gradle 8.11.1'; then
  echo "This project is pinned to Gradle 8.11.1. Your 'gradle' command is a different version." >&2
  exit 1
fi
exec gradle "$@"
