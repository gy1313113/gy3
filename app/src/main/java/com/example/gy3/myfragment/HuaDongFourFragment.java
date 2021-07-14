package com.example.gy3.myfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventbus.MessageEventMainActivity;
import com.example.gy3.R;

import org.greenrobot.eventbus.EventBus;

import androidx.fragment.app.Fragment;

/**
 * 第四页的Fragment
 */
public class HuaDongFourFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (ViewGroup) inflater.inflate(
            R.layout.fragment_hua_dong_four, container, false);
    }
    
    /**
     * Fragment可交互时发出消息
     */
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MessageEventMainActivity(4));
    }
}

