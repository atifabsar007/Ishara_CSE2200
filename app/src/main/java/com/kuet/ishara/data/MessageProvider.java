package com.kuet.ishara.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MessageProvider extends ContentProvider {
    public static final String AUTHORITY = "com.kuet.ishara.provider";
    public static final Uri MESSAGES_URI = Uri.parse("content://" + AUTHORITY + "/messages");
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static { MATCHER.addURI(AUTHORITY, "messages", 1); MATCHER.addURI(AUTHORITY, "messages/#", 2); }
    private MessageDbHelper helper;
    @Override public boolean onCreate() { helper = new MessageDbHelper(getContext()); return true; }
    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) {
        int match = MATCHER.match(uri);
        if (match != 1 && match != 2) throw new IllegalArgumentException("Unknown URI: " + uri);
        String sel = match == 2 ? "_id=?" : selection;
        String[] selArgs = match == 2 ? new String[]{uri.getLastPathSegment()} : args;
        Cursor c = helper.getReadableDatabase().query(MessageDbHelper.TABLE_MESSAGES,
                projection == null ? new String[]{"_id","text","source","created_at"} : projection,
                sel, selArgs, null, null, order == null ? "created_at DESC" : order);
        c.setNotificationUri(getContext().getContentResolver(), MESSAGES_URI);
        return c;
    }
    @Override public Uri insert(Uri uri, ContentValues values) {
        if (MATCHER.match(uri) != 1) throw new IllegalArgumentException("Unknown URI: " + uri);
        if (values == null || values.getAsString("text") == null || values.getAsString("text").trim().isEmpty())
            throw new IllegalArgumentException("Text is required");
        ContentValues data = new ContentValues(values);
        if (!data.containsKey("source")) data.put("source", "manual");
        if (!data.containsKey("created_at")) data.put("created_at", System.currentTimeMillis());
        long id = helper.getWritableDatabase().insertOrThrow(MessageDbHelper.TABLE_MESSAGES,null,data);
        notifyChanges();
        return ContentUris.withAppendedId(MESSAGES_URI, id);
    }
    @Override public int delete(Uri uri, String selection, String[] args) {
        if (MATCHER.match(uri) != 2) throw new IllegalArgumentException("Use a message ID URI");
        int removed = helper.getWritableDatabase().delete(MessageDbHelper.TABLE_MESSAGES,"_id=?", new String[]{uri.getLastPathSegment()});
        if (removed > 0) notifyChanges();
        return removed;
    }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) {
        if (MATCHER.match(uri) != 2) throw new IllegalArgumentException("Use a message ID URI");
        int changed = helper.getWritableDatabase().update(MessageDbHelper.TABLE_MESSAGES, values, "_id=?", new String[]{uri.getLastPathSegment()});
        if (changed > 0) notifyChanges();
        return changed;
    }
    @Override public String getType(Uri uri) {
        if (MATCHER.match(uri)==1) return "vnd.android.cursor.dir/vnd."+AUTHORITY+".message";
        if (MATCHER.match(uri)==2) return "vnd.android.cursor.item/vnd."+AUTHORITY+".message";
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    private void notifyChanges() { getContext().getContentResolver().notifyChange(MESSAGES_URI, null); }
}
