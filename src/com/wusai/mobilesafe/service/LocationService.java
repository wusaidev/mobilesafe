/*package com.itheima.mobilesafe74.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;


import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.MyApplication;
import com.itheima.mobilesafe74.utils.SpUtil;
import com.itheima.mobilesafe74.utils.ToastUtil;

public class LocationService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LocationManager lm=(LocationManager) MyApplication.getContext().
				getSystemService(MyApplication.getContext().LOCATION_SERVICE);
		Criteria criteria=new Criteria();
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider=lm.getBestProvider(criteria, true);
		MyLocationListener myLocationListener=new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
		
	}
	class MyLocationListener implements LocationListener{

		@SuppressWarnings("deprecation")
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			ToastUtil.show(getApplicationContext(), "定位服务已经启动");
			double longitude=location.getLongitude();
			double latitude=location.getLatitude();
			TelephonyManager tm=(TelephonyManager)MyApplication.getContext().
					getSystemService(MyApplication.getContext().toString());
			String contact_phone=SpUtil.getString(MyApplication.getContext(), 
					ConstantValue.CONTACT_PHONE, "");
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(contact_phone, null, 
					"longitude"+longitude+latitude, null, null);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}}
}
*/
package com.wusai.mobilesafe.service;



import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.baidu.location.LocationClient;
import com.wusai.mobilesafe.utils.ConstantValue;
import com.wusai.mobilesafe.utils.SpUtil;

public class LocationService extends Service {
	String locationMessage=null;
	@Override
	public void onCreate() {
		super.onCreate();
		LocationManager lm=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
		/*List<String> allProviders = lm.getAllProviders();  
        for (String string : allProviders) {  
            System.out.println("所有定位方式：>>>"+string);  
        }*/  
          
        Criteria criteria = new Criteria();  
        criteria.setCostAllowed(true);//设置产生费用，收费的一般比较精确  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//精确度设为最佳  
          
        //动态获取最佳定位方式  
        String bestProvider = lm.getBestProvider(criteria, true);  
        /**  
         * 1、provider:那种定位方式  
         * 2、minTime：定位的时间差  
         * 3、minDistance：定位距离差  
         * 4、定位监听回调  
         *   
         */  
        //Register for location updates   
		lm.requestLocationUpdates(bestProvider, 0, 0, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// gps 状态发生改变
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// gps zhuangtai 可用
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// GPS 状态不可用
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// 位置发生改变
				String contact_phone=SpUtil.getString
						(getApplicationContext(),ConstantValue.CONTACT_PHONE,"");
				double longitude=location.getLongitude();
				double latitude=location.getLatitude();
				/*tv_location.setText("经度"+longitude+"\n纬度"+latitude);*/
				String locationMessage="longitude:"+longitude+"\nlatitude:"+latitude;
				SmsManager smsManager=SmsManager.getDefault();
				smsManager.sendTextMessage(contact_phone, null, locationMessage, null, null);
				
			}
		});
	   }
		/*//获取手机的经纬度坐标
		//1,获取位置管理者对象
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2,以最优的方式获取经纬度坐标()
		Criteria criteria = new Criteria();
		//允许花费
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//指定获取经纬度的精确度
		String bestProvider = lm.getBestProvider(criteria, true);
		//3,在一定时间间隔,移动一定距离后获取经纬度坐标
		MyLocationListener myLocationListener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
		class MyLocationListener implements LocationListener{

			@Override
			public void onLocationChanged(Location location) {
				//经度
				double longitude = location.getLongitude();
				//纬度
				double latitude = location.getLatitude();
				
				//4,发送短信(添加权限)
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage("5558", null, "longitude = "+longitude+",latitude = "+latitude, null, null);
			}
			@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}*/

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
		
}
	

