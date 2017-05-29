package com.wusai.mobilesafe.receiver;

import com.wusai.mobilesafe.service.UpdateWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyAppWidgetProvider extends AppWidgetProvider {
	@SuppressLint("NewApi") @Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
		Log.e("MyAppWidgetProvider", "onAppWidgetOptionsChanged"+"窗体小部件宽高改变调用方法");
		context.startService(new Intent(context,UpdateWidgetService.class));

	}
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.e("MyAppWidgetProvider", "onEnabled"+"创建第一个窗体小部件调用方法");
		context.startService(new Intent(context,UpdateWidgetService.class));

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.e("MyAppWidgetProvider", "onUpdate"+"创建多个窗体小部件调用方法");
		context.startService(new Intent(context,UpdateWidgetService.class));


	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		Log.e("MyAppWidgetProvider", "onEnabled"+"创建第一个窗体小部件调用方法");

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		Log.e("MyAppWidgetProvider", "onEnabled"+"删除一个窗体小部件调用方法");

	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		Log.e("MyAppWidgetProvider", "onEnabled"+"删除最后一个窗体小部件调用方法");
		context.stopService(new Intent(context,UpdateWidgetService.class));


	}
}
