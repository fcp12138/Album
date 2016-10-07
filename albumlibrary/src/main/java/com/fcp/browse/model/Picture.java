package com.fcp.browse.model;

import android.os.Parcelable;

/**
 * 图片路径提供者
 * Created by fcp on 2016/9/27.
 */

public interface Picture extends Parcelable{

    /**
     * 提供图片的路径
     */
     String getPath();

}
