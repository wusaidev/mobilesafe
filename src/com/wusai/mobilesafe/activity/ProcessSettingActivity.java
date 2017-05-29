package com.wusai.mobilesafe.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.service.LockScreenService;
import com.wusai.mobilesafe.utils.ConstantValue;
import com.wusai.mobilesafe.utils.ServiceUtil;
import com.wusai.mobilesafe.utils.SpUtil;

public class ProcessSettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		showSystem();
		lockClean();
	}

	private void showSystem() {
		final CheckBox show_system = (CheckBox) findViewById(R.id.cb_process_setting_shosystem);
		boolean showSystem = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.SHOW_SYSTEM_PROCESS, false);
		show_system.setChecked(showSystem);
		if (showSystem) {
			show_system.setText("显示系统进程");
		} else {
			show_system.setText("隐藏系统进程");
		}
		show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					show_system.setText("显示系统进程");
				} else {
					show_system.setText("隐藏系统进程");
				}
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.SHOW_SYSTEM_PROCESS, isChecked);
			}
		});
	}

	private void lockClean() {
		final CheckBox lock_clean = (CheckBox) findViewById(R.id.cb_process_setting_lockclean);
		final Intent intent=new Intent(getApplicationContext(),LockScreenService.class);
		boolean isRunning=ServiceUtil.isRunning(getApplicationContext(),
				"com.itheima.mobilesafe74.service.LockScreenService");
		lock_clean.setChecked(isRunning);
		if (isRunning) {
			lock_clean.setText("锁屏清理已开启");
			lock_clean.setChecked(isRunning);
		} else {
			lock_clean.setText("锁屏清理已关闭");
			lock_clean.setChecked(isRunning);
		}
		lock_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					lock_clean.setText("锁屏清理已开启");
					startService(intent);
				} else {
					lock_clean.setText("锁屏清理已关闭");
					stopService(intent);
				}
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.LOCK_CLEAN, isChecked);
			}
		});
	}
}
