package com.kuet.ishara.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

/** Bound service: the UI binds while visible. No background/foreground service permission needed. */
public class SpeechService extends Service implements TextToSpeech.OnInitListener {
    private final IBinder binder = new LocalBinder();
    private TextToSpeech tts;
    private boolean ready;
    private boolean banglaAvailable;
    public class LocalBinder extends Binder { public SpeechService getService() { return SpeechService.this; } }
    @Override public void onCreate() { super.onCreate(); tts = new TextToSpeech(getApplicationContext(), this); }
    @Override public IBinder onBind(Intent intent) { return binder; }
    @Override public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            int result = tts.setLanguage(new Locale("bn", "BD"));
            banglaAvailable = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
            ready = banglaAvailable;
            if (!ready) Log.w("IsharaTTS", "Bangla voice data missing or unsupported by device engine");
        }
    }
    /** @return false if the Bangla voice engine is unavailable */
    public boolean speak(String message) {
        if (!ready || message == null || message.trim().isEmpty()) return false;
        return tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "ishara-" + System.nanoTime())
                != TextToSpeech.ERROR;
    }
    public boolean isReady() { return ready; }
    public boolean isBanglaAvailable() { return banglaAvailable; }
    @Override public void onDestroy() { if (tts != null) { tts.stop(); tts.shutdown(); } super.onDestroy(); }
}
