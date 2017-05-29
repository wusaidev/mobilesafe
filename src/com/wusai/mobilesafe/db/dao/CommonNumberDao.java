package com.wusai.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommonNumberDao {
	public List<Group> queryClass(){
		List<Group> groupList=new ArrayList<CommonNumberDao.Group>();
		String path="data/data/com.itheima.mobilesafe74/files/commonnum.db";
		SQLiteDatabase db=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cusor=db.query("classlist", null, null, null,null,null,null);
		while (cusor.moveToNext()) {
			Group group=new Group();
			group.name=cusor.getString(0);
			group.idx=cusor.getString(1);
			group.childList=queryChild(group.idx);
			groupList.add(group);
		}
		cusor.close();
		db.close();
		return groupList;
	}
	public List<Child> queryChild(String idx){
		List<Child> childList=new ArrayList<CommonNumberDao.Child>();
		String path="data/data/com.itheima.mobilesafe74/files/commonnum.db";
		SQLiteDatabase db=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String tableName="table"+idx;
		Log.e("CommentNumberActivity", tableName);
		Cursor cusor=db.query(tableName,null, null, null,null,null,null);
		while (cusor.moveToNext()) {
			Child child=new Child();
			child.id=cusor.getString(0);
			child.number=cusor.getString(1);
			child.name=cusor.getString(2);
			childList.add(child);
		}
		cusor.close();
		db.close();
		return childList;
	}
	public class Group{
		public String name;
		public String idx;
		public List<Child> childList;
	}
	public class Child{
		public String id;
		public String name;
		public String number;
	}
}
