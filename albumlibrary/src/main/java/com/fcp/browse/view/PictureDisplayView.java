package com.fcp.browse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fcp.albumlibrary.R;
import com.fcp.browse.model.Picture;

import java.util.ArrayList;

/**
 * 图片展示显示控件
 * Created by fcp on 2015/12/12.
 */
public class PictureDisplayView<T extends Picture> extends FlowLayout implements View.OnClickListener{

    private Context mContext;
    /**
     * 图片列表
     */
    private ArrayList<T> mArrayList;
    /**
     * 最大的数量
     */
    private int maxCount = 9 ;
    /**
     * 图片方格
     */
    private int size;
    /**
     * 边距
     */
    private int side;


    public PictureDisplayView(Context context) {
        super(context);
        initView(context);
    }

    public PictureDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PictureDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化
     * @param context Context
     */
    private void initView(Context context) {
        this.mContext = context;
        mArrayList = new ArrayList<T>();
        size = dip2px(context, 60);
        side = size / 10;
        this.setPadding(side, side, side, side);
    }

    /**
     * 显示图片
     * @param arrayList ArrayList<PhotoData>
     */
    public void showPicture(ArrayList<T> arrayList){
        removeAllViews();//清空
        mArrayList.clear();
        for(T photoData :arrayList){
            ImageView imageView = createItem();
            if(imageView!=null){
                mArrayList.add(photoData);
                Glide.with(getContext())
                        .load(photoData.getPath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_album_default_error)
                        .error(R.drawable.ic_album_default_error)
                        .into(imageView);
            }
        }
    }

    private ImageView createItem(){
        ImageView imageView = null;
        if(mArrayList.size() < maxCount){
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.selector_pictureview_add);
            if(this.isClickable()) {
                imageView.setOnClickListener(this);
            }else {
                imageView.setClickable(false);
            }
            LayoutParams layoutParams = new LayoutParams(size,size);
            layoutParams.setMargins(side,side,side,side);
            this.addView(imageView, mArrayList.size(),layoutParams );
        }
        return imageView;
    }

    private OnPictureDisplayClick mOnPictureDisplayClick;

    public void setOnPictureDisplayClick(OnPictureDisplayClick onPictureDisplayClick) {
        if(onPictureDisplayClick != null){
            this.setClickable(true);
        }
        mOnPictureDisplayClick = onPictureDisplayClick;
    }

    @Override
    public void onClick(View view) {
        if(mOnPictureDisplayClick == null)return;
        for(int i=0 ;i< getChildCount();i++){
            if(getChildAt(i) == view){
                mOnPictureDisplayClick.clickItem(i);
                break;
            }
        }
    }

    public ArrayList<T> getArrayList() {
        return mArrayList;
    }

    public interface OnPictureDisplayClick{
        void clickItem(int position);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
