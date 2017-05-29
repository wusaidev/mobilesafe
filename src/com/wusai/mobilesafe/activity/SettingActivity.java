package com.wusai.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.service.AddressService;
import com.wusai.mobilesafe.service.BlackNumberService;
import com.wusai.mobilesafe.service.WatchDogService;
import com.wusai.mobilesafe.utils.ConstantValue;
import com.wusai.mobilesafe.utils.ServiceUtil;
import com.wusai.mobilesafe.utils.SpUtil;
import com.wusai.mobilesafe.utils.ToastUtil;
import com.wusai.mobilesafe.view.SettingClickView;
import com.wusai.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {
	private String[] mToastStyleDes;
	private int toast_style;
	private SettingClickView siv_toast_style;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initUpdate();
		initAddress();
		initToastStyle();
		initLocation();
		initBlackNumber();
		initApplock();
	}

	/**
	 * 初始化程序锁方法
	 */
	private void initApplock() {
		final SettingItemView siv_app_lock=(SettingItemView) findViewById(R.id.siv_app_lock);
		boolean isCheck=ServiceUtil.isRunning(getApplicationContext(), "com.itheima.mobilesafe74.service.WatchDogService");
		siv_app_lock.setCheck(isCheck);
		siv_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCheck=siv_app_lock.isCheck();
				siv_app_lock.setCheck(!isCheck);
				if(!isCheck){
					startService(new Intent(SettingActivity.this,WatchDogService.class));
				}else{
					stopService(new Intent(SettingActivity.this,WatchDogService.class));
				}
			}
		});
	}

	private void initBlackNumber() {
		final SettingItemView siv_black_number = (SettingItemView) 
				findViewById(R.id.siv_black_number);
		final boolean isCheckedService=ServiceUtil.isRunning(getApplicationContext(),
				"com.itheima.mobilesafe74.service.BlackNumberService");
		boolean isCheckedSp=SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_BLACK_NUMBER,false);
		if(isCheckedService){
			siv_black_number.setCheck(isCheckedService);
		}else if(isCheckedSp){
			siv_black_number.setCheck(isCheckedSp);
			startService(new Intent(SettingActivity.this,BlackNumberService.class));
		}

		siv_black_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isChecked=siv_black_number.isCheck();
				siv_black_number.setCheck(!isChecked);
				if(!isChecked){
					startService(new Intent(SettingActivity.this,BlackNumberService.class));
				}else {
					stopService(new Intent(SettingActivity.this,BlackNumberService.class));
				}
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_BLACK_NUMBER, !isChecked);
			}
		});
	}

	private void initLocation() {
		SettingClickView scv_toast_location=(SettingClickView) findViewById(R.id.scv_toast_location);
		scv_toast_location.setTitle("电话归属地所在位置");
		scv_toast_location.setDes("设置电话归属地所在位置");
		scv_toast_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SettingActivity.this,ToastLocationActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initToastStyle() {
		siv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		siv_toast_style.setTitle("设置归属地显示风格");
		mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
		toast_style = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
		siv_toast_style.setDes(mToastStyleDes[toast_style]);
		siv_toast_style.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtil.show(getApplicationContext(), "显示风格");
				showToastStyleDialog();
			}
		});
	}
	private void showToastStyleDialog() {
		Builder toastStyleDialog=new AlertDialog.Builder(SettingActivity.this);
		toastStyleDialog.setTitle("请选择归属地样式");
		toastStyleDialog.setIcon(R.drawable.ic_launcher);
		toastStyleDialog.setSingleChoiceItems(mToastStyleDes,toast_style , new DialogInterface.OnClickListener(){
			// 记录索引值   关闭对话框  显示选中描述
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE,which);
				dialog.dismiss();
				siv_toast_style.setDes(mToastStyleDes[which]);
			}
		});
		toastStyleDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		toastStyleDialog.show();
	}

	private void initAddress() {
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
		//对服务是否开的状态做显示
	/*	boolean isRunning = ServiceUtil.isRunning(this, "com.itheima.mobilesafe74.service.AddressService");
		siv_address.setCheck(isRunning);*/
		
		//点击过程中,状态(是否开启电话号码归属地)的切换过程
		//服务可能被系统干掉，会导致条目显示描述与服务实际状态不一致，所以直接判断服务是否开启，来决定描述
		boolean serviceIsRunning=ServiceUtil.isRunning
				(getApplicationContext(), "com.itheima.mobilesafe74.service.AddressService");
		siv_address.setCheck(serviceIsRunning);
		siv_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//返回点击前的选中状态
				boolean isCheck = siv_address.isCheck();
				siv_address.setCheck(!isCheck);
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_ADDRESS, !isCheck);
				if(!isCheck){
					ToastUtil.show(getApplicationContext(), "开启");
					//开启服务,管理吐司
					startService(new Intent(SettingActivity.this,AddressService.class));
				}else{
					ToastUtil.show(getApplicationContext(), "关闭");
					//关闭服务,不需要显示吐司
					stopService(new Intent(SettingActivity.this,AddressService.class));
				}
			}
		});
	}
	/**
	 * 版本更新开关
	 */
	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		
		//获取已有的开关状态,用作显示
		boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		//是否选中,根据上一次存储的结果去做决定
		siv_update.setCheck(open_update);
		
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//如果之前是选中的,点击过后,变成未选中
				//如果之前是未选中的,点击过后,变成选中
				
				//获取之前的选中状态
				boolean isCheck = siv_update.isCheck();
				//将原有状态取反,等同上诉的两部操作
				siv_update.setCheck(!isCheck);
				//将取反后的状态存储到相应sp中
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE,!isCheck);
			}
		});
	}

}
