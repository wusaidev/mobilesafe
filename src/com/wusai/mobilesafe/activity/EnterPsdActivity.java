package com.wusai.mobilesafe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.utils.ToastUtil;



public class EnterPsdActivity extends Activity {
	private ApplicationInfo applicationInfo;
	private TextView tv_applock_name;
	private ImageView iv_applock_logo;
	private EditText et_applock_psd;
	private Button bt_applock_confirm;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_psd);
		initUI();
		initData();
	}

	private void initUI() {
		tv_applock_name = (TextView) findViewById(R.id.tv_applock_name);
		iv_applock_logo = (ImageView) findViewById(R.id.iv_applock_logo);
		et_applock_psd = (EditText) findViewById(R.id.et_applock_psd);
		bt_applock_confirm = (Button) findViewById(R.id.bt_applock_confirm);
		
	}

	@SuppressLint("NewApi") private void initData() {
		Intent intent =getIntent();
		packageName = intent.getStringExtra("packagename");
		PackageManager pm=getPackageManager();
		try {
			//通过包名，获取应用信息对象
			applicationInfo = pm.getApplicationInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String appName = applicationInfo.name;
		Drawable icon = applicationInfo.loadIcon(pm);
		tv_applock_name.setText(appName);
		iv_applock_logo.setBackground(icon);
		bt_applock_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String psw=et_applock_psd.getText().toString();
				if(!TextUtils.isEmpty(psw)){
					if("123".equals(psw)){
						Intent broadIntent = new Intent("android.intent.action.SKIP_APP");
						broadIntent.putExtra("packagename",packageName);
						sendBroadcast(broadIntent);
						finish();
					}else{
						ToastUtil.show(getApplicationContext(), "密码错误，请重新输入");
					}
				}else{
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.e("enterpsd","点击返回");
		Intent intent=new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		super.onBackPressed();
	}
}
