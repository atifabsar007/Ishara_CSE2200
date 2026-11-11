package com.kuet.ishara.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/** V3 secondary SQLite store, intentionally separate from the V2 database (non-destructive upgrade). */
public class IsharaDataStore extends SQLiteOpenHelper {
    private static final String DB = "ishara_v3.db";
    public IsharaDataStore(Context context) { super(context, "ishara_v3"+com.kuet.ishara.auth.SessionManager.dataSuffix(context)+".db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE captions(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, body TEXT NOT NULL, language TEXT NOT NULL, created_at INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE gesture_trials(_id INTEGER PRIMARY KEY AUTOINCREMENT, expected TEXT, detected TEXT NOT NULL, confidence REAL NOT NULL, outcome TEXT NOT NULL, created_at INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE phrase_usage(phrase TEXT PRIMARY KEY, category TEXT NOT NULL, use_count INTEGER NOT NULL DEFAULT 0, last_used INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_trials_date ON gesture_trials(created_at)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { /* No migration necessary for version 1. */ }
    public long saveCaption(String title, String body, String language) {
        if (body == null || body.trim().isEmpty()) return -1;
        ContentValues v = new ContentValues();v.put("title",title);v.put("body",body.trim());v.put("language",language);v.put("created_at",System.currentTimeMillis());
        return getWritableDatabase().insert("captions",null,v);
    }
    public Cursor captions() { return getReadableDatabase().rawQuery("SELECT _id,title,body,language,created_at FROM captions ORDER BY created_at DESC",null); }
    public void recordTrial(String expected, String detected, float confidence) {
        String outcome = (expected == null || expected.isEmpty()) ? "uncertain" : (expected.equals(detected) ? "success" : "failed");
        ContentValues v = new ContentValues();v.put("expected",expected);v.put("detected",detected);v.put("confidence",confidence);v.put("outcome",outcome);v.put("created_at",System.currentTimeMillis());
        getWritableDatabase().insert("gesture_trials",null,v);
    }
    public int countTrials(String outcome) {
        String query=outcome == null ? "SELECT COUNT(*) FROM gesture_trials" : "SELECT COUNT(*) FROM gesture_trials WHERE outcome=?";
        try(Cursor c=getReadableDatabase().rawQuery(query,outcome == null ? null:new String[]{outcome})) { return c.moveToFirst()?c.getInt(0):0; }
    }
    public double averageConfidence() {
        try(Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(AVG(confidence),0) FROM gesture_trials",null)) {return c.moveToFirst()?c.getDouble(0):0;}
    }
    public void usedPhrase(String phrase,String category) {
        if(phrase==null || phrase.trim().isEmpty()) return;
        SQLiteDatabase db=getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues v=new ContentValues();v.put("phrase",phrase);v.put("category",category);v.put("use_count",0);v.put("last_used",System.currentTimeMillis());
            db.insertWithOnConflict("phrase_usage",null,v,SQLiteDatabase.CONFLICT_IGNORE);
            db.execSQL("UPDATE phrase_usage SET use_count=use_count+1,last_used=? WHERE phrase=?", new Object[]{System.currentTimeMillis(),phrase});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public List<String> topPhrases(int limit) {
        List<String> out=new ArrayList<>();
        try(Cursor c=getReadableDatabase().rawQuery("SELECT phrase FROM phrase_usage ORDER BY use_count DESC,last_used DESC LIMIT "+Math.max(1,Math.min(10,limit)),null)) {
            while(c.moveToNext())out.add(c.getString(0));
        }return out;
    }
    public String mostUsedCategory() {
        try(Cursor c=getReadableDatabase().rawQuery("SELECT category FROM phrase_usage GROUP BY category ORDER BY SUM(use_count) DESC LIMIT 1",null)) {
            return c.moveToFirst()?c.getString(0):"Everyday";
        }
    }
}
