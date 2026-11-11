
package com.kuet.ishara.cloud;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class FirebaseServices {

    private FirebaseServices() {
    }

    public interface AuthCallback {
        void onComplete(
                boolean success,
                String uid,
                String displayName,
                String message
        );
    }

    public interface RoomAuthCallback {
        void onReady(boolean success, String uid, String message);
    }

    // ------------------------------------------------
    // FIREBASE INITIALIZATION
    // ------------------------------------------------

    public static FirebaseApp app(Context context) {

        synchronized (FirebaseServices.class) {

            try {
                return FirebaseApp.getInstance();

            } catch (IllegalStateException exception) {

                FirebaseApp initialized = FirebaseApp.initializeApp(
                        context.getApplicationContext()
                );

                if (initialized == null) {
                    throw new IllegalStateException(
                            "Firebase initialization failed. " +
                                    "Check app/google-services.json and Gradle configuration."
                    );
                }

                return initialized;
            }
        }
    }

    // Check the default Firebase configuration.
    public static boolean isConfigured(Context context) {

        try {

            FirebaseOptions options = app(context).getOptions();

            return !TextUtils.isEmpty(options.getApplicationId())
                    && !TextUtils.isEmpty(options.getApiKey())
                    && !TextUtils.isEmpty(options.getProjectId());

        } catch (Exception exception) {

            return false;
        }
    }

    // Check whether Realtime Database is configured.
    public static boolean isRealtimeDatabaseConfigured(Context context) {

        if (!isConfigured(context)) {
            return false;
        }

        try {

            String url = app(context)
                    .getOptions()
                    .getDatabaseUrl();

            return !TextUtils.isEmpty(url);

        } catch (Exception exception) {

            return false;
        }
    }

    // ------------------------------------------------
    // FIREBASE SERVICES
    // ------------------------------------------------

    public static FirebaseAuth auth(Context context) {
        return FirebaseAuth.getInstance(app(context));
    }

    public static FirebaseFirestore firestore(Context context) {
        return FirebaseFirestore.getInstance(app(context));
    }

    public static FirebaseDatabase database(Context context) {

        FirebaseApp firebaseApp = app(context);

        String databaseUrl = firebaseApp.getOptions().getDatabaseUrl();

        if (TextUtils.isEmpty(databaseUrl)) {
            throw new IllegalStateException(
                    "Realtime Database URL is missing from Firebase configuration."
            );
        }

        return FirebaseDatabase.getInstance(firebaseApp, databaseUrl);
    }

    public static DatabaseReference roomMessages(
            Context context,
            String roomCode
    ) {

        if (TextUtils.isEmpty(roomCode)) {
            throw new IllegalArgumentException("Room code cannot be empty.");
        }

        return database(context)
                .getReference("rooms")
                .child(roomCode.trim())
                .child("messages");
    }

    // ------------------------------------------------
    // EMAIL REGISTRATION
    // ------------------------------------------------

    public static void register(
            Context context,
            String email,
            String password,
            String displayName,
            AuthCallback callback
    ) {

        try {

            auth(context)
                    .createUserWithEmailAndPassword(email, password)

                    .addOnSuccessListener(result -> {

                        FirebaseUser user = result.getUser();

                        if (user == null) {

                            callback.onComplete(
                                    false,
                                    null,
                                    null,
                                    "Firebase did not return a user."
                            );

                            return;
                        }

                        UserProfileChangeRequest request =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .build();

                        user.updateProfile(request)
                                .addOnCompleteListener(profileTask -> {

                                    writeProfile(
                                            context,
                                            user,
                                            displayName,
                                            true,
                                            callback
                                    );
                                });
                    })

                    .addOnFailureListener(error ->
                            callback.onComplete(
                                    false,
                                    null,
                                    null,
                                    friendlyError(error.getMessage())
                            )
                    );

        } catch (Exception exception) {

            callback.onComplete(
                    false,
                    null,
                    null,
                    friendlyError(exception.getMessage())
            );
        }
    }

    // ------------------------------------------------
    // EMAIL LOGIN
    // ------------------------------------------------

    public static void signIn(
            Context context,
            String email,
            String password,
            AuthCallback callback
    ) {

        try {

            auth(context)
                    .signInWithEmailAndPassword(email, password)

                    .addOnSuccessListener(result -> {

                        FirebaseUser user = result.getUser();

                        if (user == null) {

                            callback.onComplete(
                                    false,
                                    null,
                                    null,
                                    "Firebase did not return a user."
                            );

                            return;
                        }

                        String name = user.getDisplayName();

                        if (TextUtils.isEmpty(name)) {

                            String userEmail = user.getEmail();

                            name = userEmail != null && userEmail.contains("@")
                                    ? userEmail.substring(0, userEmail.indexOf("@"))
                                    : "Ishara User";
                        }

                        writeProfile(
                                context,
                                user,
                                name,
                                false,
                                callback
                        );
                    })

                    .addOnFailureListener(error ->
                            callback.onComplete(
                                    false,
                                    null,
                                    null,
                                    friendlyError(error.getMessage())
                            )
                    );

        } catch (Exception exception) {

            callback.onComplete(
                    false,
                    null,
                    null,
                    friendlyError(exception.getMessage())
            );
        }
    }

    // ------------------------------------------------
    // FIRESTORE USER PROFILE
    // ------------------------------------------------

    private static void writeProfile(
            Context context,
            FirebaseUser user,
            String name,
            boolean isNew,
            AuthCallback callback
    ) {

        String safeName = TextUtils.isEmpty(name)
                ? "Ishara User"
                : name.trim();

        long timestamp = System.currentTimeMillis();

        Map<String, Object> profile = new HashMap<>();

        profile.put("uid", user.getUid());
        profile.put("displayName", safeName);
        profile.put("email",
                user.getEmail() == null ? "" : user.getEmail());

        profile.put("updatedAt", timestamp);

        if (isNew) {
            profile.put("createdAt", timestamp);
        }

        try {

            firestore(context)
                    .collection("users")
                    .document(user.getUid())
                    .set(profile, SetOptions.merge())

                    .addOnSuccessListener(unused -> {

                        callback.onComplete(
                                true,
                                user.getUid(),
                                safeName,
                                isNew
                                        ? "Account created successfully. Firestore profile saved."
                                        : "Login successful. Firestore profile synchronized."
                        );
                    })

                    .addOnFailureListener(error -> {

                        callback.onComplete(
                                true,
                                user.getUid(),
                                safeName,
                                "Authentication successful, but Firestore profile " +
                                        "could not be saved: " + error.getMessage()
                        );
                    });

        } catch (Exception exception) {

            callback.onComplete(
                    true,
                    user.getUid(),
                    safeName,
                    "Authentication successful, but profile saving failed: "
                            + exception.getMessage()
            );
        }
    }

    // ------------------------------------------------
    // ANONYMOUS GUEST AUTHENTICATION
    // ------------------------------------------------

    public static void ensureRoomAuthentication(
            Context context,
            RoomAuthCallback callback
    ) {

        try {

            FirebaseAuth firebaseAuth = auth(context);

            FirebaseUser current = firebaseAuth.getCurrentUser();

            if (current != null) {

                callback.onReady(
                        true,
                        current.getUid(),
                        "Authenticated with Firebase."
                );

                return;
            }

            firebaseAuth.signInAnonymously()

                    .addOnSuccessListener(result -> {

                        FirebaseUser user = result.getUser();

                        if (user == null) {

                            callback.onReady(
                                    false,
                                    null,
                                    "Firebase returned no user."
                            );

                        } else {

                            callback.onReady(
                                    true,
                                    user.getUid(),
                                    "Temporary guest identity created."
                            );
                        }
                    })

                    .addOnFailureListener(error ->

                            callback.onReady(
                                    false,
                                    null,
                                    "Firebase sign-in failed: "
                                            + friendlyError(error.getMessage())
                            )
                    );

        } catch (Exception exception) {

            callback.onReady(
                    false,
                    null,
                    friendlyError(exception.getMessage())
            );
        }
    }

    // ------------------------------------------------
    // REALTIME DATABASE MESSAGING
    // ------------------------------------------------

    public static Task<Void> sendRoomMessage(
            Context context,
            String roomCode,
            String uid,
            String displayName,
            String message
    ) {

        if (TextUtils.isEmpty(uid)) {
            throw new IllegalArgumentException("User ID is required.");
        }

        if (TextUtils.isEmpty(message) || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }

        Map<String, Object> row = new HashMap<>();

        row.put("uid", uid);

        row.put(
                "displayName",
                TextUtils.isEmpty(displayName)
                        ? "Ishara User"
                        : displayName
        );

        row.put("text", message.trim());
        row.put("createdAt", System.currentTimeMillis());

        return roomMessages(context, roomCode)
                .push()
                .setValue(row);
    }

    // ------------------------------------------------
    // SIGN OUT
    // ------------------------------------------------

    public static void signOut(Context context) {

        try {

            if (isConfigured(context)) {
                auth(context).signOut();
            }

        } catch (Exception ignored) {
            // Local and guest access should remain available.
        }
    }

    // ------------------------------------------------
    // USER-FRIENDLY ERROR MESSAGES
    // ------------------------------------------------

    private static String friendlyError(String message) {

        if (message == null) {
            return "Firebase request failed. Check your internet connection.";
        }

        String error = message.toLowerCase(Locale.US);

        if (error.contains("network")) {
            return "Network unavailable. Check your internet connection.";
        }

        if (error.contains("badly formatted")) {
            return "Enter a valid email address.";
        }

        if (error.contains("password is invalid")
                || error.contains("no user record")
                || error.contains("invalid credential")) {

            return "Email or password is incorrect.";
        }

        if (error.contains("already in use")) {
            return "This email already has an account. Please log in.";
        }

        if (error.contains("weak password")) {
            return "Please choose a stronger password.";
        }

        if (error.contains("operation not allowed")) {
            return "Enable this authentication provider in Firebase Console.";
        }

        if (error.contains("permission_denied")
                || error.contains("permission denied")) {

            return "Firebase access denied. Check your database security rules.";
        }

        return message;
    }
}
