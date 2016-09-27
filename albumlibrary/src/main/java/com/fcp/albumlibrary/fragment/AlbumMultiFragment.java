package com.fcp.albumlibrary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fcp.albumlibrary.R;
import com.fcp.albumlibrary.activity.BrowseActivity;
import com.fcp.albumlibrary.adapter.ImageGridAdapter;
import com.fcp.albumlibrary.adapter.ImageMultiAdapter;
import com.fcp.albumlibrary.bean.Image;

import java.util.ArrayList;

/**
 * 多选
 * Created by fcp on 2016/7/29.
 */
public class AlbumMultiFragment extends AlbumFragment implements ImageMultiAdapter.MultiListener {

    public static final int REQUEST_CODE = 0x100;

    private TextView mBrowseText;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //显示下方的
        mBrowseText = (TextView) view.findViewById(R.id.browse_text);
        mBrowseText.setVisibility(View.VISIBLE);
        mBrowseText.setEnabled(false);
        mBrowseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseActivity.startBrowsePhotoActivity(AlbumMultiFragment.this, resultList, 0
                        , resultList,mDesireImageCount,REQUEST_CODE);
            }
        });
    }

    @Override
    ImageGridAdapter createGridAdapter() {
        ImageMultiAdapter multiAdapter = new ImageMultiAdapter(this,resultList,isShowCamera);
        multiAdapter.setMultiListener(this);
        return multiAdapter;
    }

    @Override
    void gridClickPicture(View view, int i) {
        BrowseActivity.startBrowsePhotoActivity(this, (ArrayList<Image>) mImageAdapter.getImages(),
                i, (ArrayList<Image>) mImageAdapter.getSelectedImages(),mDesireImageCount,REQUEST_CODE);
    }

    /**
     * 勾选框
     */
    @Override
    public void onItemClick(int position, View convertView) {
        Image image = mImageAdapter.getItem(position);
        if(image == null)return;
        if (resultList.contains(image)) {//已经有了
            resultList.remove(image);
            if (mCallBackListener != null) {
                mCallBackListener.onMultiImageSelected();
            }
        } else {
            // 判断选择数量问题
            if(mDesireImageCount == resultList.size()){
                Toast.makeText(getActivity(), "已经达到最高选择数量", Toast.LENGTH_SHORT).show();
                return;
            }
            resultList.add(image);
            if (mCallBackListener != null) {
                mCallBackListener.onMultiImageSelected();
            }
        }
        showBrowseText(resultList.size());//更新底部浏览
        ((ImageMultiAdapter)mImageAdapter).changeSelectStatue(position, convertView, mGridView);//更换状态
    }

    /**
     * 浏览返回
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && data != null){
            //设置返回回来的数据
            ArrayList<Image> mArrayList = data.getParcelableArrayListExtra(BrowseActivity.SELECT_IMAGE_LIST);
            resultList.clear();
            if(mArrayList == null){
                mArrayList = new ArrayList<Image>();
            }
            resultList.addAll(mArrayList);
            showBrowseText(mArrayList.size());//更新底部浏览
            mImageAdapter.notifyDataSetChanged();//更新相册
            if(resultCode == BrowseActivity.RESULT_OK){
                if (mCallBackListener != null) {
                    mCallBackListener.onMultiImageSelected();//更新顶部
                }
            }else if(resultCode == BrowseActivity.RESULT_FINISH){
                //浏览模式下选择完成
                if (mCallBackListener != null) {
                    mCallBackListener.onFinishInBrowse();
                }
            }
        }
    }

    private void showBrowseText(int num){
        if(num > 0 ){
            mBrowseText.setText("浏览("+num+")");
            mBrowseText.setEnabled(true);
        }else {
            mBrowseText.setText("浏览");
            mBrowseText.setEnabled(false);
        }
    }

}
