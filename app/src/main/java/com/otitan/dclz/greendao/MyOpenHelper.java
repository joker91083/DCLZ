package com.otitan.dclz.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

/**
 * Created by otitan_li on 2018/7/12.
 * MyOpenHelper
 */

//public class MyOpenHelper extends DaoMaster.DevOpenHelper{
//
//    /**
//     *
//     * @param context  上下文
//     * @param name     原来定义的数据库的名字   新旧数据库一致
//     * @param factory  可以null
//     */
//    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
//        super(context, name, factory);
//    }
//
//    /**
//     *
//     * @param db
//     * @param oldVersion
//     * @param newVersion
//     *  更新数据库的时候自己调用
//     */
//    @Override
//    public void onUpgrade(Database db, int oldVersion, int newVersion) {
//        Log.d("flag","-----调用了");
//        //具体的数据转移在MigrationHelper2类中
//        /**
//         *  将db传入     将gen目录下的所有的Dao.类传入
//         */
//        if(newVersion > oldVersion){
//            MigrationHelper.migrate(db, EmergencyDao.class);
//        }
//    }
//
//}
