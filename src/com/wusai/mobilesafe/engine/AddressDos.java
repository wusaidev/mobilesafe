package com.wusai.mobilesafe.engine;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDos {
	//1 指定访问数据库的路径
	public static String path="data/data/com.itheima.mobilesafe74/files/address.db";
	//2 开启数据库链接，进行访问
	/**传递一个电话号码，开启数据库链接，精心访问，返回一个归属地
	 * @param phone
	 * 查询电话号码
	 */
	private static String tag="AddressDos";
	private static String outkey;
	private static String location;
	public static String getAddress(String phone){
		location="未知号码";
		String regularExpression="^1[3-8]\\d{9}";
		//2.1开启数据库链接 （只读形式打开）
		SQLiteDatabase db=SQLiteDatabase.openDatabase
						(path, null, SQLiteDatabase.OPEN_READONLY);
		if(phone.matches(regularExpression)){
			phone=phone.substring(0,7);
			//3. 数据库查询
			Cursor cursor=db.query("data1", new String[]{"outkey"}, "id=?",
					new String[]{phone}, null, null, null);
			if(cursor.moveToNext()){
				outkey = cursor.getString(0);
				Log.i(tag, "outkey="+outkey);
			}
			Cursor cursorAddress=db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey},null,null,null);
			if(cursorAddress.moveToNext()){
				location = cursorAddress.getString(0);
				Log.i(tag, "电话归属地"+location);
			}
		}
		return location;
	}
	
}
