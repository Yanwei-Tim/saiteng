package com.saiteng.st_master;

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
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Menu_LocateActivity extends Activity implements OnGetGeoCoderResultListener{
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;
	private ImageView mImage;
	private TextView mView_title;
	private RelativeLayout relativeLayout;
	private boolean bTransfer = true;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		if(isPad()){//判断是不是平板
			setContentView(R.layout.menu_locateactivity);
		}else{
			setContentView(R.layout.menu_phonelocateactivity);
		}
		initView();
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView_locate);
		mBaiduMap = mMapView.getMap();

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}
	
	private boolean isPad() {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		 Display display = wm.getDefaultDisplay();
		 // 屏幕宽度
		 float screenWidth = display.getWidth();
		 // 屏幕高度
		 float screenHeight = display.getHeight();
		 DisplayMetrics dm = new DisplayMetrics();
		 display.getMetrics(dm);
		 double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		 double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		 // 屏幕尺寸
		 double screenInches = Math.sqrt(x + y);
		 // 大于6尺寸则为Pad
		 if (screenInches >= 6.0) {
		  return true;
		 }
		 return false;
	}

	private void initView() {
		mView_title = (TextView) findViewById(R.id.action_bar_preview_txt);
		mView_title.setText("手工定位");
		
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
				Toast.makeText(Menu_LocateActivity.this,"坐标不能为空",Toast.LENGTH_LONG).show();
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
				Toast.makeText(Menu_LocateActivity.this,"地址不能为空",Toast.LENGTH_LONG).show();
			}else{
				// Geo搜索
				mSearch.geocode(new GeoCodeOption().city(
						editCity.getText().toString()).address(
						editGeoCodeKey.getText().toString()));
			}
			
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(Menu_LocateActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
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
		Toast.makeText(Menu_LocateActivity.this, strInfo, Toast.LENGTH_LONG).show();
		
	}


	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(Menu_LocateActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(Menu_LocateActivity.this, result.getAddress(),
				Toast.LENGTH_LONG).show();
		
	}

}
