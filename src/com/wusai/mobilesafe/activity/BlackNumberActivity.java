package com.wusai.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.db.dao.BlackNumberDao;
import com.wusai.mobilesafe.db.domain.BlackNumberInfo;
import com.wusai.mobilesafe.utils.ToastUtil;



public class BlackNumberActivity extends Activity {
	private Button bt_add_black;
	private ListView lv_black_number;
	private List<BlackNumberInfo> blackNumberList;
	private MyAdapter myAdapter;
	private int mode=1;
	private boolean mIsLoad=false;
	private int mCount;
	private BlackNumberDao mDao;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.e("BlackNumberActivity","收到通知，获取List数据");
			if(myAdapter==null){
				myAdapter = new MyAdapter();
				lv_black_number.setAdapter(myAdapter);
			}else {
				myAdapter.notifyDataSetChanged();
			}
			//本次数据加载完成，mIsLoad 重置为false，用于判断下次加载中第一次加载
			mIsLoad=false;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);
		initUI();
		initData();
	}
	private void initUI() {
		lv_black_number = (ListView) findViewById(R.id.lv_black_number);
		bt_add_black = (Button) findViewById(R.id.bt_add_black);
		bt_add_black.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		lv_black_number.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(blackNumberList!=null){
					//1、划不动      2、显示到最后一个条目（最后一个条目的索引值》=数据适配器集合中集合的条目个数-1）    3、第一次加载
					if(scrollState==OnScrollListener.SCROLL_STATE_IDLE&&
							lv_black_number.getLastVisiblePosition()>=blackNumberList.size()-1&&
							!mIsLoad){
						/*mIsLoad 放置重复加载的变量，
						 * 如果当前正在加载mIsLoad 就会为true，本次加载完毕后，再将mIsLoad改为false
						 * 如果下一次加载需要去做执行的时候，回判断上诉mIsLoad变量是否为false，如果true，
						 * 就需要得带上一次加载完成，将其值至为false后再去加载*/
						mIsLoad=true;
						if(mCount>blackNumberList.size()){
							new Thread(){
								public void run(){
									mDao=BlackNumberDao.getInstance(getApplicationContext());
									//查询部分数据
									List<BlackNumberInfo> moreData=mDao.find(blackNumberList.size());
									blackNumberList.addAll(moreData);
									//通知刷新
									mHandler.sendEmptyMessage(0);
								}
							}.start();
						}else {
							ToastUtil.show(getApplicationContext(), "没有更多数据");
						}
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}
	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.e("BlackNumberActivity","线程已开启");
				blackNumberList = new ArrayList<BlackNumberInfo>();
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				blackNumberList=mDao.find(0);
				//获取数据库中数据的数量
				mCount=mDao.getCount();
				/*String phone=blackNumberList.get(0).phone;
				Log.e("BlackNumberActivity","线程中查询到的电话："+phone);*/
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}
	private void showDialog() {
		Builder builder=new AlertDialog.Builder(this);
		final AlertDialog dialog=builder.create();
		View view=View.inflate(getApplicationContext(), R.layout.dialog_black_number, null);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		final EditText et_black_number=(EditText) view.findViewById(R.id.et_black_number);
		RadioGroup rg_group=(RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_black_confirm= (Button) view.findViewById(R.id.bt_black_confirm);
		Button bt_black_cancel= (Button) view.findViewById(R.id.bt_black_cancel);
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.e("BlackNumberActivity","radioGroup已点击");
				switch (checkedId) {
				//拦截短信
				case R.id.rb_sms:
					mode=1;
					break;
				//拦截电话
				case R.id.rb_phone:
					mode=2;
					break;
				//拦截短信和电话
				case R.id.rb_sms_phone:
					mode=3;
					break;
				default:
					break;
				}
			}
		});
		bt_black_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BlackNumberDao mDao=BlackNumberDao.getInstance(getApplicationContext());
				String phone=et_black_number.getText().toString();
				if(phone!=null){
					mDao.insert(phone, mode+"");
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.phone=phone;
					blackNumberInfo.mode=mode+"";
					blackNumberList.add(0, blackNumberInfo);
					if(myAdapter!=null){
						myAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}else{
					ToastUtil.show(getApplicationContext(), "请输入要拦截的电话");
				}
				
			}
		});
		bt_black_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
	}
	class MyAdapter extends BaseAdapter{
		private TextView tv_black_phone;
		private TextView tv_black_mode;
		private ImageView tv_black_delete;
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			/*View  view=null;
			if(convertView==null){
				view=View.inflate(getApplicationContext(),R.layout.listview_blackname_item, null);
			}else {
				view=convertView;
			}*/
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=View.inflate(getApplicationContext(), R.layout.listview_blackname_item, null);
				viewHolder.tv_black_phone = (TextView) convertView.findViewById(R.id.tv_black_phone);
				viewHolder.tv_black_mode = (TextView) convertView.findViewById(R.id.tv_black_mode);
				viewHolder.tv_black_delete = (ImageView) convertView.findViewById(R.id.tv_black_delete);
				convertView.setTag(viewHolder);
			}else{
				//在服用convertView的条目展示的时候，找到之前设置过的tag,ViewHolder=getTag.viewholder（），
				//中就包含已经找过的控件
				viewHolder=(ViewHolder) convertView.getTag();
			}
			viewHolder.tv_black_phone.setText(blackNumberList.get(position).phone);
			int mode=Integer.parseInt(blackNumberList.get(position).mode);
			Log.e("BlackNumberActivity","类型："+mode);
			viewHolder.tv_black_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BlackNumberDao mDao=BlackNumberDao.getInstance(getApplicationContext());
					mDao.delete(blackNumberList.get(position).phone);
					blackNumberList.remove(position);
					mHandler.sendEmptyMessage(0);
				}
			});
			switch (mode) {
			case 1:
				viewHolder.tv_black_mode.setText("拦截短信");
				break;
			case 2:
				viewHolder.tv_black_mode.setText("拦截电话");
				break;
			case 3:
				viewHolder.tv_black_mode.setText("拦截所有");
				break;
			default:
				break;
			}
			return convertView;
		}
		@Override
		public Object getItem(int position) {
			return blackNumberList.get(position);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return blackNumberList.size();
		}
	}
	static class ViewHolder{
		TextView tv_black_phone;
		TextView tv_black_mode;
		ImageView tv_black_delete;
	}
}
