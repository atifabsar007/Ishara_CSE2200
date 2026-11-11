package com.kuet.ishara.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_SAVED = "saved_phrases";
    public MessageDbHelper(Context context) { super(context, "ishara"+com.kuet.ishara.auth.SessionManager.dataSuffix(context)+".db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE messages (_id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT NOT NULL, source TEXT NOT NULL, created_at INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE saved_phrases (_id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT NOT NULL UNIQUE)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future migrations should preserve data. Version 1 has no migration yet.
    }
    public long addMessage(String text, String source) {
        if (text == null || text.trim().isEmpty()) return -1;
        ContentValues v = new ContentValues();
        v.put("text", text.trim()); v.put("source", source); v.put("created_at", System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE_MESSAGES, null, v);
    }
    public long savePhrase(String text) {
        if (text == null || text.trim().isEmpty()) return -1;
        ContentValues v = new ContentValues(); v.put("text", text.trim());
        return getWritableDatabase().insertWithOnConflict(TABLE_SAVED, null, v, SQLiteDatabase.CONFLICT_IGNORE);
    }
    public Cursor getSavedPhrases() {
        return getReadableDatabase().query(TABLE_SAVED, new String[]{"_id", "text"}, null,null,null,null,"_id DESC");
    }
    public int deleteById(String table, long id) {
        if (!TABLE_MESSAGES.equals(table) && !TABLE_SAVED.equals(table)) return 0;
        return getWritableDatabase().delete(table,"_id=?",new String[]{String.valueOf(id)});
    }
}
