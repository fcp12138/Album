package com.fcp.albumlibrary.bean;

import java.util.List;

/**
 * 文件夹
 * Created by Nereo on 2015/4/7.
 */
public class Folder {
    public String name;
    public String path;
    public Image cover;
    public List<Image> images;

    public Folder() {
    }

    public Folder(String name, String path, Image cover) {
        this.name = name;
        this.path = path;
        this.cover = cover;
    }

    @Override
    public boolean equals(Object o) {
        try {
            return this.path.equalsIgnoreCase(((Folder) o).path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
