package com.kuet.ishara.logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/** Deterministic, editable Bangla sentence suggestions; never calls an AI service. */
public final class SentenceRules {
    private SentenceRules() { }
    public static String suggest(List<String> tokens) {
        if (tokens==null || tokens.isEmpty()) return "";
        List<String> normalized=new ArrayList<>();
        for(String s:tokens) if(s!=null && !s.trim().isEmpty()) normalized.add(s.trim());
        if(normalized.isEmpty()) return "";
        String joined=String.join(" ",normalized).replaceAll("\\s+"," ").trim();
        // Only expand short, known patterns; do not change already-complete sentences.
        if (joined.equals("আমি পানি চাই")) return "আমি পানি চাই।";
        if (joined.equals("আমি খাবার চাই")) return "আমি খাবার চাই।";
        if (joined.equals("আমি সাহায্য চাই")) return "আমার সাহায্য দরকার।";
        if (joined.equals("আমি বাসায় যেতে চাই")) return "আমি বাসায় যেতে চাই।";
        if (joined.equals("আপনি পানি চাই")) return "আপনি কি পানি চান?";
        if(joined.endsWith("।") || joined.endsWith("?") || joined.endsWith("!")) return joined;
        return joined+"।";
    }
    public static String suggest(String text) {
        if(text==null || text.trim().isEmpty())return "";
        return suggest(Arrays.asList(text.trim().split("\\s+")));
    }
}
