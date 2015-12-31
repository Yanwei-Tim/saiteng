package com.saiteng.smartlisten.baidumap;

import java.util.Calendar;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.smarteye.adapter.BVCU_PUCFG_GPSData;
import com.smarteye.adapter.BVCU_WallTime;
import com.smarteye.coresdk.BVPU;
import android.content.Context;
import android.util.Log;

public class BaiduLocationTools {
	private LocationClient locationClient;
	private LocationListener listener;

	public BaiduLocationTools(Context context) {
		locationClient = new LocationClient(context.getApplicationContext());
		listener = new LocationListener();
		locationClient.registerLocationListener(listener);

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);
		option.setTimeOut(10 * 1000);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		locationClient.setLocOption(option);
	}

	public void startLocationStart() {
		locationClient.start();
	}

	public void stopLocationStart() {
		if (locationClient != null) {
			locationClient.stop();
		}
	}

	private class LocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				Log.i("BaiduLocationTools", "location.getLocType----->"
						+ location.getLocType());
				GlobalTool.BAIDU_to_WGS84(location);
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				BVCU_PUCFG_GPSData data = new BVCU_PUCFG_GPSData();
				BVCU_WallTime time = new BVCU_WallTime();

				Calendar calendar = Calendar.getInstance();
				time.iDay = (char) calendar.get(Calendar.DAY_OF_MONTH);
				time.iHour = ((char) calendar.get(Calendar.HOUR));
				time.iMinute = ((char) calendar.get(Calendar.MINUTE));
				time.iMonth = ((char) (calendar.get(Calendar.MONTH) + 1));
				time.iSecond = ((char) calendar.get(Calendar.SECOND));
				time.iYear = ((short) calendar.get(Calendar.YEAR));
				data.stTime = time;
				data.iLatitude = ((int) (latitude * 10000000));
				data.iLongitude = ((int) (longitude * 10000000));
				data.bOrientationState = 1;
				data.bAntennaState = 1;
				BVPU.InputGPSData(data);
			}
		}
	}

}
