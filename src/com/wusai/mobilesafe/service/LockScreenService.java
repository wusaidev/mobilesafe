package com.wusai.mobilesafe.service;

import java.util.zip.Inflater;

import com.wusai.mobilesafe.utils.ProcessUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmStore.Action;
import android.os.IBinder;
import android.view.InflateException;

public class LockScreenService extends Service {
	private InnerReceiver innerReceiver;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(innerReceiver);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			ProcessUtil.killAllProcess(getApplicationContext());
		}
		
	}
}
