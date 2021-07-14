package com.example.gy3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.example.interpolator.SpringScaleInterpolator;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 陀螺仪与动画控制
 */
public class DongHuaActivity extends AppCompatActivity implements SensorEventListener {
    
    private static final String TAG = "DongHuaActivity";
    private ConstraintLayout cl;
    private TextView game_count;
    private ImageView game_man;
    private ImageView game_coin;
    private ImageView game_monster;
    private SensorManager mSensorMgr;// 声明一个传感管理器对象
    private float mTimestamp; // 记录上次的时间戳
    private final float[] mAngle = new float[3]; // 记录xyz三个方向上的旋转角度
    private static final float NS2S = 1.0f / 1000000000.0f; // 将纳秒转化为秒
    private static float angleX_final,angleY_final,angleZ_final;//处理后设定边际的陀螺仪数据
    private static boolean flagX = false,flagY = false;//是否碰撞边框
    private static boolean flag_touch = false;//碰撞边框后锁住状态直到脱离
    private static boolean flag_coin_catch = false;//确认金币抓取
    private static boolean flag_catch = false;//抓住金币后锁住状态直到抓取到下一个
    private static boolean flag_monster = false;//获知怪物是否已生成
    private static boolean flag_Thread = true;//线程开启标志
    private AtomicInteger count;//积分计算（原子类）
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dong_hua);
    
        game_count = findViewById(R.id.game_count);
        game_man = findViewById(R.id.game_man);
    
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/count_word.ttf");
        game_count.setTypeface(typeface);
    
        flag_Thread = true;//启动所有线程
        new Thread(new coin_run()).start();//启动子线程，执行金币部分
        new Thread(new monster_run()).start();//启动子线程，执行怪物部分
        
        judge();//执行快速逻辑判断
        
        // 从系统服务中获取传感管理器对象
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    
        //注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
        //SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
        //SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
        //SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
        //SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //注册感光器
        mSensorMgr.registerListener(this,
            mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mSensorMgr.unregisterListener(this);//注销当前活动的传感监听器
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag_Thread = false;//终止所有线程
    }
    
    /**
     * 返回键的监听
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        count.decrementAndGet();//自减1，消除按回退键后莫名其妙加1的bug
    }
    
    /**
     * 陀螺仪数据获取
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) { // 陀螺仪角度变更事件
            if (mTimestamp != 0) {
                final float dT = (event.timestamp - mTimestamp) * NS2S;
                mAngle[0] += event.values[0] * dT;
                mAngle[1] += event.values[1] * dT;
                mAngle[2] += event.values[2] * dT;
                // x轴的旋转角度，手机平放桌上，然后绕侧边转动
                float angleX_base = (float) Math.toDegrees(mAngle[0]);
                // y轴的旋转角度，手机平放桌上，然后绕底边转动
                float angleY_base = (float) Math.toDegrees(mAngle[1]);
                // z轴的旋转角度，手机平放桌上，然后水平旋转
                float angleZ_base = (float) Math.toDegrees(mAngle[2]);
                
                angleY_final = BianJiY(angleY_base);//控制动画图像的移动边际
                angleX_final = BianJiX(angleX_base);
                
                DongHua_translation();//执行移动动画
            }
            mTimestamp = event.timestamp;
        }
    }
    
    //当传感器精度改变时回调该方法，一般无需处理
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    
    }
    
    /**
     * game_man移动动画控制
     */
    public void DongHua_translation(){
        AnimatorSet animatorSet_man = new AnimatorSet();//同时编排多个动画
        
        ObjectAnimator animator_translation_Y = ObjectAnimator.ofFloat(game_man,"translationX",angleY_final*8);
        animator_translation_Y
            .setDuration(200)
            .setInterpolator(new AccelerateDecelerateInterpolator());//添加插值器
        //该插值器两头慢中间快
        
        ObjectAnimator animator_translation_X = ObjectAnimator.ofFloat(game_man,"translationY",angleX_final*18);
        animator_translation_X
            .setDuration(200)
            .setInterpolator(new AccelerateDecelerateInterpolator());//添加插值器
        
        animatorSet_man
            .play(animator_translation_X)
            .with(animator_translation_Y);//上下移动和左右移动同时进行
        animatorSet_man.start();//开启动画
    }
    /**
     * 边际控制Y轴
     */
    private float BianJiY(float Y){
        if(Y <= 53.9f && Y >= -53.9f){
            flagY = false;
            return Y;
        }else if (Y <= -53.9f){
            flagY = true;
            return -53.9f;
        }else {
            flagY = true;
            return 53.9f;
        }
    }
    /**
     * 边际控制X轴
     */
    private float BianJiX(float X){
        if(X <= 53.6f && X >= -53.6f){
            flagX = false;
            return X;
        }else if (X <= -53.6f){
            flagX = true;
            return -53.6f;
        }else {
            flagX = true;
            return 53.6f;
        }
    }
    /**
     * game_man大小发生改变的弹性动画控制
     */
    public void DongHua_scale(){
        AnimatorSet animatorSet_man_scale = new AnimatorSet();
        ObjectAnimator animator_Scale_big_X = ObjectAnimator.ofFloat(game_man,"scaleX",1.0f,2.0f,1.0f);
        ObjectAnimator animator_Scale_big_Y = ObjectAnimator.ofFloat(game_man,"scaleY",1.0f,2.0f,1.0f);
        animatorSet_man_scale
            .setDuration(1000)
            .playTogether(animator_Scale_big_X,animator_Scale_big_Y);
        animatorSet_man_scale.setInterpolator(new SpringScaleInterpolator(0.4f));//添加插值器
        animatorSet_man_scale.start();
    }
    
    /**
     *  金币生成
     */
    class coin_run implements Runnable{
        @Override
        public void run() {
            while(flag_Thread){
                runOnUiThread(() -> {
                    cl = findViewById(R.id.cl);
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getDrawable(R.mipmap.game_coin) ;
                    game_coin = new ImageView(DongHuaActivity.this);
                    game_coin.setImageDrawable(drawable);
                    game_coin.setX((float) Math.random()*850f);//设定图片的开始位置的横坐标，Y同理
                    game_coin.setY((float) Math.random()*1800f);
                    cl.addView(game_coin);
                    Log.d(TAG,"金币");
                });
                try {
                    Thread.sleep(3000); //金币持续3秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> cl.removeView(game_coin));
            }
        }
    }
    
    /**
     * 金币抓取
     */
    public void catch_coin(){
        int[] man_location = new int[2];
        game_man.getLocationOnScreen(man_location);//获取game_man的坐标
        float man_X = (float)man_location[0];
        float man_Y = (float)man_location[1];
        
        int[] coin_location = new int[2];
        game_coin.getLocationOnScreen(coin_location);//获取game_coin的坐标
        float coin_X = (float)coin_location[0];
        float coin_Y = (float)coin_location[1];
        
        if((((coin_X+100f)>=man_X)&&((coin_X-100f)<=man_X))
            &&(((coin_Y+100f)>=man_Y)&&((coin_Y-100f)<=man_Y))){
            Log.d(TAG,"有了");
            flag_coin_catch = true;
        }else{
            flag_coin_catch = false;
        }
    }
    
    /**
     * 金币抓取动画
     */
    public void catch_coin_animator(){
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator_alpha = ObjectAnimator
            .ofFloat(game_coin,"alpha",1,0);
        ObjectAnimator animator_scaleY = ObjectAnimator
            .ofFloat(game_coin,"scaleY",1.0f,1.8f);
        animatorSet
            .playTogether(animator_alpha,animator_scaleY);
        animatorSet.setDuration(500);
        animatorSet.start();
    }
    
    /**
     * 怪物生成及运行
     */
    class monster_run implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(5000); //怪物5秒后生成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                cl = findViewById(R.id.cl);
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable drawable = getDrawable(R.mipmap.game_monster) ;
                game_monster = new ImageView(DongHuaActivity.this);
                game_monster.setImageDrawable(drawable);
                game_monster.setX((float) Math.random()*850f);//设定图片的开始位置的横坐标，Y同理
                game_monster.setY((float) Math.random()*1800f);
                cl.addView(game_monster);
                flag_monster = true;//怪物已生成
            });
            while (flag_Thread){
                try {
                    Thread.sleep(1000); //怪物每1秒重置运动方向
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(DongHuaActivity.this :: monster_animator);//怪物运动
            }
        }
    }
    
    /**
     * 怪物运动动画
     */
    public void monster_animator(){
        AnimatorSet animatorSet_monster = new AnimatorSet();
        ObjectAnimator animator_translation_Y = ObjectAnimator.ofFloat(game_monster,"translationX",(float) Math.random()*850f);
        ObjectAnimator animator_translation_X = ObjectAnimator.ofFloat(game_monster,"translationY",(float) Math.random()*1800f);
        animatorSet_monster
            .setDuration(1500)
            .playTogether(animator_translation_X,animator_translation_Y);
        animatorSet_monster.start();//开启动画
    }
    
    /**
     * 怪物抓人判断
     */
    public boolean monster_catch(){
        int[] monster_location = new int[2];
        game_monster.getLocationOnScreen(monster_location);//获取game_monster的坐标
        float monster_X = (float)monster_location[0];
        float monster_Y = (float)monster_location[1];
    
        int[] man_location = new int[2];
        game_man.getLocationOnScreen(man_location);//获取game_man的坐标
        float man_X = (float)man_location[0];
        float man_Y = (float)man_location[1];
    
        if((((monster_X+110f)>=man_X)&&((monster_X-110f)<=man_X))
            &&(((monster_Y+110f)>=man_Y)&&((monster_Y-110f)<=man_Y))){
            Log.d(TAG,"被抓了");
           return true;
        }else {
            return false;
        }
    }
    
    /**
     * 怪物变大动画
     */
    public void monster_bigger(){
        AnimatorSet animatorSet_monster = new AnimatorSet();
        ObjectAnimator animator_scale_Y = ObjectAnimator.ofFloat(game_monster,"scaleX",1.0f,2.5f);
        ObjectAnimator animator_scale_X = ObjectAnimator.ofFloat(game_monster,"scaleY",1.0f,2.5f);
        animatorSet_monster
            .setDuration(500)
            .playTogether(animator_scale_X,animator_scale_Y);
        animatorSet_monster.setInterpolator(new SpringScaleInterpolator(0.4f));//添加弹性插值器
        animatorSet_monster.start();//开启动画
    }
    
    /**
     * 跳转到游戏结束页面
     * @param MyCount 分数
     */
    public void gameover(String MyCount){
        Intent intent = new Intent();
        intent.putExtra("count",MyCount);
        intent.setClass(this,GameoverActivity.class);
        startActivity(intent);
    }
    
    /**
     * 快速逻辑判断
     */
    public void judge(){
        count = new AtomicInteger(0);//记录分数,从0开始,原子类
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                runOnUiThread(() -> {
                    /*
                    先判断之前是否已经接触了边框，防止做出重复的弹性动画，接触边框一次，执行一次弹性动画
                    */
                    if (!flag_touch) {
                        if (flagX || flagY) {
                            DongHua_scale();//执行弹性动画
                        }
                    }
                    flag_touch = (flagX || flagY);
        
                    catch_coin();//金币抓取判定
                    /*
                    先判断是否已经抓取过该金币
                    */
                    if (!flag_catch) {
                        if (flag_coin_catch) {
                            count.getAndIncrement();//积分加一
                            game_count.setText(Integer.toString(count.get()));
                            catch_coin_animator();//金币抓取动画
                        }
                    }
                    flag_catch = flag_coin_catch;
                    /*
                    判断是否被怪物抓住了,要在怪物生成后
                    */
                    if (flag_monster) {
                        if (monster_catch()) {
                            monster_bigger();//怪物变大
                            flag_monster = false;//获知怪物回归未生成状态
                            gameover(Integer.toString(count.get()));//跳转到游戏结束页面
                            count.set(0);//积分清零
                            mTimer.cancel();//计时停止
                            finish();
                        }
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask,0,100);
    }
}