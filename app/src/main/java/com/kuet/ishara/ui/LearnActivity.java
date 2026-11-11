package com.kuet.ishara.ui;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kuet.ishara.databinding.ActivityLearnBinding;
import com.kuet.ishara.logic.GestureDictionary;
import java.util.ArrayList;
import java.util.List;
public class LearnActivity extends BaseSpeechActivity {
    @Override protected void onCreate(Bundle b){super.onCreate(b);
        ActivityLearnBinding ui=ActivityLearnBinding.inflate(getLayoutInflater());setContentView(ui.getRoot());
        ui.btnBack.setOnClickListener(v->finish());
        List<String> lessons=new ArrayList<>();for(String gesture:GestureDictionary.all().keySet())lessons.add(gesture);
        ui.lessonList.setLayoutManager(new LinearLayoutManager(this));
        ui.lessonList.setAdapter(new LessonAdapter(lessons,label->{
            Intent i=new Intent(this,GestureActivity.class);i.putExtra("practice_expected",label);startActivity(i);
        }));
    }
}
