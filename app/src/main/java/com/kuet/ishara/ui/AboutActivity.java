package com.kuet.ishara.ui;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kuet.ishara.databinding.ActivityAboutBinding;
public class AboutActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state){super.onCreate(state);
        ActivityAboutBinding b=ActivityAboutBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v->finish());
    }
}
