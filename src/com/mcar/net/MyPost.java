package com.mcar.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.mcar.model.Model;

/**
 * 我的post请求方式工具�?
 * */

public class MyPost {

	public String doPost(String url, String img, String value) {
		String result = null;
		HttpResponse httpResponse = null;
		HttpPost post = new HttpPost(Model.HTTPURL + url);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				30000); // 超时设置
		client.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 10000);// 连接超时
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// Json字符串拼�?
		nameValuePairs.add(new BasicNameValuePair("value", value));
		nameValuePairs.add(new BasicNameValuePair("img", img));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			httpResponse = client.execute(post);
			Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils
						.toString(httpResponse.getEntity(), "utf-8");
				Log.e("HTTP", "result:" + result);
			} else {
				result = null;
			}
		} catch (UnsupportedEncodingException e) {
			result = null;
		} catch (ClientProtocolException e) {
			result = null;
		} catch (IOException e) {
			result = null;
		}
		return result;
	}

	public String doPost(String url, String value) {
		String result = null;
		HttpResponse httpResponse = null;
		HttpPost post = new HttpPost(Model.HTTPURL + url);

		InputStream inputStream = null;
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				30000); // 超时设置
		client.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 10000);// 连接超时
		// ArrayList<NameValuePair> nameValuePairs = new
		// ArrayList<NameValuePair>();
		// Json字符串拼�?
		// nameValuePairs.add(new BasicNameValuePair("value", value));
		try {
			StringEntity se = new StringEntity(value);

			post.setEntity(se);

			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");

			// post.setEntity(new UrlEncodedFormEntity(nameValuePairs,
			// "utf-8"));
			httpResponse = client.execute(post);
			Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
			// if (httpResponse.getStatusLine().getStatusCode() == 200) {
			// result = EntityUtils
			// .toString(httpResponse.getEntity(), "utf-8");
			// Log.e("HTTP", "result:" + result);
			// } else {
			// result = null;
			// }
			inputStream = httpResponse.getEntity().getContent();
			// 10. convert inputstream to string
			if (inputStream != null){
				result = convertInputStreamToString(inputStream);
			}
			else{
				result = "Did not work!";
			}
		} catch (UnsupportedEncodingException e) {
			result = null;
		} catch (ClientProtocolException e) {
			result = null;
		} catch (IOException e) {
			result = null;
		}
		return result;
	}

	// This method is used to change the received json object to json string.
	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}
}
