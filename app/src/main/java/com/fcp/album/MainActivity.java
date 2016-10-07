package com.fcp.album;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fcp.albumlibrary.activity.AlbumActivity;
import com.fcp.albumlibrary.bean.Image;
import com.fcp.browse.BrowseDeleteActivity;
import com.fcp.browse.view.PictureSelectView;

import java.util.ArrayList;

import static com.fcp.albumlibrary.activity.AlbumActivity.EXTRA_RESULT;
import static com.fcp.browse.BrowseDeleteActivity.OPEN_PHOTO;
import static com.fcp.browse.BrowseDeleteActivity.PICTURE_DELETE;

public class MainActivity extends AppCompatActivity {

    private static final int mRequestCode = 0x10;
    private static final int mRequestCode2 = 0x11;
    private PictureSelectView<Image> mSelectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSelectView = (PictureSelectView<Image>) findViewById(R.id.picture_view);
        mSelectView.setOnPictureSelectClick(new PictureSelectView.OnPictureSelectClick() {
            @Override
            public void clickAddPicture() {
                //打开相册获得图片
                AlbumActivity.startAlbumActivity(MainActivity.this,AlbumActivity.MODE_MULTI,true,mSelectView.remainSize(),mRequestCode);
            }

            @Override
            public void clickNormalItem(int position) {
                BrowseDeleteActivity.startBrowsePhotoActivity(MainActivity.this,mSelectView.getArrayList(),position,true,mRequestCode2);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == mRequestCode && resultCode == RESULT_OK){
            ArrayList<Image> images = data.getParcelableArrayListExtra(EXTRA_RESULT);
            mSelectView.addPicture(images);
        }else if(requestCode == mRequestCode2 && resultCode == OPEN_PHOTO){
            ArrayList<Integer> mDeleteArrayList = data.getIntegerArrayListExtra(PICTURE_DELETE);
            for(Integer integer :mDeleteArrayList){
                mSelectView.delete(integer);
            }
        }
    }

    /**
     * 点击确定
     */
    public void onClickBtn(View view) {
        Intent intent = new Intent(this,BrowsePhotoActivity.class);
        intent.putExtra("data",mSelectView.getArrayList());
        startActivity(intent);
    }
}
