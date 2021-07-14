package com.example.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.gy3.R;
import com.example.interpolator.SpringScaleInterpolator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

import androidx.core.view.accessibility.AccessibilityViewCommand.SetTextArguments;

/**
 * RecycleView的适配器
 */
public class MyRecyclerViewAdapter extends BaseQuickAdapter<Model,BaseViewHolder> {
    public MyRecyclerViewAdapter(int layoutResId, @Nullable List<Model> data) {
        super(layoutResId, data);
    }
    
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Model model) {
        baseViewHolder
            .setText(R.id.rv_tv_title,model.getTitle())
            .setText(R.id.rv_tv_content,model.getContent())
            .setText(R.id.rv_tv_value,model.getValue());
    }
}