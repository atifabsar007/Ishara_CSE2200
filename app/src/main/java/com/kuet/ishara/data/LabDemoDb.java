package com.kuet.ishara.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kuet.ishara.auth.SessionManager;

/** Separate tutorial table: no experiment can overwrite actual conversations. */
public final class LabDemoDb extends SQLiteOpenHelper {
    public LabDemoDb(Context c){super(c,"ishara_lab_demo"+SessionManager.dataSuffix(c)+".db",null,1);}
    @Override public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE samples(_id INTEGER PRIMARY KEY AUTOINCREMENT, label TEXT NOT NULL, updated_at INTEGER NOT NULL)");}
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){}
    public long create(){ContentValues v=new ContentValues();v.put("label","First sample");v.put("updated_at",System.currentTimeMillis());return getWritableDatabase().insert("samples",null,v);}
    public String read(){try(Cursor c=getReadableDatabase().rawQuery("SELECT _id,label FROM samples ORDER BY _id DESC LIMIT 5",null)) {
        StringBuilder sb=new StringBuilder();while(c.moveToNext()){if(sb.length()>0)sb.append("\n");sb.append("#").append(c.getLong(0)).append(": ").append(c.getString(1));}
        return sb.length()==0?"No sample rows. Tap Create.":sb.toString();}}
    public int updateLatest(){return getWritableDatabase().update("samples",newValues("Updated with SQLite"),"_id=(SELECT MAX(_id) FROM samples)",null);}
    public int deleteLatest(){return getWritableDatabase().delete("samples","_id=(SELECT MAX(_id) FROM samples)",null);}
    private ContentValues newValues(String label){ContentValues v=new ContentValues();v.put("label",label);v.put("updated_at",System.currentTimeMillis());return v;}
}
