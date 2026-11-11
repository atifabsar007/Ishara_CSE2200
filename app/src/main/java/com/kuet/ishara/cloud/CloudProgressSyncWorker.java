package com.kuet.ishara.cloud;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/** User-triggered background upload of aggregate practice totals. No transcript or camera data is sent. */
public class CloudProgressSyncWorker extends Worker {
    public CloudProgressSyncWorker(@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Context context = getApplicationContext();
            FirebaseUser user = FirebaseAuth.getInstance(FirebaseServices.app(context)).getCurrentUser();
            if (user == null) return Result.failure();

            Map<String, Object> summary = new HashMap<>();
            summary.put("gestureAttempts", getInputData().getInt("attempts", 0));
            summary.put("gestureSuccesses", getInputData().getInt("successes", 0));
            summary.put("gestureFailures", getInputData().getInt("failures", 0));
            summary.put("averageConfidence", getInputData().getDouble("confidence", 0.0));
            summary.put("questPoints", getInputData().getInt("questPoints", 0));
            summary.put("questSolved", getInputData().getInt("questSolved", 0));
            summary.put("syncedAt", System.currentTimeMillis());

            FirebaseFirestore db = FirebaseFirestore.getInstance(FirebaseServices.app(context));
            Tasks.await(db.collection("users").document(user.getUid())
                    .collection("progress").document("summary").set(summary, SetOptions.merge()),
                    25, TimeUnit.SECONDS);
            return Result.success();
        } catch (ExecutionException error) {
            Throwable cause = error.getCause();
            if (cause instanceof FirebaseFirestoreException) return Result.failure();
            return isStopped() ? Result.failure() : Result.retry();
        } catch (TimeoutException error) {
            return isStopped() ? Result.failure() : Result.retry();
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            return Result.retry();
        } catch (Exception error) {
            return Result.failure();
        }
    }
}
