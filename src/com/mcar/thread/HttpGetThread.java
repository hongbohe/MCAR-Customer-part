package com.mcar.thread;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mcar.net.MyGet;
import com.mcar.model.Model;

/**
 * 网络Get请求的线�?
 * */
public class HttpGetThread implements Runnable {

	private Handler hand;
	private String url;
	private MyGet myGet = new MyGet();

	public HttpGetThread(Handler hand, String endParamerse) {
		this.hand = hand;
		// 拼接访问服务器完整的地址
		url = Model.HTTPURL + endParamerse;
	}

	@Override
	public void run() {
		// 获取我们回调主ui的message
		Message msg = hand.obtainMessage();
		Log.e("MCAR", url);
		try {
			String result = myGet.doGet(url);
			msg.what = 200;
			msg.obj = result;
		} catch (ClientProtocolException e) {
			msg.what = 404;
		} catch (IOException e) {
			msg.what = 100;
		}
		// 给主ui发�?�消息传递数�?
		hand.sendMessage(msg);
	}
}
