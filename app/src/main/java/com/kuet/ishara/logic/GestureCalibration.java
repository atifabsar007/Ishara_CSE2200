package com.kuet.ishara.logic;
import android.content.Context;
import android.content.SharedPreferences;
/** User-adjustable recognition thresholds, not machine-learning model training. */
public final class GestureCalibration {
    public static final float DEFAULT_THRESHOLD = 0.65f;
    private static final String PREF = "gesture_calibration_v6";
    private GestureCalibration() { }
    public static float threshold(Context context, String label) {
        float stored = context.getSharedPreferences(PREF,Context.MODE_PRIVATE).getFloat("t_"+label,DEFAULT_THRESHOLD);
        return Math.max(0.50f,Math.min(0.95f,stored));
    }
    public static void setThreshold(Context context, String label, float value) {
        if (GestureDictionary.translate(label)==null) throw new IllegalArgumentException("Unknown gesture");
        float bounded=Math.max(0.50f,Math.min(0.95f,value));
        context.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().putFloat("t_"+label,bounded).apply();
    }
    public static void reset(Context context,String label) {
        context.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().remove("t_"+label).apply();
    }
}
