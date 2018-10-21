package com.otitan.dclz.util;


import com.otitan.dclz.Myapplication;
import com.titan.baselibrary.util.MemoryUtil;

import java.io.File;

public class ResourceHelper {


    static class  LazyHolder {
        private static ResourceHelper instance = new ResourceHelper();
    }

    public static ResourceHelper getInstance(){
        return LazyHolder.instance;
    }

    private String maps="/maps";
    private String sqlite="/sqlite";


    public String getDbpath(String dbname){
        String path = maps+sqlite+"/"+dbname;
        File file = MemoryUtil.getInstance(Myapplication.getInstance()).getFilePath(path);
        if(file.exists()){
            return file.getAbsolutePath();
        }
        return "";
    }


}
