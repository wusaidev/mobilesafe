package com.wusai.mobilesafe.test;

import java.util.List;

import android.R.integer;
import android.test.AndroidTestCase;

import com.wusai.mobilesafe.bean.InstalledAppInfoBean;
import com.wusai.mobilesafe.db.dao.BlackNumberDao;
import com.wusai.mobilesafe.db.domain.BlackNumberInfo;
import com.wusai.mobilesafe.utils.AppInfoUtils;

public class Test extends AndroidTestCase {
	public void testAppInfo(){
		List<InstalledAppInfoBean> allInstalledAppInfos = AppInfoUtils.getAllInstalledAppInfos(getContext());
		for(InstalledAppInfoBean bean:allInstalledAppInfos){
			System.out.println(bean);
		}
	}
	public void insert(){
		BlackNumberDao dao=BlackNumberDao.getInstance(getContext());
		for(int i=0;i<100;i++){
			if(i<10){
				dao.insert("1860000000"+i,"1");
			}else{
				dao.insert("1860000000"+i,"1");
			}
		}
	}
	public void updata(){
		BlackNumberDao dao=BlackNumberDao.getInstance(getContext());
		dao.update("110","3");
	}
	public void delete(){
		BlackNumberDao dao=BlackNumberDao.getInstance(getContext());
		dao.delete("110");
	}
	public void findAll(){
		BlackNumberDao dao=BlackNumberDao.getInstance(getContext());
		List<BlackNumberInfo> blackNumberList=dao.findAll();
	}
	
}
