package com.wusai.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.wusai.mobilesafe.db.dao.BlackNumberDao;

public class BlackNumberService extends Service {
	private String tag="BlackNumberService";
	private BlackNumberDao mDao;
	private InterceptSmsReceiver mInterceptSmsReceiver;
	private TelephonyManager mTM;
	private MyPhoneStateListener myPhoneStateListener;
	private MyContentObserver myContentObserver;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mDao=BlackNumberDao.getInstance(getApplicationContext());
		Log.e(tag, "黑名单服务已开启");
		//拦截短信
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		mInterceptSmsReceiver = new InterceptSmsReceiver();
		registerReceiver(mInterceptSmsReceiver, intentFilter);
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				int mode=mDao.getMode(incomingNumber);
				if(mode==2||mode==3){
					endCall(incomingNumber);
				}
				break;

			default:
				break;
			}
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	class InterceptSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//获取短信内容，获取发送短信的电话号码，若在黑名单中，并且在拦截模式为1 或3  则拦截
			//获取短信内容
			Object[] objects=(Object[]) intent.getExtras().get("pdus");
//			循环遍历短信过程
			for(Object object:objects){
				//获取短信对象
				SmsMessage sms=SmsMessage.createFromPdu((byte[])object);
				//获取短信对象的基本信息
				String originatingAddress=sms.getOriginatingAddress();
				String messageBody=sms.getMessageBody();
				int mode=mDao.getMode(originatingAddress);
				if(mode==1||mode==3){
					abortBroadcast();
				}
			}
		}
	}
	public void endCall(String phone){
		 try {  
	            //获取到ServiceManager  
	            Class<?> clazz = Class.forName("android.os.ServiceManager");  
	            //获取到ServiceManager里面的方法  
	            Method method = clazz.getDeclaredMethod("getService", String.class);  
	            //通过反射的方法调用方法  
	            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);  
	            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);  
	            iTelephony.endCall();  
	        } catch (Exception e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }
		 myContentObserver = new MyContentObserver(new Handler(), phone);
		 getContentResolver().registerContentObserver
		 (CallLog.Calls.CONTENT_URI,true, myContentObserver);

	}
	class MyContentObserver extends ContentObserver{

		private String phone;
		public MyContentObserver(Handler handler,String phone) {
			super(handler);
			this.phone=phone;
			// TODO Auto-generated constructor stub
		} 
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			 getContentResolver().delete(CallLog.Calls.CONTENT_URI, "number=?",
					 new String[]{phone});
		}	
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(tag, "黑名单服务已关闭");
		if(mInterceptSmsReceiver!=null){
			unregisterReceiver(mInterceptSmsReceiver);
		}
		if(myPhoneStateListener!=null){
			mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if(myContentObserver!=null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
		
	}
}
