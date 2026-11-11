package com.kuet.ishara.ui;

import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kuet.ishara.data.MessageDbHelper;
import com.kuet.ishara.data.MessageProvider;
import com.kuet.ishara.databinding.ActivityHistoryBinding;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseSpeechActivity {
    private ActivityHistoryBinding b;
    private HistoryAdapter adapter;
    private boolean savedTab;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); b=ActivityHistoryBinding.inflate(getLayoutInflater()); setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v -> finish());
        b.listMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter=new HistoryAdapter(new ArrayList<>(),new HistoryAdapter.OnAction() {
            @Override public void onSpeak(HistoryAdapter.Item item) { speakBangla(item.text); }
            @Override public void onSave(HistoryAdapter.Item item) {
                new MessageDbHelper(HistoryActivity.this).savePhrase(item.text); toast("Saved to favourites");
            }
            @Override public void onDelete(HistoryAdapter.Item item) {
                if (savedTab) new MessageDbHelper(HistoryActivity.this).deleteById(MessageDbHelper.TABLE_SAVED,item.id);
                else getContentResolver().delete(ContentUris.withAppendedId(MessageProvider.MESSAGES_URI,item.id),null,null);
                load();
            }
        });
        b.listMessages.setAdapter(adapter);
        b.btnHistory.setOnClickListener(v -> { savedTab=false; load(); });
        b.btnSaved.setOnClickListener(v -> { savedTab=true; load(); });
        load();
    }
    private void load() {
        List<HistoryAdapter.Item> rows=new ArrayList<>();
        if (savedTab) {
            try (MessageDbHelper db = new MessageDbHelper(this); Cursor c=db.getSavedPhrases()) {
                while(c.moveToNext()) rows.add(new HistoryAdapter.Item(c.getLong(0),c.getString(1),"saved",0));
            }
        } else {
            // Week 10 demonstration: query SQLite through a ContentProvider, not directly.
            try (Cursor c=getContentResolver().query(MessageProvider.MESSAGES_URI,null,null,null,null)) {
                if(c!=null) while(c.moveToNext())
                    rows.add(new HistoryAdapter.Item(c.getLong(c.getColumnIndexOrThrow("_id")),
                        c.getString(c.getColumnIndexOrThrow("text")),c.getString(c.getColumnIndexOrThrow("source")),
                        c.getLong(c.getColumnIndexOrThrow("created_at"))));
            }
        }
        adapter.submit(rows);
        b.txtEmpty.setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
        b.btnHistory.setEnabled(savedTab); b.btnSaved.setEnabled(!savedTab);
        b.txtTitle.setText(savedTab?"Saved Phrases":"Communication History");
    }
    @Override protected void onResume() { super.onResume(); load(); }
}
