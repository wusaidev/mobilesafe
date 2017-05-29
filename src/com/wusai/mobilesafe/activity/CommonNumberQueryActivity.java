package com.wusai.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.wusai.mobilesafe.db.dao.CommonNumberDao;
import com.wusai.mobilesafe.db.dao.CommonNumberDao.Child;
import com.wusai.mobilesafe.db.dao.CommonNumberDao.Group;



public class CommonNumberQueryActivity extends Activity {
	private List<Group> groupList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comnumber_query);
		initData();
		initUI();
	}

	private void initUI() {
		ExpandableListView elv_common_number=(ExpandableListView) 
				findViewById(R.id.elv_common_number);
		MyAdapter myAdapter=new MyAdapter();
		elv_common_number.setAdapter(myAdapter);
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				startCall(groupList.get(groupPosition).childList.get(childPosition).number);
				return false;
			}

		});
	}
	private void startCall(String number) {
		Intent intent=new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	private void initData() {
		CommonNumberDao commonDao=new CommonNumberDao();
		groupList = commonDao.queryClass();
		
	}
	class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return groupList.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			Log.e("commNumber", getChild(groupPosition, childPosition).name);
			View view=View.inflate(getApplicationContext(), R.layout.view_comnumber_elv_child,null);
			TextView child_name=(TextView) view.findViewById(R.id.tv_common_elv_child_name);
			TextView child_number=(TextView) view.findViewById(R.id.tv_common_elv_child_number);
			child_name.setText(getChild(groupPosition, childPosition).name);
			child_number.setText(getChild(groupPosition, childPosition).number);
			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groupList.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv_group=new TextView(getApplicationContext());
			tv_group.setText("         "+getGroup(groupPosition).name);
			tv_group.setTextColor(Color.RED);
			tv_group.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
			return tv_group;
		}
//是否将group的id设置为adapter的id，   此项不用设置
		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
}
