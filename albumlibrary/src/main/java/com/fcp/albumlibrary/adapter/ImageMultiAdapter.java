package com.fcp.albumlibrary.adapter;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fcp.albumlibrary.R;
import com.fcp.albumlibrary.bean.Image;

import java.util.List;

/**
 * 多选适配器
 * Created by fcp on 2016/3/6.
 */
public class ImageMultiAdapter extends ImageGridAdapter {


    public ImageMultiAdapter(Fragment fragment, List<Image> mSelectedImages, boolean showCamera) {
        super(fragment, mSelectedImages, showCamera);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if(type == TYPE_CAMERA){
            convertView = mInflater.inflate(R.layout.album_item_list_camera, parent, false);
            convertView.setTag(null);
        }else if(type == TYPE_NORMAL){
            ViewHold holde;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.album_item_list_multi, parent, false);
                holde = new ViewHold(convertView);
            }else{
                holde = (ViewHold) convertView.getTag();
                if(holde == null){
                    convertView = mInflater.inflate(R.layout.album_item_list_multi, parent, false);
                    holde = new ViewHold(convertView);
                }
            }
            holde.bindData(getItem(position));
            final View finalConvertView = convertView;
            holde.indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    multiListener.onItemClick(position, finalConvertView);
                }
            });
        }

        return convertView;
    }

    /**
     * 改变选择的状态
     */
    public void changeSelectStatue(int position, View convertView, ViewGroup parent){
        ViewHold holde;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.album_item_list_multi, parent, false);
            holde = new ViewHold(convertView);
        }else{
            holde = (ViewHold) convertView.getTag();
            if(holde == null){
                convertView = mInflater.inflate(R.layout.album_item_list_multi, parent, false);
                holde = new ViewHold(convertView);
            }
        }
        holde.change(getItem(position));
    }

    private class ViewHold{

        ImageView image;
        ImageView indicator;
        View mask;

        ViewHold(View view){
            image = (ImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        void bindData(Image data){
            if(data == null) return;
            // 处理单选和多选状态
            change(data);
            // 显示图片
            loadImage(data.path,image);
        }

        private void change(Image data){
            if(mSelectedImages.contains(data)){
                // 设置选中状态
                indicator.setImageResource(R.drawable.ic_album_btn_selected);
                mask.setVisibility(View.VISIBLE);
            }else{
                // 未选择
                indicator.setImageResource(R.drawable.ic_album_btn_unselected);
                mask.setVisibility(View.GONE);
            }
        }


    }

    MultiListener multiListener = new MultiListener() {
        @Override
        public void onItemClick(int position, View convertView) {

        }
    };

    public void setMultiListener(MultiListener multiListener) {
        this.multiListener = multiListener;
    }

    public interface MultiListener{
        void onItemClick(int position,View convertView);
    }
}
