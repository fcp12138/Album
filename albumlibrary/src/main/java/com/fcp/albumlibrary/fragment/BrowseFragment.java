package com.fcp.albumlibrary.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fcp.albumlibrary.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 浏览网页
 * Created by fcp on 2016/8/9.
 */
public class BrowseFragment extends Fragment {

    private String mImageUrl;
    private ProgressBar progressBar;
    private PhotoView imageView;
    private PhotoViewAttacher.OnViewTapListener mOnClickListener;

    public static BrowseFragment newInstance(String imageUrl) {
        BrowseFragment f = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl",imageUrl);
        f.setArguments(args);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mImageUrl = getArguments() != null ? getArguments().getString("imageUrl") : null;
        View v = inflater.inflate(R.layout.fragment_browse, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.browse_photo_loading);
        imageView=(PhotoView)v.findViewById(R.id.browse_photo_item_image);
        imageView.setOnViewTapListener(mOnClickListener);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(this).load(mImageUrl).asBitmap().listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                Toast.makeText(getContext(),"获取失败",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    public void setOnClickListener(PhotoViewAttacher.OnViewTapListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

}
