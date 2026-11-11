package com.kuet.ishara.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kuet.ishara.databinding.ActivityConversationBinding;
import com.kuet.ishara.logic.CaptionController;
import java.util.ArrayList;

public class ConversationActivity extends BaseSpeechActivity {
    private ActivityConversationBinding b;
    private final ArrayList<String> items=new ArrayList<>();
    private ChatAdapter adapter;
    private CaptionController recognizer;
    private ActivityResultLauncher<String> mic;
    private String captionLanguage="bn-BD";
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);b=ActivityConversationBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v->finish());
        b.speechLanguage.setAdapter(new IsharaSpinnerAdapter(this,
                new String[]{"বাংলা", "English", "Auto বাংলা + English (experimental)"}));
        b.speechLanguage.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> a,android.view.View v,int position,long id) {
                captionLanguage=position==0?"bn-BD":position==1?"en-US":"auto";
                if(recognizer!=null && recognizer.isActive()) {
                    recognizer.stop();b.btnListen.setText("Start listening");b.txtListening.setText("Language changed. Tap Start listening.");
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> a) {}
        });
        b.btnMicHelp.setOnClickListener(v->new AlertDialog.Builder(this)
                .setTitle("Microphone and speech captions")
                .setMessage("Emulator: Extended Controls (⋮) → Microphone → Enable Host Microphone Access.\n\nMac: Privacy & Security → Microphone → allow Android Studio or Emulator.\n\nAndroid: allow Ishara microphone permission. Test English first, then Bangla. Automatic switching is experimental and device-dependent.")
                .setPositiveButton("OK",(dialog,which)->{}).show());
        adapter=new ChatAdapter(items);b.chatList.setLayoutManager(new LinearLayoutManager(this));b.chatList.setAdapter(adapter);
        b.btnReply.setOnClickListener(v->{String s=b.editReply.getText().toString().trim();if(s.isEmpty())return;
            add("Ishara reply: "+s);recordMessage(s,"conversation_reply");speakBangla(s);b.editReply.setText("");
        });
        mic=registerForActivityResult(new ActivityResultContracts.RequestPermission(),yes->{if(yes)toggle();else b.txtListening.setText("Microphone access needed for speech captions");});
        recognizer=new CaptionController(this,new CaptionController.Callback(){
            @Override public void onState(String s){b.txtListening.setText(s);}
            @Override public void onPartial(String s){b.txtPartial.setText(s.isEmpty()?"":"Live: "+s);}
            @Override public void onRecognitionProblem(int error,String reason){b.txtListening.setText("Speech service: "+reason);}
            @Override public void onDetectedLanguage(String language){b.txtDetectedLanguage.setText("Detected: "+language);}
            @Override public void onFinal(String s){add("Speaker: "+s);recordMessage(s,"conversation_caption");b.txtPartial.setText("");}
        });
        b.btnListen.setOnClickListener(v->toggle());
    }
    private void add(String msg){items.add(msg);adapter.notifyItemInserted(items.size()-1);b.chatList.scrollToPosition(items.size()-1);}
    private void toggle(){if(!recognizer.available()){toast("Speech recognition is unavailable");return;}
        if(recognizer.isActive()){recognizer.stop();b.btnListen.setText("Start listening");return;}
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){mic.launch(Manifest.permission.RECORD_AUDIO);return;}
        recognizer.start(captionLanguage);b.btnListen.setText("Stop listening");
    }
    @Override protected void onStop(){if(recognizer!=null){recognizer.stop();b.btnListen.setText("Start listening");}super.onStop();}
    @Override protected void onDestroy(){if(recognizer!=null)recognizer.destroy();super.onDestroy();}
}
