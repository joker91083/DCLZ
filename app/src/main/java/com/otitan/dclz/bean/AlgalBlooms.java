package com.otitan.dclz.bean;

/**
 * Created by sp on 2018/9/27.
 * 未来水华发生概率
 */
public class AlgalBlooms {

    private String TIME; // 未来天数

    private String GL; // 概率

    private String QGL;

    private String NODATE; // 无数据日期

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getGL() {
        return GL;
    }

    public void setGL(String GL) {
        this.GL = GL;
    }

    public String getQGL() {
        return QGL;
    }

    public void setQGL(String QGL) {
        this.QGL = QGL;
    }

    public String getNODATE() {
        return NODATE;
    }

    public void setNODATE(String NODATE) {
        this.NODATE = NODATE;
    }
}
