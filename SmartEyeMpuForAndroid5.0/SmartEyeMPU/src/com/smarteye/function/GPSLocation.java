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
	private LocationManager locationManager;// λ�ù���
	private Context mContext;
	public GPSLocation(Context context) {
		this.mContext = context;
	}

	/**
	 * ����GPS��
	 */
	public void openGPSSettings(LocationListener listener) {
		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			getLocation(listener);
			return;
		}
//		// ��ʾ�û���GPS
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setMessage("����Ҫ����GPS����ʹ�ô˳��򣬿�����");
//		builder.setTitle("��ʾ");
//		builder.setPositiveButton("ȷ��",
//				new android.content.DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						Intent intent = new Intent(
//								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//						mContext.startActivityForResult(intent, 0); // ��Ϊ������ɺ󷵻ص���ȡ����
//					}
//				});
//		builder.setNegativeButton("�˳�",
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

		String provider = locationManager.getBestProvider(criteria, true); // ��ȡGPS��Ϣ
		// Location location = locationManager.getLastKnownLocation(provider);
		// // ͨ��GPS��ȡλ��

		// listener.updateLocation(location);// ���÷���������λ����Ϣ

		// ���ü�������1�����һ��
		locationManager.requestLocationUpdates(provider, 1000, 0, listener);
	}
}