package com.wusai.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SDClearActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(getApplicationContext());
		textView.setText("sd卡清理界面");
		setContentView(textView);
	}
}
