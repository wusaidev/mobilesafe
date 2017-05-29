package com.wusai.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VirusDao {
	String path="data/data/com.itheima.mobilesafe74/files/antivirus.db";
	//SQLiteDatabase db=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	public List<String> find(){
		SQLiteDatabase db=SQLiteDatabase.openDatabase(path,null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor=db.query("datable",new String[]{"md5"}, null,null,null,null,null);
		List<String> virusMD5List=new ArrayList<String>();
		while (cursor.moveToNext()) {
			virusMD5List.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return virusMD5List;
	}

}
