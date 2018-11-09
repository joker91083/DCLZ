package com.otitan.dclz.util;

import android.content.Context;
import android.os.storage.StorageManager;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sp on 2018/10/21.
 * 获取设备内存地址
 */
public class ResourcesManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Context mContext;

	private String ROOT_MAPS = "/maps";
	private String otitan_map = "/otitan.map";
	private String sqlite = "/sqlite";
	private String otms = "/otms";
	private String filePath = "文件可用地址";

	private static class LazyHolder {
		private static final ResourcesManager INSTANCE = new ResourcesManager();
	}

	public static ResourcesManager getInstance(Context context) {
		mContext = context;
		return ResourcesManager.LazyHolder.INSTANCE;
	}

	/** 获取手机内部存储地址和外部SD卡存储地址 */
	private String[] getStoragePath() {

		StorageManager sm = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		String[] paths = null;
		try {
			//paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0]).invoke(sm,new Object[]{});
			paths = (String[])sm.getClass().getMethod("getVolumePaths").invoke(sm);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return paths;
	}

	/** 获取影像文件列表  */
	public List<File> getImgTitlePath() {
		List<String> fileter = new ArrayList<>();
		fileter.add("image");
		return getPahts(otitan_map, fileter);
	}

	private List<File> getPahts(String path, List<String> list) {
		List<File> fileList = new  ArrayList<>();
		String[] array = getStoragePath();
		for (String a : array) {
			File file = new File(a + ROOT_MAPS + path);
			if (file.exists()) {
				for (int i = 0; i < fileList.size(); i++) {
					if (fileList.get(i).isFile() && fileList.get(i).getName().contains(list.get(0))) {
						fileList.add(file.listFiles()[i]);
					}
				}
			}
		}
		return fileList;
	}
}
