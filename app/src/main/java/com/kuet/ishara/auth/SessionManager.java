package com.kuet.ishara.auth;

import android.content.Context;
import android.content.SharedPreferences;

/** Stores the active device session. Firebase credentials themselves remain managed by Firebase Auth. */
public final class SessionManager {
    private static final String NAME = "ishara_local_session";
    private SessionManager() { }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static boolean hasSession(Context context) {
        return prefs(context).contains("user_id") || !firebaseUid(context).isEmpty();
    }

    public static long id(Context context) { return prefs(context).getLong("user_id", 0L); }
    public static String displayName(Context context) { return prefs(context).getString("display_name", "Guest"); }
    public static String firebaseUid(Context context) { return prefs(context).getString("firebase_uid", ""); }
    public static boolean isCloudAccount(Context context) { return !firebaseUid(context).isEmpty(); }
    public static boolean isGuest(Context context) { return hasSession(context) && id(context) == 0L && !isCloudAccount(context); }

    /** Retained for migration compatibility with the previous device-local account implementation. */
    public static void enter(Context context, long id, String name) {
        prefs(context).edit().putLong("user_id", id).putString("display_name", name)
                .remove("firebase_uid").apply();
    }

    public static void enterCloud(Context context, String uid, String name) {
        prefs(context).edit().putLong("user_id", 0L).putString("firebase_uid", uid)
                .putString("display_name", name).apply();
    }

    public static void guest(Context context) {
        prefs(context).edit().putLong("user_id", 0L).putString("display_name", "Guest")
                .remove("firebase_uid").apply();
    }

    public static void logout(Context context) { prefs(context).edit().clear().apply(); }

    /** Keeps SQLite data isolated for cloud accounts as well as legacy local accounts. */
    public static String dataSuffix(Context context) {
        String uid = firebaseUid(context);
        if (!uid.isEmpty()) return "_cloud_" + uid.replaceAll("[^A-Za-z0-9]", "_");
        long id = id(context);
        return id == 0L ? "" : "_account_" + id;
    }
}
