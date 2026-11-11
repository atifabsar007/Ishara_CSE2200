package com.kuet.ishara.ui;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.kuet.ishara.auth.SessionManager;
import com.kuet.ishara.cloud.FirebaseServices;
import com.kuet.ishara.data.IsharaDataStore;
import com.kuet.ishara.data.QuestDb;
import com.kuet.ishara.databinding.ActivityMainBinding;
import java.util.List;
public class MainActivity extends BaseSpeechActivity {
 private ActivityMainBinding b;
 @Override protected void onCreate(Bundle state){super.onCreate(state);
  if(!SessionManager.hasSession(this)){startActivity(new Intent(this,WelcomeActivity.class));finish();return;}
  Log.d("IsharaLifecycle","MainActivity onCreate");b=ActivityMainBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
  b.btnCamera.setOnClickListener(v->go(GestureActivity.class));b.btnCaptions.setOnClickListener(v->go(CaptionActivity.class));
  b.btnConversation.setOnClickListener(v->go(ConversationActivity.class));b.btnPhrases.setOnClickListener(v->go(PhraseActivity.class));
  b.btnBuilder.setOnClickListener(v->go(SentenceActivity.class));b.btnBoard.setOnClickListener(v->go(BoardActivity.class));
  b.btnLearn.setOnClickListener(v->go(LearnActivity.class));b.btnProgress.setOnClickListener(v->go(ProgressActivity.class));
  b.btnHistory.setOnClickListener(v->go(HistoryActivity.class));b.btnSettings.setOnClickListener(v->go(SettingsActivity.class));
  b.btnAbout.setOnClickListener(v->go(AboutActivity.class));b.btnQuest.setOnClickListener(v->go(QuestActivity.class));
  b.btnPeer.setOnClickListener(v->go(PeerActivity.class));
  b.btnSimulator.setOnClickListener(v->go(SimulatorActivity.class));
  b.btnLab.setOnClickListener(v->go(LabActivity.class));
  b.btnDictionary.setOnClickListener(v->go(DictionaryActivity.class));
  b.btnCalibration.setOnClickListener(v->go(CalibrationActivity.class));
  b.btnUseTop.setOnClickListener(v->{List<String> a=new IsharaDataStore(this).topPhrases(1);if(!a.isEmpty()){recordMessage(a.get(0),"adaptive");speakBangla(a.get(0));}});
  b.btnLogout.setOnClickListener(v->{FirebaseServices.signOut(this);SessionManager.logout(this);Intent i=new Intent(this,WelcomeActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);finish();});
 }
 private void go(Class<?> cls){startActivity(new Intent(this,cls));}
 @Override protected void onResume(){super.onResume();if(b==null)return;
  b.txtHello.setText("Hello, "+SessionManager.displayName(this)+" 👋");
  b.txtAccount.setText(SessionManager.isCloudAccount(this)
          ? "Firebase account • Cloud profile"
          : SessionManager.isGuest(this) ? "Guest mode • Private device space" : "Local account • Private device space");
  IsharaDataStore d=new IsharaDataStore(this);List<String> top=d.topPhrases(3);
  b.txtAdaptive.setText(top.isEmpty()?"Your frequently used messages appear here.":android.text.TextUtils.join("  ·  ",top));
  b.btnUseTop.setEnabled(!top.isEmpty());
  try(QuestDb q=new QuestDb(this)){b.txtQuestScore.setText(q.totalPoints()+" points earned  •  "+q.totalSolved()+" puzzles solved");}
 }
 @Override protected void onPause(){Log.d("IsharaLifecycle","MainActivity onPause");super.onPause();}
}
