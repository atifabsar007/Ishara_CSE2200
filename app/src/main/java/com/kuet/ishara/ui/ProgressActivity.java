package com.kuet.ishara.ui;

import android.os.Bundle;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.kuet.ishara.auth.SessionManager;
import com.kuet.ishara.cloud.CloudProgressSyncWorker;
import com.kuet.ishara.cloud.FirebaseServices;
import com.kuet.ishara.data.IsharaDataStore;
import com.kuet.ishara.data.QuestDb;
import com.kuet.ishara.databinding.ActivityProgressBinding;

import java.util.Locale;

public class ProgressActivity extends BaseSpeechActivity {
    private ActivityProgressBinding b;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        b = ActivityProgressBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v -> finish());
        b.btnRefresh.setOnClickListener(v -> refresh());
        b.btnSyncCloud.setOnClickListener(v -> queueCloudSync());
        refresh();
        boolean canSync = SessionManager.isCloudAccount(this) && FirebaseServices.isConfigured(this);
        b.btnSyncCloud.setEnabled(canSync);
        b.txtCloudSync.setText(canSync
                ? "Your aggregate learning progress can be synced when you choose."
                : "Sign in with a configured Firebase account to sync progress. Guest data stays on this device.");
    }

    private void refresh() {
        IsharaDataStore db = new IsharaDataStore(this);
        int total = db.countTrials(null), success = db.countTrials("success");
        int failed = db.countTrials("failed"), uncertain = db.countTrials("uncertain");
        b.txtAttempts.setText(String.valueOf(total));
        b.txtSuccess.setText(String.valueOf(success));
        b.txtUncertain.setText(String.valueOf(uncertain));
        b.txtRate.setText(success + failed > 0
                ? String.format(Locale.US, "%.0f%%", 100.0 * success / (success + failed)) : "N/A");
        b.txtConfidence.setText(String.format(Locale.US, "%.0f%%", 100 * db.averageConfidence()));
        b.txtFailed.setText(String.valueOf(failed));
        b.txtNote.setText("Success is measured only in guided practice with an expected gesture. Free scanning is logged as uncertain, not as proof of accuracy.");
    }

    private void queueCloudSync() {
        if (!SessionManager.isCloudAccount(this) || !FirebaseServices.isConfigured(this)) {
            b.txtCloudSync.setText("Sign in with Firebase first.");
            return;
        }
        IsharaDataStore data = new IsharaDataStore(this);
        int questPoints, questSolved;
        try (QuestDb quest = new QuestDb(this)) {
            questPoints = quest.totalPoints();
            questSolved = quest.totalSolved();
        }
        Data input = new Data.Builder()
                .putInt("attempts", data.countTrials(null))
                .putInt("successes", data.countTrials("success"))
                .putInt("failures", data.countTrials("failed"))
                .putDouble("confidence", data.averageConfidence())
                .putInt("questPoints", questPoints)
                .putInt("questSolved", questSolved)
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(CloudProgressSyncWorker.class)
                .setInputData(input).setConstraints(constraints).build();
        WorkManager.getInstance(this).enqueueUniqueWork(
                "ishara-progress-" + SessionManager.firebaseUid(this), ExistingWorkPolicy.REPLACE, request);
        b.txtCloudSync.setText("Progress sync queued. Android WorkManager will upload aggregate totals when a network is available.");
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.getId()).observe(this, info -> {
            if (info == null) return;
            if (info.getState() == WorkInfo.State.SUCCEEDED)
                b.txtCloudSync.setText("Synced to Cloud Firestore successfully.");
            else if (info.getState() == WorkInfo.State.FAILED)
                b.txtCloudSync.setText("Sync failed. Check Firebase configuration, authentication and Firestore rules.");
            else if (info.getState() == WorkInfo.State.RUNNING)
                b.txtCloudSync.setText("Uploading progress in a background worker...");
        });
    }
}
