package com.wusai.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.db.dao.VirusDao;
import com.wusai.mobilesafe.utils.Md5Util;

public class AnitVirusActivity extends Activity {
	protected static final int SCANING = 100;
	protected static final int SCAN_FINISH = 101;
	private ImageView iv_scanner;
	private TextView tv_scanningname;
	private ProgressBar pb_scanner;
	private LinearLayout ll_scannered;
	private List<ScanAppInfo> virusAppList = new ArrayList<AnitVirusActivity.ScanAppInfo>();
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanAppInfo info=(ScanAppInfo) msg.obj;
				tv_scanningname.setText(info.name);
				TextView tv_scanedName=new TextView(getApplicationContext());
				if(info.isVirus){
					tv_scanedName.setTextColor(Color.RED);
					tv_scanedName.setText("发现病毒："+info.name);
				}else{
					tv_scanedName.setTextColor(Color.BLACK);
					tv_scanedName.setText("扫描安全："+info.name);
				}
				ll_scannered.addView(tv_scanedName,0);
				break;
			case SCAN_FINISH:
				iv_scanner.clearAnimation();
				tv_scanningname.setText("扫描完成");
				for(ScanAppInfo appInfo:virusAppList){
					String packageName=appInfo.packageName;
					Uri uri = Uri.parse("package:"+packageName);//获取删除包名的URI
					Intent intent=new Intent(Intent.ACTION_DELETE,uri);//设置我们要执行的卸载动作
					startActivity(intent);
				}
				break;

			default:
				break;
			}
		};
	};
	private RotateAnimation rotateAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anitvirus);
		initAnimation();
		initUI();
		checkVirus();
	}

	private void initAnimation() {
		rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);
	}

	/**
	 * 扫描病毒方法
	 */
	private void checkVirus() { 
		new Thread(){

			public void run() {
				
				//获取到手机应用的签名，   获取应用信息对象 ，获取pm
				PackageManager pm=getPackageManager();
				VirusDao mDao=new VirusDao();
				//创建包含签名，应用名，的bean对象，创建病毒bean集合，创建扫描总应用的bean集合,扫描过程中向集合中添加bean对象
				List<String> dbVirusList = mDao.find();
				List<PackageInfo> installedPackageList = pm.getInstalledPackages
						(PackageManager.GET_SIGNATURES+PackageManager.GET_UNINSTALLED_PACKAGES);
				List<ScanAppInfo> allAppList=new ArrayList<AnitVirusActivity.ScanAppInfo>();
				Log.e("AnitVirusActivity",installedPackageList+"总数");
				pb_scanner.setMax(installedPackageList.size());
				int index=0;
				for(PackageInfo packageInfo:installedPackageList){
					ScanAppInfo scanAppInfo=new ScanAppInfo();
					Signature[] signatures = packageInfo.signatures;
					Signature signature=signatures[0];
					//签名文件MD5加密
					String encoder=Md5Util.encoder(signature.toCharsString());
					//与病毒数据库中的MD5相比对，若contain 则该应用为病毒，获取病毒数据库中的签名List
					if(dbVirusList.contains(encoder)){
						//是病毒   要记录
						scanAppInfo.isVirus=true;
						virusAppList.add(scanAppInfo);
					}else {
						scanAppInfo.isVirus=false;
					}
					scanAppInfo.packageName=packageInfo.packageName;
					scanAppInfo.name=packageInfo.applicationInfo.loadLabel(pm).toString();
					allAppList.add(scanAppInfo);
					index++;
					Log.e("AnitVirusActivity",index+"已扫描");
					pb_scanner.setProgress(index);
					try {
						//扫描随机睡眠时间
						Thread.sleep(50+new Random().nextInt(100));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//根据扫描次数更新progressBar,for循环中是正在扫描，for结束 扫描结束，通过handler通知主线程改Text
					Message msg=Message.obtain();
					msg.what=SCANING;
					msg.obj=scanAppInfo;
					mHandler.sendMessage(msg);
				}
				Message msg=Message.obtain();
				msg.what=SCAN_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
		
	}
	class ScanAppInfo{
		public boolean isVirus;
		public String name;
		public String packageName;
	}

	private void initUI() {
		iv_scanner = (ImageView) findViewById(R.id.iv_virus_scanner);
		tv_scanningname = (TextView) findViewById(R.id.tv_virus_appname);
		pb_scanner = (ProgressBar) findViewById(R.id.pb_virus_scanner);
		ll_scannered = (LinearLayout) findViewById(R.id.ll_virus_scannered);
		iv_scanner.startAnimation(rotateAnimation);
	}

}
