package com.wusai.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.activity.HomeActivity;
import com.wusai.mobilesafe.receiver.MyAppWidgetProvider;
import com.wusai.mobilesafe.utils.ProcessUtil;


public class UpdateWidgetService extends Service {

	private MyReceiver mReceiver;
	private Timer mTimer;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 管理进程总数和可用内存数更新(定时)
		startTimer();
		Log.e("UpdateWidgetService", "更新服务已创建");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mReceiver = new MyReceiver();
		registerReceiver(mReceiver,intentFilter);
	}
	class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				//开启5秒刷新
				startTimer();
			}else{
				//关闭5秒刷新
				closeTimerTask();
			}
		}
	}
	private void closeTimerTask() {
		// TODO Auto-generated method stub
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
	}
	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateAppWidget();
			}
		}, 0, 5000);
	}

	private void updateAppWidget() {
		Log.e("UpdateWidgetService", "更新一次");
		  //1 获取AppWidge 对象 AppWidgetManager
		AppWidgetManager aWM=AppWidgetManager.getInstance(this); //2
		  //获取窗体小部件布局转换成的viewdui对象(定位应用的包名，当前应用的那块布局文件)； 
		RemoteViews remoteViews=new RemoteViews(getPackageName(),
		  R.layout.process_widget); //3 给窗体小部件布view对象，内部空间赋值
		  remoteViews.setTextViewText
		  (R.id.tv_process_count,"进程总数："+ProcessUtil
		  .getProgressCount(this)); 
		  //4显示可用内存大小 
		  String availSpace=Formatter.formatFileSize(this,
		  ProcessUtil.getAvailSpace(getApplicationContext()));
		  remoteViews.setTextViewText(R.id.tv_process_memory,
		  "可用内存："+availSpace);
		  //添加点击事件
		  Intent intent=new Intent(UpdateWidgetService.this,HomeActivity.class);
		  PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
		  remoteViews.setOnClickPendingIntent(R.id.ll_widget_enter, pendingIntent);
		  //添加一键清理点击事件
		  Intent brodcastIntent=new Intent("android.intent.action.KILL_BACK_PROCESS");
		  PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(), 0, brodcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		  remoteViews.setOnClickPendingIntent(R.id.bt_widget_clear, broadcast);
		  //通知刷新 
		  ComponentName componentName=new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
		  aWM.updateAppWidget(componentName,remoteViews);
		 
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mReceiver!=null){
			unregisterReceiver(mReceiver);
		}
		
	}
}
