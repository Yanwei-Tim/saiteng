package com.saiteng.st_individual.fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.model.LatLng;
import com.saiteng.st_individual.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentBaiduMap extends Fragment{

	private MapView mapView = null;
	private BaiduMap baiduMap = null;
	private LocationClient locationClient = null;
	private LocationListener listener;
	private MyLocationData locationData;
	private InfoWindow infoWindow;
	private String TAG="FragmentBaiduMap";
	
	public static FragmentBaiduMap getInstance(Context context) {
		return new FragmentBaiduMap();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_baidumap, null);
		
		mapView = (MapView) rootView
				.findViewById(R.id.mapView_fragment_campus_baidumap);
		
		return rootView;
	}
	boolean isShowInfoWindow = false;

	private void initMap() {
		baiduMap = mapView.getMap();
		baiduMap.setOnMyLocationClickListener(new OnMyLocationClickListener() {

			@Override
			public boolean onMyLocationClick() {
				if (isShowInfoWindow) {
					isShowInfoWindow = false;
					baiduMap.hideInfoWindow();
				} else {
					isShowInfoWindow = true;
					baiduMap.showInfoWindow(infoWindow);
				}
				return false;
			}
		});
		baiduMap.setMyLocationEnabled(true);
		baiduMap.setMapStatus(MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder(baiduMap.getMapStatus())
						.zoom(16).build()));
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

		locationClient = new LocationClient(getActivity()
				.getApplicationContext());
		listener = new LocationListener();
		locationClient.registerLocationListener(listener);

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);
		option.setTimeOut(10 * 1000);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000*60);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		locationClient.setLocOption(option);

	}
	@Override
	public void onStart() {
		initMap();
		locationClient.start();
		super.onStart();
	}
	@Override
	public void onStop() {

		super.onStop();
	}

	@Override
	public void onResume() {
		// mapView.setVisibility(View.VISIBLE);
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onPause() {
		// mapView.setVisibility(View.GONE);
		mapView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (locationClient != null) {
			locationClient.stop();
		}
		baiduMap.setMyLocationEnabled(false);
		mapView.onDestroy();
		mapView = null;
		super.onDestroy();
	}

	private class LocationListener implements BDLocationListener {

		private void initInfoWindow(LatLng point) {
			OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick() {
					baiduMap.hideInfoWindow();
					isShowInfoWindow = false;
				}
			};
		//	infoWindow = new InfoWindow(tipView, point, infoWindowClickListener);
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			Log.i(TAG, "latitude------>" + location.getLatitude());
			Log.i(TAG, "lontitude------>" + location.getLongitude());
			try {
				locationData = new MyLocationData.Builder()
						.accuracy(location.getRadius())
						.direction(location.getDirection())
						.latitude(location.getLatitude())
						.longitude(location.getLongitude())
						.speed(location.getSpeed()).build();

				baiduMap.setMyLocationData(locationData);

				LatLng point = new LatLng(location.getLatitude(),
						location.getLongitude());
				//将定位到的经纬度在地图上显示出来
				MapStatus mapStatus = new MapStatus.Builder(
						baiduMap.getMapStatus()).target(point).build();
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
						.newMapStatus(mapStatus);
				baiduMap.animateMapStatus(mapStatusUpdate);

			} catch (Exception e) {
			}
		}
	}
}
