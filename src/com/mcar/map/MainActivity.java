package com.mcar.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mcar.info.DriverLoc;
import com.mcar.model.Model;
import com.mcar.net.ThreadPoolUtils;
import com.mcar.thread.HttpPostThread;
import com.mcar.utils.MyJson;

public class MainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	private static final String DEFAULT = "N/A";
	// private static final String GLOBAL_ADDRESS = "";
	private final List<String> addressList = new ArrayList<String>();
	GoogleMap mMap;

	@SuppressWarnings("unused")
	private static final double JALGAON_LAT = 40.694131,
			JALGAON_LNG = -73.986927;
	private static final float DEFAULTZOOM = 15;
	LocationClient mLocationClient;
	Marker marker;
	Marker marker1 = null;
	Marker marker2 = null;
	String pickupAdd = null;
	String destAdd = null;

	Polyline line;

	Circle shape;

	LatLng latLng, pickupll, destll;

	private DriverLoc driverLoc = new DriverLoc();
	private MyJson myJson = new MyJson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (servicesOK()) {
			setContentView(R.layout.activity_map);

			if (initMap()) {
				Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT)
						.show();
				gotoLocation(JALGAON_LAT, JALGAON_LNG, DEFAULTZOOM);
				mMap.setMyLocationEnabled(true);
				mLocationClient = new LocationClient(this, this, this);
				mLocationClient.connect();
			} else {
				Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			setContentView(R.layout.activity_main);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mapTypeNone:
			mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			break;
		case R.id.mapTypeNormal:
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.mapTypeSatellite:
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.mapTypeTerrain:
			mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case R.id.mapTypeHybrid:
			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case R.id.gotoCurrentLocation:
			gotoCurrentLocation();
			break;

		// case R.id.createNote:
		// create_note();
		// break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean servicesOK() {
		int isAvailables = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (isAvailables == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailables)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailables,
					this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		} else {
			Toast.makeText(this, "Google service not available",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private boolean initMap() {
		if (mMap == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mMap = mapFrag.getMap();

			if (mMap != null) {
				mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

					@Override
					public View getInfoWindow(Marker arg0) {
						return null;
					}

					@Override
					public View getInfoContents(Marker marker) {
						View v = getLayoutInflater().inflate(
								R.layout.info_window, null);
						TextView tvLocality = (TextView) v
								.findViewById(R.id.tv_locality);
						TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
						TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
						TextView tvSnippet = (TextView) v
								.findViewById(R.id.tv_snippet);

						LatLng ll = marker.getPosition();

						tvLocality.setText(marker.getTitle());
						tvLat.setText("Latitude: " + ll.latitude);
						tvLng.setText("Longitude: " + ll.longitude);
						tvSnippet.setText(marker.getSnippet());

						return v;
					}
				});

				mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

					@Override
					public void onMapLongClick(LatLng ll) {
						/*
						 * Geocoder gc = new Geocoder(MainActivity.this);
						 * List<Address> list = null;
						 * 
						 * try { list = gc.getFromLocation(ll.latitude,
						 * ll.longitude, 1); } catch (IOException e) {
						 * e.printStackTrace(); return; }
						 * 
						 * Address add = list.get(0);
						 * MainActivity.this.setMarker(add.getLocality(),
						 * add.getCountryName(), ll.latitude, ll.longitude);
						 */

					}
				});

				mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker marker) {
						String msg = marker.getTitle() + " ("
								+ marker.getPosition().latitude + ","
								+ marker.getPosition().longitude + ")";
						// Toast.makeText(MainActivity.this, msg,
						// Toast.LENGTH_LONG).show();
						return false;
					}
				});

				mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

					@Override
					public void onMarkerDragStart(Marker arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMarkerDragEnd(Marker marker) {
						Geocoder gc = new Geocoder(MainActivity.this);
						List<Address> list = null;
						LatLng ll = marker.getPosition();
						try {
							list = gc.getFromLocation(ll.latitude,
									ll.longitude, 1);
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}

						Address add = list.get(0);
						marker.setTitle(add.getLocality());
						marker.setSnippet(add.getCountryName());
						marker.showInfoWindow();
					}

					@Override
					public void onMarkerDrag(Marker arg0) {
						// TODO Auto-generated method stub

					}
				});

				mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
					public void onCameraChange(CameraPosition arg0) {

						// Adding Marker on the touched location with address

						mMap.clear();
						// mMap.addMarker(new
						// MarkerOptions().position(arg0.target));

						// LatLng centerOfMap = mMap.getCameraPosition().target;
						final LatLng ll = mMap.getCameraPosition().target;
						// EditText et = (EditText) findViewById(R.id.etPickup);
						// et.setText(ll.latitude+" "+ll.longitude);

						new ReverseGeocodingTask(getBaseContext()).execute(ll);

						ImageView img = (ImageView) findViewById(R.id.UserCamera);
						img.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
										MainActivity.this);

								// Setting Dialog Title
								alertDialog2.setTitle("Set your locations");

								// Setting Dialog Message
								alertDialog2
										.setMessage("Pickup or Destination?");

								// Setting Icon to Dialog
								alertDialog2.setIcon(R.drawable.ic_mapmarker);

								// Setting Negative "NO" Btn
								alertDialog2.setNegativeButton("Pickup",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												// Write your code here to
												// execute after dialog
												// String addressText;

												EditText et = (EditText) findViewById(R.id.etPickup);

												et.setText(addressList.get(0));
												pickupAdd = addressList.get(0);
												addressList.clear();
												// et.setText(ll.latitude+" "+ll.longitude);

												pickupll = ll;

												dialog.cancel();
											}
										});

								// Setting Positive "Yes" Btn
								alertDialog2.setPositiveButton("Destination",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												// Write your code here to
												// execute after dialog
												EditText et = (EditText) findViewById(R.id.etDestination);
												// et.setText(ll.latitude+" "+ll.longitude);
												et.setText(addressList.get(0));
												destAdd = addressList.get(0);
												addressList.clear();
												destll = ll;

											}
										});

								// Showing Alert Dialog
								alertDialog2.show();

							}
						});

						String msg = "New Latitude: " + ll.latitude
								+ " Longitude: " + ll.longitude;
						// Toast.makeText(MainActivity.this, msg,
						// Toast.LENGTH_LONG).show();

						// Update your Marker's position to the center of the
						// Map.
						// mMap.addMarker(new
						// MarkerOptions().position(centerOfMap));
						// yourMarkerInstance.setPosition(centerOfMap);
					}
				});

			}
		}
		return (mMap != null);
	}

	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		// Finding address using reverse geocoding
		@Override
		protected String doInBackground(LatLng... params) {
			Geocoder geocoder = new Geocoder(mContext);
			double latitude = params[0].latitude;
			double longitude = params[0].longitude;

			List<Address> addresses = null;
			String addressText = "";

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);

				addressText = String.format(
						"%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address.getLocality(),
						address.getCountryName());

			}

			return addressText;
		}

		@Override
		protected void onPostExecute(String addressText) {
			// Setting the title for the marker.
			// This will be displayed on taping the marker
			// markerOptions.title(addressText);

			// Placing a marker on the touched position
			// googleMap.addMarker(markerOptions);
			// GLOBAL_ADDRESS = addressText;
			addressList.add(addressText);
			// Toast.makeText(MainActivity.this, addressText,
			// Toast.LENGTH_LONG).show();

		}
	}

	private void gotoLocation(double lat, double lng) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
		mMap.moveCamera(update);
	}

	private void gotoLocation(double lat, double lng, float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.moveCamera(update);
	}

	public void geoLocate(View v) throws IOException {
		hideSoftKeyboard(v);

		/*
		 * EditText et = (EditText) findViewById(R.id.etPickup); String location
		 * = et.getText().toString();
		 * 
		 * 
		 * Geocoder gc = new Geocoder(this); List<Address> list =
		 * gc.getFromLocationName(location, 1); Address add = list.get(0);
		 * String locality = add.getLocality(); Toast.makeText(this, locality,
		 * Toast.LENGTH_LONG).show();
		 * 
		 * double lat = add.getLatitude(); double lng = add.getLongitude();
		 * 
		 * gotoLocation(lat, lng, DEFAULTZOOM); if (marker != null) {
		 * marker.remove(); } MarkerOptions options = new MarkerOptions()
		 * .title(locality) .position(new LatLng(lat, lng));
		 */
		// marker = mMap.addMarker(options);

		Toast.makeText(
				this,
				pickupll.latitude + pickupll.longitude + destll.latitude
						+ destll.longitude + pickupAdd + destAdd,
				Toast.LENGTH_SHORT).show();

		retrieveDriverLoc(pickupll.latitude, pickupll.longitude,
				destll.latitude, destll.longitude, pickupAdd, destAdd);
		requestStatus();

	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		MapStateManager mgr = new MapStateManager(this);
		mgr.saveMapState(mMap);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MapStateManager mgr = new MapStateManager(this);
		CameraPosition position = mgr.getSavedCameraPosition();
		if (position != null) {
			CameraUpdate update = CameraUpdateFactory
					.newCameraPosition(position);
			mMap.moveCamera(update);
			// This is part of the answer to the code challenge
			mMap.setMapType(mgr.getSavedMapType());
		}
	}

	protected void gotoCurrentLocation() {
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation == null) {
			// Toast.makeText(this, "Current location isn't available",
			// Toast.LENGTH_SHORT).show();
		} else {
			LatLng ll = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,
					DEFAULTZOOM);
			mMap.animateCamera(update);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Toast.makeText(this, "Connected to location service",
		// Toast.LENGTH_SHORT).show();
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(5000);
		request.getFastestInterval();
		mLocationClient.requestLocationUpdates(request, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// String msg = "Location: " + location.getLatitude() + "," +
		// location.getLongitude();
		// Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		SharedPreferences sharedPreferences = getSharedPreferences("SaveData",
				Context.MODE_PRIVATE);
		// String lat=sharedPreferences.getString("lat", DEFAULT);
		double lat = Double.longBitsToDouble(sharedPreferences
				.getLong("lat", 0));
		// String lng=sharedPreferences.getString("lng", DEFAULT);
		String message = sharedPreferences.getString("message", DEFAULT);
		double lng = Double.longBitsToDouble(sharedPreferences
				.getLong("lng", 0));
		if (lat == 0 || lng == 0 || message.equals(DEFAULT)) {
			// Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();

		} else {
			// String latDecrease = Float.toString((float)
			// (Float.parseFloat(lat) - 0.090));

			// DecimalFormat dFormat = new DecimalFormat("#.####");
			// Double Latitude =
			// Double.valueOf(dFormat.format(location.getLatitude()));
			// Double Longitude =
			// Double.valueOf(dFormat.format(location.getLongitude()));
			// Toast.makeText(this, "Match Found" + Double.parseDouble(lat) + ""
			// + Double.parseDouble(lng) , Toast.LENGTH_SHORT).show();

			//
			double Latitude = location.getLatitude();
			double Longitude = location.getLongitude();
			// if (distance(lat, lng, Latitude, Longitude) < 0.1 &&
			// distance(lat, lng, Latitude, Longitude) > 0.1) {
			double dist = distance(lat, lng, Latitude, Longitude);
			// Toast.makeText(this, "" + dist, Toast.LENGTH_SHORT).show();
			// }
		}
	}

	private double distance(double lat, double lng, double Latitude,
			double Longitude) {

		double earthRadius = 3958.75; // in miles, change to 6371 for kilometer
										// output

		double dLat = Math.toRadians(Latitude - lat);
		double dLng = Math.toRadians(Longitude - lng);

		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);

		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat))
				* Math.cos(Math.toRadians(Latitude));

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double dist = earthRadius * c;

		return dist; // output distance, in MILES
	}

	private void setMarker(String locality, String country, double lat,
			double lng) {
		LatLng ll = new LatLng(lat, lng);

		MarkerOptions options = new MarkerOptions()
				.title(locality)
				.position(new LatLng(lat, lng))
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_mapmarker))
				// .icon(BitmapDescriptorFactory.defaultMarker())
				.anchor(.5f, .5f).draggable(true);

		if (marker1 == null) {
			marker1 = mMap.addMarker(options);
		} else if (marker2 == null) {
			marker2 = mMap.addMarker(options);
			drawLine();
			marker1.remove();
			marker1 = null;
			marker1 = marker2;
			marker2 = null;
		} else {

		}

		/*
		 * if (country.length() > 0) { options.snippet(country); }
		 * 
		 * if (marker != null) { removeEverything(); }
		 * 
		 * marker = mMap.addMarker(options); shape = drawCircle(ll);
		 */

	}

	private void drawLine() {
		// TODO Auto-generated method stub
		PolylineOptions options = new PolylineOptions().add(
				marker1.getPosition()).add(marker2.getPosition());

		line = mMap.addPolyline(options);
	}

	private Circle drawCircle(LatLng ll) {
		CircleOptions options = new CircleOptions().center(ll).radius(500)
				.fillColor(0x330000FF).strokeColor(Color.BLUE).strokeWidth(3);
		return mMap.addCircle(options);
	}

	private void removeEverything() {
		marker.remove();
		marker = null;
		shape.remove();
		shape = null;
	}

	public void create_note() {
		setContentView(R.layout.message_save);
		TextView lat = (TextView) findViewById(R.id.lat);
		TextView lng = (TextView) findViewById(R.id.lng);
		LatLng ll = marker.getPosition();

		lat.setText("Latitude: " + ll.latitude);
		lng.setText("Longitude: " + ll.longitude);

	}

	// if (marker != null) {
	// button.setVisibility(1);
	// }else {
	// button.setVisibility(2);
	// }
	public void saveNote(View v) {
		// setContentView(R.layout.activity_map);
		LatLng ll = marker.getPosition();
		EditText message = (EditText) findViewById(R.id.message);
		TextView lat = (TextView) findViewById(R.id.lat);
		TextView lng = (TextView) findViewById(R.id.lng);
		Log.d("nik", "SaveText");
		SharedPreferences sharedPreferences = getSharedPreferences("SaveData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong("lat", Double.doubleToLongBits(ll.latitude));
		editor.putLong("lng", Double.doubleToLongBits(ll.longitude));
		editor.putString("message", message.getText().toString());
		editor.commit();
		// Toast.makeText(this, "Note Created Sucessfully",
		// Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void retrieveDriverLoc(double pickuplat, double pickuplng,
			double destlat, double destlng, String pickupAdd, String destAdd) {
		// latitude longitude function(address);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmd", "requestcar");
			jsonObject.put("pickuplat", pickuplat);
			jsonObject.put("pickuplng", pickuplng);
			jsonObject.put("destlat", destlat);
			jsonObject.put("destlng", destlng);
			jsonObject.put("pickupAdd", pickupAdd);
			jsonObject.put("destAdd", destAdd);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// String Json = "{\"cmd\":\""+"requestcar" +"\"pickuplat\":\"" +
		// pickuplat +"\","
		// + "\"pickuplng\":\"" + pickuplng +"\","+ "\"destlat\":\"" + destlat
		// +"\","
		// + "\"destlng\":\"" + destlng +"\","+"\"pickupAdd\":\"" + pickupAdd
		// +"\","+"\"destAdd\":\"" + destAdd +"\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand, Model.SENDREQUESTCAR,
				jsonObject.toString()));
	}

	private void requestStatus() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmd", "requestStatus");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ThreadPoolUtils.execute(new HttpPostThread(hand, Model.SENDREQUESTCAR,
				jsonObject.toString()));
	}

	private void requestDriverLocation() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmd", "requestDriverLocation");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ThreadPoolUtils.execute(new HttpPostThread(hand, Model.SENDREQUESTCAR,
				jsonObject.toString()));
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(MainActivity.this,
						"Server error! Request failed!", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(MainActivity.this, "Server no response", 1)
						.show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result == null) {
					Toast.makeText(MainActivity.this,
							"Server no response, please check network", 1)
							.show();
					return;
				} else {
					try {
						JSONObject jsonObject = new JSONObject(result);
						String resultStatus = null;
						resultStatus = jsonObject.getString("status");
						if (resultStatus != null) {
							if (resultStatus.equals("updatedLocation")) {
								Double lat = jsonObject.getDouble("latitude");
								Double lng = jsonObject.getDouble("longitude");
								
								 Toast.makeText(MainActivity.this,
								"Get driver location", Toast.LENGTH_SHORT).show();

								Geocoder gc = new Geocoder(MainActivity.this);
								List<Address> list = null;

								try {
									list = gc.getFromLocation(lat, lng, 1);

									// list = gc.getFromLocation(ll.latitude,
									// ll.longitude, 1);
								} catch (IOException e) {
									e.printStackTrace();
									return;
								}

								Address add = list.get(0);
								MainActivity.this.setMarker(add.getLocality(),
										add.getCountryName(), lat, lng);
								requestDriverLocation();

							} else {
								if (!resultStatus.equals("requestAccepted")) {
									requestStatus();
									// new Timer().schedule(new TimerTask() {
									//
									// @Override
									// public void run() {
									// // TODO Auto-generated method stub
									// requestStatus();
									// }
									// }, 0, 2000);
								} else {
									requestDriverLocation();
								}
							}

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// handle result json myJson.method(); addList
				// driverLoc = myJson.getDriverLoc(result);

				// if (driverLoc != null) {

				/*
				 * for (Marker oldMarker : markerList) { oldMarker.remove(); }
				 * markerList.clear(); for (NearByKid kid : nearKidList) {
				 * String snippet = kid.getChildAge() + " " + kid.getChildSex()
				 * + " " + kid.getUserID(); MarkerOptions options = new
				 * MarkerOptions() .title(locality) .position( new
				 * LatLng(kid.getLatitude(), kid .getLongitude()))
				 * .icon(BitmapDescriptorFactory
				 * .defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
				 * .title(kid.getUserName()).snippet(snippet); Marker markerNew
				 * = mMap.addMarker(options); markerNew.showInfoWindow();
				 * markerList.add(markerNew); }
				 */

				// } else {
				// Toast.makeText(MapMainActivity.this,
				// "No kid is found nearby", Toast.LENGTH_LONG).show();
				// }

				// Toast.makeText(getApplicationContext(), result,
				// Toast.LENGTH_LONG).show();
				// Log.e("DriverLocation", result);

			}
		};
	};
}
