package com.saiteng.st_master;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.saiteng.st_master.conn.ConnSocketServer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LocateActivity extends Activity{
	private TextView mView_Title;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Marker mMarkerE;
	private static double longitude;
	private static double latitude;
	private static Handler handler;
	private static Context mcontext;
	// 初始化全局 bitmap 信息，不用时及时 recycle
		BitmapDescriptor bdE = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locate);
		MyApplication.getInstance().addActivity(this);
		mcontext = LocateActivity.this;
		mView_Title = (TextView) findViewById(R.id.action_bar_preview_txt);
		mView_Title.setText("追踪");
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==0){
					Toast.makeText(mcontext, "设备不在线，暂无定位数据", Toast.LENGTH_LONG).show();
				}else if(msg.what==1){
					initOverlay();
				}
				
			}
		};
		ConnSocketServer.sendOrder("[ST*"+Config.imei+"*"+Config.phonenum+"*GetlatLng");
		
	}
	public void initOverlay() {
		// add marker overlay
		LatLng llE = new LatLng(latitude, longitude);
		MarkerOptions ooE = new MarkerOptions().position(llE).icon(bdE)
				.zIndex(9).draggable(true);
		mMarkerE = (Marker) (mBaiduMap.addOverlay(ooE));
		MapStatus mapStatus1 = new MapStatus.Builder(
				mBaiduMap.getMapStatus()).target(llE).build();
		MapStatusUpdate mapStatusUpdate1 = MapStatusUpdateFactory
				.newMapStatus(mapStatus1);
		mBaiduMap.animateMapStatus(mapStatusUpdate1);

	}
	
	public static void setlatLng(String latLng){
		if(latLng==null||"".equals(latLng)){
			handler.sendEmptyMessage(0);
		}else{
			String[] arr_data = latLng.split(",");
			longitude=Double.parseDouble(arr_data[1]);
            latitude=Double.parseDouble(arr_data[1].replace("]",""));
            Config.mGZLongitude =longitude;
            Config.mGZLatitude  = latitude;
        	handler.sendEmptyMessage(1);
		}
			
		
	}
}
