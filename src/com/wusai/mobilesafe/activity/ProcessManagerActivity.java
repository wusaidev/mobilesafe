package com.wusai.mobilesafe.activity;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager.OnActivityResultListener;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe74.R;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.wusai.mobilesafe.bean.RunningProgressInfoBean;
import com.wusai.mobilesafe.utils.ConstantValue;
import com.wusai.mobilesafe.utils.ProcessUtil;
import com.wusai.mobilesafe.utils.SpUtil;
import com.wusai.mobilesafe.utils.ToastUtil;

public class ProcessManagerActivity extends Activity implements OnClickListener {
	private TextView tv_process_count,tv_memory_info,tv_progress_lvtag;
	private ListView lv_process_list;
	private Button bt_select_all,bt_select_reverse,bt_clear,bt_setting;
	private int mProcessCount;
	private List<RunningProgressInfoBean> mProcessInfoList;
	
	private ArrayList<RunningProgressInfoBean> mCustomerList=new ArrayList<RunningProgressInfoBean>();
	private ArrayList<RunningProgressInfoBean> mSystemList = new ArrayList<RunningProgressInfoBean>();
	private MyAdapter mAdapter;
	private static final int LOADING=0;
	private static final int FINISH=1;
	private static final int SETTING_FINISHED=2;
	private RunningProgressInfoBean mProcessInfo;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				dataLoading.setVisibility(View.VISIBLE);
				lv_process_list.setVisibility(View.GONE);
				buttons.setVisibility(View.GONE);
				tv_progress_lvtag.setVisibility(View.GONE);
				break;
			case FINISH:
				mAdapter = new MyAdapter();
				lv_process_list.setAdapter(mAdapter);
				
				if(tv_progress_lvtag!=null && mCustomerList!=null){
					tv_progress_lvtag.setText("用户应用("+mCustomerList.size()+")");
				}
				Log.e("ProcessManagerActivity", "数据加载已完成");
				dataLoading.setVisibility(View.GONE);
				lv_process_list.setVisibility(View.VISIBLE);
				buttons.setVisibility(View.VISIBLE);
				tv_progress_lvtag.setVisibility(View.VISIBLE);
				break;
			case SETTING_FINISHED:
				if(mAdapter!=null&&mCustomerList!=null&&mProcessInfoList!=null){
					mAdapter.notifyDataSetChanged();
					boolean showSystem=SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM_PROCESS,false);
					
					if(showSystem){
						tv_process_count.setText("进程总数:"+mProcessInfoList.size());
					}else{
						tv_process_count.setText("进程总数:"+mCustomerList.size());
					}
				}
				break;
			default:
				break;
			}
			
		};
	};
	private long mAvailSpace;
	private String mStrTotalSpace;
	private LinearLayout dataLoading;
	private LinearLayout buttons;
	private String strAvailSpace;
	
	class MyAdapter extends BaseAdapter{
		//获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount()+1;
		}
		
		//指定索引指向的条目类型,条目类型状态码指定(0(复用系统),1)
		@Override
		public int getItemViewType(int position) {
			if(position == 0 || position == mCustomerList.size()+1){
				//返回0,代表纯文本条目的状态码
				return 0;
			}else{
				//返回1,代表图片+文本条目状态码
				return 1;
			}
		}
		
		//listView中添加两个描述条目
		@Override
		public int getCount() {
			
			if(SpUtil.getBoolean(getApplicationContext(), 
					ConstantValue.SHOW_SYSTEM_PROCESS,false)){
				return mCustomerList.size()+mSystemList.size()+2;
			}else{
				return mCustomerList.size()+1;
			}
		}

		@Override
		public RunningProgressInfoBean getItem(int position) {
			if(position == 0 || position == mCustomerList.size()+1){
				return null;
			}else{
				if(position<mCustomerList.size()+1){
					return mCustomerList.get(position-1);
				}else{
					//返回系统进程对应条目的对象
					return mSystemList.get(position - mCustomerList.size()-2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setBackgroundColor(Color.GRAY);
				tv_usertag.setTextColor(Color.WHITE);
				tv_usertag.setText("用户进程(" + mCustomerList.size()
						+ ")");
				return tv_usertag;
			} else if (position == (mCustomerList.size() + 1)) {
				TextView tv_systemtag = new TextView(getApplicationContext());
				tv_systemtag.setBackgroundColor(Color.GRAY);
				tv_systemtag.setTextColor(Color.WHITE);
				tv_systemtag.setText("系统进程("
						+ mSystemList.size() + ")");
				return tv_systemtag;
			}else{
				//展示图片+文字条目
				ViewHolder holder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_progress_logo);
					holder.tv_name = (TextView)convertView.findViewById(R.id.tv_progress_name);
					holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
					holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_progress_box);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
				holder.tv_memory_info.setText(strSize);
				
				//本进程不能被选中,所以先将checkbox隐藏掉
				if(getItem(position).packageName.equals(getPackageName())){
					holder.cb_box.setVisibility(View.GONE);
				}else{
					holder.cb_box.setVisibility(View.VISIBLE);
				}
				
				holder.cb_box.setChecked(getItem(position).isCheck);
				
				return convertView;
			}
		}
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}
	
	static class ViewTitleHolder{
		TextView tv_title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_manager);
		
		initUI();
		initTitleData();
		initListData();
		initEvent();
	}

	private void initEvent() {
		lv_process_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(mCustomerList!=null&&mSystemList!=null){
					if(firstVisibleItem>=(mCustomerList.size()+1)){
						tv_progress_lvtag.setText("系统进程("+mSystemList.size()+")");
					}else {
						tv_progress_lvtag.setText("用户进程("+mCustomerList.size()+")");
					}
				}
				
			}
		});
		/*lv_process_list.setOnScrollListener(new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			//滚动过程中调用方法
			//AbsListView中view就是listView对象
			//firstVisibleItem第一个可见条目索引值
			//visibleItemCount当前一个屏幕的可见条目数
			//总共条目总数
			if(mCustomerList!=null && mSystemList!=null){
				if(firstVisibleItem>=mCustomerList.size()+1){
					//滚动到了系统条目
					tv_des.setText("系统进程("+mSystemList.size()+")");
				}else{
					//滚动到了用户应用条目
					tv_des.setText("用户进程("+mCustomerList.size()+")");
				}
			}
			
		}
	});*/
		lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			RunningProgressInfoBean infoBean=new RunningProgressInfoBean();
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				if(position==0&&position==mCustomerList.size()+1){
					return;
				}else if(position<=mCustomerList.size()){
					infoBean=mCustomerList.get(position-1);
				}else{
					infoBean=mSystemList.get(position-mCustomerList.size()-2);
				}
				infoBean.isCheck=!infoBean.isCheck;
				CheckBox cb_progress_box=(CheckBox) view.findViewById(R.id.cb_progress_box);
				cb_progress_box.setChecked(infoBean.isCheck);
			}
		});
		
	}

	private void initListData() {
		getData();
	}
	
	private void getData() {
		new Thread(){
			public void run() {
				mHandler.obtainMessage(LOADING).sendToTarget();
				mProcessInfoList = ProcessUtil.getProgressInfo(getApplicationContext());
				for (RunningProgressInfoBean info : mProcessInfoList) {
					if(info.isSystem){
						//系统进程
						mSystemList.add(info);
					}else{
						//用户进程
						mCustomerList.add(info);
					}
				}
				RunningProgressInfoBean processTest=mSystemList.get(2);
				Log.e("ProcessManagerActivity",processTest.getPackageName());
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
	}

	private void initTitleData() {
		mProcessCount = ProcessUtil.getProgressCount(this);
		tv_process_count.setText("进程总数:"+mProcessCount);
		
		//获取可用内存大小,并且格式化
		mAvailSpace = ProcessUtil.getAvailSpace(this);
		strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
		
		//总运行内存大小,并且格式化
		long totalSpace = ProcessUtil.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);
		
		tv_memory_info.setText("剩余/总共:"+strAvailSpace+"/"+mStrTotalSpace);
	}

	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_progress_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_progress_memory);
		
		tv_progress_lvtag = (TextView) findViewById(R.id.tv_progress_lvtag);
	
		lv_process_list = (ListView) findViewById(R.id.lv_progress_list);
		dataLoading = (LinearLayout) findViewById(R.id.progress_loading);
		buttons = (LinearLayout) findViewById(R.id.ll_progress_buttons);
		bt_select_all = (Button) findViewById(R.id.bt_progress_selectall);
		bt_select_reverse = (Button) findViewById(R.id.bt_progress_reverseselect);
		bt_clear = (Button)  findViewById(R.id.bt_progress_clear);
		bt_setting = (Button) findViewById(R.id.bt_progress_setting);
		
		bt_select_all.setOnClickListener(this);
		bt_select_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
		
		/*lv_process_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//滚动过程中调用方法
				//AbsListView中view就是listView对象
				//firstVisibleItem第一个可见条目索引值
				//visibleItemCount当前一个屏幕的可见条目数
				//总共条目总数
				if(mCustomerList!=null && mSystemList!=null){
					if(firstVisibleItem>=mCustomerList.size()+1){
						//滚动到了系统条目
						tv_des.setText("系统进程("+mSystemList.size()+")");
					}else{
						//滚动到了用户应用条目
						tv_des.setText("用户进程("+mCustomerList.size()+")");
					}
				}
				
			}
		});*/
		
		/*lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			//view选中条目指向的view对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(position == 0 || position == mCustomerList.size()+1){
					return;
				}else{
					if(position<mCustomerList.size()+1){
						mProcessInfo = mCustomerList.get(position-1);
					}else{
						//返回系统应用对应条目的对象
						mProcessInfo = mSystemList.get(position - mCustomerList.size()-2);
					}
					if(mProcessInfo!=null){
						if(!mProcessInfo.packageName.equals(getPackageName())){
							//选中条目指向的对象和本应用的包名不一致,才需要去状态取反和设置单选框状态
							//状态取反
							mProcessInfo.isCheck = !mProcessInfo.isCheck;
							//checkbox显示状态切换
							//通过选中条目的view对象,findViewById找到此条目指向的cb_box,然后切换其状态
							CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
							cb_box.setChecked(mProcessInfo.isCheck);
						}
					}
				}
			}
		});*/
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_progress_selectall:
			selectAll();
			break;
		case R.id.bt_progress_reverseselect:
			selectReverse();
			break;
		case R.id.bt_progress_clear:
			clear();
			break;
		case R.id.bt_progress_setting:
			setting();
			break;
		}
	}

	/*private void setting() {
		Intent intent = new Intent(this, ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}*/
	
	private void setting() {
		Intent intent=new Intent(this,ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		boolean showSystem=SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM_PROCESS,false);
		mHandler.obtainMessage(SETTING_FINISHED).sendToTarget();
	}
	private void clear() {
		//记录要杀死的对象
		List<RunningProgressInfoBean> killProcessList=new ArrayList<RunningProgressInfoBean>();
		for(RunningProgressInfoBean bean:mCustomerList){
			if(bean.getPackageName().equals(getPackageName())){
				continue;
			}
			killProcessList.add(bean);
		}
		for(RunningProgressInfoBean bean:mSystemList){
			killProcessList.add(bean);
		}
		//从容器中一处要杀死的对象
		long releaseSpace=0;
		for(RunningProgressInfoBean bean:killProcessList){
			if (mCustomerList.contains(bean)) {
				mCustomerList.remove(bean);
			}else {
				mSystemList.remove(bean);
			}
			releaseSpace+=bean.getMemSize();
			ProcessUtil.killProdess(getApplicationContext(), bean);
			
		}
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		//刷新界面数据
		//当前进程数量
		tv_process_count.setText("进程总数："+(mProcessCount-killProcessList.size()));
		//占用内存刷新
		mAvailSpace+=releaseSpace;
		strAvailSpace=Formatter.formatFileSize(getApplicationContext(), mAvailSpace);
		tv_memory_info.setText("剩余/总共："+strAvailSpace+"/"+mStrTotalSpace);
		//弹出吐司
		String strReleaseSpace=Formatter.formatFileSize(getApplicationContext(), releaseSpace);
		ToastUtil.show(getApplicationContext(), 
				String.format("清理%d个进程，释放%s内存",killProcessList.size(),strReleaseSpace));
		
	}

	private void selectReverse() {
		for(RunningProgressInfoBean processBean:mCustomerList){
			if(processBean.packageName.equals(getPackageName())){
				continue;
			}
			processBean.setCheck(!processBean.isCheck);
		}
		for(RunningProgressInfoBean processBean:mSystemList){
			processBean.setCheck(!processBean.isCheck);
		}
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}

	private void selectAll() {
		for(RunningProgressInfoBean processBean:mCustomerList){
			if(processBean.packageName.equals(getPackageName())){
				continue;
			}
			processBean.setCheck(true);
		}
		for(RunningProgressInfoBean processBean:mSystemList){
			processBean.setCheck(true);
		}
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}*/

	/**
	 * 清理选中进程
	 */
/*	private void clearAll() {
		//1,获取选中进程
		//2,创建一个记录需要杀死的进程的集合
		List<RunningProgressInfoBean> killProcessList = new ArrayList<RunningProgressInfoBean>();
		for(RunningProgressInfoBean processInfo:mCustomerList){
			if(processInfo.getPackageName().equals(getPackageName())){
				continue;
			}
			if(processInfo.isCheck){
				//不能在集合循环过程中去移除集合中的对象
//				mCustomerList.remove(processInfo);
				//3,记录需要杀死的用户进程
				killProcessList.add(processInfo);
			}
		}
		
		for(RunningProgressInfoBean processInfo:mSystemList){
			if(processInfo.isCheck){
				//4,记录需要杀死的系统进程
				killProcessList.add(processInfo);
			}
		}
		//5,循环遍历killProcessList,然后去移除mCustomerList和mSystemList中的对象
		long totalReleaseSpace = 0;
		for (RunningProgressInfoBean processInfo : killProcessList) {
			//6,判断当前进程在那个集合中,从所在集合中移除
			if(mCustomerList.contains(processInfo)){
				mCustomerList.remove(processInfo);
			}
			
			if(mSystemList.contains(processInfo)){
				mSystemList.remove(processInfo);
			}
			//7,杀死记录在killProcessList中的进程
			//ProgressUtil.killProcess(this,processInfo);
			
			//记录释放空间的总大小
			totalReleaseSpace += processInfo.memSize;
		}
		//8,在集合改变后,需要通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		//9,进程总数的更新
		mProcessCount -= killProcessList.size();
		//10,更新可用剩余空间(释放空间+原有剩余空间 == 当前剩余空间)
		mAvailSpace += totalReleaseSpace;
		//11,根据进程总数和剩余空间大小
		tv_process_count.setText("进程总数:"+mProcessCount);
		tv_memory_info.setText("剩余/总共"+Formatter.formatFileSize(this, mAvailSpace)+"/"+mStrTotalSpace);
		//12,通过吐司告知用户,释放了多少空间,杀死了几个进程,
		String totalRelease = Formatter.formatFileSize(this, totalReleaseSpace);
//		ToastUtil.show(getApplicationContext(), "杀死了"+killProcessList.size()+"个进程,释放了"+totalRelease+"空间");
		
//		jni  java--c   c---java
		//占位符指定数据%d代表整数占位符,%s代表字符串占位符
		ToastUtil.show(getApplicationContext(), 
				String.format("杀死了%d进程,释放了%s空间", killProcessList.size(),totalRelease));
	}

	private void selectReverse() {
		//1,将所有的集合中的对象上isCheck字段取反
		for(RunningProgressInfoBean processInfo:mCustomerList){
			if(processInfo.getPackageName().equals(getPackageName())){
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}
		for(RunningProgressInfoBean processInfo:mSystemList){
			processInfo.isCheck = !processInfo.isCheck;
		}
		//2,通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}

	private void selectAll() {
		//1,将所有的集合中的对象上isCheck字段设置为true,代表全选,排除当前应用
		for(RunningProgressInfoBean processInfo:mCustomerList){
			if(processInfo.getPackageName().equals(getPackageName())){
				continue;
			}
			processInfo.isCheck = true;
		}
		for(RunningProgressInfoBean processInfo:mSystemList){
			processInfo.isCheck = true;
		}
		//2,通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}*/
}
