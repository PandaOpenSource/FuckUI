package com.example.fuckui.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.fuckui.R;


/**
 * 锁定宽高比的ImageView控件
 * <p>
 * Created by lixianpeng on 2019/1/9.
 * Copyright (c) 2019 Xunlei. All rights reserved.
 *
 * @author lixianpeng
 * @since 2019/1/9
 */
@SuppressWarnings("unused")
public class AspectRatioImageView extends AppCompatImageView implements AspectRatioLayoutHelper.Constants {
    /**
     * 宽高比
     */
    private float mAspectRatio = 16f / 9f;

    /**
     * 固定宽，固定高
     */
    @EdgeFixedType
    private int mFixedEdge = EDGE_FIXED_WIDTH;

    public AspectRatioImageView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView, defStyleAttr, defStyleRes);

        mAspectRatio = a.getFloat(R.styleable.AspectRatioImageView_arl_aspect_ratio, mAspectRatio);
        mFixedEdge = a.getInt(R.styleable.AspectRatioImageView_arl_fixed_edge, mFixedEdge);

        a.recycle();
    }

    public float getAspectRatio() {
        return mAspectRatio;
    }

    /**
     * 设置宽高比
     */
    public void setAspectRatio(float ratio) {
        mAspectRatio = ratio;
        if (isInLayout()) {
            return;
        }
        requestLayout();
    }

    /**
     * 设置宽高比(兼容旧的类)
     */
    public void setRatio(float ratio) {
        setAspectRatio(ratio);
    }

    /**
     * 设置宽，单位px
     */
    public void setWidth(int width) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
//        AspectRatioLayoutHelper.AspectRatioSavedState savedState = new AspectRatioLayoutHelper.AspectRatioSavedState(state);
//        savedState.mAspectRatio = mAspectRatio;
//        savedState.mFixedEdge = mFixedEdge;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof AspectRatioLayoutHelper.AspectRatioSavedState) {
            final AspectRatioLayoutHelper.AspectRatioSavedState savedState = (AspectRatioLayoutHelper.AspectRatioSavedState) state;
            mAspectRatio = savedState.mAspectRatio;
            mFixedEdge = savedState.mFixedEdge;
            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mAspectRatio > 0) {
            if (mFixedEdge == EDGE_FIXED_WIDTH) {
                int width = getMeasuredWidth();
                int height = Math.round(width / mAspectRatio + 0.5f);
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            } else if (mFixedEdge == EDGE_FIXED_HEIGHT) {
                int height = getMeasuredHeight();
                int width = Math.round(height * mAspectRatio + 0.5f);
                super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }
    }
}
