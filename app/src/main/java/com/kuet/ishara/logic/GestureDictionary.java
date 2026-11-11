package com.kuet.ishara.logic;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Seven GENERAL gestures with custom demo mappings. Not Bengali Sign Language. */
public final class GestureDictionary {
    private static final Map<String, String> PHRASES;
    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("Thumb_Up", "হ্যাঁ");
        m.put("Thumb_Down", "না");
        m.put("Open_Palm", "একটু থামুন");
        m.put("Closed_Fist", "আমার সাহায্য দরকার");
        m.put("Victory", "ধন্যবাদ");
        m.put("Pointing_Up", "একটু অপেক্ষা করুন");
        m.put("ILoveYou", "আপনাকে ভালোবাসি");
        PHRASES = Collections.unmodifiableMap(m);
    }
    private GestureDictionary() {}
    public static String translate(String gesture) { return PHRASES.get(gesture); }
    public static Map<String,String> all() { return PHRASES; }

    /** Same recognized handshape can represent context-specific *demo* phrases. */
    public static String translateForContext(String gesture, int context) {
        if (!PHRASES.containsKey(gesture)) return null;
        if (context == 1) {
            switch (gesture) {
                case "Thumb_Up": return "আমি বুঝতে পেরেছি";
                case "Thumb_Down": return "আমি বুঝতে পারিনি";
                case "Open_Palm": return "দয়া করে আবার বুঝিয়ে বলুন";
                case "Closed_Fist": return "স্যার, আমার সাহায্য প্রয়োজন";
                case "Victory": return "আপনাকে ধন্যবাদ";
                case "Pointing_Up": return "আমার একটি প্রশ্ন আছে";
                case "ILoveYou": return "আপনাদের সবাইকে শুভেচ্ছা";
                default: break;
            }
        }
        if (context == 2) {
            switch (gesture) {
                case "Thumb_Up": return "হ্যাঁ, আমি যেতে চাই";
                case "Thumb_Down": return "না, এখানে নয়";
                case "Open_Palm": return "দয়া করে এখানে থামুন";
                case "Closed_Fist": return "আমাকে পথ খুঁজে পেতে সাহায্য করুন";
                case "Victory": return "আপনাকে অনেক ধন্যবাদ";
                case "Pointing_Up": return "এই জায়গাটি কোথায়?";
                case "ILoveYou": return "ভ্রমণটা খুব ভালো লাগছে";
                default: break;
            }
        }
        if (context == 3) {
            switch (gesture) {
                case "Thumb_Up": return "আমি ভালো আছি";
                case "Thumb_Down": return "এটা চাই না";
                case "Open_Palm": return "একটু সময় দিন";
                case "Closed_Fist": return "আমাকে সাহায্য করুন";
                case "Victory": return "সব ঠিক আছে";
                case "Pointing_Up": return "আমাকে ডেকে দিন";
                case "ILoveYou": return "আপনাদের ভালোবাসি";
                default: break;
            }
        }
        if (context == 4) {
            switch (gesture) {
                case "Thumb_Up": return "আমি এখন ভালো আছি";
                case "Thumb_Down": return "আমি ভালো বোধ করছি না";
                case "Open_Palm": return "একটু থামুন";
                case "Closed_Fist": return "আমাকে সহায়তা করুন";
                case "Victory": return "ধন্যবাদ";
                case "Pointing_Up": return "আমার একটি প্রশ্ন আছে";
                case "ILoveYou": return "আপনাকে ধন্যবাদ";
                default: break;
            }
        }
        return PHRASES.get(gesture);
    }
}
