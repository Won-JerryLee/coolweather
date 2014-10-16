package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	/**
	 * ��ʾ��������
	 */
	private TextView cityNameText;
	/**
	 * ��ʾ����ʱ��
	 */
	private TextView publishTimeText;
	/**
	 * ����������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ��ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ��ʵ����2
	 */
	private TextView temp2Text;
	/**
	 * ��ʵ��ǰ����
	 */
	private TextView currentDateText;
	/**
	 * �л����а�ť
	 */
	private Button switchCity;
	/**
	 * ����������ť
	 */
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		init();
		String county_code = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(county_code)){
			getWeatherCode(county_code);
		}else{
			showWeatherInfo();
		}
	}
	
	/**
	 * ͨ���ؼ���������������
	 */
	private void getWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" +countyCode+ ".xml";
		qerryFromService(address, "county");
	}
	
	/**
	 * ͨ�����������ȡ������Ϣ
	 */
	private void getWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/" +weatherCode+ ".html";
		qerryFromService(address, "weatherCode");
	}
	
	/**
	 * �ӷ�������������������
	 */
	private void qerryFromService(String address, final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				if("county".equals(type)){
					if(response != null){
						String[] arr = response.split("\\|");
						String weatherCode = arr[1];
						getWeatherInfo(weatherCode);
					}
				}else if("weatherCode".equals(type)){
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeatherInfo();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishTimeText.setText("����ʧ��");
						weatherInfoLayout.setVisibility(View.INVISIBLE);
					}
				});
			}
		});
	}
	
	/**
	 * ��ʾ������Ϣ
	 */
	private void showWeatherInfo(){
		SharedPreferences sPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(sPreferences.getString("city_name", ""));
		publishTimeText.setText("����"+sPreferences.getString("publish_time", "")+"����");
		currentDateText.setText(sPreferences.getString("current_date", ""));
		weatherDespText.setText(sPreferences.getString("weather_desp", ""));
		temp1Text.setText(sPreferences.getString("temp1", ""));
		temp2Text.setText(sPreferences.getString("temp2", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}

	private void init() {
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishTimeText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_City);
		switchCity.setOnClickListener(this);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_City:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("isFromWeatherActivity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishTimeText.setText("������...");
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = sharedPreferences.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				getWeatherInfo(weatherCode);
			}
			break;
		}
	}

}
