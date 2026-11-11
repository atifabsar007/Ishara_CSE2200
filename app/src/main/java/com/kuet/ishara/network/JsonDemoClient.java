package com.kuet.ishara.network;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** HTTPS/JSON exercise, intentionally does NOT claim this endpoint translates gestures. */
public final class JsonDemoClient {
    private JsonDemoClient() {}
    public static String fetchDemo() throws Exception {
        HttpURLConnection conn=(HttpURLConnection) new URL("https://jsonplaceholder.typicode.com/todos/1").openConnection();
        try {
            conn.setRequestMethod("GET"); conn.setConnectTimeout(7000); conn.setReadTimeout(7000);
            if(conn.getResponseCode()!=200) throw new IllegalStateException("HTTP " + conn.getResponseCode());
            StringBuilder json=new StringBuilder();
            try (BufferedReader r=new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line; while((line=r.readLine())!=null && json.length()<20000) json.append(line);
            }
            JSONObject result=new JSONObject(json.toString());
            return "HTTP GET ✓\nJSON title: " + result.optString("title") + "\nCompleted: " + result.optBoolean("completed") +
                "\n\nThis is a public test API. Ishara's core communication works offline.";
        } finally { conn.disconnect(); }
    }
}
