package com.otitan.dclz.greendao;

import com.otitan.dclz.Myapplication;
import com.otitan.dclz.util.ResourceHelper;

/**
 * Created by otitan_li on 2018/7/11.
 * GreenDaoManager
 */

public class GreenDaoManager {


//    private DaoMaster mDaoMaster;
//    private DaoSession mDaoSession;
//
//    public MyOpenHelper getmSQLiOpenHelper() {
//        return mSQLiOpenHelper;
//    }
//
//    public void setmSQLiOpenHelper(MyOpenHelper mSQLiOpenHelper) {
//        this.mSQLiOpenHelper = mSQLiOpenHelper;
//    }
//
//    private MyOpenHelper mSQLiOpenHelper;
//
//
//    public GreenDaoManager(){
//        String dbname = ResourceHelper.getInstance().getDbpath("db.sqlite");
//        init(dbname);
//    }
//
//    /**
//     * 静态内部类，实例化对象使用
//     */
//    private static class SingleInstanceHolder {
//        private static final GreenDaoManager INSTANCE = new GreenDaoManager();
//    }
//
//    /**
//     * 对外唯一实例的接口
//     *
//     * @return
//     */
//    public static GreenDaoManager getInstance() {
//        return SingleInstanceHolder.INSTANCE;
//    }
//
//    /**
//     * 初始化数据
//     */
//    public void init(String dbname) {
//        mSQLiOpenHelper = new MyOpenHelper(Myapplication.getInstance(),dbname,null);
//        mDaoMaster = new DaoMaster(mSQLiOpenHelper.getWritableDatabase());
//        mDaoSession = mDaoMaster.newSession();
//    }
//
//    public DaoMaster getmDaoMaster() {
//        return mDaoMaster;
//    }
//
//    public DaoSession getmDaoSession() {
//        return mDaoSession;
//    }
//
//    public DaoSession getNewSession() {
//        mDaoSession = mDaoMaster.newSession();
//        return mDaoSession;
//    }


}
