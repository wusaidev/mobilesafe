package com.wusai.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lidroid.xutils.db.converter.StringColumnConverter;
import com.wusai.mobilesafe.db.BlackNumberOpenHelper;
import com.wusai.mobilesafe.db.domain.BlackNumberInfo;

/**
 * @author happy
 *
 */
public class BlackNumberDao {
	private BlackNumberOpenHelper blackNumberOpenHelper;

	private BlackNumberDao(Context context){
		//创建数据库以及其表结构
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	//声明一个当前类的对象
	public static BlackNumberDao blackNumberDao=null;
	//提供一个静态方法，如果当前类的对象为空，创建一个新的
	public static BlackNumberDao getInstance(Context context){
		if(blackNumberDao==null){
			blackNumberDao=new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	/**增加一个条目
	 * @param phone 拦截的电话号码
	 * @param mode  拦截类型（1短信    2 电话   3 短信家电话）
	 */
	public void insert(String phone,String mode){ 
		SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("phone",phone);
		values.put("mode",mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	public void delete(String phone){
		SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
		db.delete("blacknumber", "phone=?",new String[]{phone});
		db.close();
	}
	public void update(String phone,String mode){
		SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("mode",mode);
		db.update("blacknumber", values,"phone=?",new String[]{phone});
		db.close();
	}
	public List<BlackNumberInfo >findAll(){
		SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
		Cursor cursor=db.query("blacknumber",new String[]{"phone","mode"}, null, null,null,null,"_id desc");
		List<BlackNumberInfo> blackNumberList=new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
			blackNumberInfo.phone=cursor.getString(0);
			blackNumberInfo.mode=cursor.getString(1);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberList;
	}
	public List<BlackNumberInfo >find(int index){
		SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20 ;",new String[]{index+""});
		List<BlackNumberInfo> blackNumberList=new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
			blackNumberInfo.phone=cursor.getString(0);
			blackNumberInfo.mode=cursor.getString(1);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberList;
	}

	public int getCount() {
		SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
		int count=0;
		Cursor cursor=db.rawQuery("select count(*) from blacknumber;", null);
		if(cursor.moveToNext()){
			count=cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	public int getMode(String originatingAddress) {
		SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor=db.query("blacknumber", new String[]{"mode"},"phone=?",new String[]{originatingAddress}, null,null,null);
		int mode=0;
		if(cursor.moveToNext()){
			mode=Integer.parseInt(cursor.getString(0));
		}
		return mode;
	}
}
	