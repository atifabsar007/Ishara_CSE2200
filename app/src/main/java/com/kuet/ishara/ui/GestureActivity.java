package com.kuet.ishara.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.kuet.ishara.data.MessageDbHelper;
import com.kuet.ishara.databinding.ActivityGestureBinding;
import com.kuet.ishara.logic.GestureDictionary;
import com.kuet.ishara.logic.ModelInstaller;
import com.kuet.ishara.logic.SentenceComposer;
import com.kuet.ishara.logic.SentenceRules;
import com.kuet.ishara.logic.GestureCalibration;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Local CameraX + MediaPipe recognition. No video is uploaded. */
public class GestureActivity extends BaseSpeechActivity {
    private static final String TAG = "IsharaCamera";
    private static final long FRAME_INTERVAL_MS = 165;
    private ActivityGestureBinding b;
    private final ExecutorService analyzer = Executors.newSingleThreadExecutor();
    private final ExecutorService downloads = Executors.newSingleThreadExecutor();
    private GestureRecognizer recognizer;
    private MappedByteBuffer modelBuffer;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraLens = CameraSelector.DEFAULT_FRONT_CAMERA;
    private ActivityResultLauncher<String> permissionLauncher;
    private long lastFrameMs;
    private String candidate = "", lastAccepted = "", currentPhrase = "", currentLabel = "";
    private int stableCount = 0, contextIndex = 0;
    private long handAbsentSince;
    private float lastConfidence = 0f;
    private final List<String> sentenceParts = new ArrayList<>();
    private volatile boolean destroyed, paused, installing, showLandmarks=true;

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        b = ActivityGestureBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.txtCameraStatus.setText("Starting camera and AI engine…");
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            granted -> { if (granted) openCamera(); else setStatus("Camera access denied. Enable it in Settings."); });
        b.btnBack.setOnClickListener(v -> finish());
        b.btnSwitchCamera.setOnClickListener(v -> {
            cameraLens = cameraLens == CameraSelector.DEFAULT_FRONT_CAMERA ? CameraSelector.DEFAULT_BACK_CAMERA : CameraSelector.DEFAULT_FRONT_CAMERA;
            b.txtCameraMode.setText(cameraLens == CameraSelector.DEFAULT_FRONT_CAMERA ? "●  FRONT CAMERA" : "●  BACK CAMERA");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) openCamera();
        });
        b.btnRetryModel.setOnClickListener(v -> downloadModel());
        b.btnDictionary.setOnClickListener(v -> startActivity(new Intent(this,DictionaryActivity.class)));
        b.btnCalibration.setOnClickListener(v -> {
            Intent intent=new Intent(this,CalibrationActivity.class);
            intent.putExtra("predicted",currentLabel);intent.putExtra("confidence",lastConfidence);
            startActivity(intent);
        });
        b.switchLandmarks.setOnCheckedChangeListener((button, enabled) -> {showLandmarks=enabled;if(!enabled)b.landmarkOverlay.clear();});
        b.btnSuggestSentence.setOnClickListener(v -> {
            String suggestion=SentenceRules.suggest(composedText());
            if(suggestion.isEmpty()){toast("Add words first");return;}
            b.txtSuggestedSentence.setText("Suggested: "+suggestion+"  (tap to use)");
            b.txtSuggestedSentence.setOnClickListener(v2 -> {
                sentenceParts.clear();sentenceParts.add(suggestion);updateSentence();b.txtSuggestedSentence.setText("Suggestion applied. You can now speak or save it.");
            });
        });
        b.btnPause.setOnClickListener(v -> {
            paused = !paused;
            if(paused)b.landmarkOverlay.clear();
            b.btnPause.setText(paused ? "Resume scanning" : "Pause scanning");
            setStatus(paused ? "Scanning paused" : "Scanning resumed · show one hand");
        });
        b.txtDetected.setOnClickListener(v -> addRecognizedPhrase());
        b.btnAdd.setOnClickListener(v -> addRecognizedPhrase());
        b.btnUndo.setOnClickListener(v -> {
            if (!sentenceParts.isEmpty()) sentenceParts.remove(sentenceParts.size() - 1);
            updateSentence();
        });
        b.btnSpeak.setOnClickListener(v -> {
            String msg = composedText();
            if (msg.isEmpty()) { toast("প্রথমে একটি gesture দেখান"); return; }
            recordMessage(msg, "gesture"); speakBangla(msg);
        });
        b.btnSave.setOnClickListener(v -> {
            String msg = composedText();
            if (msg.isEmpty()) { toast("ফাঁকা বার্তা সংরক্ষণ করা যাবে না"); return; }
            new MessageDbHelper(this).savePhrase(msg); toast("Saved to favourite phrases");
        });
        b.btnClear.setOnClickListener(v -> { sentenceParts.clear(); updateSentence(); });
        b.btnShare.setOnClickListener(v -> shareMessage(composedText()));
        b.btnPeerShare.setOnClickListener(v -> {String msg=composedText();if(msg.isEmpty()){toast("Recognize and add a gesture first.");return;}
            startActivity(new Intent(this,PeerActivity.class).putExtra("draft",msg));});
        ArrayAdapter<String> adapter = new IsharaSpinnerAdapter(this,
            new String[]{"Everyday", "Classroom", "Travel", "Home", "Healthcare"});
        b.contextSpinner.setAdapter(adapter);
        b.contextSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contextIndex = position;
                if (!currentLabel.isEmpty()) {
                    currentPhrase = GestureDictionary.translateForContext(currentLabel, contextIndex);
                    b.txtDetected.setText(currentPhrase);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        if (saved != null) {
            java.util.ArrayList<String> stored = saved.getStringArrayList("sentenceParts");
            if (stored != null) { sentenceParts.addAll(stored); updateSentence(); }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) openCamera();
        else permissionLauncher.launch(Manifest.permission.CAMERA);
        prepareRecognizer();
    }

    private String composedText() {
        String sentence = b.txtSentence.getText().toString().trim();
        return sentence.isEmpty() ? currentPhrase : sentence;
    }
    private void addRecognizedPhrase() {
        if (currentPhrase == null || currentPhrase.isEmpty()) { toast("প্রথমে ক্যামেরায় হাতের ইশারা দেখান"); return; }
        sentenceParts.add(currentPhrase);
        new com.kuet.ishara.data.IsharaDataStore(this).usedPhrase(currentPhrase, "Gesture");
        updateSentence();
        b.txtSentence.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }
    private void updateSentence() {
        String sentence = "";
        for (String part : sentenceParts) sentence = SentenceComposer.append(sentence, part);
        b.txtSentence.setText(sentence);
    }
    private void setStatus(String message) {
        if (!destroyed) runOnUiThread(() -> { if (!destroyed) b.txtCameraStatus.setText(message); });
    }
    private void modelReady() {
        if (destroyed) return;
        runOnUiThread(() -> {
            if (destroyed) return;
            b.btnRetryModel.setVisibility(View.GONE);
            b.modelProgress.setVisibility(View.GONE);
            b.txtAiBadge.setText("AI READY");
            b.txtCameraStatus.setText("AI ready · show one hand steadily");
        });
    }
    private void prepareRecognizer() {
        analyzer.execute(() -> {
            try {
                if (!ModelInstaller.hasBundledModel(this) && !ModelInstaller.hasCachedModel(this)) {
                    runOnUiThread(() -> { if (!destroyed) downloadModel(); });
                    return;
                }
                BaseOptions base;
                if (ModelInstaller.hasBundledModel(this)) {
                    base = BaseOptions.builder().setModelAssetPath(ModelInstaller.MODEL_NAME).build();
                } else {
                    modelBuffer = ModelInstaller.mapCachedModel(this);
                    base = BaseOptions.builder().setModelAssetBuffer(modelBuffer).build();
                }
                recognizer = GestureRecognizer.createFromOptions(this,
                    GestureRecognizer.GestureRecognizerOptions.builder()
                        .setBaseOptions(base)
                        .setNumHands(1)
                        .setMinHandDetectionConfidence(0.55f)
                        .setMinTrackingConfidence(0.55f)
                        .setRunningMode(RunningMode.VIDEO)
                        .build());
                modelReady();
            } catch (Exception e) {
                Log.e(TAG, "Recognizer initialization failed", e);
                setStatus("AI setup failed. Check your model or tap Retry.");
                runOnUiThread(() -> { if (!destroyed) b.btnRetryModel.setVisibility(View.VISIBLE); });
            }
        });
    }
    private void downloadModel() {
        if (installing || destroyed) return;
        installing = true;
        b.btnRetryModel.setVisibility(View.GONE);
        b.modelProgress.setProgress(0);
        b.modelProgress.setVisibility(View.VISIBLE);
        b.txtAiBadge.setText("SETTING UP AI");
        setStatus("Downloading the official gesture engine (one-time setup)…");
        downloads.execute(() -> {
            try {
                ModelInstaller.download(getApplicationContext(), p -> runOnUiThread(() -> {
                    if (!destroyed) { b.modelProgress.setProgress(p); b.txtCameraStatus.setText("Installing gesture engine · " + p + "%"); }
                }));
                installing = false;
                if (!destroyed) prepareRecognizer();
            } catch (Exception e) {
                installing = false;
                setStatus("AI download failed. Check internet and tap Retry. Camera still works.");
                runOnUiThread(() -> { if (!destroyed) {
                    b.modelProgress.setVisibility(View.GONE);
                    b.btnRetryModel.setVisibility(View.VISIBLE);
                    b.txtAiBadge.setText("MODEL REQUIRED");
                }});
            }
        });
    }
    private void openCamera() {
        setStatus("Opening camera…");
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            if (destroyed || isFinishing()) return;
            try {
                cameraProvider = future.get();
                cameraProvider.unbindAll();
                if (!cameraProvider.hasCamera(cameraLens)) {
                    cameraLens = cameraLens == CameraSelector.DEFAULT_FRONT_CAMERA ? CameraSelector.DEFAULT_BACK_CAMERA : CameraSelector.DEFAULT_FRONT_CAMERA;
                }
                b.txtCameraMode.setText(cameraLens == CameraSelector.DEFAULT_FRONT_CAMERA ? "●  FRONT CAMERA" : "●  BACK CAMERA");
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(b.cameraPreview.getSurfaceProvider());
                ImageAnalysis analysis = new ImageAnalysis.Builder()
                    .setTargetResolution(new android.util.Size(640, 480))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build();
                analysis.setAnalyzer(analyzer, this::analyzeFrame);
                cameraProvider.bindToLifecycle(this, cameraLens, preview, analysis);
                if (recognizer != null) setStatus("Camera live · show one hand steadily");
            } catch (Exception e) {
                Log.e(TAG, "Camera failed", e);
                setStatus("Camera unavailable. Try your real Android phone or another emulator camera.");
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void analyzeFrame(@NonNull ImageProxy frame) {
        try {
            if (recognizer == null || destroyed || paused) return;
            long now = SystemClock.elapsedRealtime();
            if (now - lastFrameMs < FRAME_INTERVAL_MS) return;
            lastFrameMs = now;
            int width = frame.getWidth(), height = frame.getHeight();
            ByteBuffer pixelBuffer = frame.getPlanes()[0].getBuffer();
            pixelBuffer.rewind();
            int rowStride = frame.getPlanes()[0].getRowStride();
            if (rowStride < width * 4 || rowStride % 4 != 0) return;
            int paddedWidth = rowStride / 4;
            Bitmap paddedBitmap = Bitmap.createBitmap(paddedWidth, height, Bitmap.Config.ARGB_8888);
            paddedBitmap.copyPixelsFromBuffer(pixelBuffer);
            Bitmap bitmap = paddedWidth == width ? paddedBitmap : Bitmap.createBitmap(paddedBitmap, 0, 0, width, height);
            MPImage mpImage = new BitmapImageBuilder(bitmap).build();
            ImageProcessingOptions options = ImageProcessingOptions.builder()
                .setRotationDegrees(frame.getImageInfo().getRotationDegrees()).build();
            GestureRecognizerResult result = recognizer.recognizeForVideo(mpImage, options, now);
            // Landmarks are drawn on the PreviewView, never saved or uploaded.
            if(showLandmarks && !result.landmarks().isEmpty()) {
                final java.util.List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark> pts =
                    new java.util.ArrayList<>(result.landmarks().get(0));
                final boolean mirror = cameraLens == CameraSelector.DEFAULT_FRONT_CAMERA;
                runOnUiThread(() -> {if(!destroyed && showLandmarks) b.landmarkOverlay.updatePoints(pts,mirror);});
            } else runOnUiThread(() -> {if(!destroyed)b.landmarkOverlay.clear();});
            if (result.gestures().isEmpty() || result.gestures().get(0).isEmpty()) { onHandMissing(); return; }
            Category top = result.gestures().get(0).get(0);
            String label = top.categoryName();
            float score = top.score();
            if ("None".equals(label) || GestureDictionary.translate(label) == null || score < GestureCalibration.threshold(this,label)) {
                onHandMissing(); return;
            }
            handAbsentSince = 0;
            stableCount = label.equals(candidate) ? stableCount + 1 : 1;
            candidate = label;
            if (stableCount >= 3 && !label.equals(lastAccepted)) {
                lastAccepted = label;
                currentLabel = label;
                lastConfidence = score;
                final float confidence = score;
                final String expected = getIntent().getStringExtra("practice_expected");
                runOnUiThread(() -> { if (!destroyed) {
                    new com.kuet.ishara.data.IsharaDataStore(this).recordTrial(expected, label, confidence);
                    if (expected != null) b.txtCameraStatus.setText(expected.equals(label) ? "Practice: correct gesture!" : "Practice: try again · expected " + expected);
                }});
                runOnUiThread(() -> {
                    if (destroyed) return;
                    currentPhrase = GestureDictionary.translateForContext(label, contextIndex);
                    b.txtDetected.setText(currentPhrase);
                    b.txtGestureLabel.setText(label.replace('_', ' ') + "  ·  " + Math.round(confidence * 100) + "% confidence");
                    b.confidenceView.setConfidence(confidence);
                    b.txtAiBadge.setText("HAND DETECTED");
                    if (getIntent().getStringExtra("practice_expected") == null) b.txtCameraStatus.setText("Gesture captured. Move your hand away to repeat.");
                    b.txtDetected.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                    if (b.switchAutoAdd.isChecked()) addRecognizedPhrase();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Recognition frame error", e);
            setStatus("Could not process frame. Check light and camera permissions.");
        } finally { frame.close(); }
    }
    private void onHandMissing() {
        candidate = "";
        stableCount = 0;
        if (handAbsentSince == 0) handAbsentSince = SystemClock.elapsedRealtime();
        if (SystemClock.elapsedRealtime() - handAbsentSince > 900) {
            runOnUiThread(() -> {if(!destroyed)b.landmarkOverlay.clear();});
            lastAccepted = "";
            runOnUiThread(() -> {
                if (!destroyed && recognizer != null) {
                    b.txtAiBadge.setText("SCANNING");
                    b.txtCameraStatus.setText("Place one hand inside the camera frame");
                }
            });
        }
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle out) {
        out.putStringArrayList("sentenceParts", new java.util.ArrayList<>(sentenceParts));
        super.onSaveInstanceState(out);
    }
    @Override protected void onDestroy() {
        destroyed = true;
        if (cameraProvider != null) cameraProvider.unbindAll();
        analyzer.execute(() -> { if (recognizer != null) { recognizer.close(); recognizer = null; } });
        analyzer.shutdown(); downloads.shutdown();
        super.onDestroy();
    }
}
