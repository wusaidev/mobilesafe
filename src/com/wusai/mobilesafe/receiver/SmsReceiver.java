package com.wusai.mobilesafe.receiver;



import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.service.LocationService;
import com.wusai.mobilesafe.utils.ConstantValue;
import com.wusai.mobilesafe.utils.MyApplication;
import com.wusai.mobilesafe.utils.SpUtil;
import com.wusai.mobilesafe.utils.ToastUtil;

public class SmsReceiver extends BroadcastReceiver {
//	 DevicePolicyManager mDPM=(DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);

	/*DevicePolicyManager mDPM =
		    (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);*/
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean open_security=SpUtil.getBoolean(MyApplication.getContext(), ConstantValue.OPEN_SECURITY, false);
		if(open_security){
			ToastUtil.show(MyApplication.getContext(),"有短信了");
			Object[] objects=(Object[])intent.getExtras().get("pdus");
			for(Object object:objects){
				SmsMessage sms=SmsMessage.createFromPdu((byte[])object);
				String originatingAddress=sms.getOriginatingAddress();
				String messageBobyString=sms.getMessageBody();
				if(messageBobyString.contains("#*alarm*#")){
					MediaPlayer mediaPlayer=MediaPlayer.create(MyApplication.getContext(), R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
				}
				if(messageBobyString.contains("#*location*#")){
					MyApplication.getContext().startService(new Intent(MyApplication.getContext(),LocationService.class));
					ToastUtil.show(MyApplication.getContext(), "服务正在启动");
				}
				if(messageBobyString.contains("#*lockscreen*#")){
//					mDPM.lockNow();
//					mDPM.resetPassword("123", 0);
				}
				if(messageBobyString.contains("#*wipedata*#")){
//					mDPM.wipeData(0);
				}
			}
		}
	}
	/*private DevicePolicyManager getSystemService(String devicePolicyService) {
		return (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
	}*/
}

