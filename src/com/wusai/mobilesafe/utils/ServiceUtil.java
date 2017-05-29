package com.wusai.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	/**
	 * @param context上下文
	 * @param serviceName服务名称
	 * @return  true云心  false没有运行
	 */
	public static boolean isRunning(Context context,String serviceName){
		ActivityManager mActivityManager=(ActivityManager) 
				context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取手机中正在运行的服务
		List<RunningServiceInfo> runningServices = mActivityManager.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			
			if(serviceName.equals(runningServiceInfo.service.getClassName())){
				return true ;
			}
		}
		return false;
	}
}
