package com.kuet.ishara.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kuet.ishara.auth.SessionManager;
/** Separate local feedback store; preserves existing accounts and messages on upgrades. */
public final class GestureFeedbackDb extends SQLiteOpenHelper {
    public GestureFeedbackDb(Context c) { super(c,"gesture_feedback_v6"+SessionManager.dataSuffix(c)+".db",null,1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE corrections (_id INTEGER PRIMARY KEY AUTOINCREMENT,predicted TEXT NOT NULL,actual TEXT NOT NULL,confidence REAL,created_at INTEGER NOT NULL)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int a,int b) { }
    public void record(String predicted,String actual,float confidence){
        ContentValues v=new ContentValues();v.put("predicted",predicted);v.put("actual",actual);v.put("confidence",confidence);v.put("created_at",System.currentTimeMillis());
        getWritableDatabase().insert("corrections",null,v);
    }
    public int count(){try(Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM corrections",null)){return c.moveToFirst()?c.getInt(0):0;}}
}
