# Firebase setup for Ishara 6.1.0

Firebase is part of the required project implementation. The Java source includes:

- **Firebase Authentication:** email/password registration and login; anonymous sign-in is used only for guest access to a live room.
- **Realtime Database:** room-based live messages in `rooms/{roomCode}/messages`.
- **Cloud Firestore:** account profile documents at `users/{uid}` and optional aggregate learning progress at `users/{uid}/progress/summary`.

No Firebase Storage is used. The Picture Board reads a selected photo through an Android content URI and does not upload or permanently store it. Therefore there is no picture upload or Storage dependency to demonstrate.

## 1. Create and register the Android app

1. Open the Firebase Console and create a project.
2. Add an Android app with the exact package name `com.kuet.ishara`.
3. In **Authentication → Sign-in method**, enable **Email/Password** and **Anonymous**. Email/Password is used for accounts; Anonymous is used for the guest live-room demonstration.
4. Create a **Realtime Database** instance.
5. Create the default **Cloud Firestore** database.
6. Copy the Android app's Firebase App ID, the project's Web API key, Project ID and Realtime Database URL.

The project initializes a named FirebaseApp in Java. It does not require the Google Services Gradle plugin or `google-services.json`.

## 2. Add the Firebase app values

Open `app/src/main/res/values/firebase_config.xml` and replace the empty values:

```xml
<resources>
    <string name="firebase_app_id" translatable="false">YOUR_FIREBASE_APP_ID</string>
    <string name="firebase_api_key" translatable="false">YOUR_WEB_API_KEY</string>
    <string name="firebase_project_id" translatable="false">YOUR_PROJECT_ID</string>
    <string name="firebase_database_url" translatable="false">YOUR_REALTIME_DATABASE_URL</string>
</resources>
```

These are client configuration identifiers, not service-account credentials. Never put a Firebase Admin SDK private key or service-account JSON in an Android app or GitHub repository. Database security rules, not hidden client strings, protect user data.

## 3. Realtime Database rules

Use authenticated access and ensure a user can only write a message using their own Firebase UID:

```json
{
  "rules": {
    "rooms": {
      "$roomCode": {
        ".read": "auth != null",
        "messages": {
          "$messageId": {
            ".write": "auth != null && newData.child('uid').val() === auth.uid",
            ".validate": "newData.hasChildren(['uid', 'displayName', 'text', 'createdAt']) && newData.child('text').isString() && newData.child('text').val().length > 0 && newData.child('text').val().length <= 400 && newData.child('uid').val() === auth.uid"
          }
        }
      }
    }
  }
}
```

For a class demonstration, anyone who knows a room code and is authenticated can read that room. Do not use room codes as a substitute for private access control. Do not send sensitive information. This prototype is not end-to-end encrypted.

## 4. Cloud Firestore rules

```text
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
      match /progress/{documentId} {
        allow read, write: if request.auth != null && request.auth.uid == uid;
      }
    }
  }
}
```

A successful email/password registration or login writes a profile document. The Practice Progress screen has a separate **Sync aggregate progress to Firebase** action. It queues a Java WorkManager worker that uploads counts and average model confidence only. It does not upload captions, messages, camera frames or selected pictures.

## 5. Run and verify

1. Sync Gradle and build the app.
2. Open Ishara → Register and create an email/password account.
3. Confirm the user appears in **Authentication → Users**.
4. Confirm `users/{uid}` appears in **Firestore → Data**.
5. Open **Connect → Live room**, create a room, share the room code, then join from a second device or emulator.
6. Send a short test message and verify it appears under `rooms/{roomCode}/messages` in Realtime Database and on the second client.
7. Complete a few guided gesture practices and Quest activities. Open **Practice progress & cloud sync**, tap the sync button and verify `users/{uid}/progress/summary` in Firestore.
8. In **Project Lab & System Tools**, test battery and airplane-mode broadcasts, then run the HTTPS JSON request.
9. In **Picture Board**, choose a photo. The app reads its content URI with `ContentResolver`; the photo remains local and is not uploaded.

## 6. Evidence to capture for the report

- Firebase Authentication user list after registering.
- Firestore `users/{uid}` profile document.
- Firestore `users/{uid}/progress/summary` after the user-triggered sync.
- Realtime Database room messages after sending a message from one device.
- Ishara screen showing the same live message on the other device.
- Picture Board after choosing a photo, plus the displayed content-provider read status.

If a Firebase operation fails, first check that the provider is enabled, the app values match the Firebase project, the rules are published, the device has internet and the correct database URL was copied.
