package com.kuet.ishara.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.kuet.ishara.auth.SessionManager;
import com.kuet.ishara.cloud.FirebaseServices;
import com.kuet.ishara.data.MessageProvider;
import com.kuet.ishara.databinding.ActivityPeerBinding;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Live room messaging using Firebase Realtime Database; messages are opt-in and room-scoped. */
public class PeerActivity extends BaseSpeechActivity {
    private ActivityPeerBinding b;
    private final List<String> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private DatabaseReference roomRef;
    private Query roomQuery;
    private ChildEventListener roomListener;
    private String activeRoom = "";
    private String latestReceived = "";
    private String currentUid = "";
    private final SecureRandom random = new SecureRandom();

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        b = ActivityPeerBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v -> finish());
        b.chatList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(messages);
        b.chatList.setAdapter(adapter);
        b.btnSend.setEnabled(false);
        b.btnCreateRoom.setEnabled(false);
        b.btnJoinRoom.setEnabled(false);
        b.btnShareRoom.setEnabled(false);
        b.btnStop.setOnClickListener(v -> stopRoom("You left the room."));
        b.btnLoadLatest.setOnClickListener(v -> {
            try (Cursor cursor = getContentResolver().query(MessageProvider.MESSAGES_URI,
                    new String[]{"text"}, null, null, "created_at DESC")) {
                if (cursor != null && cursor.moveToFirst()) b.editMessage.setText(cursor.getString(0));
                else toast("No saved history yet. Create a gesture sentence first.");
            } catch (Exception error) { toast("Could not read local message history."); }
        });
        b.btnSpeakLast.setOnClickListener(v -> {
            if (TextUtils.isEmpty(latestReceived)) toast("No incoming message yet.");
            else speakBangla(latestReceived);
        });

        if (!FirebaseServices.isConfigured(this)) {
            b.txtRoomStatus.setText("Firebase is not configured. Add the values in res/values/firebase_config.xml and follow docs/FIREBASE_SETUP.md.");
            b.btnCreateRoom.setEnabled(false);
            b.btnJoinRoom.setEnabled(false);
            return;
        }

        b.txtRoomStatus.setText("Connecting to Firebase Authentication...");
        FirebaseServices.ensureRoomAuthentication(this, (success, uid, message) -> {
            if (isFinishing() || isDestroyed()) return;
            if (success) {
                currentUid = uid;
                b.txtRoomStatus.setText(message + " Create a room or enter a room code.");
                b.btnCreateRoom.setEnabled(true);
                b.btnJoinRoom.setEnabled(true);
            } else {
                b.txtRoomStatus.setText(message + " Enable Anonymous Authentication for guest room access.");
            }
        });

        b.btnCreateRoom.setOnClickListener(v -> {
            String code = String.format(Locale.US, "%06d", random.nextInt(1_000_000));
            b.editRoomCode.setText(code);
            joinRoom(code);
        });
        b.btnJoinRoom.setOnClickListener(v -> joinRoom(b.editRoomCode.getText().toString().trim()));
        b.btnShareRoom.setOnClickListener(v -> {
            if (activeRoom.isEmpty()) toast("Create or join a room first.");
            else shareMessage("Join my Ishara Connect room. Room code: " + activeRoom);
        });
        b.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void joinRoom(String roomCode) {
        if (TextUtils.isEmpty(currentUid)) {
            toast("Wait until Firebase authentication is ready.");
            return;
        }
        if (roomCode == null || !roomCode.matches("[A-Za-z0-9_-]{4,24}")) {
            b.txtRoomStatus.setText("Use a room code containing 4–24 letters, numbers, _ or -.");
            return;
        }
        stopListenerOnly();
        activeRoom = roomCode.toLowerCase(Locale.ROOT);
        messages.clear(); adapter.notifyDataSetChanged();
        roomRef = FirebaseServices.roomMessages(this, activeRoom);
        roomListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                String uid = snapshot.child("uid").getValue(String.class);
                String name = snapshot.child("displayName").getValue(String.class);
                String text = snapshot.child("text").getValue(String.class);
                if (TextUtils.isEmpty(text)) return;
                boolean mine = currentUid.equals(uid);
                String line = (mine ? "You" : (TextUtils.isEmpty(name) ? "Ishara user" : name)) + ": " + text;
                messages.add(line);
                adapter.notifyItemInserted(messages.size() - 1);
                b.chatList.scrollToPosition(messages.size() - 1);
                if (!mine) latestReceived = text;
            }
            @Override public void onChildChanged(DataSnapshot snapshot, String previousChildName) { }
            @Override public void onChildRemoved(DataSnapshot snapshot) { }
            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) { }
            @Override public void onCancelled(DatabaseError error) {
                b.txtRoomStatus.setText("Room listener stopped: " + error.getMessage());
            }
        };
        roomQuery = roomRef.limitToLast(60);
        roomQuery.addChildEventListener(roomListener);
        b.txtRoomStatus.setText("Connected to room " + activeRoom + ". Share the code with another Ishara user.");
        b.btnSend.setEnabled(true);
        b.btnShareRoom.setEnabled(true);
    }

    private void sendMessage() {
        String text = b.editMessage.getText().toString().trim();
        if (roomRef == null || TextUtils.isEmpty(activeRoom)) { toast("Create or join a room first."); return; }
        if (text.isEmpty() || text.length() > 400) { toast("Enter a message between 1 and 400 characters."); return; }
        String displayName = SessionManager.displayName(this);
        if (TextUtils.isEmpty(displayName) || "Guest".equals(displayName)) displayName = "Ishara user";
        b.btnSend.setEnabled(false);
        FirebaseServices.sendRoomMessage(this, activeRoom, currentUid, displayName, text)
                .addOnCompleteListener(task -> {
                    if (isFinishing() || isDestroyed()) return;
                    b.btnSend.setEnabled(roomRef != null);
                    if (task.isSuccessful()) {
                        recordMessage(text, "firebase_room");
                        b.editMessage.setText("");
                    } else {
                        toast("Message not sent: " + (task.getException() == null ? "unknown error" : task.getException().getMessage()));
                    }
                });
    }

    private void stopRoom(String message) {
        stopListenerOnly();
        activeRoom = "";
        b.btnSend.setEnabled(false);
        b.btnShareRoom.setEnabled(false);
        b.txtRoomStatus.setText(message);
    }

    private void stopListenerOnly() {
        if (roomQuery != null && roomListener != null) roomQuery.removeEventListener(roomListener);
        roomListener = null;
        roomQuery = null;
        roomRef = null;
    }

    @Override protected void onDestroy() {
        stopListenerOnly();
        super.onDestroy();
    }
}
