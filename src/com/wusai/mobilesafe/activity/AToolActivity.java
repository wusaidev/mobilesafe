package com.wusai.mobilesafe.activity;

import java.io.File;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.engine.SmsBackUp;
import com.wusai.mobilesafe.engine.SmsBackUp.CallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address,tv_sms_backup;
	private ProgressBar pb_bar;
	private TextView tv_commonnumber_query;
	private TextView tv_app_lock;
	private TextView tv_common_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		
		//电话归属地查询方法
		initPhoneAddress();
		//短信备份方法
		initSmsBackUp();
		//常用号码查询
		initCommonNumberQuery();
		//程序锁
		initAppLock();
	}

	private void initAppLock() {
		TextView tv_applock=(TextView) findViewById(R.id.tv_atool_applock);
		tv_applock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(AToolActivity.this,AppLockActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initCommonNumberQuery() {
		tv_common_number = (TextView) findViewById(R.id.tv_common_number);
		tv_common_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(AToolActivity.this,CommonNumberQueryActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initSmsBackUp() {
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		pb_bar = (ProgressBar) findViewById(R.id.pb_backup_sms);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSmsBackUpDialog();
			}
		});
	}

	protected void showSmsBackUpDialog() {
		//1,创建一个带进度条的对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		//2,指定进度条的样式为水平
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//3,展示进度条
		progressDialog.show();
		//4,直接调用备份短信方法即可
		new Thread(){
			@Override
			public void run() {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"sms74.xml";
				SmsBackUp.backup(getApplicationContext(), path, new CallBack() {
					@Override
					public void setProgress(int index) {
						//由开发者自己决定,使用对话框还是进度条
						progressDialog.setProgress(index);
						pb_bar.setProgress(index);
					}
					
					@Override
					public void setMax(int max) {
						//由开发者自己决定,使用对话框还是进度条
						progressDialog.setMax(max);
						pb_bar.setMax(max);
					}
				});
				
				progressDialog.dismiss();
			}
		}.start();
	}

	private void initPhoneAddress() {
		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
			}
		});
	}
}
