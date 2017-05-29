package com.wusai.mobilesafe.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;
import com.wusai.mobilesafe.bean.InstalledAppInfoBean;
import com.wusai.mobilesafe.utils.AppInfoUtils;
import com.wusai.mobilesafe.utils.ToastUtil;
import com.wusai.mobilesafe.view.TextProgressView;

public class AppManagerActivity extends Activity {
	private String tag = "AppManagerActivity";
	private MyAdapter myAdapter;// 适配器
	/*
	 * private TextView tv_memory; private TextView tv_tv_sd;
	 */
	private ListView lv_app;
	private RelativeLayout view_pb_center;
	private static final int LOADING = 1;
	private static final int FINISH = 2;
	// 系统的App容器
	private List<InstalledAppInfoBean> systemInstalledAppInfoList = new ArrayList<InstalledAppInfoBean>();
	// 用户的App容器
	private List<InstalledAppInfoBean> userInstalledAppInfoList = new ArrayList<InstalledAppInfoBean>();
	private InstalledAppInfoBean testBean;
	private List<InstalledAppInfoBean> allInstalledAppInfoList;
	private long phoneAvailMem;
	private long phoneTotalMem;
	private long sdAvailMem;
	private long sdTotalMem;
	private LinearLayout ll_loading;
	private TextView tv_app_lvtag;
	private InstalledAppInfoBean clickAppInfo;// 点击条目数据
	/*
	 * private ProgressBar pb_phone_progress; private ProgressBar
	 * pb_sd_progress;
	 */
	private TextProgressView phoneMemProgress;
	private TextProgressView sdMemProgress;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:// 正在加载数据
				ll_loading.setVisibility(View.VISIBLE);
				lv_app.setVisibility(View.GONE);
				tv_app_lvtag.setVisibility(View.GONE);
				break;
			case FINISH:// 加载数据完成
				ll_loading.setVisibility(View.GONE);
				lv_app.setVisibility(View.VISIBLE);
				tv_app_lvtag.setVisibility(View.VISIBLE);
				// 显示数据
				phoneMemProgress.setText("rom可用内存"
						+ Formatter.formatFileSize(getApplicationContext(),
								phoneAvailMem));
				sdMemProgress.setText("SD可用内存"
						+ Formatter.formatFileSize(getApplicationContext(),
								sdAvailMem));
				tv_app_lvtag.setText("用户软件(" + userInstalledAppInfoList.size()
						+ ")");
				// 更新数据
				myAdapter.notifyDataSetChanged();
				Log.e(tag, "应用名称" + phoneAvailMem + "空");
				Log.e(tag, "应用名称" + phoneTotalMem + "总");
				int phoneProgress = (int) ((phoneTotalMem - phoneAvailMem)
						* 1.0 / phoneTotalMem * 100);
				Log.e(tag, "应用名称" + phoneProgress + "总");
				phoneMemProgress.setProgress(phoneProgress);
				Log.e(tag, "应用名称" + sdAvailMem + "空");
				Log.e(tag, "应用名称" + sdTotalMem + "总");
				int sdProgress = (int) ((sdTotalMem - sdAvailMem) * 1.0 / sdTotalMem);
				sdMemProgress.setProgress(sdProgress);
				break;
			default:
				break;
			}
		};
	};
	private PopupWindow mPW;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initUI();
		initEvent();
		initPopup();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
	}

	private void initPopup() {
		View viewPopup = View.inflate(getApplicationContext(),
				R.layout.view_appmanager_popup, null);
		LinearLayout llSetting = (LinearLayout) viewPopup
				.findViewById(R.id.ll_app_poup_setting);
		LinearLayout llShare = (LinearLayout) viewPopup
				.findViewById(R.id.ll_app_poup_share);
		LinearLayout llStart = (LinearLayout) viewPopup
				.findViewById(R.id.ll_app_poup_start);
		LinearLayout llUninstall = (LinearLayout) viewPopup
				.findViewById(R.id.ll_app_poup_uninstall);
		OnClickListener listenter = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_app_poup_setting:
					appSetting();
					break;
				case R.id.ll_app_poup_share:
					appShare();
					break;
				case R.id.ll_app_poup_start:
					appStart();
					break;
				case R.id.ll_app_poup_uninstall:
					appUninstall();
					break;

				default:
					break;
				}
				mPW.dismiss();
			}
		};
		llSetting.setOnClickListener(listenter);
		llShare.setOnClickListener(listenter);
		llStart.setOnClickListener(listenter);
		llUninstall.setOnClickListener(listenter);

		mPW = new PopupWindow(viewPopup, LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		// 获取焦点，保证里面的组件可以点击
		mPW.setFocusable(true);
		mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 外部点击消失
		mPW.setOutsideTouchable(true);
		// 设置动画
		mPW.setAnimationStyle(R.style.appPopupAnimation);
	}

	private void appStart() {
		Log.e(tag, "开始：" + clickAppInfo.getPackageName());
		Intent intentStart = getPackageManager().getLaunchIntentForPackage(
				clickAppInfo.getPackageName());
		if (intentStart != null) {
			startActivity(intentStart);
		} else {
			ToastUtil.show(getApplicationContext(), "该应用不能开启");
		}
	}

	private void appUninstall() {
		Log.e(tag, "卸载：" + clickAppInfo.getPackageName());
		if (clickAppInfo.isSystem()) {
			ToastUtil.show(getApplicationContext(), "系统软件，不能卸载");
			try {
				RootTools.sendShell("mount -o remount rw/system", 5000);
				RootTools.sendShell("rm -r" + clickAppInfo.getSorceDir(), 5000);
				RootTools.sendShell("mount -o remote r/system", 5000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootToolsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			/*
			 * <intent-filter> <action android:name="android.intent.action.VIEW"
			 * /> <action android:name="android.intent.action.DELETE" />
			 * <category android:name="android.intent.category.DEFAULT" /> <data
			 * android:scheme="package" /> </intent-filter>
			 */
			Intent intentUninstall = new Intent("android.intent.action.DELETE");
			intentUninstall.setData(Uri.parse("package:"
					+ clickAppInfo.getPackageName()));
			intentUninstall.addCategory("android.intent.category.DEFAULT");
			startActivity(intentUninstall);
		}
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { // TODO Auto-generated method stub
	 * super.onActivityResult(requestCode, resultCode, data); switch
	 * (requestCode) { case 0: initData(); break;
	 * 
	 * default: break; } }
	 */
	private void appShare() {
		Log.e(tag, "分享：" + clickAppInfo.getPackageName());
		Intent intentShare = new Intent(Intent.ACTION_SEND);
		intentShare.putExtra(Intent.EXTRA_TEXT,
				"分享一个应用，报名为：" + clickAppInfo.getPackageName());
		intentShare.setType("text/plain");
		startActivity(intentShare);
	}

	private void appSetting() {
		Log.e(tag, "设置：" + clickAppInfo.getPackageName());
	}

	private void initEvent() {
		// 点击事件
		lv_app.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 显示pop
				mPW.showAsDropDown(arg1, 45, -arg1.getHeight());
				if (position < userInstalledAppInfoList.size()) {
					clickAppInfo = userInstalledAppInfoList.get(position - 1);
				} else {
					clickAppInfo = allInstalledAppInfoList.get(position
							- (userInstalledAppInfoList.size() + 2));
				}

			}
		});
		// lv滑动时间
		lv_app.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (firstVisibleItem >= userInstalledAppInfoList.size() + 1) {
					tv_app_lvtag.setText("系统软件("
							+ systemInstalledAppInfoList.size() + ")");
				} else {
					tv_app_lvtag.setText("用户软件("
							+ userInstalledAppInfoList.size() + ")");
				}
			}
		});

	}

	private void initData() {
		Message msg = Message.obtain();
		new Thread() {
			@Override
			public void run() {
				// 1 发送加载进度条消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				// 2 获取数据
				// 获取所有App信息
				allInstalledAppInfoList = AppInfoUtils
						.getAllInstalledAppInfos(getApplicationContext());
				phoneAvailMem = AppInfoUtils.getPhoneAvailMem();
				phoneTotalMem = AppInfoUtils.getPhoneTotalMem();
				sdAvailMem = AppInfoUtils.getSDAvailMem();
				sdTotalMem = AppInfoUtils.getSDTotalMem();
				systemInstalledAppInfoList.clear();
				userInstalledAppInfoList.clear();
				for (InstalledAppInfoBean installedAppInfoBean : allInstalledAppInfoList) {
					if (installedAppInfoBean.isSystem()) {
						systemInstalledAppInfoList.add(installedAppInfoBean);
					} else {
						userInstalledAppInfoList.add(installedAppInfoBean);
					}
				}
				testBean = systemInstalledAppInfoList.get(5);
				String nameString = testBean.getAppName();
				Log.e(tag, "应用名称" + nameString);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 3 发送数据加载完成
				mHandler.obtainMessage(FINISH).sendToTarget();
			}
		}.start();
	}

	private void initUI() {
		setContentView(R.layout.activity_app_manager);
		// tv_memory = (TextView) findViewById(R.id.tv_memory);
		// tv_tv_sd = (TextView) findViewById(R.id.tv_sd);
		lv_app = (ListView) findViewById(R.id.lv_app);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_app_lvtag = (TextView) findViewById(R.id.tv_app_lvtag);
		// 绑定适配器
		myAdapter = new MyAdapter();
		lv_app.setAdapter(myAdapter);
		phoneMemProgress = (TextProgressView) findViewById(R.id.tpv_appmanager_rom_mess);
		sdMemProgress = (TextProgressView) findViewById(R.id.tpv_appmanager_sd_mess);

	}

	private void initTitle() {
		// 获取磁盘路径
		String path = Environment.getDataDirectory().getAbsolutePath();
		// 获取SD卡路径
		String sdPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		getAvailSpace(path);
		getAvailSpace(sdPath);
	}

	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		long count = statFs.getBlockCount();
		long size = statFs.getBlockSize();
		return count * size;
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return systemInstalledAppInfoList.size() + 1
					+ userInstalledAppInfoList.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			InstalledAppInfoBean bean = null;
			if (position == 0) {
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setBackgroundColor(Color.GRAY);
				tv_usertag.setTextColor(Color.WHITE);
				tv_usertag.setText("用户软件(" + userInstalledAppInfoList.size()
						+ ")");
				return tv_usertag;
			} else if (position == (userInstalledAppInfoList.size() + 1)) {
				TextView tv_systemtag = new TextView(getApplicationContext());
				tv_systemtag.setBackgroundColor(Color.GRAY);
				tv_systemtag.setTextColor(Color.WHITE);
				tv_systemtag.setText("系统软件("
						+ systemInstalledAppInfoList.size() + ")");
				return tv_systemtag;
			}
			ViewHolder viewHolder = null;
			if (convertView != null && (!(convertView instanceof TextView))) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_appmanager_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_app_lv_icon = (ImageView) convertView
						.findViewById(R.id.iv_app_lv_icon);
				viewHolder.tv_app_lv_name = (TextView) convertView
						.findViewById(R.id.tv_app_lv_name);
				viewHolder.tv_app_lv_location = (TextView) convertView
						.findViewById(R.id.tv_app_lv_location);
				viewHolder.tv_app_lv_size = (TextView) convertView
						.findViewById(R.id.tv_app_lv_size);
				// 设置标记
				convertView.setTag(viewHolder);
			}
			// 取值赋值
			if (position <= userInstalledAppInfoList.size()) {
				// 用户数据
				bean = userInstalledAppInfoList.get(position - 1);
			} else {
				// 系统数据
				bean = systemInstalledAppInfoList.get(position
						- (userInstalledAppInfoList.size() + 2));
			}
			// 显示数据
			viewHolder.tv_app_lv_location.setText(bean.isSD() ? "sd存储"
					: "rom存储");// 位置
			viewHolder.tv_app_lv_name.setText(bean.getAppName());// 名字
			viewHolder.iv_app_lv_icon.setImageDrawable(bean.getIcon());// 位置图标
			viewHolder.tv_app_lv_size.setText(Formatter.formatFileSize(
					getApplicationContext(), bean.getSize()));// 大小
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView iv_app_lv_icon;
		TextView tv_app_lv_name;
		TextView tv_app_lv_location;
		TextView tv_app_lv_size;
	}

}
