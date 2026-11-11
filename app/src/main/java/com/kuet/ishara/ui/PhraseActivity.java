package com.kuet.ishara.ui;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import com.kuet.ishara.data.MessageDbHelper;
import com.kuet.ishara.databinding.ActivityPhraseBinding;
import com.kuet.ishara.logic.PhraseCatalog;
import com.kuet.ishara.logic.SentenceComposer;

public class PhraseActivity extends BaseSpeechActivity {
    private ActivityPhraseBinding b;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        b = ActivityPhraseBinding.inflate(getLayoutInflater()); setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v -> finish());
        b.phrasesList.setLayoutManager(new GridLayoutManager(this,2));
        b.phrasesList.setAdapter(new PhraseGridAdapter(PhraseCatalog.common(), phrase ->
            { b.editSentence.setText(SentenceComposer.append(b.editSentence.getText().toString(), phrase));
            new com.kuet.ishara.data.IsharaDataStore(this).usedPhrase(phrase, "Everyday"); }));
        b.btnSpeak.setOnClickListener(v -> {
            String message = text();
            if (TextUtils.isEmpty(message)) { toast("Please type or choose a phrase"); return; }
            recordMessage(message,"phrase"); speakBangla(message);
        });
        b.btnSave.setOnClickListener(v -> {
            String message=text();
            if (message.isEmpty()) { toast("Please type or choose a phrase"); return; }
            new MessageDbHelper(this).savePhrase(message); toast("Phrase saved");
        });
        b.btnClear.setOnClickListener(v -> b.editSentence.setText(""));
        b.btnShare.setOnClickListener(v -> shareMessage(text()));
    }
    private String text() { return b.editSentence.getText().toString().trim(); }
}
