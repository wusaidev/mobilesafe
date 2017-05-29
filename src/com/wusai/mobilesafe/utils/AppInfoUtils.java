package com.wusai.mobilesafe.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.wusai.mobilesafe.bean.InstalledAppInfoBean;

public class  AppInfoUtils{
	/**
	 * @return
	 *     手机可用内存
	 */
	public static long getPhoneAvailMem(){
		File dataDirectory = Environment.getDataDirectory();
		return dataDirectory.getFreeSpace();
	}
	/**
	 * @return
	 *     手机总内存
	 */
	public static long getPhoneTotalMem(){
		File dataDirectory = Environment.getDataDirectory();
		return dataDirectory.getTotalSpace();
	}
	/**
	 * @return
	 *     sd可用内存
	 */
	public static long getSDAvailMem(){
		File dataDirectory = Environment.getExternalStorageDirectory();
		return dataDirectory.getFreeSpace();
	}
	/**
	 * @return
	 *     sd总内存
	 */
	public static long getSDTotalMem(){
		File dataDirectory = Environment.getExternalStorageDirectory();
		return dataDirectory.getTotalSpace();
	}
	//getAllInstalledAppInfos
	public static List<InstalledAppInfoBean> getAllInstalledAppInfos(Context context){
		List<InstalledAppInfoBean> data=new ArrayList<InstalledAppInfoBean>();
		PackageManager pm=context.getPackageManager();
		List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
		InstalledAppInfoBean bean=null;
		for(ApplicationInfo applicationInfo:installedApplications){
			//组织数据
			bean=new InstalledAppInfoBean();
			//名字
			bean.setAppName(applicationInfo.loadLabel(pm)+"");
			//图标
			bean.setIcon(applicationInfo.loadIcon(pm));
			//包名
			bean.setPackageName(applicationInfo.packageName);
			//
			if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)!=0){
				bean.setSystem(true);
			}
			if((applicationInfo.flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
				bean.setSD(true);
			}
			bean.setSize(new File(applicationInfo.sourceDir).length());
			bean.setSorceDir(applicationInfo.sourceDir);
			data.add(bean);
		}
		return data;
	}
}
