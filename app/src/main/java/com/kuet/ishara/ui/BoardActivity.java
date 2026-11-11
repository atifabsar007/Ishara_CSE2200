package com.kuet.ishara.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kuet.ishara.data.IsharaDataStore;
import com.kuet.ishara.databinding.ActivityBoardBinding;

import java.util.Arrays;

/** Picture communication board plus Android Photo Picker / ContentResolver demonstration. */
public class BoardActivity extends BaseSpeechActivity {
    private ActivityBoardBinding b;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPicker;
    private Uri selectedImage;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        b = ActivityBoardBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v -> finish());

        photoPicker = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri == null) return;
            selectedImage = uri;
            try {
                // Photo Picker grants a scoped content URI; ContentResolver reads the shared media item.
                try (java.io.InputStream stream = getContentResolver().openInputStream(uri)) {
                    if (stream == null) throw new java.io.IOException("Unable to open selected image");
                }
                b.imgSelected.setImageURI(uri);
                b.txtImageInfo.setText("Selected: " + displayName(uri)
                        + "\nRead through Android's content provider. This image stays on this device and is not uploaded.");
                b.imgSelected.setVisibility(android.view.View.VISIBLE);
                b.btnClearImage.setVisibility(android.view.View.VISIBLE);
            } catch (Exception error) {
                selectedImage = null;
                b.txtImageInfo.setText("Could not read this image. Please choose another one.");
            }
        });

        b.btnAddImage.setOnClickListener(v -> photoPicker.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
        b.btnClearImage.setOnClickListener(v -> {
            selectedImage = null;
            b.imgSelected.setImageDrawable(null);
            b.imgSelected.setVisibility(android.view.View.GONE);
            b.btnClearImage.setVisibility(android.view.View.GONE);
            b.txtImageInfo.setText("No image selected. Choose a photo to demonstrate Android media access.");
        });

        b.boardList.setLayoutManager(new GridLayoutManager(this, 2));
        b.boardList.setAdapter(new PhraseGridAdapter(Arrays.asList(
                "💧 পানি চাই", "🍚 খাবার চাই", "🧑‍🏫 আবার বুঝিয়ে বলুন", "🛑 একটু থামুন",
                "🏡 বাসায় যেতে চাই", "📖 বই চাই", "✍️ লিখে দিন", "☎️ ফোন করতে চাই",
                "🚌 বাসে উঠব", "⏰ একটু সময় দিন", "❤️ আমি ভালো আছি", "🙏 ধন্যবাদ"), phrase -> {
            b.txtBoardSelection.setText(phrase);
            new IsharaDataStore(this).usedPhrase(phrase, "Picture board");
        }));
        b.btnSpeak.setOnClickListener(v -> {
            String message = b.txtBoardSelection.getText().toString();
            recordMessage(message, "picture_board");
            speakBangla(message);
        });
    }

    private String displayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) return cursor.getString(index);
            }
        } catch (Exception ignored) { }
        return "Selected image";
    }
}
