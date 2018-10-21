package com.otitan.dclz.bean;

import java.io.Serializable;

/**
 * Created by otitan_li on 2018/6/26.
 * Audio
 */

public class Audio implements Serializable{
    private static final long serialVersionUID = -3278304949421058187L;

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
