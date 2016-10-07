package com.fcp.album;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fcp.albumlibrary.bean.Image;
import com.fcp.browse.BrowseAnimActivity;
import com.fcp.browse.model.BitmapLocation;
import com.fcp.browse.view.PictureDisplayView;

import java.util.ArrayList;

/**
 * 照片结果显示界面
 */
public class BrowsePhotoActivity extends AppCompatActivity {

    PictureDisplayView<Image> mImagePictureDisplayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_photo);
        mImagePictureDisplayView = (PictureDisplayView<Image>) findViewById(R.id.browse_display);
        //获得图片信息
        ArrayList<Image> images = getIntent().getParcelableArrayListExtra("data");
        mImagePictureDisplayView.showPicture(images);
        mImagePictureDisplayView.setOnPictureDisplayClick(new PictureDisplayView.OnPictureDisplayClick() {
            @Override
            public void clickItem(int position) {
                Intent intent = new Intent(BrowsePhotoActivity.this,BrowseAnimActivity.class);
                ImageView imageView = (ImageView) mImagePictureDisplayView.getChildAt(position);
                int[] location = new int[2];
                imageView.getLocationOnScreen(location);
                BitmapLocation bitmapLocation = new BitmapLocation(mImagePictureDisplayView.getArrayList().get(position),
                        location[0],location[1],imageView.getHeight(),imageView.getWidth());
                intent.putExtra(BrowseAnimActivity.DATA,bitmapLocation);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }



}
