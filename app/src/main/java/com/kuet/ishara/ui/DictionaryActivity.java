package com.kuet.ishara.ui;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kuet.ishara.databinding.ActivityDictionaryBinding;
import com.kuet.ishara.logic.GestureDictionary;
import java.util.ArrayList;
import java.util.List;
/** Searchable visual reference of the seven supported, general demo hand shapes. */
public final class DictionaryActivity extends BaseSpeechActivity {
    private ActivityDictionaryBinding b;
    private DictionaryAdapter adapter;
    @Override protected void onCreate(Bundle state){ super.onCreate(state);
        b=ActivityDictionaryBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v->finish());
        adapter=new DictionaryAdapter(label->{Intent intent=new Intent(this,GestureActivity.class);intent.putExtra("practice_expected",label);startActivity(intent);});
        b.dictionaryList.setLayoutManager(new LinearLayoutManager(this));b.dictionaryList.setAdapter(adapter);
        adapter.filter("");
        b.inputSearch.addTextChangedListener(new TextWatcher(){
            @Override public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            @Override public void onTextChanged(CharSequence s,int start,int before,int count){adapter.filter(s.toString());}
            @Override public void afterTextChanged(Editable e){}
        });
    }
}
