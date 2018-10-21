package com.otitan.dclz.bean;

import java.io.Serializable;

/**
 * Created by otitan_li on 2018/6/26.
 * Video
 */

public class Video implements Serializable{
    private static final long serialVersionUID = 8022485088855585635L;

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
