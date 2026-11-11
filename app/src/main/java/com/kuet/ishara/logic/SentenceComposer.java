package com.kuet.ishara.logic;

public final class SentenceComposer {
    private SentenceComposer() {}
    public static String append(String oldText, String nextText) {
        String oldValue = oldText == null ? "" : oldText.trim();
        String nextValue = nextText == null ? "" : nextText.trim();
        if (nextValue.isEmpty()) return oldValue;
        if (oldValue.isEmpty()) return nextValue;
        return oldValue + " " + nextValue;
    }
}
