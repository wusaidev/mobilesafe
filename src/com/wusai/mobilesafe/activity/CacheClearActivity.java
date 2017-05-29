package com.wusai.mobilesafe.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;

public class CacheClearActivity extends Activity {
	//protected static final int CHANGE_TEXT = 101;
	private Button bt_cache_clear;
	private ProgressBar pb_cacheclear;
	private LinearLayout ll_cache_app;
	private TextView tv_cache_state;
	private PackageManager mPm;
	private final int UPDATE_CACHE_APP=100;
	protected static final int SCAN_NAME_CHANGE = 101;
	private final int CLEAN_CHANGE=102;
	private final int SCAN_FINISHED=103;
	

	private String mScanAppName;
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CACHE_APP:
				Log.e("CacheClear","收到消息，添加view");
				final CacheAppBean cacheAppBean=(CacheAppBean) msg.obj;
				View view=View.inflate(getApplicationContext(), R.layout.view_cache_item, null);
				ImageView iv_logo=(ImageView)view. findViewById(R.id.iv_cache_logo);
				TextView tv_app_name=(TextView) view.findViewById(R.id.tv_cache_app_name);
				TextView tv_cache_size=(TextView)view. findViewById(R.id.tv_cache_size);
				Button bt_cache_single=(Button) view.findViewById(R.id.bt_cache_single);
				iv_logo.setBackgroundDrawable(cacheAppBean.icon);
				tv_app_name.setText(cacheAppBean.name);
				tv_cache_size.setText(cacheAppBean.cacheSize);
				tv_cache_state.setText("正在扫描"+cacheAppBean.name);
				ll_cache_app.addView(view,0);
				//deleteApplicationCacheFiles
				bt_cache_single.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						/*由于只有系统应用才可获取此方法的权限，此应用为用户应用，换思路（点击跳转到系统的应用信息界面）
						 * try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							Method method=clazz.getMethod("deleteApplicationCacheFiles", 
									String.class,IPackageDataObserver.class);
							method.invoke(mPm, cacheAppBean.name,new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									// TODO Auto-generated method stub
									
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						Intent intent=new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+cacheAppBean.packageName));
						startActivity(intent);
					}
				});
				break;
			case SCAN_NAME_CHANGE:
				tv_cache_state.setText("正在扫描："+mScanAppName);
				break;
			case CLEAN_CHANGE:
				ll_cache_app.removeAllViews();
				break;
			case SCAN_FINISHED:
				tv_cache_state.setText("缓存扫描完成");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initUI();
		initData();
	}

	private void initData() {
		new Thread(){
			public void run() {
				mPm = getPackageManager();
				List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
				pb_cacheclear.setMax(installedPackages.size());
				
				int index=0;
				for(PackageInfo packageInfo:installedPackages){
					String packageName=packageInfo.packageName;
					index++;
					Log.e("CacheClear","第"+index+"次循环"+packageName);
					pb_cacheclear.setProgress(index);
					getCacheSize(packageName);
					try {
						Thread.sleep(50+new Random().nextInt(100));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Message msg=Message.obtain();
				msg.what=SCAN_FINISHED;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	private void getCacheSize(String packageName) {
		IPackageStatsObserver.Stub statesStub=new IPackageStatsObserver.Stub(){


			@Override
			public void onGetStatsCompleted(PackageStats pStats,
					boolean succeeded){
				// TODO Auto-generated method stub
				long cacheSize = pStats.cacheSize;
				Log.e("CacheClear","缓存大小："+cacheSize);

				if(cacheSize>0){
					CacheAppBean  cacheAppBean=new CacheAppBean();
					String strCacheSize=Formatter.formatFileSize(getApplicationContext(), cacheSize);
					cacheAppBean.cacheSize=strCacheSize;
					cacheAppBean.packageName=pStats.packageName;
					try {
						cacheAppBean.name=mPm.getApplicationInfo(pStats.packageName, 0).loadLabel(mPm).toString();
						cacheAppBean.icon=mPm.getApplicationInfo(pStats.packageName, 0).loadIcon(mPm);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg=Message.obtain();
					msg.what=UPDATE_CACHE_APP;
					msg.obj=cacheAppBean;
					mHandler.sendMessage(msg);
				}else {
					try {
						mScanAppName = mPm.getApplicationInfo(pStats.packageName, 0)
								.loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg=Message.obtain();
					msg.what=SCAN_NAME_CHANGE;
					mHandler.sendMessage(msg);
				}
			}
			
			
			
		};
//		mPm.getPackageSizeInfo(packageName,st);
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method=clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
			method.invoke(mPm, packageName,statesStub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	class CacheAppBean{
		public String name;
		public String cacheSize;
		public String packageName;
		public Drawable icon;
	}
	private void initUI() {
		bt_cache_clear = (Button) findViewById(R.id.bt_cache_clear);
		pb_cacheclear = (ProgressBar) findViewById(R.id.pb_cacheclear_progress);
		ll_cache_app = (LinearLayout) findViewById(R.id.ll_cache_app);
		tv_cache_state = (TextView) findViewById(R.id.tv_cache_state);
		bt_cache_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//mPm.freeStorageAndNotify();
				try {
					Class<?> clazz=Class.forName("android.content.pm.PackageManager");
					Method method=clazz.getMethod("freeStorageAndNotify", 
							long.class,IPackageDataObserver.class);
					method.invoke(mPm,Long.MAX_VALUE,new IPackageDataObserver.Stub() {
						@Override
						public void onRemoveCompleted(String packageName, boolean succeeded)
								throws RemoteException {
							// TODO Auto-generated method stub
							Message msg=Message.obtain();
							msg.what=CLEAN_CHANGE;
							mHandler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
