package com.kuet.ishara.ui;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kuet.ishara.auth.SessionManager;
import com.kuet.ishara.databinding.ActivityWelcomeBinding;
public class WelcomeActivity extends AppCompatActivity {
 @Override protected void onCreate(Bundle state){super.onCreate(state);
  if(SessionManager.hasSession(this)){startActivity(new Intent(this,MainActivity.class));finish();return;}
  ActivityWelcomeBinding b=ActivityWelcomeBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
  b.btnLogin.setOnClickListener(v->go(false));b.btnRegister.setOnClickListener(v->go(true));
  b.btnGuest.setOnClickListener(v->{SessionManager.guest(this);startActivity(new Intent(this,MainActivity.class));finish();});
  b.btnLearn.setOnClickListener(v->startActivity(new Intent(this,AboutActivity.class)));
 }
 private void go(boolean register){startActivity(new Intent(this,AuthActivity.class).putExtra("register",register));}
}
