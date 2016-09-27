package com.fcp.albumlibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fcp.albumlibrary.R;
import com.fcp.albumlibrary.bean.Image;
import com.fcp.albumlibrary.fragment.AlbumFragment;
import com.fcp.albumlibrary.fragment.AlbumMultiFragment;
import com.fcp.albumlibrary.fragment.AlbumSingleFragment;

import java.io.File;

/**
 * 相册
 * Created by fcp on 2016/7/29.
 */
public class AlbumActivity extends AppCompatActivity implements AlbumFragment.CallBackListener{
    //单选
    public static final int MODE_SINGLE = 0;
    //多选
    public static final int MODE_MULTI = 1;

    /** 图片选择模式，默认多选 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
    public static final String EXTRA_RESULT = "select_result";

    private int mDefaultCount = 9;

    //private ImageButton mToolleftbtn;
    private TextView mToolrightbtn;
    //private FrameLayout mAlbumContent;

    //界面
    private AlbumFragment mAlbumFragment;

    /**
     * 打开相册
     * @param modeType 单选/多选
     * @param showCamera  显示拍照
     * @param maxNum 最大选择数量
     */
    public static void startAlbumActivity(Activity mActivity , int modeType , boolean showCamera , int maxNum , int mRequestCode){
        Intent intent = new Intent(mActivity,AlbumActivity.class);
        intent.putExtra(EXTRA_SELECT_MODE , modeType);
        intent.putExtra(EXTRA_SHOW_CAMERA , showCamera);
        intent.putExtra(EXTRA_SELECT_COUNT , maxNum);
        mActivity.startActivityForResult(intent,mRequestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        //mAlbumContent = (FrameLayout) findViewById(R.id.album_content);
        mToolrightbtn = (TextView) findViewById(R.id.album_tool_right_btn);
        ImageButton mToolleftbtn = (ImageButton) findViewById(R.id.album_tool_left_btn);
        if (mToolleftbtn != null) {
            mToolleftbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        mToolrightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlbumFragment.getSelectedImages().size() > 0){
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putParcelableArrayListExtra(EXTRA_RESULT, mAlbumFragment.getSelectedImages());
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        //获得数据判断情况
        Intent intent = getIntent();
        int modeType = intent.getIntExtra(EXTRA_SELECT_MODE,MODE_SINGLE);
        boolean showCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA,false);
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT , 9);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_SHOW_CAMERA,showCamera);
        bundle.putInt(AlbumActivity.EXTRA_SELECT_COUNT, mDefaultCount);//选择的最大数量
        //选择的Fragment
        mAlbumFragment = (AlbumFragment) getSupportFragmentManager().findFragmentByTag("Fragment");
        if(mAlbumFragment != null){
            return;
        }
        if(modeType == MODE_MULTI){
            //多选
            mAlbumFragment = (AlbumFragment) Fragment.instantiate(this, AlbumMultiFragment.class.getName(), bundle);
            mAlbumFragment.setCallBackListener(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.album_content, mAlbumFragment,"Fragment")
                    .commit();
            mToolrightbtn.setVisibility(View.VISIBLE);
            mToolrightbtn.setBackgroundResource(R.drawable.selector_album_action_btn);
            mToolrightbtn.setText("完成");
            mToolrightbtn.setEnabled(false);
        }else {
            mAlbumFragment = (AlbumFragment) Fragment.instantiate(this, AlbumSingleFragment.class.getName(), bundle);
            mAlbumFragment.setCallBackListener(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.album_content, mAlbumFragment ,"Fragment")
                    .commit();
            mToolrightbtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSingleImageSelected() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(EXTRA_RESULT, mAlbumFragment.getSelectedImages());
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * 在多选模式下选中/取消
     */
    @Override
    public void onMultiImageSelected() {
        int num = mAlbumFragment.getSelectedImages().size();
        if(num > 0){
            mToolrightbtn.setText("完成(" +  num  + "/" + mDefaultCount + ")");
            if(!mToolrightbtn.isEnabled()){
                mToolrightbtn.setEnabled(true);
            }
        }else {
            mToolrightbtn.setText("完成");
            mToolrightbtn.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if(imageFile != null) {
            Intent data = new Intent();
            Image image = new Image(imageFile.getAbsolutePath(),imageFile.getName(),System.currentTimeMillis());
            mAlbumFragment.getSelectedImages().add(image);
            data.putParcelableArrayListExtra(EXTRA_RESULT, mAlbumFragment.getSelectedImages());
            setResult(RESULT_OK, data);
            finish();
        }
    }

    /**
     * 在浏览模式下选择完成
     */
    @Override
    public void onFinishInBrowse() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(EXTRA_RESULT, mAlbumFragment.getSelectedImages());
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache() {
        try {
            Glide.get(this).clearMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
