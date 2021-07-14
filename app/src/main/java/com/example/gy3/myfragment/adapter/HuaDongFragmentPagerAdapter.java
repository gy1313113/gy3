package com.example.gy3.myfragment.adapter;

import com.example.gy3.myfragment.HuaDongFourFragment;
import com.example.gy3.myfragment.HuaDongOneFragment;
import com.example.gy3.myfragment.HuaDongThreeFragment;
import com.example.gy3.myfragment.HuaDongTwoFragment;

import org.jetbrains.annotations.NotNull;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * 页导航控件适配器
 */
public class HuaDongFragmentPagerAdapter extends FragmentStateAdapter {
    
    public HuaDongFragmentPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0){
            fragment = new HuaDongOneFragment();//下标为0时显示第一页
        }
        else if(position == 1){
            fragment = new HuaDongTwoFragment();//下标为1时显示第二页
        }
        else if(position == 2){
            fragment = new HuaDongThreeFragment();//下标为2时显示第三页
        }
        else {
            fragment = new HuaDongFourFragment();//下标为3时显示第四页
        }
        return fragment;
    }
    
    @Override
    public int getItemCount() {
        return 4;//设置页数
    }
}
