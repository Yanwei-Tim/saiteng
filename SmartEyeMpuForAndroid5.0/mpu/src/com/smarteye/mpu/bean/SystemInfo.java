package com.smarteye.mpu.bean;

public class SystemInfo {
	private String androidModel;
	private String androidManufacture;
	private String androidVersion;
	private int androidAPILevel;

	public int getAndroidAPILevel() {
		return androidAPILevel;
	}

	public void setAndroidAPILevel(int androidAPILevel) {
		this.androidAPILevel = androidAPILevel;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	public String getAndroidManufacture() {
		return androidManufacture;
	}

	public void setAndroidManufacture(String androidManufacture) {
		this.androidManufacture = androidManufacture;
	}

	public String getAndroidModel() {
		return androidModel;
	}

	public void setAndroidModel(String androidModel) {
		this.androidModel = androidModel;
	}
}
