package com.wusai.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class InstalledAppInfoBean {
	private Drawable icon;//图标
	private String appName;//应用名
	private boolean isSystem;//是否为系统应用
	private boolean isSD;//是否安装在内存卡
	private String packageName;//包名
	private long size;//安装大小
	private String sorceDir;//安装路径
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public boolean isSD() {
		return isSD;
	}
	public void setSD(boolean isSD) {
		this.isSD = isSD;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getSorceDir() {
		return sorceDir;
	}
	public void setSorceDir(String sorceDir) {
		this.sorceDir = sorceDir;
	}
}
