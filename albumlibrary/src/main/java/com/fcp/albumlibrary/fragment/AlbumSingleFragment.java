package com.fcp.albumlibrary.fragment;

import android.view.View;

import com.fcp.albumlibrary.adapter.ImageGridAdapter;
import com.fcp.albumlibrary.adapter.ImageSingleAdapter;
import com.fcp.albumlibrary.bean.Image;

/**
 * 单选
 * Created by fcp on 2016/7/29.
 */
public class AlbumSingleFragment extends AlbumFragment {
    @Override
    ImageGridAdapter createGridAdapter() {
        return new ImageSingleAdapter(this,resultList,isShowCamera);
    }

    @Override
    void gridClickPicture(View view, int i) {
        Image image =  mImageAdapter.getItem(i);
        resultList.add(image);
        // 单选模式
        if(mCallBackListener != null && image != null){
            mCallBackListener.onSingleImageSelected();
        }
    }
}
