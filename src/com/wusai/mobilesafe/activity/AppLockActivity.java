package com.wusai.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.bean.InstalledAppInfoBean;
import com.wusai.mobilesafe.db.dao.AppLockDao;
import com.wusai.mobilesafe.utils.AppInfoUtils;

public class AppLockActivity extends Activity {
	private Button bt_unlock,bt_lock;
	private LinearLayout ll_unlock,ll_lock;
	private TextView tv_unclockcoun,tv_clockcoun;
	private ListView lv_unclock,lv_clock;
	private List<InstalledAppInfoBean> mUnlockList;
	private List<InstalledAppInfoBean> mLockList;
	private TranslateAnimation mTranslateAnimation;
	private AppLockDao mDao;
	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;
	private Handler mHandler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			//加锁   未加锁   条目数更新
			tv_clockcoun.setText("已加锁应用："+mLockList.size());
			tv_unclockcoun.setText("未加锁应用："+mUnlockList.size());
			//加锁   未加锁   适配器刷新
			if(mLockAdapter==null||mUnLockAdapter==null){
				mLockAdapter = new MyAdapter(true);
				mUnLockAdapter = new MyAdapter(false);
				lv_clock.setAdapter(mLockAdapter);
				lv_unclock.setAdapter(mUnLockAdapter);
			}else{
				mLockAdapter.notifyDataSetChanged();
				mUnLockAdapter.notifyDataSetChanged();
			}
		};
	};
	class MyAdapter extends BaseAdapter{
		private boolean islock;

		public MyAdapter(boolean islock){
			this.islock=islock;
		}
		@Override
		public int getCount() {
			if(islock){
				return mLockList.size();
			}else{
				return mUnlockList.size();
			}
		}

		@Override
		public InstalledAppInfoBean getItem(int position) {
			if(islock){
				return mLockList.get(position);
			}else{
				return mUnlockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ViewHolder viewHolder=null;
			final InstalledAppInfoBean appInfoBean=getItem(position);
			if(convertView==null){
				convertView=View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
				viewHolder=new ViewHolder();
				viewHolder.iv_logo=(ImageView) convertView.findViewById(R.id.iv_islock_logo);
				viewHolder.tv_packagename=(TextView) convertView.findViewById(R.id.tv_islock_packagename);
				viewHolder.iv_lock=(ImageView) convertView.findViewById(R.id.iv_islock_lock);
				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder) convertView.getTag();
			}
			final View animationView=convertView;
			viewHolder.iv_logo.setImageDrawable(appInfoBean.getIcon());
			viewHolder.tv_packagename.setText(appInfoBean.getPackageName());
			if(islock){
				viewHolder.iv_lock.setImageDrawable(getResources().getDrawable(R.drawable.lock));
			}else {
				viewHolder.iv_lock.setImageDrawable(getResources().getDrawable(R.drawable.unlock));
			}
			viewHolder.iv_lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					animationView.startAnimation(mTranslateAnimation);
					mTranslateAnimation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {
							if(islock){
								//加锁App
								//枷锁列表移除  加锁数据库delete  未加锁添加
								mDao.delete(appInfoBean.getPackageName());
								mLockList.remove(appInfoBean);
								mUnlockList.add(appInfoBean);
								mLockAdapter.notifyDataSetChanged();
							}else {
								//未加锁
								//未加锁列表移除，加锁列表添加，加锁数据库添加
								mDao.insert(appInfoBean.getPackageName());
								mLockList.add(appInfoBean);
								mUnlockList.remove(appInfoBean);
								mUnLockAdapter.notifyDataSetChanged();
							}
							mHandler.sendEmptyMessage(0);
						}
					});
				}
			});
			return convertView;
		}
		
	}
	static class ViewHolder{
		ImageView iv_logo;
		TextView tv_packagename;
		ImageView iv_lock;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		initUI();
		initData();
		initAnimation();
	}

	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation
				(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, 
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnimation.setDuration(500);
	}

	private void initData() {
		new Thread(){
			@Override
			public void run() {
				List<InstalledAppInfoBean> appInfoBeans=
						AppInfoUtils.getAllInstalledAppInfos(getApplicationContext());
				mUnlockList = new ArrayList<InstalledAppInfoBean>();
				mLockList = new ArrayList<InstalledAppInfoBean>();
				mDao = AppLockDao.getInstance(getApplicationContext());
				List<String> lockPackageList=mDao.queryAll();
				for(InstalledAppInfoBean appInfoBean:appInfoBeans){
					if(lockPackageList.contains(appInfoBean.getPackageName())){
						mLockList.add(appInfoBean);
					}else{
						mUnlockList.add(appInfoBean);
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		}.start();
		
	}

	private void initUI() {
		bt_unlock = (Button) findViewById(R.id.bt_applock_unlock);
		bt_lock = (Button) findViewById(R.id.bt_applock_lock);
		
		ll_unlock = (LinearLayout) findViewById(R.id.ll_applock_unlock);
		tv_unclockcoun = (TextView) findViewById(R.id.tv_applock_unlockcount);
		lv_unclock = (ListView) findViewById(R.id.lv_applock_unlock);
		
		ll_lock = (LinearLayout) findViewById(R.id.ll_applock_lock);
		tv_clockcoun = (TextView) findViewById(R.id.tv_applock_lockcount);
		lv_clock = (ListView) findViewById(R.id.lv_applock_lock);
		bt_unlock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
				ll_unlock.setVisibility(View.VISIBLE);
				ll_lock.setVisibility(View.GONE);
			}
		});
		bt_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				ll_unlock.setVisibility(View.GONE);
				ll_lock.setVisibility(View.VISIBLE);
			}
		});
	}
}
