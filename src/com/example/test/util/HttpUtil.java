package com.example.test.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;//HttpURLConnection类主要用来服务器端发送Http请求
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");//设置请求方式
					connection.setConnectTimeout(8000);// 8秒 连接主机的超时时间（单位：毫秒）
					connection.setReadTimeout(8000);// 8秒 从主机读取数据的超时时间（单位：毫秒）
					InputStream in = connection.getInputStream();//得到一个输入流
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						// 回调onFinish()方法
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}