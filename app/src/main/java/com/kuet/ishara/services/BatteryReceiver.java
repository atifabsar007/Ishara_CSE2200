package com.kuet.ishara.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/** Receives selected Android system broadcasts while the Lab Components screen is visible. */
public class BatteryReceiver extends BroadcastReceiver {
    public interface OnSystemEvent { void onSystemEvent(String message); }
    private final OnSystemEvent listener;

    public BatteryReceiver(OnSystemEvent listener) { this.listener = listener; }

    @Override public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null || listener == null) return;
        String action = intent.getAction();
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (scale > 0 && level >= 0) {
                int percent = Math.round(100f * level / scale);
                listener.onSystemEvent("Battery changed: " + percent + "%  •  ACTION_BATTERY_CHANGED");
            }
        } else if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            listener.onSystemEvent("System warning: battery is low  •  ACTION_BATTERY_LOW");
        } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            listener.onSystemEvent("Battery returned to normal  •  ACTION_BATTERY_OKAY");
        } else if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            boolean enabled = intent.getBooleanExtra("state", false);
            listener.onSystemEvent("Airplane mode " + (enabled ? "ON" : "OFF")
                    + "  •  ACTION_AIRPLANE_MODE_CHANGED");
        }
    }
}
