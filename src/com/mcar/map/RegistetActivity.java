package com.mcar.map;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;


import com.mcar.map.R;
import com.appkefu.lib.interfaces.KFIMInterfaces;
import com.mcar.model.Model;
import com.mcar.net.ThreadPoolUtils;
import com.mcar.thread.HttpPostThread;


import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegistetActivity extends Activity {
	private ImageView mClose;
	private EditText mName, mPassword;
	private RelativeLayout mNext;
	private TextView mStartlogin;
	private String url = null;
	private String value = null;
	private String username = null;
	private String password = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
//		mClose = (ImageView) findViewById(R.id.registClose);
		mName = (EditText) findViewById(R.id.Redit_name);
		mPassword = (EditText) findViewById(R.id.Redit_password);
		mNext = (RelativeLayout) findViewById(R.id.signup);
		mStartlogin = (TextView) findViewById(R.id.startlogin);
		MyOnClickListener my = new MyOnClickListener();
//		mClose.setOnClickListener(my);
		mNext.setOnClickListener(my);
	//	mStartlogin.setOnClickListener(my);

	}
	
	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int mId = v.getId();
			switch (mId) {
//			case R.id.registClose:
//				finish();
//				break;
			case R.id.signup:
				reginstet();
				break;
			case R.id.startlogin:

				break;
			}

		}
	}

	private void reginstet() {
	//	url = Model.REGISTET;
	//	value = "{\"uname\":\"" + username + "\",\"upassword\":\"" + password
	//			+ "\"}";
	//	Log.e("KidBuddies", value);
	//	ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
		username = mName.getText().toString();
		password = mPassword.getText().toString();
		
		userRegister(username, password);
		
		Intent intentmap = new Intent(RegistetActivity.this,
				MainActivity.class);
		startActivity(intentmap);
	}

	
	private void userRegister(String username, String password) {
		// latitude longitude function(address);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmd", "register");
			jsonObject.put("username", username);
			jsonObject.put("password", password);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	//	String Json = "{\"cmd\":\""+"requestcar" +"\"pickuplat\":\"" + pickuplat +"\","
		//		+ "\"pickuplng\":\"" + pickuplng  +"\","+ "\"destlat\":\"" + destlat +"\","
			//	+ "\"destlng\":\"" + destlng +"\","+"\"pickupAdd\":\"" + pickupAdd +"\","+"\"destAdd\":\"" + destAdd +"\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand,
				Model.SENDREQUESTCAR, jsonObject.toString()));
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(RegistetActivity.this,
						"Server error! Request failed!", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(RegistetActivity.this, "Server no response", 1)
						.show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result == null) {
					Toast.makeText(RegistetActivity.this,
							"Server no response, please check network", 1)
							.show();
					return;
				}
				// handle result json myJson.method(); addList
		//		driverLoc = myJson.getDriverLoc(result);
				

		//		if (driverLoc != null) {

		/*		for (Marker oldMarker : markerList) {
						oldMarker.remove();
					}
					markerList.clear();
					for (NearByKid kid : nearKidList) {
						String snippet = kid.getChildAge() + " "
								+ kid.getChildSex() + " " + kid.getUserID();
						MarkerOptions options = new MarkerOptions()
								.title(locality)
								.position(
										new LatLng(kid.getLatitude(), kid
												.getLongitude()))
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
								.title(kid.getUserName()).snippet(snippet);
						Marker markerNew = mMap.addMarker(options);
						markerNew.showInfoWindow();
						markerList.add(markerNew);
					} */

		//		} else {
					// Toast.makeText(MapMainActivity.this,
					// "No kid is found nearby", Toast.LENGTH_LONG).show();
		//		}

			//	Toast.makeText(getApplicationContext(), result,
				//		Toast.LENGTH_LONG).show();
			//	Log.e("DriverLocation", result);

			}
		};
	};



	//}

}
