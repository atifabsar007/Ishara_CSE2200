package com.kuet.ishara.logic;

import java.util.Arrays;
import java.util.List;

public final class PhraseCatalog {
    private PhraseCatalog() {}
    public static List<String> common() {
        return Arrays.asList(
            "আমি পানি চাই", "আমি ক্ষুধার্ত", "একটু সাহায্য করবেন?",
            "আমি ভালো আছি", "আমি অসুস্থ বোধ করছি", "ধন্যবাদ",
            "দয়া করে অপেক্ষা করুন", "আমার পরিবারের সঙ্গে কথা বলতে চাই",
            "আমি বাড়ি যেতে চাই", "হ্যাঁ", "না", "দয়া করে আবার বলুন"
        );
    }
}
