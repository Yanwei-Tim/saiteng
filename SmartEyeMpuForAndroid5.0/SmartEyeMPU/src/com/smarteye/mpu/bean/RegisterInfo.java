package com.smarteye.mpu.bean;

public class RegisterInfo {
	private String serverAddr;
	private int serverPort;
	private int deviceId;
	private String deviceName;
	private String serverAliasName;

	public String getServerAliasName() {
		return serverAliasName;
	}

	public void setServerAliasName(String serverAliasName) {
		this.serverAliasName = serverAliasName;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}
}
