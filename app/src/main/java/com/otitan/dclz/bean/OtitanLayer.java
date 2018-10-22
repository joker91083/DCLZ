package com.otitan.dclz.bean;

import java.io.Serializable;

/**
 * Created by sp on 2018/10/21
 * 图层
 */
public class OtitanLayer implements Serializable {

    private String name; // 图层名称

    private String url; // 图层地址

    private int index; // 图层索引

    private boolean isSelect; // 是否选中

    private int status; // 图层状态

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
