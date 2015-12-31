package com.saiteng.smartlisten.baidumap;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;

public class GpsLocation {
	private LocationManager mLocationManager;// λ�ù���
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
	 * ����GPS��
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
	 * ��ȡ����λ�á�
	 */
	private void getLocation(LocationListener listener) {

		// ���ҵ�������Ϣ
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // �߾���
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // �͹���

		/**
		 * ANDROID�������ֻ�ȡλ�õķ�ʽ,LocationManager.NETWORK_PROVIDER��LocationManager.
		 * GPS_PROVIDER�� ǰ�������ƶ������л�ȡλ�ã����Ƚϵ͵��ٶȺܿ죬 ����ʹ��GPS���ж�λ�����Ⱥܸߵ�һ����Ҫ10-60��ʱ
		 * ����ܿ�ʼ��1�ζ�λ��������� ������������޷���λ�� �˷���ʹ��Criteria�õ���ѵķ�ʽ
		 */

		String provider = mLocationManager.getBestProvider(criteria, true); // ��ȡGPS��Ϣ
		// Location location = locationManager.getLastKnownLocation(provider);
		// // ͨ��GPS��ȡλ��

		// listener.updateLocation(location);// ���÷���������λ����Ϣ

		// ���ü�������1�����һ��
		mLocationManager.requestLocationUpdates(provider,1000, 0, listener);
	}

}
