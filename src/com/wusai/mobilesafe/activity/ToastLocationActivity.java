package com.wusai.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.utils.ConstantValue;
import com.wusai.mobilesafe.utils.SpUtil;

public class ToastLocationActivity extends Activity {
	private ImageView iv_drag;
	private Button bt_toast_top;
	private Button bt_toast_bottom;
	private WindowManager mWM;
	private long[] mHits=new long[2];
	private int left;
	private int top;
	private int right;
	private int bottom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_toast_location);
		initUI();
	}
	private void initUI() {
		mWM = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		final int mWHeight=mWM.getDefaultDisplay().getHeight();
		final int mWWidth=mWM.getDefaultDisplay().getWidth();
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		bt_toast_top = (Button) findViewById(R.id.bt_toast_top);
		bt_toast_bottom = (Button) findViewById(R.id.bt_toast_bottom);
		int toast_location_y=SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,0);
		int toast_location_x=SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X,0);
		LayoutParams layoutParams=new RelativeLayout.LayoutParams
				(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = toast_location_x;
		layoutParams.topMargin = toast_location_y;
		iv_drag.setLayoutParams(layoutParams);
		if(toast_location_y>mWHeight/2){
			bt_toast_top.setVisibility(View.VISIBLE);
			bt_toast_bottom.setVisibility(View.INVISIBLE);
		}else{
			bt_toast_top.setVisibility(View.INVISIBLE);
			bt_toast_bottom.setVisibility(View.VISIBLE);
		}
		iv_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
		        mHits[mHits.length-1] = SystemClock.uptimeMillis();
		        if(mHits[mHits.length-1]-mHits[0]<500){
		        	left=mWWidth/2-iv_drag.getWidth()/2;
		        	top=mWHeight/2-iv_drag.getHeight()/2;
		        	right=mWWidth/2+iv_drag.getWidth()/2;
		        	bottom=mWHeight/2+iv_drag.getHeight()/2;
		        	iv_drag.layout(left, top, right, bottom);
		        	SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X,iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,iv_drag.getTop());
		        }
			}
		});
		
		iv_drag.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;
			private int moveX;
			private int moveY;
			private int disX;
			private int disY;
			

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
					moveX = (int) event.getRawX();
					moveY = (int) event.getRawY();
					disX = moveX-startX;
					disY = moveY-startY;
					left = iv_drag.getLeft()+disX;
					top = iv_drag.getTop()+disY;
					right = iv_drag.getRight()+disX;
					bottom = iv_drag.getBottom()+disY;
					if(left<0||top<0||right>mWM.getDefaultDisplay().getWidth()||bottom>(mWHeight-50)){
						return true ;
					}
					if(top>(mWM.getDefaultDisplay().getHeight()/2)){
						bt_toast_top.setVisibility(View.VISIBLE);
						bt_toast_bottom.setVisibility(View.INVISIBLE);
					}else{
						bt_toast_top.setVisibility(View.INVISIBLE);
						bt_toast_bottom.setVisibility(View.VISIBLE);
					}
					iv_drag.layout(left, top, right, bottom);
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
					case MotionEvent.ACTION_UP:
						
						SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X,iv_drag.getLeft());
						SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,iv_drag.getTop());
						break;
					default:
						break;
				}
				//返回  false在当前情况下不响应事件
				return false;
			}
		});
	}
}
