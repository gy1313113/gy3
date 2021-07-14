package com.example.gy3;


import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import com.example.eventbus.MessageEventMainActivity;
import com.example.gy3.myfragment.adapter.HuaDongFragmentPagerAdapter;
import com.example.mypermission.MyPermission;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends FragmentActivity {
    
    private static final String TAG = "MainActivity";
    private ViewPager2 mViewPager2;
    private HuaDongFragmentPagerAdapter mHuaDongFragmentPagerAdapter;
    private TextView main_icon_1;
    private TextView main_icon_2;
    private TextView main_icon_3;
    private TextView main_icon_4;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        main_icon_1 = findViewById(R.id.main_icon_1);
        main_icon_2 = findViewById(R.id.main_icon_2);
        main_icon_3 = findViewById(R.id.main_icon_3);
        main_icon_4 = findViewById(R.id.main_icon_4);
        
        mViewPager2 = findViewById(R.id.pager_1);
        mHuaDongFragmentPagerAdapter = new HuaDongFragmentPagerAdapter(this);
        mViewPager2.setAdapter(mHuaDongFragmentPagerAdapter);//设置ViewPager2的适配器
    
        MyPermission myPermission = new MyPermission();//获取权限窗口
        myPermission.getMyPermission(this);
        
        Button mBtn_1 = findViewById(R.id.btn_1);
        mBtn_1.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RVActivity.class);
            startActivity(intent);
        });
    
        Button mBtn_2 = findViewById(R.id.btn_2);
        mBtn_2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, DongHuaActivity.class);
            startActivity(intent);
        });
    
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/count_word.ttf");//引入字体
        mBtn_1.setTypeface(typeface);
        mBtn_2.setTypeface(typeface);
    }
    
    /**
     * 后退时的处理
     */
    @Override
    public void onBackPressed() {
        if (mViewPager2.getCurrentItem() == 0) {
            //如果用户当前正在查看第一步，请允许系统处理
            //后退按钮。此操作调用finish（）并弹出后台堆栈
            super.onBackPressed();
        } else {
            //否则，选择上一步
            mViewPager2.setCurrentItem(mViewPager2.getCurrentItem() - 1);
        }
    }
    
    /**
     * eventbus的访问权限必须是public！不然会闪退，血的教训
     * 订阅方法，当接收到事件的时候，会调用该方法
     * @param messageEvent 发送来的数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEventMainActivity messageEvent){
        Log.d(TAG,Integer.toString(messageEvent.getNumber()));
        switch (messageEvent.getNumber()){
            case 1:main_icon_1.setBackgroundColor(Color.parseColor("#CAFF70"));
                main_icon_2.setBackgroundColor(Color.LTGRAY);
                main_icon_3.setBackgroundColor(Color.LTGRAY);
                main_icon_4.setBackgroundColor(Color.LTGRAY);
                break;
            case 2:main_icon_1.setBackgroundColor(Color.LTGRAY);
                main_icon_2.setBackgroundColor(Color.parseColor("#EEC900"));
                main_icon_3.setBackgroundColor(Color.LTGRAY);
                main_icon_4.setBackgroundColor(Color.LTGRAY);
                break;
            case 3:main_icon_1.setBackgroundColor(Color.LTGRAY);
                main_icon_2.setBackgroundColor(Color.LTGRAY);
                main_icon_3.setBackgroundColor(Color.parseColor("#D15FEE"));
                main_icon_4.setBackgroundColor(Color.LTGRAY);
                break;
            case 4:main_icon_1.setBackgroundColor(Color.LTGRAY);
                main_icon_2.setBackgroundColor(Color.LTGRAY);
                main_icon_3.setBackgroundColor(Color.LTGRAY);
                main_icon_4.setBackgroundColor(Color.parseColor("#7B68EE"));
                break;
            default:main_icon_1.setBackgroundColor(Color.LTGRAY);
                main_icon_2.setBackgroundColor(Color.LTGRAY);
                main_icon_3.setBackgroundColor(Color.LTGRAY);
                main_icon_4.setBackgroundColor(Color.LTGRAY);
                break;
        }
    }
    
    /**
     * 注册EventBus
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    
    /**
     * EventBus解除注册
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}