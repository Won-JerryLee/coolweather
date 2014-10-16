package com.coolweather.app.receiver;

import java.lang.Character.UnicodeBlock;

import com.coolweather.app.service.AutoUpdateBroadcast;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.text.TextUtils;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/**
		 * �����̸߳�������
		 */
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		/**
		 * ���ö�ʱ����
		 */
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		int anHour = 8 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent startBroadcast = new Intent(this, AutoUpdateBroadcast.class);
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, startBroadcast, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, operation);
		
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * ��������
	 */
	protected void updateWeather() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = sharedPreferences.getString("weather_code", "");
		if(!TextUtils.isEmpty(weatherCode)){
			String address = "http://www.weather.com.cn/data/citiinfo/" + weatherCode + ".html";
			HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
				
				@Override
				public void onFinish(String response) {
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
				}
				
				@Override
				public void onError(Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

}
