package com.smarteye.mpu.bean;

public class GPSData {
	private WallTime time;
	private int longitude;
	private int latitude;
	private int height;
	private int angle;
	private int speed;
	private int starCount;
	private int antennaState;
	private int orientationState;

	public WallTime getTime() {
		return time;
	}

	public void setTime(WallTime time) {
		this.time = time;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getStarCount() {
		return starCount;
	}

	public void setStarCount(int startCount) {
		this.starCount = startCount;
	}

	public int getAntennaState() {
		return antennaState;
	}

	public void setAntennaState(int antennaState) {
		this.antennaState = antennaState;
	}

	public int getOrientationState() {
		return orientationState;
	}

	public void setOrientationState(int orientationState) {
		this.orientationState = orientationState;
	}

}
