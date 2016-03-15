package com.saiteng.st_individual;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.saiteng.st_individual.view.PreViewPopwindow;

/**
 * 在地图显示选中的信标位置
 */
public class TrackActivity extends Activity implements OnClickListener{
	private ImageView mImage;
	private RelativeLayout relativeLayout;
	private boolean bTransfer = true;
	private Context context;
	/**
	 * MapView 地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Marker mMarkerE;
	private InfoWindow mInfoWindow;
	private Button mLocate_btn;
	private LocationClient locationClient = null;
	private LocationListener listener;
	private MyLocationData locationData;
	private PreViewPopwindow preViewPopwindow;
	private double longitude,latitude;

	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdE = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);
	
	boolean isShowInfoWindow = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gengzong);
		// 顶部标题栏
		mImage = (ImageView) findViewById(R.id.preview_jiankongmap_btn);
		mImage.setOnClickListener(this);
		relativeLayout = (RelativeLayout) findViewById(R.id.preview_jiankongmap_layout);
		context=TrackActivity.this;
        Intent intent1 = getIntent();
        //将activity传递过来值转成double类型。
        String str_longitude = intent1.getStringExtra("longitude");  
        String str_latitude = intent1.getStringExtra("latitude");
        longitude=Double.parseDouble(str_longitude);
        latitude=Double.parseDouble(str_latitude);
        mLocate_btn = (Button) findViewById(R.id.location_btn);
        mLocate_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				locationClient.start();
			}
		});
        
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setOnMyLocationClickListener(new OnMyLocationClickListener() {
			@Override
			public boolean onMyLocationClick() {
				if (isShowInfoWindow) {
					isShowInfoWindow = false;
					mBaiduMap.hideInfoWindow();
				} else {
					isShowInfoWindow = true;
					mBaiduMap.showInfoWindow(mInfoWindow);
				}
				return false;
			}
		});
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder(mBaiduMap.getMapStatus())
						.zoom(16).build()));
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
		locationClient = new LocationClient(this);
		listener = new LocationListener();
		locationClient.registerLocationListener(listener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);
		//option.setTimeOut(10 * 1000);
		option.setCoorType("bd09ll");
		//option.setScanSpan(1000*60);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		locationClient.setLocOption(option);
		//locationClient.start();
		initOverlay();
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				OnInfoWindowClickListener listener = null;
			 if (marker == mMarkerE) {
					button.setText("更改图标");
					button.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//marker.setIcon(bd);
							mBaiduMap.hideInfoWindow();
						}
					});
					LatLng ll = marker.getPosition();
					mInfoWindow = new InfoWindow(button, ll, -47);
					mBaiduMap.showInfoWindow(mInfoWindow);
				} 
				return true;
			}
		});
	}
	
	private class LocationListener implements BDLocationListener {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			Log.i("tag", "latitude------>" + location.getLatitude());
			Log.i("tag", "lontitude------>" + location.getLongitude());
			try {
				locationData = new MyLocationData.Builder()
						.accuracy(location.getRadius())
						.direction(location.getDirection())
						.latitude(location.getLatitude())
						.longitude(location.getLongitude())
						.speed(location.getSpeed()).build();

				mBaiduMap.setMyLocationData(locationData);

				LatLng point = new LatLng(location.getLatitude(),
						location.getLongitude());
				//将定位到的经纬度在地图上显示出来
				MapStatus mapStatus = new MapStatus.Builder(
						mBaiduMap.getMapStatus()).target(point).build();
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
						.newMapStatus(mapStatus);
				mBaiduMap.animateMapStatus(mapStatusUpdate);

			} catch (Exception e) {
			}
		}
	}

	public void initOverlay() {
		// add marker overlay
		LatLng llE = new LatLng(31.205767, 122.470494);
		MarkerOptions ooE = new MarkerOptions().position(llE).icon(bdE)
				.zIndex(9).draggable(true);
		mMarkerE = (Marker) (mBaiduMap.addOverlay(ooE));
		MapStatus mapStatus1 = new MapStatus.Builder(
				mBaiduMap.getMapStatus()).target(llE).build();
		MapStatusUpdate mapStatusUpdate1 = MapStatusUpdateFactory
				.newMapStatus(mapStatus1);
		mBaiduMap.animateMapStatus(mapStatusUpdate1);

	}
	public void exitSystem() {
		Config.mIsLogined=false;
		Config.mIsFristLogined=false;
		Config.ip=null;
		Config.port=null;
		Config.phoneNum=null;
		Config.loginInfo=null;
		Config.medit.clear();
		Config.medit.commit();
		finish();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
		bdE.recycle();
	}
	@Override
	public void onClick(View v) {
		 if (bTransfer) {
				preViewPopwindow = new PreViewPopwindow(context);
				preViewPopwindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						bTransfer = true;
					}
				});
				int[] location = new int[2];
				relativeLayout.getLocationOnScreen(location);
				preViewPopwindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY,
						location[0] - preViewPopwindow.getWidth(), 0);
				bTransfer = false;
			}else{
				preViewPopwindow.dismiss();
				bTransfer = true;
			}
			
		
	}
  
}
