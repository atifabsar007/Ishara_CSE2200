package com.kuet.ishara.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import com.kuet.ishara.data.LabDemoDb;
import com.kuet.ishara.databinding.ActivityLabBinding;
import org.json.JSONArray;
import org.json.JSONObject;

/** Optional instructor demonstration. Real component lifecycle + SQLite + JSON + intents. */
public class LabActivity extends BaseSpeechActivity {
    private ActivityLabBinding b;
    private final StringBuilder lifecycle=new StringBuilder();
    private static final String TAG="IsharaLab";
    private void event(String name){String s=name+"  "+android.text.format.DateFormat.format("HH:mm:ss",System.currentTimeMillis());
        Log.d(TAG,s);lifecycle.append(s).append('\n');if(lifecycle.length()>1200)lifecycle.delete(0,500);
        if(b!=null)b.txtLifecycle.setText(lifecycle.toString());}
    @Override protected void onCreate(Bundle saved){super.onCreate(saved);
        b=ActivityLabBinding.inflate(getLayoutInflater());setContentView(b.getRoot());event("onCreate");
        b.btnBack.setOnClickListener(v->finish());
        b.btnCreate.setOnClickListener(v->{try(LabDemoDb db=new LabDemoDb(this)){db.create();b.txtDb.setText("CREATE → "+db.read());}});
        b.btnRead.setOnClickListener(v->{try(LabDemoDb db=new LabDemoDb(this)){b.txtDb.setText("READ → "+db.read());}});
        b.btnUpdate.setOnClickListener(v->{try(LabDemoDb db=new LabDemoDb(this)){int n=db.updateLatest();b.txtDb.setText("UPDATE "+n+" row(s)\n"+db.read());}});
        b.btnDelete.setOnClickListener(v->{try(LabDemoDb db=new LabDemoDb(this)){int n=db.deleteLatest();b.txtDb.setText("DELETE "+n+" row(s)\n"+db.read());}});
        b.btnParseJson.setOnClickListener(v->{try {String sample="{\"messages\":[{\"lang\":\"bn\",\"text\":\"ধন্যবাদ\"},{\"lang\":\"en\",\"text\":\"Thank you\"}]}";
            JSONObject parsed=new JSONObject(sample);JSONArray rows=parsed.getJSONArray("messages");
            StringBuilder lines=new StringBuilder("Parsed "+rows.length()+" JSON messages:\n");
            for(int i=0;i<rows.length();i++)lines.append(rows.getJSONObject(i).getString("lang")).append(": ").append(rows.getJSONObject(i).getString("text")).append('\n');
            b.txtJson.setText(lines.toString());
        }catch(Exception ex){b.txtJson.setText("JSON error: "+ex.getClass().getSimpleName());}});
        b.btnBattery.setOnClickListener(v->{Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if(i!=null){int lvl=i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);int scale=i.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
                String percent=scale>0?Integer.toString(Math.round(100f*lvl/scale)):"unknown";
                b.txtBattery.setText("BroadcastReceiver / sticky system broadcast: "+percent+"% battery");}
            else b.txtBattery.setText("Battery broadcast unavailable in this emulator.");});
        b.btnShare.setOnClickListener(v->shareMessage("Ishara demonstrates an Android implicit share Intent."));
        b.btnSpeech.setOnClickListener(v->speakBangla("ইশারা যোগাযোগ সহজ করে"));
        b.btnNetworking.setOnClickListener(v->startActivity(new Intent(this,PeerActivity.class)));
        b.btnContentProvider.setOnClickListener(v->startActivity(new Intent(this,BoardActivity.class)));
    }
    @Override protected void onStart(){super.onStart();event("onStart");}
    @Override protected void onResume(){super.onResume();event("onResume");}
    @Override protected void onPause(){event("onPause");super.onPause();}
    @Override protected void onStop(){event("onStop");super.onStop();}
    @Override protected void onDestroy(){event("onDestroy");super.onDestroy();}
}
