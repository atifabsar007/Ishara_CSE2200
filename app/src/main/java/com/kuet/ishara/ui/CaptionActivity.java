package com.kuet.ishara.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.kuet.ishara.data.IsharaDataStore;
import com.kuet.ishara.databinding.ActivityCaptionBinding;
import com.kuet.ishara.logic.CaptionController;

public class CaptionActivity extends BaseSpeechActivity {
    private ActivityCaptionBinding b;
    private CaptionController speech;
    private final StringBuilder transcript = new StringBuilder();
    private ActivityResultLauncher<String> requestMic;
    private String selectedLanguage = "bn-BD";
    private int captionSegments = 0;

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        b = ActivityCaptionBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v -> finish());
        b.btnStart.setOnClickListener(v -> toggle());
        b.btnMicHelp.setOnClickListener(v -> showMicrophoneHelp());
        b.btnSave.setOnClickListener(v -> save());
        b.btnClear.setOnClickListener(v -> {
            transcript.setLength(0); captionSegments = 0;
            b.txtTranscript.setText("Captions appear here…");
            b.txtPartial.setText("");
            b.txtSegmentCount.setText("0 captured sentences");
        });
        b.btnShare.setOnClickListener(v -> {
            if (transcript.length() == 0) { toast("Nothing to share yet"); return; }
            shareMessage(transcript.toString());
        });
        b.btnCopy.setOnClickListener(v -> {
            if (transcript.length() == 0) { toast("No captions to copy yet"); return; }
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("Ishara captions", transcript.toString()));
            toast("Transcript copied");
        });

        b.languageSpinner.setAdapter(new IsharaSpinnerAdapter(this,
                new String[]{"বাংলা (বাংলাদেশ)", "English (United States)", "Auto: বাংলা + English (experimental)"}));
        b.languageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent,
                                                 android.view.View view, int position, long id) {
                selectedLanguage = position == 0 ? "bn-BD" : position == 1 ? "en-US" : "auto";
                if (speech != null && speech.isActive()) {
                    speech.stop(); b.btnStart.setText("Start captions");
                    b.txtStatus.setText("Language changed. Tap Start captions again.");
                }
                if (position == 2) {
                    b.txtLanguageInfo.setText("Auto switching requires Android 14+ and a compatible speech service. If it doesn't work, choose Bangla or English.");
                } else {
                    b.txtLanguageInfo.setText("Recognition results depend on installed speech services and language support. Interim captions may be delayed.");
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        requestMic = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) startCaptions();
            else { b.txtStatus.setText("Microphone permission denied."); showMicrophoneHelp(); }
        });
        speech = new CaptionController(this, new CaptionController.Callback() {
            @Override public void onState(String state) {
                b.txtStatus.setText(state);
                if (speech != null && !speech.isActive()) b.btnStart.setText("Start captions");
            }
            @Override public void onPartial(String text) {
                b.txtPartial.setText(text.isEmpty() ? "" : "Live preview: " + text);
            }
            @Override public void onFinal(String text) {
                if (transcript.length() > 0) transcript.append("\n");
                transcript.append(text);
                captionSegments++;
                b.txtTranscript.setText(transcript.toString());
                b.txtSegmentCount.setText(captionSegments + (captionSegments == 1 ? " captured sentence" : " captured sentences"));
                b.txtProblem.setText("");
            }
            @Override public void onAudioLevel(float level) {
                // SpeechRecognizer's RMS callback is a rough indicator, not calibrated volume.
                int progress = Math.max(0, Math.min(100, (int) ((level + 2) * 9)));
                b.micActivity.setProgress(progress);
            }
            @Override public void onDetectedLanguage(String language) {
                b.txtLanguageInfo.setText("Detected language: " + language + " (provider estimate)");
            }
            @Override public void onRecognitionProblem(int code, String reason) {
                b.txtProblem.setText("Speech service: " + reason + " (code " + code + ")");
            }
        });
        if (saved != null) {
            String previous = saved.getString("transcript", "");
            transcript.append(previous);
            captionSegments = saved.getInt("segments", 0);
            if (transcript.length() > 0) b.txtTranscript.setText(previous);
            b.txtSegmentCount.setText(captionSegments + " captured sentences");
        }
        if (!speech.available()) b.txtProblem.setText("No speech recognition service detected. See Microphone help.");
    }

    private void toggle() {
        if (speech == null || !speech.available()) {
            showMicrophoneHelp(); return;
        }
        if (speech.isActive()) {
            speech.stop(); b.btnStart.setText("Start captions"); return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestMic.launch(Manifest.permission.RECORD_AUDIO); return;
        }
        startCaptions();
    }

    private void startCaptions() {
        if (speech == null || !speech.available()) return;
        b.txtProblem.setText("");
        b.txtPartial.setText("");
        speech.start(selectedLanguage);
        b.btnStart.setText("Pause captions");
    }

    private void showMicrophoneHelp() {
        new AlertDialog.Builder(this)
            .setTitle("Make live captions work")
            .setMessage("1. Android Emulator: open Extended Controls (⋮) → Microphone → Enable Host Microphone Access.\n\n" +
                    "2. macOS: System Settings → Privacy & Security → Microphone. Allow Android Studio / Android Emulator.\n\n" +
                    "3. Android: allow Ishara microphone permission. Test English mode first, then Bangla.\n\n" +
                    "4. If you see 'speech service unavailable' or 'language unsupported', use an Android device with a compatible Google speech-recognition service and language support.\n\n" +
                    "5. The Android speech recognizer works in short segments; not every device returns live partial words.")
            .setPositiveButton("Got it", (dialog, which) -> {})
            .setNeutralButton("App permissions", (dialog, which) -> {
                Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(settings);
            })
            .show();
    }

    private void save() {
        if (transcript.length() == 0) { toast("No caption text to save"); return; }
        String title = b.editTitle.getText().toString().trim();
        if (title.isEmpty()) title = "Lecture / conversation";
        new IsharaDataStore(this).saveCaption(title, transcript.toString(), selectedLanguage);
        recordMessage(transcript.toString(), "caption");
        toast("Transcript saved to Ishara");
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putString("transcript", transcript.toString());
        state.putInt("segments", captionSegments);
        super.onSaveInstanceState(state);
    }
    @Override protected void onStop() {
        if (speech != null) { speech.stop(); b.btnStart.setText("Start captions"); }
        super.onStop();
    }
    @Override protected void onDestroy() {
        if (speech != null) speech.destroy();
        super.onDestroy();
    }
}
