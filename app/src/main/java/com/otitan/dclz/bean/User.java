package com.otitan.dclz.bean;

import java.io.Serializable;

/**
 * Created by sp on 2018/9/30.
 * 用户信息
 */
public class User implements Serializable {

    private String code; //

    private String ID; //

    private String USERCODE; // 用户名

    private String REALNAME; // 真实姓名

    private String SEX; // 姓名

    private String USERMZ; //

    private String MOBILEPHONENO; // 手机号

    private String USEREMAIL; // email

    private String DEPTIDS; //

    private String UNITID; //

    private String DQLEVEL; //

    private String UNITNAME; // 单位

    private String DEPTNAME; // 部门

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUSERCODE() {
        return USERCODE;
    }

    public void setUSERCODE(String USERCODE) {
        this.USERCODE = USERCODE;
    }

    public String getREALNAME() {
        return REALNAME;
    }

    public void setREALNAME(String REALNAME) {
        this.REALNAME = REALNAME;
    }

    public String getSEX() {
        return SEX;
    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }

    public String getUSERMZ() {
        return USERMZ;
    }

    public void setUSERMZ(String USERMZ) {
        this.USERMZ = USERMZ;
    }

    public String getMOBILEPHONENO() {
        return MOBILEPHONENO;
    }

    public void setMOBILEPHONENO(String MOBILEPHONENO) {
        this.MOBILEPHONENO = MOBILEPHONENO;
    }

    public String getUSEREMAIL() {
        return USEREMAIL;
    }

    public void setUSEREMAIL(String USEREMAIL) {
        this.USEREMAIL = USEREMAIL;
    }

    public String getDEPTIDS() {
        return DEPTIDS;
    }

    public void setDEPTIDS(String DEPTIDS) {
        this.DEPTIDS = DEPTIDS;
    }

    public String getUNITID() {
        return UNITID;
    }

    public void setUNITID(String UNITID) {
        this.UNITID = UNITID;
    }

    public String getDQLEVEL() {
        return DQLEVEL;
    }

    public void setDQLEVEL(String DQLEVEL) {
        this.DQLEVEL = DQLEVEL;
    }

    public String getUNITNAME() {
        return UNITNAME;
    }

    public void setUNITNAME(String UNITNAME) {
        this.UNITNAME = UNITNAME;
    }

    public String getDEPTNAME() {
        return DEPTNAME;
    }

    public void setDEPTNAME(String DEPTNAME) {
        this.DEPTNAME = DEPTNAME;
    }
}
