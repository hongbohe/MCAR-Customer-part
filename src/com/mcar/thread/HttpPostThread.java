package com.mcar.thread;

import android.os.Handler;
import android.os.Message;

import com.mcar.net.MyPost;

/**
 * 网络Post请求的线�?
 * */

public class HttpPostThread implements Runnable {

	private Handler hand;
	private String url;
	private String value;
	private String img = "";
	private MyPost myPost = new MyPost();

	public HttpPostThread(Handler hand, String endParamerse, String value,
			String img) {
		this.hand = hand;
		// 拼接访问服务器完整的地址
		url = endParamerse;
		this.value = value;
		this.img = img;
	}

	public HttpPostThread(Handler hand, String endParamerse, String value) {
		this.hand = hand;
		// 拼接访问服务器完整的地址
		url = endParamerse;
		this.value = value;
	}

	@Override
	public void run() {
		// 获取我们回调主ui的message
		Message msg = hand.obtainMessage();
		String result = null;
		if (img.equalsIgnoreCase("")) {
			result = myPost.doPost(url, value);
		} else {
			result = myPost.doPost(url, img, value);
		}
		msg.what = 200;
		msg.obj = result;
		// 给主ui发�?�消息传递数�?
		hand.sendMessage(msg);

	}

}
