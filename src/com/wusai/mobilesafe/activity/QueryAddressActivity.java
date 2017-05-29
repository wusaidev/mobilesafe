package com.wusai.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.engine.AddressDos;

public class QueryAddressActivity extends Activity {
	private TextView tv_query_address_result;
	private EditText et_query_phone;
	private Button bt_query_phone_address;
	private String locationText;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_query_address_result.setText(locationText);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_address_query);
		tv_query_address_result=(TextView)findViewById
				(R.id.tv_query_address_result);
		et_query_phone=(EditText)findViewById(R.id.et_query_phone);
		bt_query_phone_address=(Button)findViewById(R.id.bt_query_phone_address);
		bt_query_phone_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_query_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					query(phone);
				}else{
					Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
			        findViewById(R.id.et_query_phone).startAnimation(shake);
			        shake.setInterpolator(new Interpolator() {
						
						@Override
						public float getInterpolation(float arg0) {
							// TODO Auto-generated method stub
							return 0;
						}
					});
			        et_query_phone.startAnimation(shake);
			        Vibrator vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			        vibrator.vibrate(new long[]{2000,1000}, -1);
				}
			}
		});
		et_query_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String phone=et_query_phone.getText().toString();
				query(phone);
			}
		});
	}

	protected void query(final String phone) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				locationText = AddressDos.getAddress(phone);
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

}
