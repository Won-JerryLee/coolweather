package com.jer.coolweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class HttpUtil {

	public static void sendHttpRequest(final String address,
			final HttpCallBackListener httpCallBackListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection httpURLConnection = null;
				try {
					URL url = new URL(address);
					httpURLConnection = (HttpURLConnection) url
							.openConnection();
					httpURLConnection.setRequestMethod("GET");
					httpURLConnection.setReadTimeout(5000);
					httpURLConnection.setConnectTimeout(5000);
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpURLConnection
									.getInputStream()));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = bufferedReader.readLine()) != null){
						response.append(line);
					}
					if(httpCallBackListener != null){
						/**
						 * �ص�onFinish()����
						 */
						httpCallBackListener.onFinish(response.toString());
					}
				} catch (IOException e) {
					if(httpCallBackListener != null){
						/**
						 * �ص�onError()����
						 */
						httpCallBackListener.onError(e);
					}
				}finally{
					if(httpURLConnection != null){
						/**
						 * �ͷ�������Դ
						 */
						httpURLConnection.disconnect();
					}
				}

			}
		}).start();
	}

}
