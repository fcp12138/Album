package com.fcp.albumlibrary.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fcp.albumlibrary.R;
import com.fcp.albumlibrary.bean.Image;
import com.fcp.albumlibrary.fragment.BrowseFragment;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 浏览照片
 * Created by fcp on 2016/8/1.
 */
public class BrowseActivity extends AppCompatActivity {
    /**
     * 记录状态
     */
    private static final String STATE_POSITION = "STATE_POSITION";
    /**
     * 图片的Url/path
     */
    public static final String PICTUREURL_LIST = "pictureurl_list";
    /**
     * 已选择的图片的Url/path
     */
    public static final String SELECT_IMAGE_LIST = "select_image_list";
    /**
     * 最大选择数量
     */
    public static final String SELECT_MAX_NUMBER = "select_max_number";
    /**
     * 显示的图片index
     */
    public static final String IMAGE_INDEX = "image_index";

    private ImageButton mleftbtn;//返回键
    private TextView mRightbtn;//完成键
    private TextView mIndicator;
    private ViewPager mViewPager;
    private RelativeLayout mToolbarLayout;//顶部toolbar
    private RelativeLayout mBottomBar;//底部栏
    //private MyCheckBox mCheckBox;//选择框
    private ImageView mImageView;//
    private LinearLayout mBottomLayout;//底部选择布局
    private ImagePagerAdapter mImagePagerAdapter;
    private ArrayList<Image> selectImages;

    private int mDefaultCount = 9;

    /**
     * 返回（点击完成）
     */
    public static final int RESULT_FINISH = 0x101;
    /**
     * 普通返回
     */
    public static final int RESULT_OK = 0x102;


    public static void startBrowsePhotoActivity(Fragment mFragment, ArrayList<Image> imageUrls, int imageIndex,
                                                ArrayList<Image> selectImages, int mDefaultCount, int requestCode) {
        Intent intent = new Intent(mFragment.getContext(), BrowseActivity.class);
        intent.putParcelableArrayListExtra(PICTUREURL_LIST, imageUrls);
        intent.putParcelableArrayListExtra(SELECT_IMAGE_LIST, selectImages);
        intent.putExtra(SELECT_MAX_NUMBER, mDefaultCount);
        intent.putExtra(IMAGE_INDEX, imageIndex);
        mFragment.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Intent intent = getIntent();
        mToolbarLayout = (RelativeLayout) findViewById(R.id.browse_toolbar);
        showStatueBar();
        mViewPager = (ViewPager) findViewById(R.id.activity_app_browse_photo_id);
        mRightbtn = (TextView) findViewById(R.id.browse_tool_right_btn);
        mRightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //完成
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(SELECT_IMAGE_LIST, selectImages);
                setResult(RESULT_FINISH, intent);
                finish();
            }
        });
        mleftbtn = (ImageButton) findViewById(R.id.browse_tool_left_btn);
        mleftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //完成
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(SELECT_IMAGE_LIST, selectImages);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mIndicator = (TextView) findViewById(R.id.browse_indicator);
        mDefaultCount = intent.getIntExtra(SELECT_MAX_NUMBER, 9);
        //底部
        mBottomBar = (RelativeLayout) findViewById(R.id.browse_bottom_bar);
        mBottomLayout = (LinearLayout) findViewById(R.id.browse_bottom_check_layout);
        mImageView = (ImageView) findViewById(R.id.browse_bottom_bar_check_mark);
        mBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectImages.contains(mImagePagerAdapter.fileList.get(mViewPager.getCurrentItem()))) {
                    selectImages.remove(mImagePagerAdapter.fileList.get(mViewPager.getCurrentItem()));
                    mImageView.setImageResource(R.drawable.ic_album_btn_unselected);
                } else {
                    if (selectImages.size() >= mDefaultCount) {
                        Toast.makeText(BrowseActivity.this, "已经达到最高选择数量", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectImages.add(mImagePagerAdapter.fileList.get(mViewPager.getCurrentItem()));
                    mImageView.setImageResource(R.drawable.ic_album_btn_selected);
                }
                if (selectImages == null || selectImages.size() <= 0) {
                    mRightbtn.setText("完成");
                    mRightbtn.setEnabled(false);
                } else {
                    mRightbtn.setText("完成(" + selectImages.size() + "/" + mDefaultCount + ")");
                    mRightbtn.setEnabled(true);
                }
            }
        });
        //数据
        ArrayList<Image> mArrayList = intent.getParcelableArrayListExtra(PICTUREURL_LIST);//总数
        selectImages = intent.getParcelableArrayListExtra(SELECT_IMAGE_LIST);//已选择
        if (selectImages == null || selectImages.size() <= 0) {
            mRightbtn.setText("完成");
            mRightbtn.setEnabled(false);
        } else {
            mRightbtn.setText("完成(" + selectImages.size() + "/" + mDefaultCount + ")");
            mRightbtn.setEnabled(true);
        }
        //获得要显示的图片的index
        int pagerPosition = getIntent().getIntExtra(IMAGE_INDEX, 0);
        //监听器
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(R.string.browse_photo_viewpager_indicator, position + 1, mViewPager.getAdapter().getCount());
                mIndicator.setText(text);
                //判断是否已选择
                if (selectImages.contains(mImagePagerAdapter.fileList.get(position))) {
                    mImageView.setImageResource(R.drawable.ic_album_btn_selected);
                } else {
                    mImageView.setImageResource(R.drawable.ic_album_btn_unselected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //取出上次的状态
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        //设置适配器
        mImagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mArrayList);
        mViewPager.setAdapter(mImagePagerAdapter);
        //底部标注
        CharSequence text = getString(R.string.browse_photo_viewpager_indicator, pagerPosition + 1, mImagePagerAdapter.getCount());
        mIndicator.setText(text);
        //选择显示页
        mViewPager.setCurrentItem(pagerPosition);
        if(pagerPosition == 0){//如果是第一个；addOnPageChangeListener抓取不到
            //判断是否已选择
            if (selectImages.contains(mImagePagerAdapter.fileList.get(0))) {
                mImageView.setImageResource(R.drawable.ic_album_btn_selected);
            } else {
                mImageView.setImageResource(R.drawable.ic_album_btn_unselected);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mViewPager.getCurrentItem());
    }


    /**
     * 图片滑动适配器
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter implements PhotoViewAttacher.OnViewTapListener {

        private ArrayList<Image> fileList;
        private Animation mToolBarDownAnimation;
        private Animation mToolbarUpAnimation;
        private Animation mBottomUpAnimation;
        private Animation mBottomDownAnimation;

        ImagePagerAdapter(FragmentManager fm, ArrayList<Image> fileList) {
            super(fm);
            this.fileList = fileList;
            mToolBarDownAnimation = AnimationUtils.loadAnimation(BrowseActivity.this, R.anim.anim_toolbar_down);
            mToolbarUpAnimation = AnimationUtils.loadAnimation(BrowseActivity.this, R.anim.anim_toolbar_up);
            mBottomUpAnimation = AnimationUtils.loadAnimation(BrowseActivity.this, R.anim.anim_bottom_up);
            mBottomDownAnimation = AnimationUtils.loadAnimation(BrowseActivity.this, R.anim.anim_bottom_down);
            mToolbarUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    hideStatueBar();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            Image image = fileList.get(position);
            BrowseFragment mBrowseFragment = BrowseFragment.newInstance(image.path);
            mBrowseFragment.setOnClickListener(this);
            return mBrowseFragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public ArrayList<Image> getFileList() {
            return fileList;
        }

        /**
         * 点击图片
         */
        @Override
        public void onViewTap(View view, float v, float v1) {
            if (mToolbarLayout.getVisibility() == View.GONE) {
                showStatueBar();
                mToolbarLayout.startAnimation(mToolBarDownAnimation);
                mBottomBar.startAnimation(mBottomUpAnimation);
                mToolbarLayout.setVisibility(View.VISIBLE);
                mBottomBar.setVisibility(View.VISIBLE);
            } else {
                mToolbarLayout.startAnimation(mToolbarUpAnimation);
                mBottomBar.startAnimation(mBottomDownAnimation);
                mToolbarLayout.setVisibility(View.GONE);
                mBottomBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //完成
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(SELECT_IMAGE_LIST, selectImages);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //完成
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(SELECT_IMAGE_LIST, selectImages);
            setResult(RESULT_OK, intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ){
            //在SDK_INT上时，将toolbar下移
            RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mToolbarLayout.getLayoutParams();
            if(mLayoutParams != null){
                int height = getStatueHeight();
                mLayoutParams.setMargins(0,height,mLayoutParams.width,mLayoutParams.height+height);
                mToolbarLayout.setLayoutParams(mLayoutParams);
            }
        }
    }

    private int getStatueHeight(){
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    private void showStatueBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void hideStatueBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

}

