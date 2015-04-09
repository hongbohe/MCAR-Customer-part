package com.mcar.info;

public class DriverLoc {
	public double driverLat;
	public double driverLng;


	public DriverLoc() {

	}

	public DriverLoc(double driverLat, double driverLng) {
		super();
		this.driverLat = driverLat;
		this.driverLng = driverLng;

	}

	public double getDriverLat() {
		return driverLat;
	}

	public void setDriverLat(double driverLat) {
		this.driverLat = driverLat;
	}

	public double getDriverLng() {
		return driverLng;
	}

	public void setDriverLng(double driverLng) {
		this.driverLng = driverLng;
	}
	
	
}
