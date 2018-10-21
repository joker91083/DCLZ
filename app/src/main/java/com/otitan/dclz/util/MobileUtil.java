package com.otitan.dclz.util;

import android.content.Context;
import android.provider.Settings.Secure;

import com.titan.baselibrary.util.MobileInfoUtil;

public class MobileUtil {

    private static class Holder{

        private static MobileUtil instance = new MobileUtil();
    }

    public static MobileUtil getInstance(){
        return Holder.instance;
    }

    /*获取设备识别唯一号*/
    public String getMacAdress(Context context){
        /* 获取mac地址 作为设备唯一号 */
        String mac = MobileInfoUtil.getMAC(context);
        if (mac != null && !mac.equals("")) {
            return mac;
        }
        return "";
    }

    /*获取软件的序列号*/
    public String getMobileXlh(Context context){
       String xlh = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
       return xlh;
    }

}
