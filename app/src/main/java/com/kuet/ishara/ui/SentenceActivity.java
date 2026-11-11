package com.kuet.ishara.ui;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import com.kuet.ishara.data.IsharaDataStore;
import com.kuet.ishara.databinding.ActivitySentenceBinding;
import com.kuet.ishara.logic.SentenceComposer;
import com.kuet.ishara.logic.SentenceRules;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
public class SentenceActivity extends BaseSpeechActivity{
 private final List<String> parts=new ArrayList<>();
 private String appliedSentence="";
 @Override protected void onCreate(Bundle state){super.onCreate(state);
 ActivitySentenceBinding b=ActivitySentenceBinding.inflate(getLayoutInflater());setContentView(b.getRoot());b.btnBack.setOnClickListener(v->finish());
 b.wordList.setLayoutManager(new GridLayoutManager(this,3));
 b.wordList.setAdapter(new PhraseGridAdapter(Arrays.asList("আমি","আপনি","পানি","খাবার","বই","ক্লাসে","বাসায়","যেতে","খেতে","চাই","পারি","ধন্যবাদ","হ্যাঁ","না","একটু","সাহায্য"),s->{
    parts.add(s);appliedSentence="";render(b);new IsharaDataStore(this).usedPhrase(s,"Sentence builder");
 }));
 b.btnUndo.setOnClickListener(v->{if(!parts.isEmpty())parts.remove(parts.size()-1);appliedSentence="";render(b);});
 b.btnClear.setOnClickListener(v->{parts.clear();appliedSentence="";render(b);});
 b.btnSuggest.setOnClickListener(v->{String s=SentenceRules.suggest(parts);b.txtSuggestion.setText(s.isEmpty()?"Select word tiles first":"Suggestion: "+s);});
 b.btnUseSuggestion.setOnClickListener(v->{String s=SentenceRules.suggest(parts);if(s.isEmpty()){toast("Choose words first");return;} appliedSentence=s;render(b);toast("Suggestion applied");});
 b.btnSpeak.setOnClickListener(v->{String s=current();if(!TextUtils.isEmpty(s)){recordMessage(s,"sentence");speakBangla(s);}});
 b.btnShare.setOnClickListener(v->shareMessage(current()));
 b.btnPeerShare.setOnClickListener(v->{String msg=current();if(msg.isEmpty()){toast("Create a sentence first.");return;}
   startActivity(new Intent(this,PeerActivity.class).putExtra("draft",msg));});
 b.btnSave.setOnClickListener(v->{String s=current();if(!s.isEmpty()){new com.kuet.ishara.data.MessageDbHelper(this).savePhrase(s);toast("Sentence saved");}});
 }
 private String current(){if(!appliedSentence.isEmpty())return appliedSentence;String s="";for(String p:parts)s=SentenceComposer.append(s,p);return s;}
 private void render(ActivitySentenceBinding b){b.txtSentence.setText(parts.isEmpty()?"Your sentence will appear here…":current());}
}
