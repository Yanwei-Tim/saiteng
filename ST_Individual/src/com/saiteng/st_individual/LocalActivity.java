package com.saiteng.st_individual;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.saiteng.st_individual.view.PreViewPopwindow;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class LocalActivity extends Activity implements OnGetGeoCoderResultListener,OnClickListener{
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;
	private ImageView mImage;
	private RelativeLayout relativeLayout;
	private boolean bTransfer = true;
	private PreViewPopwindow preViewPopwindow;
	private Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dingwei);
		//顶部标题栏
		context = LocalActivity.this;
		mImage = (ImageView) findViewById(R.id.shougongdingwei_btn);
		mImage.setOnClickListener(this);
		relativeLayout = (RelativeLayout)findViewById(R.id.shougongdingwei_layout);
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
	}
	/**
	 * 发起搜索
	 * 
	 * @param v
	 */
	public void SearchButtonProcess(View v) {
		if (v.getId() == R.id.reversegeocode) {
			EditText lat = (EditText) findViewById(R.id.lat);
			EditText lon = (EditText) findViewById(R.id.lon);
			String latut=lat.getText().toString();
			String lont = lon.getText().toString();
			if("".equals(latut)||"".equals(lont)){
				Toast.makeText(LocalActivity.this,"地址内容不能为空",Toast.LENGTH_LONG).show();
			 }else{
				LatLng ptCenter = new LatLng((Float.valueOf(lat.getText()
						.toString())), (Float.valueOf(lon.getText().toString())));
				// 反Geo搜索
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(ptCenter));
			}
		
		} else if (v.getId() == R.id.geocode) {
			EditText editCity = (EditText) findViewById(R.id.city);
			EditText editGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
			String City = editCity.getText().toString();
			String add  = editGeoCodeKey.getText().toString();
			if("".equals(City)||"".equals(add)){
				Toast.makeText(LocalActivity.this,"坐标不能为空",Toast.LENGTH_LONG).show();
			}else{
				// Geo搜索
				mSearch.geocode(new GeoCodeOption().city(
						editCity.getText().toString()).address(
						editGeoCodeKey.getText().toString()));
			}
			
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

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocalActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(LocalActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocalActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(LocalActivity.this, result.getAddress(),
				Toast.LENGTH_LONG).show();

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
