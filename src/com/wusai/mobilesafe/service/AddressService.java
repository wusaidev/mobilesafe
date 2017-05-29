package com.wusai.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.engine.AddressDos;
import com.wusai.mobilesafe.utils.ConstantValue;
import com.wusai.mobilesafe.utils.SpUtil;
import com.wusai.mobilesafe.utils.ToastUtil;

public class AddressService extends Service {
	public static final String tag = "AddressService";
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mViewToast;
	private WindowManager mWM;
	private String mAddress;
	private TextView tv_toast;
	/*private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};*/
	Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	private int[] mDrawableIds;
	private int mWMWidth;
	private int mWMHeight;
	private InnerOutCallReceiver mInnerOutCallReceiver;
	
	@Override
	public void onCreate() {
		//第一次开启服务以后,就需要去管理吐司的显示
		//电话状态的监听(服务开启的时候,需要去做监听,关闭的时候电话状态就不需要监听)
		//1,电话管理者对象
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//2,监听电话状态
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		//获取窗体对象
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWMWidth = mWM.getDefaultDisplay().getWidth();
		mWMHeight = mWM.getDefaultDisplay().getHeight();
		//监听电话播出的过滤条件
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		mInnerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(mInnerOutCallReceiver,intentFilter);
		Log.e(tag,"电话状态监听服务已创建");
		super.onCreate();
	}
	class InnerOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("AddressService","电话正在播出，这里是播出电话回调方法");
			String phone=getResultData();
			showToast(phone);
		}
	}
	class MyPhoneStateListener extends PhoneStateListener{
		//3,手动重写,电话状态发生改变会触发的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				//空闲状态,没有任何活动(移除吐司)
				Log.i(tag, "挂断电话,空闲了.......................");
				//挂断电话的时候窗体需要移除吐司
				/*if(mWM!=null && mViewToast!=null){
					mWM.removeView(mViewToast);
				}*/
				if(mWM!=null&&mViewToast!=null){
					mWM.removeView(mViewToast);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//响铃(展示吐司)
				Log.i(tag, "响铃了.......................");
				showToast(incomingNumber);
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}
	public void showToast(String incomingNumber) {
		//Toast.makeText(getApplicationContext(), incomingNumber, 0).show();
		final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//      设置动画  params.windowAnimations = com.android.internal.R.style.Animation_Toast;
//      在响铃是显示吐司和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
		
		
        //指定吐司的所在位置  所上角
        params.gravity=Gravity.LEFT+Gravity.TOP;
      //屏幕亮起来才可看到吐司
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//             默认颗touch able  需要干掉| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        //吐司的显示效果（吐司布局文件）
        mViewToast=View.inflate(getApplicationContext(), R.layout.toast_view,null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);
        mViewToast.setOnTouchListener(new OnTouchListener() {
			private int moveY;
			private int startX;
			private int startY;
			private int moveX;
			private int disX;
			private int disY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					
					int disX = moveX-startX;
					int disY = moveY-startY;
					
					params.x = params.x+disX;
					params.y = params.y+disY;
					
					//容错处理
					if(params.x<0){
						params.x = 0;
					}
					
					if(params.y<0){
						params.y=0;
					}
					
					if(params.x>mWMWidth-mViewToast.getWidth()){
						params.x = mWMWidth-mViewToast.getWidth();
					}
					
					if(params.y>mWMHeight-mViewToast.getHeight()-22){
						params.y = mWMHeight-mViewToast.getHeight()-22;
					}
					mWM.updateViewLayout(mViewToast, params);
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					
					SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X,params.x);
					SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,params.y);
					break;
				default:
					break;
			}
				return true ;
			}
		});
        params.x=SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X,0);
        params.y=SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,0);
        int[] mDrawableIds=new int[]{R.drawable.call_locate_white,
        		R.drawable.call_locate_orange,R.drawable.call_locate_blue,
        		R.drawable.call_locate_gray,R.drawable.call_locate_green};
        int mToastStyleIndex=SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE,0);
        ToastUtil.show(AddressService.this,"索引"+mToastStyleIndex);
        tv_toast.setBackgroundResource(mDrawableIds[mToastStyleIndex]);
        mWM.addView(mViewToast, mParams);
		query(incomingNumber);

	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	public void query(final String incomingNumber) {
		// TODO Auto-generated method stub
		new Thread(){
			public void run() {
				mAddress=AddressDos.getAddress(incomingNumber);	
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}
	@Override
	public void onDestroy() {
		//取消对电话状态的监听(开启服务的时候监听电话的对象)
		if(mTM!=null && mPhoneStateListener!=null){
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if(mInnerOutCallReceiver!=null){
			unregisterReceiver(mInnerOutCallReceiver);
		}
		super.onDestroy();
		Log.e(tag,"电话状态监听服务已销毁");

	}
}
