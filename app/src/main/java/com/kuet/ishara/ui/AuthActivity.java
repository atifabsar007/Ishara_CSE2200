
package com.kuet.ishara.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kuet.ishara.auth.AccountDb;
import com.kuet.ishara.auth.SessionManager;
import com.kuet.ishara.cloud.FirebaseServices;
import com.kuet.ishara.databinding.ActivityAuthBinding;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding b;

    private boolean register = false;
    private boolean localMode = false;
    private boolean submitting = false;

    private int badAttempts = 0;
    private long blockedUntil = 0;

    // ------------------------------------------------
    // ACTIVITY INITIALIZATION
    // ------------------------------------------------

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        b = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        register = getIntent().getBooleanExtra("register", false);

        render();

        // Login tab
        b.btnLoginTab.setOnClickListener(v -> {
            if (submitting) return;

            register = false;
            clearPasswords();
            render();
        });

        // Register tab
        b.btnRegisterTab.setOnClickListener(v -> {
            if (submitting) return;

            register = true;
            clearPasswords();
            render();
        });

        // Main authentication button
        b.btnContinue.setOnClickListener(v -> submit());

        // Continue as guest
        b.btnGuest.setOnClickListener(v -> continueAsGuest());

        // Switch between Firebase and local account
        b.btnLocalMode.setOnClickListener(v -> {
            if (submitting) return;

            localMode = !localMode;

            clearPasswords();
            b.txtError.setText("");

            render();
        });

        // Back button
        b.btnBack.setOnClickListener(v -> finish());
    }

    // ------------------------------------------------
    // UI RENDERING
    // ------------------------------------------------

    private void render() {

        boolean firebaseReady = FirebaseServices.isConfigured(this);

        // Authentication tabs
        b.btnLoginTab.setEnabled(!register && !submitting);
        b.btnRegisterTab.setEnabled(register && !submitting);

        // Registration-only fields
        b.wrapInputName.setVisibility(
                register ? View.VISIBLE : View.GONE
        );

        b.wrapInputConfirm.setVisibility(
                register ? View.VISIBLE : View.GONE
        );

        if (localMode) {

            // OFFLINE ACCOUNT MODE

            b.inputUsername.setHint("Username");

            b.inputUsername.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            );

            b.wrapInputUsername.setHelperText(
                    "3–24 letters, numbers, dot or underscore"
            );

            b.txtTitle.setText(
                    register
                            ? "Create your local space"
                            : "Welcome back"
            );

            b.txtSubtitle.setText(
                    register
                            ? "Create an offline account stored only on this device."
                            : "Sign in to your existing offline Ishara account."
            );

            b.btnContinue.setText(
                    register
                            ? "Create local account"
                            : "Log in locally"
            );

            b.btnLocalMode.setText(
                    "Use Firebase account instead"
            );

            b.txtOfflineNote.setText(
                    "Local accounts stay on this device and do not sync. "
                            + "Use Firebase account mode for cloud synchronization."
            );

            b.btnContinue.setEnabled(!submitting);

            b.txtError.setText("");

        } else {

            // FIREBASE CLOUD MODE

            b.inputUsername.setHint("Email address");

            b.inputUsername.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            );

            b.wrapInputUsername.setHelperText(
                    "Use the email address linked to your Firebase account"
            );

            b.txtTitle.setText(
                    register
                            ? "Create your Ishara account"
                            : "Welcome back"
            );

            b.txtSubtitle.setText(
                    register
                            ? "Create a cloud account to sync your profile and learning progress."
                            : "Sign in to access your Ishara cloud profile and progress."
            );

            b.btnContinue.setText(
                    register
                            ? "Create Firebase account"
                            : "Sign in with Firebase"
            );

            b.btnLocalMode.setText(
                    "Use existing offline account"
            );

            b.txtOfflineNote.setText(
                    firebaseReady
                            ? "Firebase Authentication is configured. Guest and offline modes are also available."
                            : "Firebase configuration is unavailable. You can still use an offline account or continue as guest."
            );

            b.btnContinue.setEnabled(
                    firebaseReady && !submitting
            );

            b.txtError.setText(
                    firebaseReady
                            ? ""
                            : "Firebase initialization failed. Check app/google-services.json and Gradle sync."
            );
        }
    }

    // ------------------------------------------------
    // MAIN SUBMISSION HANDLER
    // ------------------------------------------------

    private void submit() {

        if (submitting) return;

        if (localMode) {
            submitLocal();
        } else {
            submitFirebase();
        }
    }

    // ------------------------------------------------
    // FIREBASE AUTHENTICATION
    // ------------------------------------------------

    private void submitFirebase() {

        if (!FirebaseServices.isConfigured(this)) {

            b.txtError.setText(
                    "Firebase is not configured. Check google-services.json."
            );

            return;
        }

        String email = b.inputUsername.getText()
                .toString()
                .trim();

        String name = b.inputName.getText()
                .toString()
                .trim();

        char[] password = b.inputPassword.getText()
                .toString()
                .toCharArray();

        char[] confirm = b.inputConfirm.getText()
                .toString()
                .toCharArray();

        // Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            b.txtError.setText("Enter a valid email address.");

            clearArrays(password, confirm);
            return;
        }

        // Validate password
        if (password.length < 8) {

            b.txtError.setText(
                    "Use a password with at least 8 characters."
            );

            clearArrays(password, confirm);
            return;
        }

        // Validate registration name
        if (register && TextUtils.isEmpty(name)) {

            b.txtError.setText("Enter your display name.");

            clearArrays(password, confirm);
            return;
        }

        // Validate confirmation
        if (register && !Arrays.equals(password, confirm)) {

            b.txtError.setText("Passwords do not match.");

            clearArrays(password, confirm);
            return;
        }

        String passwordValue = new String(password);

        clearArrays(password, confirm);

        setSubmitting(true);

        b.txtError.setText("Connecting to Firebase...");

        FirebaseServices.AuthCallback callback =
                (success, uid, displayName, message) -> {

                    runOnUiThread(() -> {

                        if (isFinishing() || isDestroyed()) return;

                        setSubmitting(false);

                        clearPasswords();

                        if (!success) {

                            b.txtError.setText(message);

                            return;
                        }

                        // Save authenticated cloud session
                        SessionManager.enterCloud(
                                AuthActivity.this,
                                uid,
                                displayName
                        );

                        Toast.makeText(
                                AuthActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        goHome();
                    });
                };

        if (register) {

            FirebaseServices.register(
                    this,
                    email,
                    passwordValue,
                    name,
                    callback
            );

        } else {

            FirebaseServices.signIn(
                    this,
                    email,
                    passwordValue,
                    callback
            );
        }
    }

    // ------------------------------------------------
    // LOCAL ACCOUNT AUTHENTICATION
    // ------------------------------------------------

    private void submitLocal() {

        if (SystemClock.elapsedRealtime() < blockedUntil) {

            b.txtError.setText(
                    "Too many attempts. Try again in a minute."
            );

            return;
        }

        String username = b.inputUsername.getText()
                .toString()
                .trim();

        String name = b.inputName.getText()
                .toString()
                .trim();

        char[] password = b.inputPassword.getText()
                .toString()
                .toCharArray();

        char[] confirm = b.inputConfirm.getText()
                .toString()
                .toCharArray();

        try (AccountDb db = new AccountDb(this)) {

            if (register) {

                if (password.length < 8) {

                    b.txtError.setText(
                            "Use a password with at least 8 characters."
                    );

                    return;
                }

                if (!Arrays.equals(password, confirm)) {

                    b.txtError.setText(
                            "Passwords do not match."
                    );

                    return;
                }

                if (TextUtils.isEmpty(name)) {

                    b.txtError.setText(
                            "Enter your full name."
                    );

                    return;
                }

                AccountDb.Account account =
                        db.register(username, name, password);

                if (account == null) {

                    b.txtError.setText(
                            "Check your name and username, or choose another username."
                    );

                    return;
                }

                FirebaseServices.signOut(this);

                SessionManager.enter(
                        this,
                        account.id,
                        account.displayName
                );

                Toast.makeText(
                        this,
                        "Local account created successfully.",
                        Toast.LENGTH_SHORT
                ).show();

                goHome();

            } else {

                AccountDb.Account account =
                        db.authenticate(username, password);

                if (account == null) {

                    badAttempts++;

                    if (badAttempts >= 5) {

                        blockedUntil =
                                SystemClock.elapsedRealtime() + 60000;

                        badAttempts = 0;
                    }

                    b.txtError.setText(
                            "Username or password is incorrect."
                    );

                    return;
                }

                badAttempts = 0;

                FirebaseServices.signOut(this);

                SessionManager.enter(
                        this,
                        account.id,
                        account.displayName
                );

                Toast.makeText(
                        this,
                        "Welcome back!",
                        Toast.LENGTH_SHORT
                ).show();

                goHome();
            }

        } catch (Exception exception) {

            b.txtError.setText(
                    "Local account error: " + exception.getMessage()
            );

        } finally {

            clearArrays(password, confirm);
            clearPasswords();
        }
    }

    // ------------------------------------------------
    // GUEST ACCESS
    // ------------------------------------------------

    private void continueAsGuest() {

        if (submitting) return;

        // Clear any previous Firebase session.
        FirebaseServices.signOut(this);

        // Create a local guest session.
        SessionManager.guest(this);

        Toast.makeText(
                this,
                "Continuing as guest.",
                Toast.LENGTH_SHORT
        ).show();

        goHome();
    }

    // ------------------------------------------------
    // LOADING STATE
    // ------------------------------------------------

    private void setSubmitting(boolean loading) {

        submitting = loading;

        b.btnContinue.setEnabled(
                !loading && (localMode || FirebaseServices.isConfigured(this))
        );

        b.btnGuest.setEnabled(!loading);
        b.btnLocalMode.setEnabled(!loading);

        b.btnLoginTab.setEnabled(!loading && !register);
        b.btnRegisterTab.setEnabled(!loading && register);
    }

    // ------------------------------------------------
    // PASSWORD CLEANUP
    // ------------------------------------------------

    private void clearPasswords() {

        b.inputPassword.setText("");
        b.inputConfirm.setText("");
    }

    private void clearArrays(char[] password, char[] confirm) {

        Arrays.fill(password, '\0');
        Arrays.fill(confirm, '\0');
    }

    // ------------------------------------------------
    // NAVIGATION
    // ------------------------------------------------

    private void goHome() {

        Intent intent = new Intent(
                AuthActivity.this,
                MainActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}
