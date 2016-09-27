package com.fcp.albumlibrary.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件操作类
 * Created by Nereo on 2015/4/8.
 */
public class FileUtils {

    private static String saveFileName = "SCY";

    public static File createTmpFile(Context context){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = "multi_image_"+timeStamp;
        return createTmpFile(context , fileName);
    }

    /**
     * 创建
     * @param context Context
     * @param name 照片名字
     */
    public static File createTmpFile(Context context , String name){
        String state = Environment.getExternalStorageState();
        File destDir;
        if(state.equals(Environment.MEDIA_MOUNTED)){
            // 已挂载
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            destDir = new File(pic,saveFileName);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
        }else{
            File cacheDir = context.getCacheDir();
            destDir = new File(cacheDir,saveFileName);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
        }
        return new File(destDir, name+".jpg");

    }

}
