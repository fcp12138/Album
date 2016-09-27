package com.fcp.browse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fcp.albumlibrary.R;

import java.util.ArrayList;


/**
 * 浏览照相界面(包含删除功能)
 * intent 包含HAVEDELETE，PICTUREURL_LIST，IMAGE_INDEX
 * Created by fcp on 2015/12/9.
 */
public class BrowseDeleteActivity<T extends Picture> extends AppCompatActivity implements View.OnClickListener {
    public static final int OPEN_PHOTO = 0x834;// 打开的请求码和返回值码
    public static final String PICTURE_DELETE = "picture_delete";//返回删除的页面数值
    //记录状态
    private static final String STATE_POSITION = "STATE_POSITION";
    /**
     * 是否具有删除功能(1有； 0无)
     */
    public static final String HAVEDELETE ="havedelete";
    /**
     * 图片的Url/path
     */
    public static final String PICTUREURL_LIST ="pictureurl_list";
    /**
     * 显示的图片index
     */
    public static final String IMAGE_INDEX = "image_index";
    /**
     * 显示标注
     */
    private TextView indicator;
    private ImageButton browseLeftBtn;
    private TextView browseRightBtn;
    private RelativeLayout toolBar;
    private ViewPager viewPager;
    private ImagePagerAdapter mAdapter;// 图片适配器
    private ArrayList<Integer> mDeleteArrayList = new ArrayList<Integer>();//删除的index_in_Browse


    public static void startBrowsePhotoActivity(Activity mActivity , ArrayList<? extends Picture> imageUrls
            , int imageIndex , boolean haveDelete ,int requestCode){
        Intent intent = new Intent(mActivity,BrowseDeleteActivity.class);
        intent.putParcelableArrayListExtra(PICTUREURL_LIST,imageUrls);
        intent.putExtra(IMAGE_INDEX,imageIndex);
        intent.putExtra(HAVEDELETE , haveDelete);
        mActivity.startActivityForResult(intent , requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_delete);
        toolBar = (RelativeLayout) findViewById(R.id.browse_tool_bar);
        browseRightBtn = (TextView) findViewById(R.id.browse_tool_right_btn);
        browseLeftBtn = (ImageButton) findViewById(R.id.browse_tool_left_btn);
        browseLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnData();
                BrowseDeleteActivity.this.finish();
            }
        });
        //-----------------------------------------资源属性----------------------------------------------
        Intent intent=getIntent();
        //判断是否显示删除按钮
        if(intent.getBooleanExtra(HAVEDELETE,true)){
            browseRightBtn.setVisibility(View.VISIBLE);
            browseRightBtn.setText("删除");
            browseRightBtn.setOnClickListener(this);
        }else{
            browseRightBtn.setVisibility(View.INVISIBLE);
            browseRightBtn.setOnClickListener(null);
        }
        //获得图片imageUrl的List
        ArrayList<T> imageUrls = intent.getParcelableArrayListExtra(PICTUREURL_LIST);
        //获得要显示的图片的index
        int pagerPosition = getIntent().getIntExtra(IMAGE_INDEX, 0);

        //-----------------------------------------初始化----------------------------------------------
        //滑动控件
        viewPager = (ViewPager)findViewById(R.id.activity_app_browse_photo_id);
        //创建适配器
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageUrls);
        //绑定适配器
        viewPager.setAdapter(mAdapter);

        //底部标注
        indicator = (TextView) findViewById(R.id.browse_indicator);
        CharSequence text = getString(R.string.browse_photo_viewpager_indicator, 1, viewPager.getAdapter().getCount());
        indicator.setText(text);

        //监听器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(R.string.browse_photo_viewpager_indicator, position + 1, viewPager.getAdapter().getCount());
                indicator.setText(text);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //取出上次的状态
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        //选择显示页
        viewPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, viewPager.getCurrentItem());
    }

    /**
     * 删除
     */
    private void deletePhoto(){
        int index = viewPager.getCurrentItem();
        mDeleteArrayList.add(index);//记录下来
        if(mAdapter.fileList.size()==1){
            mAdapter.fileList.remove(index);
            mAdapter.notifyDataSetChanged();
            //返回数据
            returnData();
            this.finish();
            return;
        }else if(index>=mAdapter.fileList.size()-1){//最后一张
            viewPager.setCurrentItem(index - 1,false);
            mAdapter.fileList.remove(index);
        }else{
            mAdapter.fileList.remove(index);
        }
        CharSequence text = getString(R.string.browse_photo_viewpager_indicator, viewPager.getCurrentItem() + 1, viewPager.getAdapter().getCount());
        indicator.setText(text);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 返回数据
     */
    private void returnData(){
        Intent intent=new Intent();
        Bundle bundle1=new Bundle();
        bundle1.putIntegerArrayList(PICTURE_DELETE ,mDeleteArrayList);
        intent.putExtras(bundle1);
        setResult(OPEN_PHOTO, intent);
    }

    @Override
    public void onClick(View v) {
        deletePhoto();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //完成
            returnData();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 图片滑动适配器
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<T> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<T> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return  PhotoFragment.newInstance(fileList.get(position).getPath());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


}


