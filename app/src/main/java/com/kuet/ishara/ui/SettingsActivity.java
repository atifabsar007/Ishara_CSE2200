package com.kuet.ishara.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.kuet.ishara.cloud.FirebaseServices;
import com.kuet.ishara.databinding.ActivitySettingsBinding;
import com.kuet.ishara.network.JsonDemoClient;
import com.kuet.ishara.services.BatteryReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Lab component screen: system broadcasts, HTTP/JSON and Firebase entry points. */
public class SettingsActivity extends BaseSpeechActivity {
    private ActivitySettingsBinding b;
    private BatteryReceiver receiver;
    private boolean registered;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        b = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v -> finish());
        b.btnFetch.setOnClickListener(v -> {
            b.txtApi.setText("Loading HTTPS JSON on a background executor...");
            io.execute(() -> {
                String result;
                try { result = JsonDemoClient.fetchDemo(); }
                catch (Exception error) {
                    Log.w("IsharaNetwork", "GET failed", error);
                    result = "Network request failed: " + error.getMessage();
                }
                String finalResult = result;
                runOnUiThread(() -> { if (!isFinishing()) b.txtApi.setText(finalResult); });
            });
        });
        b.txtCloud.setText(FirebaseServices.isConfigured(this)
                ? "Firebase app values found. Authentication, Realtime Database and Firestore are available after Console setup."
                : "Firebase is not configured. Follow docs/FIREBASE_SETUP.md and add the four app values.");
        b.btnSync.setText("Open Firebase live room");
        b.btnSync.setOnClickListener(v -> startActivity(new android.content.Intent(this, PeerActivity.class)));
    }

    @Override protected void onStart() {
        super.onStart();
        receiver = new BatteryReceiver(message -> runOnUiThread(() -> {
            if (b != null) b.txtBattery.setText(message);
        }));
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.content.Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(android.content.Intent.ACTION_BATTERY_LOW);
        filter.addAction(android.content.Intent.ACTION_BATTERY_OKAY);
        filter.addAction(android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED);
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_EXPORTED);
        registered = true;
    }

    @Override protected void onStop() {
        if (registered && receiver != null) {
            unregisterReceiver(receiver);
            registered = false;
        }
        super.onStop();
    }

    @Override protected void onDestroy() {
        io.shutdownNow();
        super.onDestroy();
    }
}
