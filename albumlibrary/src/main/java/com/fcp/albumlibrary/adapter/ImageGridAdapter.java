package com.fcp.albumlibrary.adapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;


import com.fcp.albumlibrary.bean.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片适配器
 * Created by fcp on 2016/3/6.
 */
public abstract class ImageGridAdapter extends BaseLoadAdapter{

    protected static final int TYPE_CAMERA = 0;
    protected static final int TYPE_NORMAL = 1;

    protected boolean showCamera = true;
    protected LayoutInflater mInflater;

    protected List<Image> mImages = new ArrayList<>();//总的图片
    protected List<Image> mSelectedImages = new ArrayList<>();//选择的图片

    public ImageGridAdapter(Fragment mFragment, List<Image> mSelectedImages, boolean showCamera) {
        super(mFragment);
        this.showCamera = showCamera;
        mInflater = LayoutInflater.from(mFragment.getContext());
        this.mSelectedImages = mSelectedImages;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(showCamera){
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size()+1 : mImages.size();
    }

    @Override
    public Image getItem(int position) {
        if(showCamera){
            if(position == 0){
                return null;
            }
            return mImages.get(position-1);
        }else{
            return mImages.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setShowCamera(boolean b){
        if(showCamera == b) return;
        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera(){
        return showCamera;
    }

    /**
     * 设置数据集
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();
        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     */
    public void setDefaultSelected(ArrayList<Image> resultList) {
        mSelectedImages.clear();
        for(Image image : resultList){
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        if(mSelectedImages.size() > 0){
            notifyDataSetChanged();
        }
    }

    /**
     * 更新数据
     */
    public void update(List<Image> images){
        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }


    /**
     * 显示的所有图
     */
    public List<Image> getImages() {
        return mImages;
    }

    public List<Image> getSelectedImages() {
        return mSelectedImages;
    }

    public void setSelectedImages(List<Image> mSelectedImages) {
        this.mSelectedImages = mSelectedImages;
    }
}
