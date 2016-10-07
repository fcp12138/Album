package com.fcp.browse.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片的属性值
 * Created by fcp on 2016/9/30.
 */

public class BitmapLocation implements Parcelable {

    /**
     * 图片
     */
    Picture mPicture;

    /**
     * 开始的X值
     */
    float startX;
    /**
     * 开始的Y值
     */
    float startY;
    /**
     * 高度
     */
    int height;
    /**
     * 宽度
     */
     int width;

    public BitmapLocation() {
    }

    public BitmapLocation(Picture mPicture, float startX, float startY, int height, int width) {
        this.mPicture = mPicture;
        this.startX = startX;
        this.startY = startY;
        this.height = height;
        this.width = width;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mPicture, flags);
        dest.writeFloat(this.startX);
        dest.writeFloat(this.startY);
        dest.writeInt(this.height);
        dest.writeInt(this.width);
    }

    protected BitmapLocation(Parcel in) {
        this.mPicture = in.readParcelable(Picture.class.getClassLoader());
        this.startX = in.readFloat();
        this.startY = in.readFloat();
        this.height = in.readInt();
        this.width = in.readInt();
    }

    public static final Creator<BitmapLocation> CREATOR = new Creator<BitmapLocation>() {
        @Override
        public BitmapLocation createFromParcel(Parcel source) {
            return new BitmapLocation(source);
        }

        @Override
        public BitmapLocation[] newArray(int size) {
            return new BitmapLocation[size];
        }
    };

    public Picture getmPicture() {
        return mPicture;
    }

    public void setmPicture(Picture mPicture) {
        this.mPicture = mPicture;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
