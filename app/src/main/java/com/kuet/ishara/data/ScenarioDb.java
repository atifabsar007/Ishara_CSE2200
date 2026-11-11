package com.kuet.ishara.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kuet.ishara.auth.SessionManager;

/** Each local account stores its own educational scenario results. */
public final class ScenarioDb extends SQLiteOpenHelper {
    public ScenarioDb(Context c){ super(c,"ishara_scenarios"+SessionManager.dataSuffix(c)+".db",null,1); }
    @Override public void onCreate(SQLiteDatabase d) {
        d.execSQL("CREATE TABLE completed(_id INTEGER PRIMARY KEY AUTOINCREMENT, scenario TEXT NOT NULL, path TEXT NOT NULL, points INTEGER NOT NULL, created_at INTEGER NOT NULL)");
    }
    @Override public void onUpgrade(SQLiteDatabase d,int oldV,int newV) { }
    public long add(String scenario,String path,int points) {
        ContentValues v=new ContentValues();v.put("scenario",scenario);v.put("path",path);v.put("points",points);v.put("created_at",System.currentTimeMillis());
        return getWritableDatabase().insert("completed",null,v);
    }
    public String summary() {
        try(Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*), COALESCE(SUM(points),0) FROM completed",null)) {
            return c.moveToFirst()?c.getInt(0)+" completed  •  "+c.getInt(1)+" points total":"No finished scenarios yet";
        }
    }
}
