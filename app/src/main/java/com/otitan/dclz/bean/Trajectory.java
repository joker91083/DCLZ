package com.otitan.dclz.bean;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by otitan_li on 2018/7/3.
 * Trajectory
 * 轨迹记录表
 */
@Entity
public class Trajectory implements Serializable{
    private static final long serialVersionUID = 6117085139564366095L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getXC_ID() {
        return XC_ID;
    }

    public void setXC_ID(String XC_ID) {
        this.XC_ID = XC_ID;
    }

    public String getXC_NAME() {
        return XC_NAME;
    }

    public void setXC_NAME(String XC_NAME) {
        this.XC_NAME = XC_NAME;
    }

    public String getXC_STARTTIME() {
        return XC_STARTTIME;
    }

    public void setXC_STARTTIME(String XC_STARTTIME) {
        this.XC_STARTTIME = XC_STARTTIME;
    }

    public String getXC_ENDTIME() {
        return XC_ENDTIME;
    }

    public void setXC_ENDTIME(String XC_ENDTIME) {
        this.XC_ENDTIME = XC_ENDTIME;
    }

    public String getXC_METHOD() {
        return XC_METHOD;
    }

    public void setXC_METHOD(String XC_METHOD) {
        this.XC_METHOD = XC_METHOD;
    }

    public String getXC_SBH() {
        return XC_SBH;
    }

    public void setXC_SBH(String XC_SBH) {
        this.XC_SBH = XC_SBH;
    }

    public String getXC_XLLC() {
        return XC_XLLC;
    }

    public void setXC_XLLC(String XC_XLLC) {
        this.XC_XLLC = XC_XLLC;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    private String XC_ID;
    /*巡查名称*/
    private String XC_NAME;
    /*开始时间*/
    private String XC_STARTTIME;
    /*结束时间*/
    private String XC_ENDTIME;
    /*巡查方式*/
    private String XC_METHOD;
    /*设备号*/
    private String XC_SBH;
    /*巡查里程*/
    private String XC_XLLC;
    /*备注*/
    private String REMARK;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    private Long id;

}
