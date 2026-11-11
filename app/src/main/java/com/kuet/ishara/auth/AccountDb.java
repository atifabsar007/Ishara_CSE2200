package com.kuet.ishara.auth;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Locale;
/** Device-only accounts; usernames not tied to real email accounts. */
public class AccountDb extends SQLiteOpenHelper {
 public static final class Account {public final long id;public final String displayName;Account(long id,String name){this.id=id;this.displayName=name;}}
 public AccountDb(Context c){super(c,"ishara_accounts_local.db",null,1);}
 @Override public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE accounts(_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, display_name TEXT NOT NULL, salt TEXT NOT NULL, verifier TEXT NOT NULL, created_at INTEGER NOT NULL)");}
 @Override public void onUpgrade(SQLiteDatabase db,int a,int b){}
 public Account register(String username,String displayName,char[] password){String user=username.trim().toLowerCase(Locale.ROOT);if(!user.matches("[a-z0-9._]{3,24}")||displayName.trim().length()<2||password.length<8)return null;
  String salt=PasswordHasher.newSalt();ContentValues v=new ContentValues();v.put("username",user);v.put("display_name",displayName.trim());v.put("salt",salt);v.put("verifier",PasswordHasher.digest(password,salt));v.put("created_at",System.currentTimeMillis());
  long id=getWritableDatabase().insert("accounts",null,v);return id>0?new Account(id,displayName.trim()):null;
 }
 public Account authenticate(String username,char[] password){String user=username.trim().toLowerCase(Locale.ROOT);try(Cursor c=getReadableDatabase().rawQuery("SELECT _id,display_name,salt,verifier FROM accounts WHERE username=?",new String[]{user})){
  if(!c.moveToFirst())return null;return PasswordHasher.verify(password,c.getString(2),c.getString(3))?new Account(c.getLong(0),c.getString(1)):null;
 }}
}
