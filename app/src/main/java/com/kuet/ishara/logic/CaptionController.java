package com.kuet.ishara.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Foreground, segment-based captions backed by the user's Android recognition service.
 * SpeechRecognizer is NOT guaranteed to be continuous or to provide interim results.
 * All recognizer calls and listener callbacks are dispatched on Android's main thread.
 */
public final class CaptionController implements RecognitionListener {
    public interface Callback {
        void onState(String state);
        void onPartial(String text);
        void onFinal(String text);
        default void onAudioLevel(float rmsDb) {}
        default void onDetectedLanguage(String language) {}
        default void onRecognitionProblem(int code, String explanation) {}
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Callback callback;
    private SpeechRecognizer recognizer;
    private Intent recognitionIntent;
    private boolean requested;
    private boolean destroyed;
    private boolean segmentOpen;
    private int consecutiveFailures;
    private final Runnable retry = () -> {
        if (requested && !destroyed && !segmentOpen) startSegment();
    };

    public CaptionController(Context context, Callback callback) {
        this.callback = callback;
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            callback.onState("No Android speech recognition service found. Check speech services on the device.");
            return;
        }
        try {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
            recognizer.setRecognitionListener(this);
        } catch (RuntimeException ex) {
            callback.onState("Cannot initialize speech recognition: " + ex.getClass().getSimpleName());
        }
    }

    public boolean available() { return recognizer != null; }
    public boolean isActive() { return requested; }

    /** language: bn-BD, en-US, or auto. Auto switching requires a supporting service. */
    public void start(String language) {
        if (!available() || destroyed) return;
        if (requested) stop();
        requested = true;
        consecutiveFailures = 0;
        recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        if ("auto".equals(language)) {
            recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD");
            if (Build.VERSION.SDK_INT >= 34) {
                recognitionIntent.putExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_DETECTION, true);
                recognitionIntent.putExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_SWITCH,
                    RecognizerIntent.LANGUAGE_SWITCH_BALANCED);
                recognitionIntent.putStringArrayListExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_SWITCH_ALLOWED_LANGUAGES,
                    new ArrayList<>(Arrays.asList("bn-BD", "en-US")));
            }
            callback.onState("Auto Bangla/English requested. Device support varies; try one language if empty.");
        } else {
            recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        }
        handler.removeCallbacks(retry);
        // Do not start a second request until a cancelled session has unwound.
        segmentOpen = false;
        handler.postDelayed(retry, 250);
    }

    private void startSegment() {
        if (!requested || destroyed || segmentOpen || recognizer == null) return;
        try {
            segmentOpen = true;
            recognizer.startListening(recognitionIntent);
            callback.onState("Listening. Speak one short sentence close to the microphone.");
        } catch (RuntimeException error) {
            segmentOpen = false;
            requested = false;
            callback.onState("Speech recognizer could not start: " + error.getMessage());
            callback.onRecognitionProblem(-1, "Restart listening or check the device speech service.");
        }
    }

    private void restartSegment(long delayMs) {
        handler.removeCallbacks(retry);
        segmentOpen = false;
        if (requested && !destroyed) handler.postDelayed(retry, delayMs);
    }

    public void stop() {
        requested = false;
        handler.removeCallbacks(retry);
        segmentOpen = false;
        if (recognizer != null) {
            try { recognizer.cancel(); } catch (RuntimeException ignored) {}
        }
        callback.onAudioLevel(0);
        callback.onState("Paused. Microphone listening stopped.");
    }

    public void destroy() {
        stop();
        destroyed = true;
        if (recognizer != null) { recognizer.destroy(); recognizer = null; }
    }

    private String best(Bundle bundle) {
        if (bundle == null) return "";
        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        return results != null && !results.isEmpty() && results.get(0) != null
                ? results.get(0).trim() : "";
    }

    public static String errorExplanation(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio capture failed. Check emulator host microphone access or use a real phone.";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Recognizer client error. Stop and restart captions.";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Microphone permission denied. Enable it in Android app settings.";
            case SpeechRecognizer.ERROR_NETWORK:
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Speech service network issue. Check the internet and recognition service.";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No words understood. Check microphone input and language, then speak clearly.";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech detected. The emulator microphone may be disabled.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognizer busy. Retrying after a short delay.";
            case SpeechRecognizer.ERROR_SERVER:
                return "Speech service error. Check that a speech recognition service is installed.";
            case SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED:
                return "Selected language is unsupported by this recognition service. Try English.";
            case SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE:
                return "Speech recognition language pack unavailable. Install the language or try English.";
            case SpeechRecognizer.ERROR_TOO_MANY_REQUESTS:
                return "Too many requests to the speech service. Pause and retry later.";
            default:
                return "Speech recognition error " + error + ". Try again or use a different device.";
        }
    }

    @Override public void onReadyForSpeech(Bundle params) {
        if (requested) callback.onState("Microphone ready. Speak clearly.");
    }
    @Override public void onBeginningOfSpeech() {
        if (requested) callback.onState("Speech activity detected. Waiting for recognized words…");
    }
    @Override public void onRmsChanged(float rmsdB) {
        if (requested) callback.onAudioLevel(rmsdB);
    }
    @Override public void onBufferReceived(byte[] buffer) {}
    @Override public void onEndOfSpeech() {
        if (requested) callback.onState("Processing speech segment…");
    }
    @Override public void onError(int error) {
        if (!requested) return;
        segmentOpen = false;
        consecutiveFailures++;
        String explanation = errorExplanation(error);
        callback.onRecognitionProblem(error, explanation);
        boolean fatal = error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ||
                error == SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED ||
                error == SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE ||
                error == SpeechRecognizer.ERROR_TOO_MANY_REQUESTS;
        if (fatal || consecutiveFailures >= 5) {
            requested = false;
            callback.onState("Captioning stopped. " + explanation);
            return;
        }
        callback.onState(explanation + " Retrying…");
        restartSegment(error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY ? 1600 : 1100);
    }
    @Override public void onResults(Bundle results) {
        if (!requested) return;
        segmentOpen = false;
        String text = best(results);
        callback.onPartial("");
        if (!text.isEmpty()) {
            consecutiveFailures = 0;
            callback.onFinal(text);
            callback.onState("Caption added. Listening for the next sentence…");
        } else {
            callback.onRecognitionProblem(SpeechRecognizer.ERROR_NO_MATCH,
                    "Speech ended without recognized words. Check microphone and language.");
        }
        restartSegment(500);
    }
    @Override public void onPartialResults(Bundle partialResults) {
        if (requested) {
            String partial = best(partialResults);
            if (!partial.isEmpty()) callback.onPartial(partial);
        }
    }
    @Override public void onLanguageDetection(Bundle results) {
        if (!requested || Build.VERSION.SDK_INT < 34 || results == null) return;
        String lang = results.getString(SpeechRecognizer.DETECTED_LANGUAGE, "");
        if (!lang.isEmpty()) callback.onDetectedLanguage(lang);
    }
    @Override public void onEvent(int eventType, Bundle params) {}
}
