package com.wusai.mobilesafe.service;

import java.util.List;

import com.wusai.mobilesafe.activity.EnterPsdActivity;
import com.wusai.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class WatchDogService extends Service {
	private String skipPackageName;
	private MyReciver myReciver;
	private List<String> lockPackageNameList;
	private AppLockDao mDao;
	private boolean isWatch;
	private MyObserver myObserver;

	@Override
	public void onCreate() {
		super.onCreate();
		isWatch = true;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SKIP_APP");
		myReciver = new MyReciver();
		registerReceiver(myReciver, intentFilter);
		// 创建看门狗服务
		// 获取当前打开的应用包名，获取当前Activity， 获取当前应用栈，获取Activity管理器
		// 应用包名 和锁定应用数据库中的包名比对
		whatch();
		myObserver = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, myObserver);
		
	}
	class MyObserver extends ContentObserver{
		public MyObserver(Handler handler) {
			super(handler);
		}
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			new Thread(){public void run() {
				lockPackageNameList=mDao.queryAll();
			};}.start();
		}
	}
	private void whatch() {
		new Thread() {


			public void run() {
				mDao = AppLockDao
						.getInstance(getApplicationContext());
				lockPackageNameList = mDao.queryAll();
				while (isWatch) {
					//获取当前正在运行应用包名
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					String runningPackageName = runningTaskInfo.topActivity
							.getPackageName();

					if (lockPackageNameList.contains(runningPackageName)) {
						if (!runningPackageName.equals(skipPackageName)) {
							Intent intent = new Intent(getApplicationContext(),
									EnterPsdActivity.class);
							//由于拦截界面需要显示当前运行应用的图标及应用名称，这两项要根据包名来获取
							intent.putExtra("packagename", runningPackageName);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			};
		}.start();
	}

	class MyReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			skipPackageName = intent.getStringExtra("packagename");
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		isWatch=false;
		super.onDestroy();
		if(myReciver!=null){
			unregisterReceiver(myReciver);
		}
		if(myObserver!=null){
			getContentResolver().unregisterContentObserver(myObserver);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
