package com.saiteng.st_master;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class LocateActivity extends Activity{
	private TextView mView_Title;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Marker mMarkerE;
	private double longitude,latitude;
	private Handler handler;
	// 初始化全局 bitmap 信息，不用时及时 recycle
		BitmapDescriptor bdE = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locate);
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
				initOverlay();
			}
		};
		new getNewLatLngTask().execute();
	}
	class getNewLatLngTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			String result=null;
			HttpGet get = new HttpGet(Config.url+"latLng?phonenum="+Config.phonenum);
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = null;
			try {
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStream = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					builder = new StringBuilder();
					String s = null;
					for (s = reader.readLine(); s != null; s = reader.readLine()) {
						builder.append(s);
					}
					result=builder.toString();
				}else{
					result ="NetworkException";
				}
			} catch (Exception e) {
				e.printStackTrace();
				result="Exception";
			}
			return result;
		}
		@Override
		public void onPostExecute(String result) {
			if(result!=null&&!"null".equals(result)){
				String[] arr_data = result.split(",");
				longitude=Double.parseDouble(arr_data[0]);
	            latitude=Double.parseDouble(arr_data[1]);
	            Config.mGZLongitude =longitude;
	            Config.mGZLatitude  = latitude;
	            Message message = new Message();
				message.obj ="true";
				handler.sendMessage(message);
			}else{
				Toast.makeText(LocateActivity.this,"暂无定位数据", Toast.LENGTH_LONG).show();
			}
		}
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
}
