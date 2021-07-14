package com.example.gy3.myfragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eventbus.MessageEventMainActivity;
import com.example.gy3.R;
import org.greenrobot.eventbus.EventBus;
import androidx.fragment.app.Fragment;

/**
 * 第二页的Fragment
 */
public class HuaDongTwoFragment extends Fragment {
    private TextView tv_link;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(
            R.layout.fragment_hua_dong_two, container, false);
        tv_link = mView.findViewById(R.id.hua_dong_two_tv_link);
        touch_link();//点击链接
        return mView;
    }
    /**
     * 点击链接
     */
    private void touch_link(){
        tv_link.setOnClickListener(v -> {
            tv_link.setTextColor(Color.BLUE);//设置点击后网址变蓝色，表示已点击过该链接
            Uri uri = Uri.parse("http://community.kq-china.net:8081/news");//设置跳转网页
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        });
    }
    /**
     * Fragment可交互时发出消息
     */
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MessageEventMainActivity(2));
    }
}
