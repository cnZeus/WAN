package com.example.test.db;

import java.util.ArrayList;
import java.util.List;

import com.example.test.model.City;
import com.example.test.model.County;
import com.example.test.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {

	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";

	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;

	private SQLiteDatabase db;//代表一个数据库对象，提供了操作数据库的一些方法。

	/**
	 * 构造方法私有化
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();//getWritableDatabase()会调用并返回一个可以读写数据库的对象，在第一次调用时会
		                                    //调用onCreate的方法，当数据库存在时会调用onOpen方法，结束时调用onClose方法。
	}

	/**
	 * 获取CoolWeatherDB的实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
	 //synchronized可以保证在同一时刻，只有一个线程可以执行某个方法或某个代码块，同时synchronized可以保
	 //证一个线程的变化可见（可见性），即可以代替volatile。
	 
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 将Province实例存储到数据库
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();//创建一个ContentValues对象存储键值对
			values.put("province_name", province.getProvinceName());//存储省份名
			values.put("province_code", province.getProvinceCode());//存储省份编码
			db.insert("Province", null, values);//插入数据,(表名称,空列的默认值,ContentValues类型的一个封装了列名称和列值的Map)
		}
	}

	/**
	 * 从数据库读取全国所有的省份信息
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();//创建一个省份集合
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);//查询数据库中的Province表数据
		if (cursor.moveToFirst()) {//cursor.moveToFirst（）指向查询结果的第一个位置
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));//Province类中的方法
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());//moveToNext()移动光标到下一行，返回boolean值，
			                             //当为true时表明光标移动成功，为false时说明移动失败，即没有移动成功。
		}
		return list;
	}

	/**
	 * 将City实例存储到数据库
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());//存储城市名
			values.put("city_code", city.getCityCode());////存储城市编号
			values.put("province_id", city.getProvinceId());////存储省份ID
			db.insert("City", null, values);//插入数据
		}
	}

	/**
	 * 从数据库读取某省下所有的城市信息
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 将Country实例存储到数据库
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());//存储县名
			values.put("county_code", county.getCountyCode());//存储县编号
			values.put("city_id", county.getCityId());//存储城市ID
			db.insert("County", null, values);//插入数据
		}
	}

	/**
	 * 从数据库读取某城市下所有的县信息
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}

}
