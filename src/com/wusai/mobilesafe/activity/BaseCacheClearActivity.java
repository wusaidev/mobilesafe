package com.wusai.mobilesafe.activity;

import com.itheima.mobilesafe74.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost.TabSpec;

public class BaseCacheClearActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_cacheclear);
		TabSpec tab1=getTabHost().newTabSpec("cache_clear").setIndicator("缓存清理");
		TabSpec tab2=getTabHost().newTabSpec("sd_clear").setIndicator("SD卡清理");
		tab1.setContent(new Intent(BaseCacheClearActivity.this,CacheClearActivity.class));
		tab2.setContent(new Intent(BaseCacheClearActivity.this,SDClearActivity.class));
		getTabHost().addTab(tab1);
		getTabHost().addTab(tab2);
	}
}
