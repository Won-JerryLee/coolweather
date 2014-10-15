package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.CoolWeatherDB;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/**
	 * ���б�
	 */
	private List<City> cityList;
	/**
	 * ���б�
	 */
	private List<County> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province slectedProvince;
	/**
	 * ѡ�е���
	 */
	private City selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		titleText = (TextView) findViewById(R.id.title_tv);
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					slectedProvince = provinceList.get(position);
					querryCity();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					querryCounty();
				}
			}
		});
		querryProvince();
	}
	
	/**
	 * ��ѯѡ���е��أ��������ݿ��ѯ�������ٵ���������ѯ
	 */
	protected void querryCounty() {
		countyList = coolWeatherDB.getCounty(selectedCity.getId());
		if(countyList != null && countyList.size() > 0){
			dataList.clear();
			for(County c:countyList){
				dataList.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			qerryFromService(selectedCity.getCityCode(),"county");
		}
	}

	/**
	 * ��ѯѡ��ʡ�е��У��������ݿ��ѯ�������ٵ���������ѯ
	 */
	protected void querryCity() {
		cityList = coolWeatherDB.getCity(slectedProvince.getId());
		if(cityList != null && cityList.size() > 0){
			dataList.clear();
			for(City c:cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(slectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			qerryFromService(slectedProvince.getProvinceCode(),"city");
		}
	}

	/**
	 * ��ѯȫ����ʡ���������ݿ��ѯ�������ٵ���������ѯ
	 */
	private void querryProvince() {
		provinceList = coolWeatherDB.getProvince();
		if (provinceList != null && provinceList.size() > 0) {
			dataList.clear();
			for (int i = 0; i < provinceList.size(); i++) {
				dataList.add(provinceList.get(i).getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			qerryFromService(null,"province");
		}
	}
	
	/**
	 * ���ݴ����code��type�ӷ������ϲ�ѯʡ���ص�����
	 */
	private void qerryFromService(String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handleCityResponse(coolWeatherDB, response, slectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								querryProvince();
							}else if("city".equals(type)){
								querryCity();
							}else if("county".equals(type)){
								querryCounty();
							}
						}
					});
				}
			}
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * �������ȶԻ���
	 */
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(ChooseAreaActivity.this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����back���������ݵ�ǰ�ļ������жϣ�Ӧ���Ƿ������б�ʡ�б����ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			querryCity();
		}else if(currentLevel == LEVEL_CITY){
			querryProvince();
		}else{
			finish();
		}
	}
}
