package com.kuet.ishara.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kuet.ishara.auth.SessionManager;
public class QuestDb extends SQLiteOpenHelper {
 public QuestDb(Context c){super(c,"ishara_quest"+SessionManager.dataSuffix(c)+".db",null,1);}
 @Override public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE attempts(_id INTEGER PRIMARY KEY AUTOINCREMENT,mode TEXT NOT NULL,prompt TEXT NOT NULL,answer TEXT NOT NULL,chosen TEXT NOT NULL,correct INTEGER NOT NULL,points INTEGER NOT NULL,created_at INTEGER NOT NULL)");}
 @Override public void onUpgrade(SQLiteDatabase d,int o,int n){}
 public void record(String mode,String prompt,String answer,String chosen,boolean correct,int points){ContentValues v=new ContentValues();v.put("mode",mode);v.put("prompt",prompt);v.put("answer",answer);v.put("chosen",chosen);v.put("correct",correct?1:0);v.put("points",points);v.put("created_at",System.currentTimeMillis());getWritableDatabase().insert("attempts",null,v);}
 private int value(String query){try(Cursor c=getReadableDatabase().rawQuery(query,null)){return c.moveToFirst()?c.getInt(0):0;}}
 public int totalPoints(){return value("SELECT COALESCE(SUM(points),0) FROM attempts");}
 public int totalSolved(){return value("SELECT COUNT(*) FROM attempts WHERE correct=1");}
 public int totalAttempts(){return value("SELECT COUNT(*) FROM attempts");}
}
