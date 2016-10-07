package com.fcp.browse;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fcp.albumlibrary.R;
import com.fcp.browse.model.BitmapLocation;
import com.fcp.browse.model.PictureImpl;

import uk.co.senab.photoview.PhotoView;

/**
 * 带动画的浏览界面
 * Created by fcp on 2016/9/29.
 */
public class BrowseAnimActivity extends Activity{

    public static final String DATA = "data";

    BitmapLocation bitmapLocation;
    PhotoView imageView;
    RelativeLayout mParent;

    float x;
    float y;

    float mWindowWidth;
    float mWindowHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_anim_browse);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }else {
            //TODO
        }
        imageView = (PhotoView) findViewById(R.id.photo_view);
        mParent = (RelativeLayout) findViewById(R.id.photo_parent_view);
        bitmapLocation = getIntent().getParcelableExtra(DATA);
        if(bitmapLocation == null){
            bitmapLocation = new BitmapLocation();
            bitmapLocation.setmPicture(new PictureImpl());
        }
        Glide.with(this)
                .load(bitmapLocation.getmPicture().getPath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_album_default_error)
                .error(R.drawable.ic_album_default_error)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);

//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
//        layoutParams.height = bitmapLocation.getHeight();
//        layoutParams.width = bitmapLocation.getWidth();
//        imageView.setX(bitmapLocation.getStartX());
//        imageView.setY(bitmapLocation.getStartY());
//        imageView.setLayoutParams(layoutParams);

        WindowManager wm = this.getWindowManager();
        mWindowWidth = wm.getDefaultDisplay().getWidth();
        mWindowHeight = wm.getDefaultDisplay().getHeight();

        //float centerX = (bitmapLocation.getStartX() + bitmapLocation.getWidth()/2.0f);
        float centerY =  (bitmapLocation.getStartY() + bitmapLocation.getHeight()/2.0f);


        //缩放动画：
//        第一个参数fromX ,第二个参数toX:分别是动画起始、结束时X坐标上的伸缩尺寸。
//        第三个参数fromY ,第四个参数toY:分别是动画起始、结束时Y坐标上的伸缩尺寸。
//        另外还可以设置伸缩模式pivotXType、pivotYType， 伸缩动画相对于x,y 坐标的开始位置pivotXValue、pivotYValue等

        float x1 = bitmapLocation.getStartX() / (mWindowWidth * 1.0f);
        //float y1 = bitmapLocation.getStartY() / (height * 1.0f);
        float y1 = (centerY - ( bitmapLocation.getWidth() * (mWindowHeight / mWindowWidth) / 2.0f)) / (mWindowHeight * 1.0f);

        float x2 = (bitmapLocation.getStartX() + bitmapLocation.getWidth())/(mWindowWidth * 1.0f);


        x = x1/(x1 + 1.0f - x2);
        y =  y1 / x1 * x;

        float rate = (bitmapLocation.getWidth()*1.0f)/(mWindowWidth*1.0f);
        ScaleAnimation scaleAnimation = new ScaleAnimation(rate, 1.0f, rate,1.0f ,
                ScaleAnimation.RELATIVE_TO_SELF,x,ScaleAnimation.RELATIVE_TO_SELF,y);
        scaleAnimation.setDuration(300);
        mParent.startAnimation(scaleAnimation);
    }

    boolean isOperation = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && isOperation){
            return true;
        }
        //截获按键事件
        if(keyCode == KeyEvent.KEYCODE_BACK){
            isOperation = true;

            float rate = (bitmapLocation.getWidth()*1.0f)/(mWindowWidth*1.0f);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, rate ,1.0f ,rate ,
                    ScaleAnimation.RELATIVE_TO_SELF,x,ScaleAnimation.RELATIVE_TO_SELF,y);
            scaleAnimation.setDuration(300);
            mParent.startAnimation(scaleAnimation);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                    overridePendingTransition(0,0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
