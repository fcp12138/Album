package com.fcp.browse;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fcp.albumlibrary.R;

/**
 * 图片展示界面
 * Created by fcp on 2015/7/12.
 */
public class PhotoFragment extends Fragment {

    private String mImageUrl;
    private ProgressBar progressBar;
    private ImageView imageView;
    /**
     * 标记号码构造
     * @param imageUrl 图片的本地地址或网络url
     * @return PictureFragment
     */
    public static PhotoFragment newInstance(String imageUrl) {
        PhotoFragment f = new PhotoFragment();

        Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_browse_delete, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.browse_photo_loading);
        imageView=(ImageView)v.findViewById(R.id.browse_photo_item_image);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(mImageUrl)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .placeholder(R.drawable.ic_album_default_error)
                .error(R.drawable.ic_album_default_error)
                .into(imageView);

        /*ImageLoader.getInstance().displayImage(mImageUrl, imageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = getString(R.string.browse_photo_io_error);
                        break;
                    case DECODING_ERROR:
                        message = getString(R.string.browse_photo_decoding_error);
                        break;
                    case NETWORK_DENIED:
                        message = getString(R.string.browse_photo_network_denied);
                        break;
                    case OUT_OF_MEMORY:
                        message = getString(R.string.browse_photo_out_of_memory);
                        break;
                    case UNKNOWN:
                        message = getString(R.string.browse_photo_unknown);
                        break;
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }
        });*/

    }



}
