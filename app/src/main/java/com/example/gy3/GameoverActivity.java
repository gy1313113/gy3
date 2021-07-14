package com.example.gy3;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.interpolator.SpringScaleInterpolator;

/**
 * 游戏结束页面
 */
public class GameoverActivity extends AppCompatActivity {
    
    TextView mTv_1;
    
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);
    
        /*
        跳转到菜单
         */
        Button mBtn_1 = findViewById(R.id.gameover_btn_1);
        mBtn_1.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    
        /*
        再进行一次游戏
         */
        Button mBtn_2 = findViewById(R.id.gameover_btn_2);
        mBtn_2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, DongHuaActivity.class);
            startActivity(intent);
            finish();
        });
        
        /*
        添加字体
         */
        mTv_1 = findViewById(R.id.gameover_tv);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/count_word.ttf");
        mTv_1.setTypeface(typeface);
        /*
        接收分数
         */
        Intent intent = getIntent();
        String data = intent.getStringExtra("count");
        mTv_1.setText(data+"分");
        
        count_animator();//运行积分动画
    }
    
    /**
     * 积分动画
     */
    public void count_animator(){
        AnimatorSet animatorSet_count_scale = new AnimatorSet();
        ObjectAnimator animator_Scale_big_X = ObjectAnimator.ofFloat(mTv_1,"scaleX",1.0f,2.0f);
        ObjectAnimator animator_Scale_big_Y = ObjectAnimator.ofFloat(mTv_1,"scaleY",1.0f,2.0f);
        animatorSet_count_scale
            .setDuration(1000)
            .playTogether(animator_Scale_big_X,animator_Scale_big_Y);
        animatorSet_count_scale.setInterpolator(new SpringScaleInterpolator(0.4f));//添加插值器
        animatorSet_count_scale.start();
    }
}