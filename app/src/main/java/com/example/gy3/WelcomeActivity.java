package com.example.gy3;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * app启动动画
 */
public class WelcomeActivity extends AppCompatActivity {
    
    private ImageView welcome_icon;
    private RelativeLayout welcome_background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        welcome_icon = findViewById(R.id.welcome_icon);
        welcome_background = findViewById(R.id.welcome_background);
        
        start_animator();//启动动画
    }
    
    /**
     * 启动动画控制
     */
    public void start_animator(){
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator_background = ObjectAnimator.ofFloat(welcome_background,"alpha",1,0);
        //变淡
        ObjectAnimator animator_rotation = ObjectAnimator.ofFloat(welcome_icon,"rotation",0f,360f);
        //旋转
        animatorSet.setDuration(3000).playTogether(animator_background,animator_rotation);
        animatorSet.start();
        
        //监听动画是否结束，动画结束跳转页面
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();//这一步是防止使用回退键时，再次显示启动页面，当然，显示了也是黑屏
            }
        });
    }
}