package com.example.gy3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.login.Login;

/**
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity {
    
    private TextView login_tv;
    private EditText login_phone;
    private EditText login_password;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        login_tv = findViewById(R.id.login_tv);
        login_phone = findViewById(R.id.login_phone);
        login_password = findViewById(R.id.login_password);
        Button login_btn = findViewById(R.id.login_btn);
        
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/count_word.ttf");
        login_btn.setTypeface(typeface);
        
        login_btn.setOnClickListener(v -> {
            Login login = new Login(login_tv,this);
            login.getUin_getKey(login_phone.getText().toString(),login_password.getText().toString());//登录
        });
    }
    
}