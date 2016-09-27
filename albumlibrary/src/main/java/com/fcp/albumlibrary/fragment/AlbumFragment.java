package com.fcp.albumlibrary.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ListPopupWindow;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fcp.albumlibrary.R;
import com.fcp.albumlibrary.activity.AlbumActivity;
import com.fcp.albumlibrary.adapter.FolderAdapter;
import com.fcp.albumlibrary.adapter.ImageGridAdapter;
import com.fcp.albumlibrary.bean.Folder;
import com.fcp.albumlibrary.bean.Image;
import com.fcp.albumlibrary.utils.FileUtils;
import com.fcp.albumlibrary.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 相册fragment
 * Created by fcp on 2016/7/29.
 */
public abstract class AlbumFragment extends Fragment{

    //适配器
    protected ImageGridAdapter mImageAdapter;
    // 结果数据
    protected ArrayList<Image> resultList = new ArrayList<>();
    // 文件夹数据
    protected ArrayList<Folder> mResultFolder = new ArrayList<>();


    // 图片数量
    protected int mDesireImageCount = 0;
    //是否显示相机
    protected boolean isShowCamera = false;

    // 时间线
    private TextView mTimeLineText;
    // 类别
    private TextView mCategoryText;
    // 底部View
    private View mPopupAnchorView;
    // 图片Grid
    protected GridView mGridView;

    //文件框
    private ListPopupWindow mFolderPopupWindow;
    private FolderAdapter mFolderAdapter;
    private boolean hasFolderGened = false;//是否已获得文件夹信息
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;

    //拍照领时文件
    private File mTmpFile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 选择图片数量
        mDesireImageCount = getArguments().getInt(AlbumActivity.EXTRA_SELECT_COUNT);
        // 是否显示照相机
        isShowCamera = getArguments().getBoolean(AlbumActivity.EXTRA_SHOW_CAMERA, true);
        // 获得控件
        mTimeLineText = (TextView) view.findViewById(R.id.album_fragment_timeline_area);
        mCategoryText = (TextView) view.findViewById(R.id.album_fragment_category_btn);
        mPopupAnchorView = view.findViewById(R.id.album_fragment_footer);
        mGridView = (GridView) view.findViewById(R.id.album_fragment_grid);
        // 初始化，先隐藏当前timeline
        mTimeLineText.setVisibility(View.GONE);
        // 初始化，加载所有图片
        mCategoryText.setText("所有图片");
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFolderPopupWindow == null) {
                    DisplayMetrics dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
                    createPopupFolderList(dm.widthPixels, dm.heightPixels * 3 / 4);
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });
        mImageAdapter = createGridAdapter();
        //设置Grid
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // 停止滑动，日期指示器消失
                    mTimeLineText.setVisibility(View.GONE);
                } else if (scrollState == SCROLL_STATE_FLING) {
                    mTimeLineText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mTimeLineText.getVisibility() == View.VISIBLE) {
                    int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
                    Image image = (Image) view.getAdapter().getItem(index);
                    if (image != null) {
                        mTimeLineText.setText(TimeUtils.formatPhotoDate(image.path));
                    }
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mImageAdapter.isShowCamera()) {
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                    if (i == 0) {
                        showCameraAction();
                    } else {
                        // 正常操作
                        gridClickPicture(view, i-1);
                    }
                } else {
                    // 正常操作
                    gridClickPicture(view, i);
                }
            }
        });
        mFolderAdapter = new FolderAdapter(this);
    }

    /**
     * 获得适配器
     */
    abstract ImageGridAdapter createGridAdapter();

    /**
     * 点击照片
     */
    abstract void gridClickPicture(View view, int i);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 首次加载所有图片
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }


    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList(int width, int height) {
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height * 5 / 8);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mFolderAdapter.setSelectIndex(i);
                final int index = i;
                final AdapterView v = adapterView;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();
                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                            mCategoryText.setText("所有图片");
                            if (isShowCamera) {
                                mImageAdapter.setShowCamera(true);
                            } else {
                                mImageAdapter.setShowCamera(false);
                            }
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mImageAdapter.update(folder.images);
                                mCategoryText.setText(folder.name);
                            }
                            mImageAdapter.setShowCamera(false);
                        }

                        // 滑动到最初始位置
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    /**
     * 图片加载
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(id == LOADER_ALL) {
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
            }else if(id == LOADER_CATEGORY){
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0]+" like '%"+args.getString("path")+"%'", null, IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> images = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do{
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        images.add(image);
                        if( !hasFolderGened ) {
                            // 获取文件夹名称
                            File folderFile = new File(path).getParentFile();
                            if(folderFile != null){
                                Folder folder = new Folder(folderFile.getName(),folderFile.getAbsolutePath(),image);
                                if (!mResultFolder.contains(folder)) {//如果还没有文件夹,(equal重写过)
                                    List<Image> imageList = new ArrayList<>();
                                    imageList.add(image);
                                    folder.images = imageList;
                                    mResultFolder.add(folder);
                                } else {
                                    // 更新
                                    Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
                                    f.images.add(image);
                                }
                            }

                        }

                    }while(data.moveToNext());

                    mImageAdapter.update(images);
                    mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallBackListener != null) {
                        mCallBackListener.onCameraShot(mTmpFile);
                    }
                }
            }else{
                if(mTmpFile != null && mTmpFile.exists()){
                    mTmpFile.delete();
                }
            }
        }
    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = FileUtils.createTmpFile(getActivity());
            if(mTmpFile == null){
                Toast.makeText(getActivity(), "无法创建文件", Toast.LENGTH_SHORT).show();
                return;
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }else{
            Toast.makeText(getActivity(), "没有系统相机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获得选中后的图片
     */
    public ArrayList<Image> getSelectedImages() {
        return resultList;
    }

    /**
     * 回调接口
     */
    protected CallBackListener mCallBackListener;

    public void setCallBackListener(CallBackListener callBackListener) {
        mCallBackListener = callBackListener;
    }

    /**
     * 回调接口
     */
    public interface CallBackListener{
        void onSingleImageSelected();
        void onMultiImageSelected();
        void onCameraShot(File imageFile);
        void onFinishInBrowse();
    }

}
