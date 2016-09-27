package com.fcp.browse;

import android.os.Parcel;

/**
 * 图片数据
 * 这是一个简单的例子，也可以使用Image类 {@link com.fcp.albumlibrary.bean.Image }
 * Created by fcp on 2016/9/27.
 */
public class PictureImpl implements Picture {

    private String path;

    @Override
    public String getPath() {
        return path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
    }

    public PictureImpl() {
    }

    protected PictureImpl(Parcel in) {
        this.path = in.readString();
    }

    public static final Creator<PictureImpl> CREATOR = new Creator<PictureImpl>() {
        @Override
        public PictureImpl createFromParcel(Parcel source) {
            return new PictureImpl(source);
        }

        @Override
        public PictureImpl[] newArray(int size) {
            return new PictureImpl[size];
        }
    };
}
