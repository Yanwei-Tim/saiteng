package com.example.videotakepicture;

import java.io.DataOutputStream;
import java.io.IOException;

public class RootManager {
	/*
	 * ����α����
	 */
	private static void chmodApk(String busybox, String chmod) {
		try {

			Process process = null;
			DataOutputStream os = null;
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(busybox);
			os.flush();
			os.writeBytes(chmod);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void turnScreenOff() {
		try {
			DataOutputStream localDataOutputStream = new DataOutputStream(
					Runtime.getRuntime().exec("su").getOutputStream());
			localDataOutputStream
					.writeBytes("echo 0 > /sys/class/leds/button-backlight/brightness\nchmod 444 /sys/class/leds/button-backlight/brightness\n");
			localDataOutputStream
					.writeBytes("chmod 644 /sys/class/leds/lcd-backlight/brightness\necho 0 > /sys/class/leds/lcd-backlight/brightness\n chmod 444 /sys/class/leds/lcd-backlight/brightness\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void turnScreenOn() {
		try {
			DataOutputStream localDataOutputStream = new DataOutputStream(
					Runtime.getRuntime().exec("su").getOutputStream());
			localDataOutputStream
					.writeBytes("chmod 644 /sys/class/leds/button-backlight/brightness\n");
			localDataOutputStream
					.writeBytes("echo 48 > /sys/class/leds/button-backlight/brightness\n");
			localDataOutputStream
					.writeBytes("chmod 644 /sys/class/leds/lcd-backlight/brightness\necho 100 > /sys/class/leds/lcd-backlight/brightness\n chmod 444 /sys/class/leds/lcd-backlight/brightness\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void Uninstall() {
		// ����3���Ǿ�Ĭж�ص������������
		String busybox1 = "mount -o remount rw /data";
		String chmod1 = "chmod 777 /data/app/com.example.hdmonitor-1.apk";
		String uninstallapk1 = "pm uninstall com.example.hdmonitor";
		// ����Ȩ��
		chmodApk(busybox1, chmod1);
		uninstallApk(uninstallapk1);
	}

	/*
	 * ��Ҫж�ص�apk����Ȩ��
	 */
	private static void uninstallApk(String uninstallapk) {
		try {
			Process process = null;
			DataOutputStream os = null;
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(uninstallapk);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
