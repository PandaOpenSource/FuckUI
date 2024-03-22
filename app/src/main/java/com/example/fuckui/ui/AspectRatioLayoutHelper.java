package com.example.fuckui.ui;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.RequiresApi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 实现锁定宽高比控件的辅助类
 * <p>
 * Created by lixianpeng on 2019/1/9.
 * Copyright (c) 2019 Xunlei. All rights reserved.
 *
 * @author lixianpeng
 * @since 2019/1/9
 */
public class AspectRatioLayoutHelper {

    public interface Constants {
        int EDGE_FIXED_WIDTH = 0;
        int EDGE_FIXED_HEIGHT = 1;

        @IntDef({EDGE_FIXED_WIDTH, EDGE_FIXED_HEIGHT})
        @Retention(RetentionPolicy.SOURCE)
        @interface EdgeFixedType {
        }
    }

    public static class AspectRatioSavedState extends View.BaseSavedState implements Parcelable {

        public static final Creator<AspectRatioSavedState> CREATOR = new Creator<AspectRatioSavedState>() {
            @Override
            public AspectRatioSavedState createFromParcel(Parcel in) {
                return new AspectRatioSavedState(in);
            }

            @Override
            public AspectRatioSavedState[] newArray(int size) {
                return new AspectRatioSavedState[size];
            }
        };
        float mAspectRatio;
        int mFixedEdge;

        public AspectRatioSavedState(Parcel source) {
            super(source);

            mAspectRatio = source.readFloat();
            mFixedEdge = source.readInt();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public AspectRatioSavedState(Parcel source, ClassLoader loader) {
            super(source, loader);

            mAspectRatio = source.readFloat();
            mFixedEdge = source.readInt();
        }

        public AspectRatioSavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(mAspectRatio);
            dest.writeInt(mFixedEdge);
        }
    }

}
