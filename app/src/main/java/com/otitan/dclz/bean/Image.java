package com.otitan.dclz.bean;

/**
 * Created by otitan_li on 2018/4/28.
 * 照片类
 */

public class Image implements java.io.Serializable{


    private static final long serialVersionUID = 470615367151921136L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    private String name;
    private String base;



}
