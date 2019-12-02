package com.example.test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	/**
	 *  Province表建表语句
	 */
	public static final String CREATE_PROVINCE = "create table Province ("//创建Province表
				+ "id integer primary key autoincrement, " //id  起的字段名字。
														   //integer  数据类型。
														   //primary key 定义这个字段为主键。
														   //auto_increment 定义这个字段为自动增长，即如果INSERT时不赋值，
														   //则自动加1
				+ "province_name text, "//province_name 字段名，text 数据类型
				+ "province_code text)";
	/**
	 *  City表建表语句
	 */
	public static final String CREATE_CITY = "create table City ("//创建City表
				+ "id integer primary key autoincrement, " 
				+ "city_name text, " 
				+ "city_code text, " 
				+ "province_id integer)";
	/**
	 *  County表建表语句
	 */
	public static final String CREATE_COUNTY = "create table County ("//创建Country表
				+ "id integer primary key autoincrement, " 
				+ "county_name text, " 
				+ "county_code text, " 
				+ "city_id integer)";

	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);  // 创建Province表
		db.execSQL(CREATE_CITY);  // 创建City表
		db.execSQL(CREATE_COUNTY);  // 创建Country表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
