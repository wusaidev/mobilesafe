package com.wusai.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	public BlackNumberOpenHelper(Context context) {
		super(context, "blacknumber.db",null,1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blacknumber" +
				"(_id integer primary key autoincrement,phone vachar," +
				"mode varchar);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
