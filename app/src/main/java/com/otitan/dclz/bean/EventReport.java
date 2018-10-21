package com.otitan.dclz.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class EventReport {

    @Id(assignable = true)
    private Long LID;//本地保存id

    private String XJ_ID; // 事件id

    public Long getLID() {
        return LID;
    }

    public void setLID(Long LID) {
        this.LID = LID;
    }

    public String getXJ_ID() {
        return XJ_ID;
    }

    public void setXJ_ID(String XJ_ID) {
        this.XJ_ID = XJ_ID;
    }

    public String getXJ_SJMC() {
        return XJ_SJMC;
    }

    public void setXJ_SJMC(String XJ_SJMC) {
        this.XJ_SJMC = XJ_SJMC;
    }

    public String getXJ_SBBH() {
        return XJ_SBBH;
    }

    public void setXJ_SBBH(String XJ_SBBH) {
        this.XJ_SBBH = XJ_SBBH;
    }

    public String getXJ_MSXX() {
        return XJ_MSXX;
    }

    public void setXJ_MSXX(String XJ_MSXX) {
        this.XJ_MSXX = XJ_MSXX;
    }

    public String getXJ_SCRQ() {
        return XJ_SCRQ;
    }

    public void setXJ_SCRQ(String XJ_SCRQ) {
        this.XJ_SCRQ = XJ_SCRQ;
    }

    public String getXJ_JD() {
        return XJ_JD;
    }

    public void setXJ_JD(String XJ_JD) {
        this.XJ_JD = XJ_JD;
    }

    public String getXJ_WD() {
        return XJ_WD;
    }

    public void setXJ_WD(String XJ_WD) {
        this.XJ_WD = XJ_WD;
    }

    public String getXJ_XXDZ() {
        return XJ_XXDZ;
    }

    public void setXJ_XXDZ(String XJ_XXDZ) {
        this.XJ_XXDZ = XJ_XXDZ;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getXJ_ZPDZ() {
        return XJ_ZPDZ;
    }

    public void setXJ_ZPDZ(String XJ_ZPDZ) {
        this.XJ_ZPDZ = XJ_ZPDZ;
    }

    public String getXJ_YPDZ() {
        return XJ_YPDZ;
    }

    public void setXJ_YPDZ(String XJ_YPDZ) {
        this.XJ_YPDZ = XJ_YPDZ;
    }

    public String getXJ_SPDZ() {
        return XJ_SPDZ;
    }

    public void setXJ_SPDZ(String XJ_SPDZ) {
        this.XJ_SPDZ = XJ_SPDZ;
    }

    private String XJ_SJMC; // 事件名称

    private String XJ_SBBH; // 设备号

    private String XJ_MSXX; // 描述信息

    private String XJ_SCRQ; // 日期

    private String XJ_JD; // 经度

    private String XJ_WD; // 纬度

    private String XJ_XXDZ; // 详细地址

    private String REMARK; // 备注

    private String XJ_ZPDZ; // 照片地址

    private String XJ_YPDZ; // 音频地址

    private String XJ_SPDZ; // 视频地址

    public String getXC_ID() {
        return XC_ID;
    }

    public void setXC_ID(String XC_ID) {
        this.XC_ID = XC_ID;
    }

    private String XC_ID;//巡查路线id


}
