package com.fcp.albumlibrary.adapter;

import android.support.v4.app.Fragment;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fcp.albumlibrary.R;

import java.io.File;

/**
 *  图片加载
 * Created by fcp on 2016/7/29.
 */
public abstract class BaseLoadAdapter extends BaseAdapter{

    protected Fragment fragment;

    public BaseLoadAdapter(Fragment fragment) {
        this.fragment = fragment;
    }


    protected void loadImage(String path , ImageView imageView){
        Glide.with(fragment)
                .load(path)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_album_default_error)
                .error(R.drawable.ic_album_default_error)
                .diskCacheStrategy( DiskCacheStrategy.NONE )
                //.crossFade()
                .into(imageView);
    }


}
