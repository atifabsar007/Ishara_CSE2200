package com.kuet.ishara.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
import com.kuet.ishara.data.MessageProvider;
import com.kuet.ishara.services.SpeechService;

public abstract class BaseSpeechActivity extends AppCompatActivity {
    private SpeechService service;
    private boolean bound;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((SpeechService.LocalBinder) binder).getService();
        }
        @Override public void onServiceDisconnected(ComponentName name) { service = null; }
    };
    @Override protected void onStart() {
        super.onStart();
        bound = bindService(new Intent(this, SpeechService.class), connection, Context.BIND_AUTO_CREATE);
    }
    @Override protected void onStop() {
        if (bound) { unbindService(connection); bound = false; }
        service = null;
        super.onStop();
    }
    protected void speakBangla(String message) {
        if (message == null || message.trim().isEmpty()) { toast("আগে একটি বার্তা লিখুন"); return; }
        if (service == null || !service.speak(message))
            toast("বাংলা ভয়েস প্রস্তুত নয়। ফোনের Text-to-Speech সেটিংসে বাংলা ভয়েস ইনস্টল করুন।");
    }
    protected void recordMessage(String text, String source) {
        if (text == null || text.trim().isEmpty()) return;
        ContentValues v = new ContentValues(); v.put("text", text.trim()); v.put("source", source);
        getContentResolver().insert(MessageProvider.MESSAGES_URI, v);
    }
    protected void shareMessage(String text) {
        if (text == null || text.trim().isEmpty()) { toast("বার্তা ফাঁকা"); return; }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain"); i.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(i, "বার্তা শেয়ার করুন"));
    }
    protected void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
}
