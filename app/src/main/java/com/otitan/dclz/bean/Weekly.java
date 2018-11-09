package com.otitan.dclz.bean;

import java.io.Serializable;

public class Weekly implements Serializable {

    private static final long serialVersionUID = 8424303779948992293L;

    public String getJCBG_ID() {
        return JCBG_ID;
    }

    public void setJCBG_ID(String JCBG_ID) {
        this.JCBG_ID = JCBG_ID;
    }

    public String getJCBG_TITLE() {
        return JCBG_TITLE;
    }

    public void setJCBG_TITLE(String JCBG_TITLE) {
        this.JCBG_TITLE = JCBG_TITLE;
    }

    public String getJCBG_TYPE() {
        return JCBG_TYPE;
    }

    public void setJCBG_TYPE(String JCBG_TYPE) {
        this.JCBG_TYPE = JCBG_TYPE;
    }

    public String getJCBG_FILE() {
        return JCBG_FILE;
    }

    public void setJCBG_FILE(String JCBG_FILE) {
        this.JCBG_FILE = JCBG_FILE;
    }

    public String getJCBG_USERID() {
        return JCBG_USERID;
    }

    public void setJCBG_USERID(String JCBG_USERID) {
        this.JCBG_USERID = JCBG_USERID;
    }

    public String getJCBG_DATE() {
        return JCBG_DATE;
    }

    public void setJCBG_DATE(String JCBG_DATE) {
        this.JCBG_DATE = JCBG_DATE;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getJCBG_NF() {
        return JCBG_NF;
    }

    public void setJCBG_NF(String JCBG_NF) {
        this.JCBG_NF = JCBG_NF;
    }

    public String getJCBG_QS() {
        return JCBG_QS;
    }

    public void setJCBG_QS(String JCBG_QS) {
        this.JCBG_QS = JCBG_QS;
    }

    public String getJCBG_KSRQ() {
        return JCBG_KSRQ;
    }

    public void setJCBG_KSRQ(String JCBG_KSRQ) {
        this.JCBG_KSRQ = JCBG_KSRQ;
    }

    public String getJCBG_JSRQ() {
        return JCBG_JSRQ;
    }

    public void setJCBG_JSRQ(String JCBG_JSRQ) {
        this.JCBG_JSRQ = JCBG_JSRQ;
    }

    public String getJCBG_ZT() {
        return JCBG_ZT;
    }

    public void setJCBG_ZT(String JCBG_ZT) {
        this.JCBG_ZT = JCBG_ZT;
    }

    public String getJCBG_SHRQ() {
        return JCBG_SHRQ;
    }

    public void setJCBG_SHRQ(String JCBG_SHRQ) {
        this.JCBG_SHRQ = JCBG_SHRQ;
    }

    public String getJCBG_CSID() {
        return JCBG_CSID;
    }

    public void setJCBG_CSID(String JCBG_CSID) {
        this.JCBG_CSID = JCBG_CSID;
    }

    public String getSFGK() {
        return SFGK;
    }

    public void setSFGK(String SFGK) {
        this.SFGK = SFGK;
    }

    public String getBYSLYY() {
        return BYSLYY;
    }

    public void setBYSLYY(String BYSLYY) {
        this.BYSLYY = BYSLYY;
    }

    private String JCBG_ID;//监测报告id
    private String JCBG_TITLE;//监测报告标题
    private String JCBG_TYPE;//监测报告类型 1周报 2月报 3年报
    private String JCBG_FILE;//监测报告文件
    private String JCBG_USERID;//监测报告生成人
    private String JCBG_DATE;//监测报告生成时间
    private String REMARK;//监测报告备注
    private String JCBG_NF;//监测报告年份
    private String JCBG_QS;//监测报告期数
    private String JCBG_KSRQ;//监测报告开始日期
    private String JCBG_JSRQ;//监测报告结束日期
    /*已经废弃*/
    private String JCBG_ZT;//监测报告状态 0 未审核 1审核 2审核通过 3未通过
    private String JCBG_SHRQ;//监测报告审核日期
    private String JCBG_CSID;//流程类型编号
    private String SFGK;//是否公开发布 1公开 0不公开
    private String BYSLYY;//不予通过原因


}
