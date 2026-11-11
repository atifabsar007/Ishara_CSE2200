package com.kuet.ishara.logic;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;

/** Downloads the official on-device model over HTTPS on first use, never camera images. */
public final class ModelInstaller {
    public static final String MODEL_NAME = "gesture_recognizer.task";
    public static final String OFFICIAL_URL =
        "https://storage.googleapis.com/mediapipe-models/gesture_recognizer/gesture_recognizer/float16/1/gesture_recognizer.task";
    private static final long MIN_BYTES = 100_000L;
    private static final long MAX_BYTES = 45L * 1024 * 1024;

    public interface Progress { void update(int percent); }
    private ModelInstaller() {}
    public static File destination(Context context) { return new File(context.getFilesDir(), MODEL_NAME); }
    public static boolean hasBundledModel(Context context) {
        try (InputStream in = context.getAssets().open(MODEL_NAME)) { return in.read() >= 0; }
        catch (Exception e) { return false; }
    }
    public static boolean hasCachedModel(Context context) {
        return destination(context).isFile() && destination(context).length() >= MIN_BYTES;
    }
    public static MappedByteBuffer mapCachedModel(Context context) throws Exception {
        try (FileInputStream in = new FileInputStream(destination(context));
             FileChannel channel = in.getChannel()) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }
    public static void download(Context context, Progress progress) throws Exception {
        File dest = destination(context);
        File temp = new File(context.getFilesDir(), MODEL_NAME + ".part");
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL(OFFICIAL_URL).openConnection();
            c.setConnectTimeout(12000);
            c.setReadTimeout(25000);
            c.setInstanceFollowRedirects(true);
            c.setRequestProperty("User-Agent", "Ishara-Lab-Prototype/2");
            int code = c.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) throw new java.io.IOException("HTTP " + code);
            long total = c.getContentLengthLong();
            if (total > MAX_BYTES) throw new java.io.IOException("Unexpectedly large model");
            byte[] buf = new byte[16384];
            long count = 0;
            int last = -1;
            try (InputStream in = c.getInputStream(); FileOutputStream out = new FileOutputStream(temp)) {
                int n;
                while ((n = in.read(buf)) != -1) {
                    count += n;
                    if (count > MAX_BYTES) throw new java.io.IOException("Download size limit exceeded");
                    out.write(buf, 0, n);
                    if (total > 0) {
                        int pct = (int) Math.min(100, count * 100 / total);
                        if (pct >= last + 3) { last = pct; progress.update(pct); }
                    }
                }
                out.getFD().sync();
            }
            if (count < MIN_BYTES || (total > 0 && count != total))
                throw new java.io.IOException("Model download incomplete");
            if (dest.exists() && !dest.delete()) throw new java.io.IOException("Cannot replace old model");
            if (!temp.renameTo(dest)) throw new java.io.IOException("Unable to save downloaded model");
            progress.update(100);
        } catch (Exception e) {
            Log.e("IsharaModel", "Model download failed", e);
            if (temp.exists()) temp.delete();
            throw e;
        } finally { if (c != null) c.disconnect(); }
    }
}
