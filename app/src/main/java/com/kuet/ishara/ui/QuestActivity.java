package com.kuet.ishara.ui;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.button.MaterialButton;
import com.kuet.ishara.data.QuestDb;
import com.kuet.ishara.databinding.ActivityQuestBinding;
import com.kuet.ishara.logic.QuestEngine;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
/** Ishara Quest: handsign meaning match + Bangla word-order game. */
public class QuestActivity extends BaseSpeechActivity {
 private ActivityQuestBinding b;private final Random random=new Random();private QuestEngine.Item item;private int mode=QuestEngine.MATCH,round=0,earned=0;private boolean answered=false;private final List<String> chosen=new ArrayList<>();
 @Override protected void onCreate(Bundle state){super.onCreate(state);b=ActivityQuestBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
  b.btnBack.setOnClickListener(v->finish());b.btnMatchMode.setOnClickListener(v->changeMode(QuestEngine.MATCH));b.btnSentenceMode.setOnClickListener(v->changeMode(QuestEngine.SENTENCE));
  b.btnNext.setOnClickListener(v->next());b.btnUndo.setOnClickListener(v->{if(!chosen.isEmpty()&&!answered){chosen.remove(chosen.size()-1);refreshChosen();showChoices();}});
  b.btnCheck.setOnClickListener(v->checkSentence());b.btnSpeak.setOnClickListener(v->{if(item!=null)speakBangla(item.answer);});changeMode(QuestEngine.MATCH);
 }
 private void changeMode(int next){mode=next;round=0;earned=0;b.btnMatchMode.setEnabled(next!=QuestEngine.MATCH);b.btnSentenceMode.setEnabled(next!=QuestEngine.SENTENCE);next();}
 private void next(){item=QuestEngine.next(mode,random);chosen.clear();answered=false;round++;
  b.txtRound.setText("Round "+round+"  •  Session points: "+earned);
  b.txtPrompt.setText(item.prompt);b.txtSymbol.setText(item.emoji);
  b.txtHint.setText(mode==QuestEngine.MATCH?"Choose the meaning of this demonstration handshape. These are not official Bangla Sign Language translations.":"Tap the word tiles in the correct order. Then press Check.");
  b.txtFeedback.setText("Pick an answer to begin.");b.btnNext.setVisibility(View.GONE);b.btnCheck.setVisibility(mode==QuestEngine.SENTENCE?View.VISIBLE:View.GONE);
  b.btnUndo.setVisibility(mode==QuestEngine.SENTENCE?View.VISIBLE:View.GONE);b.txtBuilt.setVisibility(mode==QuestEngine.SENTENCE?View.VISIBLE:View.GONE);
  refreshChosen();showChoices();refreshTotals();
 }
 private void showChoices(){b.tileGrid.removeAllViews();for(String text:item.options){if(mode==QuestEngine.SENTENCE && chosen.contains(text))continue;
   MaterialButton button=new MaterialButton(this);button.setText(text);button.setAllCaps(false);button.setTextSize(15);button.setCornerRadius(dp(15));button.setMinHeight(dp(55));button.setTextColor(getColor(com.kuet.ishara.R.color.ink));
   button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(com.kuet.ishara.R.color.primarySoft)));button.setStrokeWidth(dp(1));button.setStrokeColor(android.content.res.ColorStateList.valueOf(getColor(com.kuet.ishara.R.color.line)));
   android.widget.LinearLayout.LayoutParams p=new android.widget.LinearLayout.LayoutParams(-1,dp(54));p.topMargin=dp(9);b.tileGrid.addView(button,p);button.setEnabled(!answered);
   button.setOnClickListener(v->{if(mode==QuestEngine.MATCH){answer(text);}else{chosen.add(text);refreshChosen();showChoices();}});
 }}
 private void refreshChosen(){b.txtBuilt.setText(chosen.isEmpty()?"Your sentence will appear here…":android.text.TextUtils.join(" ",chosen));}
 private void checkSentence(){if(chosen.size()!=item.options.length){b.txtFeedback.setText("Select every word before checking.");return;}answer(android.text.TextUtils.join(" ",chosen));}
 private void answer(String attempt){if(answered)return;answered=true;boolean ok=QuestEngine.matches(attempt,item);int score=QuestEngine.points(ok,mode);earned+=score;
  try(QuestDb db=new QuestDb(this)){db.record(mode==QuestEngine.MATCH?"Meaning Match":"Sentence Quest",item.prompt,item.answer,attempt,ok,score);}
  b.txtFeedback.setText(ok?"✓ Great work! +"+score+" points":"Not quite. Correct answer: "+item.answer);
  b.btnNext.setVisibility(View.VISIBLE);b.btnCheck.setVisibility(View.GONE);b.btnUndo.setVisibility(View.GONE);showChoices();refreshTotals();
 }
 private void refreshTotals(){try(QuestDb db=new QuestDb(this)){b.txtStats.setText(db.totalPoints()+" points  •  "+db.totalSolved()+" solved  •  "+db.totalAttempts()+" attempts  •  "+QuestEngine.badgeFor(db.totalPoints()));}}
 private int dp(int x){return (int)(getResources().getDisplayMetrics().density*x+.5f);}
}
