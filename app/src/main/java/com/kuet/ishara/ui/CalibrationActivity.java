package com.kuet.ishara.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.kuet.ishara.data.GestureFeedbackDb;
import com.kuet.ishara.databinding.ActivityCalibrationBinding;
import com.kuet.ishara.logic.GestureCalibration;
import com.kuet.ishara.logic.GestureDictionary;
import java.util.ArrayList;
import java.util.List;
/** Adjustable recognition thresholds + explicit user correction log. Not model retraining. */
public final class CalibrationActivity extends BaseSpeechActivity {
 private ActivityCalibrationBinding b;
 private final List<String> labels=new ArrayList<>(GestureDictionary.all().keySet());
 private String lastPredicted="";private float lastConfidence;
 @Override protected void onCreate(Bundle state){super.onCreate(state);
   b=ActivityCalibrationBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
   b.btnBack.setOnClickListener(v->finish());
   lastPredicted=getIntent().getStringExtra("predicted");if(lastPredicted==null)lastPredicted="";
   lastConfidence=getIntent().getFloatExtra("confidence",0f);
   b.txtPredicted.setText(lastPredicted.isEmpty()?"No previous recognition supplied. Open this from Gesture Studio after a detection.":"Last detected: "+lastPredicted.replace('_',' ')+" ("+Math.round(lastConfidence*100)+"%)");
   ArrayAdapter<String> adapter=new IsharaSpinnerAdapter(this,labels);
   b.spinnerGesture.setAdapter(adapter);b.spinnerCorrection.setAdapter(new IsharaSpinnerAdapter(this,labels));
   b.spinnerGesture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
     @Override public void onItemSelected(AdapterView<?> p, View view,int pos,long id){refresh();}
     @Override public void onNothingSelected(AdapterView<?> p){}
   });
   b.seekThreshold.setMax(45);b.seekThreshold.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener(){
     @Override public void onProgressChanged(android.widget.SeekBar s,int value,boolean byUser){b.txtThreshold.setText("Confidence threshold: "+(50+value)+"%");}
     @Override public void onStartTrackingTouch(android.widget.SeekBar s){}
     @Override public void onStopTrackingTouch(android.widget.SeekBar s){}
   });
   if(!lastPredicted.isEmpty()) {int index=labels.indexOf(lastPredicted);if(index>=0){b.spinnerGesture.setSelection(index);b.spinnerCorrection.setSelection(index);}}
   b.btnSave.setOnClickListener(v->{GestureCalibration.setThreshold(this,selection(),(50+b.seekThreshold.getProgress())/100f);toast("Threshold saved for this gesture");});
   b.btnReset.setOnClickListener(v->{GestureCalibration.reset(this,selection());refresh();toast("Default restored");});
   b.btnPractice.setOnClickListener(v->{Intent i=new Intent(this,GestureActivity.class);i.putExtra("practice_expected",selection());startActivity(i);});
   b.btnRecordCorrection.setOnClickListener(v->{if(lastPredicted.isEmpty()){toast("Recognize a hand gesture first, then return here");return;}
      String corrected=labels.get(b.spinnerCorrection.getSelectedItemPosition());
      try(GestureFeedbackDb db=new GestureFeedbackDb(this)){db.record(lastPredicted,corrected,lastConfidence);}
      toast("Correction saved locally (model not retrained)");refresh();
   });
   refresh();
 }
 private String selection(){int n=b.spinnerGesture.getSelectedItemPosition();return labels.get(Math.max(0,n));}
 private void refresh(){int value=Math.round(GestureCalibration.threshold(this,selection())*100)-50;
   b.seekThreshold.setProgress(value);b.txtThreshold.setText("Confidence threshold: "+(50+value)+"%");
   try(GestureFeedbackDb db=new GestureFeedbackDb(this)){b.txtCorrections.setText("Feedback corrections saved: "+db.count());}
 }
}
