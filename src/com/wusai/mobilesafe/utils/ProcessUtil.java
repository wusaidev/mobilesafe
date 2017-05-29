package com.wusai.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.bean.RunningProgressInfoBean;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.drm.DrmStore.Action;

public class ProcessUtil {

	/**
	 * 获取进程总数方法
	 * 
	 * @param context上下文
	 * @return 进程总数
	 */
	public static int getProgressCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		return runningAppProcesses.size();
	}

	public static long getAvailSpace(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	public static long getTotalSpace(Context context) {
		// 在低版本中能用，高版本已不兼容
		/*
		 * ActivityManager am = (ActivityManager)
		 * context.getSystemService(Context.ACTIVITY_SERVICE); MemoryInfo
		 * memoryInfo=new MemoryInfo(); am.getMemoryInfo(memoryInfo); return
		 * memoryInfo.totalMem;
		 */
		FileReader fileReader;
		try {
			fileReader = new FileReader("proc/meminfo");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String lineOne = bufferedReader.readLine();
			// 将字符转换成字符数组
			char[] charArray = lineOne.toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					stringBuffer.append(c);
				}
			}
			long totalSpace = Long.parseLong(stringBuffer.toString()) * 1024;
			return totalSpace;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public static List<RunningProgressInfoBean> getProgressInfo(Context context){
		List<RunningProgressInfoBean> progressBeanList=new ArrayList<RunningProgressInfoBean>();
		 ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		 List<RunningAppProcessInfo> runningAppProcesseList = am.getRunningAppProcesses();
		 for(RunningAppProcessInfo info:runningAppProcesseList){
			 RunningProgressInfoBean  infoBean=new RunningProgressInfoBean();
			 //1 获取进程名称==应用包名
			 infoBean.setPackageName(info.processName);
			 //2 获取进程所占内存
			 int pid=info.pid;
			 android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{pid});
			 android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			 long totalPrivateDirty = memoryInfo.getTotalPrivateDirty()*1024;
			 infoBean.setMemSize(totalPrivateDirty);
			 PackageManager pm=context.getPackageManager();
			 try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(infoBean.getPackageName(),0);
				//应用图标
				infoBean.setIcon(applicationInfo.loadIcon(pm));
				//应用名称
				infoBean.setName(applicationInfo.loadLabel(pm).toString());
				//是否为系统进程
				if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)
						==ApplicationInfo.FLAG_SYSTEM){
					infoBean.setSystem(true);
				}else{
					infoBean.setSystem(false);
				}
			} catch (NameNotFoundException e) {
				infoBean.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				infoBean.setName(infoBean.getPackageName());
				infoBean.setSystem(true);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressBeanList.add(infoBean);
		 }
		 return progressBeanList;
	}
	public static void killProdess(Context context,RunningProgressInfoBean processBean){
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(processBean.getPackageName());
	}

	public static void killAllProcess(Context context) {
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info:infoList){
			if(info.processName.equals(context.getPackageName())){
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
	}
}
