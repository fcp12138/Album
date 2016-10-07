package com.fcp.browse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fcp.albumlibrary.R;
import com.fcp.browse.model.Picture;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片选择控件
 * T 继承 Picture
 * Created by fcp on 2015/12/8.
 */
public class PictureSelectView<T extends Picture> extends FlowLayout implements View.OnClickListener {


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
    /**
     * 加号的imageview
     */
    private ImageView mAddImageView;

    public PictureSelectView(Context context) {
        super(context);
        initView(context);
    }

    public PictureSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PictureSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化
     * @param context Context
     */
    private void initView(Context context) {
        this.mContext = context;
        mArrayList = new ArrayList<T>();
        size = dip2px(context , 60);
        side = size / 10;
        this.setPadding(side, side, side, side);
        mAddImageView = addEmptyImageView();
    }

    /**
     * 添加图片
     * @param picturePath 包含信息的类
     */
    public void addPicture(T picturePath){
        if(mAddImageView != null) {
            Glide.with(getContext())
                    .load(picturePath.getPath())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.ic_album_default_error)
                    .error(R.drawable.ic_album_default_error)
                    .into(mAddImageView);
            mArrayList.add(picturePath);
            mAddImageView = addEmptyImageView();
        }
    }

    public void addPicture(List<T> pics){
        for(T t:pics){
            addPicture(t);
        }
    }

    /**
     * 删除图片
     * @param position int
     */
    public T delete(int position){
        T m = mArrayList.remove(position);
        for(int i=0;i<getChildCount();i++){
            if(position == i){
                removeViewAt(position);
                break;
            }
        }
        if(mAddImageView == null) mAddImageView = addEmptyImageView();
        return m;
    }

    /**
     * 清空
     */
    public void clearAll(){
        mArrayList.clear();
        this.removeAllViews();
        mAddImageView = addEmptyImageView();
    }

    /**
     * 获得设置大小Imageview控件(+号图片)
     * @return ImageView
     */
    private ImageView addEmptyImageView(){
        ImageView imageView = null;
        if(mArrayList.size() < maxCount){
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.selector_pictureview_add);
            imageView.setOnClickListener(this);
            LayoutParams layoutParams = new LayoutParams(size,size);
            layoutParams.setMargins(side,side,side,side);
            this.addView(imageView, mArrayList.size(),layoutParams );
        }
        return imageView;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 剩余数量
     */
    public int remainSize(){
        return maxCount - mArrayList.size();
    }


    public ArrayList<T> getArrayList() {
        return mArrayList;
    }

    public void setArrayList(ArrayList<T> arrayList) {
        mArrayList = arrayList;
    }


    OnPictureSelectClick mOnPictureSelectClick = new OnPictureSelectClick() {
        @Override
        public void clickAddPicture() {
        }

        @Override
        public void clickNormalItem(int position) {
        }
    };

    public void setOnPictureSelectClick(OnPictureSelectClick onPictureSelectClick) {
        mOnPictureSelectClick = onPictureSelectClick;
    }

    @Override
    public void onClick(View view) {
        for(int i=0 ;i< getChildCount();i++){
            if(getChildAt(i) == view){
                if(mAddImageView != null && view == mAddImageView){
                    mOnPictureSelectClick.clickAddPicture();
                }else{
                    mOnPictureSelectClick.clickNormalItem(i);
                }
                break;
            }
        }
    }

    public interface OnPictureSelectClick{
        void clickAddPicture();
        void clickNormalItem(int position);
    }




}
