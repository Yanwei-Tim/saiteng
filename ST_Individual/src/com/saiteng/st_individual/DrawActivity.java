package com.saiteng.st_individual;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.saiteng.st_individual.view.PreViewPopwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;

public class DrawActivity extends Activity implements OnClickListener{
	 // 地图相关
    MapView mMapView;
    BaiduMap mBaiduMap;
    Polyline mPolyline;
    private double longitude,latitude;
    private ImageView mImage;
	private RelativeLayout relativeLayout;
	private boolean bTransfer = true;
	private PreViewPopwindow preViewPopwindow;
	private Context context;
    LatLng p;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guijishow);
		//顶部标题栏
		mImage = (ImageView) findViewById(R.id.preview_guijishow_btn);
		mImage.setOnClickListener(this);
		relativeLayout = (RelativeLayout)findViewById(R.id.guijishow_layout);
		
		context = DrawActivity.this;
		 // 初始化地图
        mMapView = (MapView) findViewById(R.id.guijishow_bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder(mBaiduMap.getMapStatus())
						.zoom(16).build()));
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
      //接收intent传递过来的值
		 Intent intent1 = getIntent();
	     String[] arr_showdata = intent1.getStringArrayExtra("showdata");
        // 界面加载时添加绘制图层
        addCustomElementsDemo(arr_showdata);
     
	}
	private void addCustomElementsDemo(String[] arr_arr_showdata) {
		//[121.47102&31.205176, 121.470494&31.205767]
		List<LatLng> points = new ArrayList<LatLng>();
        for(int i=0;i<arr_arr_showdata.length;i++){
        	String showData = arr_arr_showdata[i];
        	String[] arr_LatLng=showData.split("&");
        	longitude=Double.parseDouble(arr_LatLng[0]);
            latitude=Double.parseDouble(arr_LatLng[1]);
         // 添加普通折线绘制
            p =new LatLng(latitude,longitude);
            points.add(p);
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        MapStatus mapStatus1 = new MapStatus.Builder(
				mBaiduMap.getMapStatus()).target(p).build();
		MapStatusUpdate mapStatusUpdate1 = MapStatusUpdateFactory
				.newMapStatus(mapStatus1);
		mBaiduMap.animateMapStatus(mapStatusUpdate1);
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
		} else {
			preViewPopwindow.dismiss();
			bTransfer = true;
		}

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

}
