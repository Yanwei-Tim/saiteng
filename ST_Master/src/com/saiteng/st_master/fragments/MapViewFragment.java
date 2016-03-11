package com.saiteng.st_master.fragments;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import com.saiteng.st_master.Config;
import com.saiteng.st_master.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ZoomControls;

public class MapViewFragment extends Fragment{
	private MapView mapView = null;
	private BaiduMap baiduMap = null;
	private View rootView;
	private LocationClient locationClient = null;
	private LocationListener listener;
	private MyLocationData locationData;
	private String TAG="FragmentBaiduMap";
	boolean isShowInfoWindow = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_mapview, null);
		return rootView;
	}
    @Override
    public void onStart() {
    	super.onStart();
    	initMap();
    	locationClient.start();
    }
    //加载地图
	private void initMap() {
		mapView = (MapView)rootView.findViewById(R.id.mapView_fragment_campus_baidumap);
		baiduMap=mapView.getMap();
		//隐藏百度logo
		View child = mapView.getChildAt(1);
		if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){            
		     child.setVisibility(View.INVISIBLE);           
		}
		//地图上比例尺        
		mapView.showScaleControl(false);
		// 隐藏缩放控件
		mapView.showZoomControls(false);
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
		option.setScanSpan(500);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		locationClient.setLocOption(option);
		Config.mlocationClient = locationClient;
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
