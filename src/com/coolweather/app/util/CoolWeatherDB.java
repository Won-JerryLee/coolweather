package com.coolweather.app.util;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherOpenHelper;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	
	/**
	 * 定义数据库名字
	 */
	private static final String DB_NAME = "cool_weather";
	
	/**
	 * 定义数据库版本
	 */
	private static final int DB_VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	
	/**
	 * 构造函数私有化实现单例模式
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
		db = coolWeatherOpenHelper.getReadableDatabase();
	}
	
	/**
	 * 返回CoolWeatherDB实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	/**
	 * 保存province数据
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/**
	 * 获取province数据
	 */
	public List<Province> getProvince(){
		List<Province> list = new ArrayList<Province>();
		Province province;
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * 保存city数据
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * 获取city数据
	 */
	public List<City> getCity(int provinceId){
		List<City> list = new ArrayList<City>();
		City city;
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				city = new City();
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * 保存county数据
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getcityId());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * 获取county数据
	 */
	public List<County> getCounty(int cityId){
		List<County> list = new ArrayList<County>();
		County county;
		Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do {
				county = new County();
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setcityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
}
