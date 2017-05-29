package com.wusai.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.wusai.mobilesafe.db.AppLockOpenHelper;

public class AppLockDao {
	private  AppLockOpenHelper appLockOpenHelper;
	private Context context;

	public AppLockDao(Context context) {
		this.context=context;
		appLockOpenHelper = new AppLockOpenHelper(context);
	}
	public static AppLockDao appLockDao=null;
	public static AppLockDao getInstance(Context context){
		if(appLockDao==null){
			appLockDao=new AppLockDao(context);
		}
		return appLockDao;
	}
	public void insert(String packageName){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("packagename", packageName);
		db.insert("applock", null, values);
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
	}
	public void delete(String packageName){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		db.delete("applock", "packagename=?",new String[]{packageName});
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
	}
	public List<String> queryAll(){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		Cursor cursor=db.query("applock", null, null, null, null, null, null);
		List<String> packageNameList=new ArrayList<String>();
		while (cursor.moveToNext()) {
			packageNameList.add(cursor.getString(1));
		}
		cursor.close();
		db.close();
		return packageNameList;
	}
}
