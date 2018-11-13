package com.otitan.dclz.bean;

/**
 * Created by sp on 2018/9/27.
 * 遥感监测实况
 */
public class Monitor {

    private String JC_DATE; // 监测日期

    private String WX_SLT;

    private String WXJC_MAP_URL;

    private String FS_AREA;

    private String JC_JGFX; // 监测结果

    private String JC_TIME;

    public String getJC_TIME() {
        return JC_TIME;
    }

    public void setJC_TIME(String JC_TIME) {
        this.JC_TIME = JC_TIME;
    }

    public String getJC_DATE() {
        return JC_DATE;
    }

    public void setJC_DATE(String JC_DATE) {
        this.JC_DATE = JC_DATE;
    }

    public String getWX_SLT() {
        return WX_SLT;
    }

    public void setWX_SLT(String WX_SLT) {
        this.WX_SLT = WX_SLT;
    }

    public String getWXJC_MAP_URL() {
        return WXJC_MAP_URL;
    }

    public void setWXJC_MAP_URL(String WXJC_MAP_URL) {
        this.WXJC_MAP_URL = WXJC_MAP_URL;
    }

    public String getFS_AREA() {
        return FS_AREA;
    }

    public void setFS_AREA(String FS_AREA) {
        this.FS_AREA = FS_AREA;
    }

    public String getJC_JGFX() {
        return JC_JGFX;
    }

    public void setJC_JGFX(String JC_JGFX) {
        this.JC_JGFX = JC_JGFX;
    }
}
