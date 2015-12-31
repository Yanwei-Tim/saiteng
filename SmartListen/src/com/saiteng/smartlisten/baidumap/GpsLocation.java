package com.saiteng.smartlisten.baidumap;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;

public class GpsLocation {
	private LocationManager mLocationManager;// 位置管理
	private Context mContext;

	private GpsLocation(Context context) {
		this.mContext = context;
	}

	private static GpsLocation GpsLocation = null;

	public static GpsLocation GpsLocation(Context context) {
		if (GpsLocation == null)
			GpsLocation = new GpsLocation(context);
		return GpsLocation;
	}

	public void cancelLocation(LocationListener listener) {
		mLocationManager.removeUpdates(listener);
	}

	/**
	 * 设置GPS。
	 */
	public boolean requestLocation(LocationListener listener) {
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		if (mLocationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			getLocation(listener);
			return true;
		}
		return false;
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

		String provider = mLocationManager.getBestProvider(criteria, true); // 获取GPS信息
		// Location location = locationManager.getLastKnownLocation(provider);
		// // 通过GPS获取位置

		// listener.updateLocation(location);// 调用方法，更新位置信息

		// 设置监听器，1秒监听一次
		mLocationManager.requestLocationUpdates(provider,1000, 0, listener);
	}

}
