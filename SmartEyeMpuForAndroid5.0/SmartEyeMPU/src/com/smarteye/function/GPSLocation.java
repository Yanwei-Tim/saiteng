package com.smarteye.function;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class GPSLocation {
	private LocationManager locationManager;// 位置管理
	private Context mContext;
	public GPSLocation(Context context) {
		this.mContext = context;
	}

	/**
	 * 设置GPS。
	 */
	public void openGPSSettings(LocationListener listener) {
		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			getLocation(listener);
			return;
		}
//		// 提示用户打开GPS
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setMessage("必须要开启GPS才能使用此程序，开启？");
//		builder.setTitle("提示");
//		builder.setPositiveButton("确认",
//				new android.content.DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						Intent intent = new Intent(
//								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//						mContext.startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
//					}
//				});
//		builder.setNegativeButton("退出",
//				new android.content.DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						mContext.finish();
//
//					}
//				});
//		builder.create().show();

	}

	/**
	 * 获取地理位置。
	 */
	private void getLocation(LocationListener listener) {

		// 查找到服务信息
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

		/**
		 * ANDROID中有两种获取位置的方式,LocationManager.NETWORK_PROVIDER和LocationManager.
		 * GPS_PROVIDER； 前者用于移动网络中获取位置，精度较低但速度很快， 后者使用GPS进行定位，精度很高但一般需要10-60秒时
		 * 间才能开始第1次定位，如果是在 室内则基本上无法定位。 此方法使用Criteria得到最佳的方式
		 */

		String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
		// Location location = locationManager.getLastKnownLocation(provider);
		// // 通过GPS获取位置

		// listener.updateLocation(location);// 调用方法，更新位置信息

		// 设置监听器，1秒监听一次
		locationManager.requestLocationUpdates(provider, 1000, 0, listener);
	}
}