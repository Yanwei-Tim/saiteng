package com.locate.location.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.locate.location.conn.HttpClientThread;
import com.locate.location.conn.MyReadDate;
import com.locate.location.conn.MySaveTask;
import com.locate.location.conn.WebsocketThread;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class LocateServices extends Service{

	private LocationManager locationManager=null; 
	private Context mcontext;
	private StringBuffer sb = new StringBuffer(256);//用来存储需要保存到本地的数据
	//用来保存需要传到服务器端的数据。
    private ArrayList<String> list=new ArrayList<String>();
    
    private ArrayList<WebsocketThread> listsocket = new ArrayList<WebsocketThread>();//管理与服务器的websocket连接
	private ArrayList<HttpClientThread> listhttp  = new ArrayList<HttpClientThread>();
	private ArrayList<MySaveTask> listsaveTask    = new ArrayList<MySaveTask>();
	private ArrayList<MyReadDate> listreadTask    = new ArrayList<MyReadDate>();
    
	//修改日期格式保存到数据库
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	private LocationClient mLocClient;
	private String deviceId; //当前设备的MIEI号。
	private ConnectivityManager connManager;//网络连接管理器
    private NetworkInfo info;
    private String name;
    private String dateString=null;
	private LocationClientOption option;
  	private String TAG = "BroadcastReceiver";
  	
	private HttpClientThread httpthread;
	private MySaveTask saveth;
	private MyReadDate readDate;
	private WebsocketThread td;
  	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo(); // 网络信息

		// 声明基于百度api的LocationClient类
		mLocClient = new LocationClient(this);
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// 获取手机一些标识信息。
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// 获取本机MIEI号码（仅手机存在）
		mcontext = LocateServices.this;
		deviceId = telephonyManager.getDeviceId();// 获取设备id号
		option = new LocationClientOption();// 定义设置相关参数
		final IntentFilter filter = new IntentFilter();
		// 屏幕灭屏广播
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		// 屏幕亮屏广播
		filter.addAction(Intent.ACTION_SCREEN_ON);
		/**
		 * 动态注册屏幕点亮和关闭广播事件
		 * 
		 */
		BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {

				String action = intent.getAction();
				if (Intent.ACTION_SCREEN_ON.equals(action)) {
					Log.d(TAG, "screen on");
					Config.mBaiduGps = false;
					setLocationOption();
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					Log.d(TAG, "screen off");
					Config.mBaiduGps = true;
					setLocationOption();
				}
			}
		};
		registerReceiver(mBatInfoReceiver, filter);
		mLocClient.registerLocationListener(new MyLocationListenner());
		setLocationOption();
		mLocClient.start();
		Log.d(TAG, "start  new mLocClient");
	}
	
	// 设置相关参数
	private void setLocationOption() {
		option.setOpenGps(Config.mBaiduGps);// 设置是否打开gps，使用gps前提是用户硬件打开gps。默认是不打开gps的。
		option.setCoorType("bd09ll");// 设置坐标类型百度加密坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");// 设置是否要返回地址信息，默认为无地址信息。String 值为
									// all时，表示返回地址信息。其他值都表示不返回地址信息。
		option.setScanSpan(Config.msetTime);// 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		// option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		// option.setPriority(LocationClientOption.NetWorkFirst);//设置网络优先,/不设置，默认是gps优先
		option.disableCache(true);
		mLocClient.setLocOption(option);
		Log.d(TAG, "set LocOption");
	}
	/**
	  * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	  */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);

			if (td != null) {
				listsocket.remove(td);
				td = null;
			}
			if (info != null && info.isConnected()) {// 在有网络的情况下才会去发送websocket链接请求
				td = new WebsocketThread(list);
				td.start();
				listsocket.add(td);
			}
			if (Config.mIsDestroy) {
				onDestroy();
			}
			// 当服务器端改变设置参数是需要重新加载配置参数
			if (Config.mIsSetOption) {
				setLocationOption();
				Log.d(TAG, "set LocOption again");
				Config.mIsSetOption = false;
			}
			/**
			 * 如果没有强制定位命令下达就按照百度定位服务规则进行定位 在有网络和GPS条件下会优先选择定位精度最好的返回定位结果
			 */
			if (!Config.mIsOpenGps) {
				Log.d(TAG, "onReceive");
				if (location == null)
					return;
				sb.setLength(0);// 清空StringBuffer的内容
				list.clear();// 清空ArrayLis的内容
				dateString = null;
				dateString = formatter.format(new Date());
				list.add(dateString);// 1采集日期
				sb.append(dateString + ",");
				sb.append(location.getLatitude() + ",");
				list.add(location.getLatitude() + "");// 2纬度
				sb.append(location.getLongitude() + ",");
				list.add(location.getLongitude() + "");// 3经度
				list.add(deviceId);// 4设备序列号
				sb.append(deviceId + ",");
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位
					if (info != null && info.isConnected()) {
						name = info.getTypeName(); // 拿到GPS定位情况下的网络状况，为后续数据处理。
						info.getSubtypeName();
						if (name.equals("WIFI")) {
							list.add("002");
							sb.append("002/");
							sendHttp(list);// 执行发送http协议方法
							Log.i(TAG, "WIFI");
						} else if (name.equalsIgnoreCase("MOBILE")) {
							Log.i(TAG, "A-GPS");
							list.add("003");
							sb.append("003/");
							savelocate(sb);// 执行本地保存方法
							if (Config.mIsForceTrans) {
								readLocate();// 执行读取本地保存文件方法
							}
						}

					} else {// 在gps定位下没有开启网络则将数据保存在本地。
						list.add("001");// 5定位方式
						sb.append("001/");
						Log.d(TAG, "GPS");
						savelocate(sb);// 执行本地保存方法
					}

				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位
					if (info != null && info.isConnected()) {
						name = info.getTypeName(); // 拿到当前网络的名字
						info.getSubtypeName();
						if (name.equals("WIFI")) {
							sendHttp(list);// 发送http协议
							list.add("002");
							sb.append("002/");
							Log.d(TAG, "WIFI");
						} else if (name.equalsIgnoreCase("MOBILE")) {
							list.add("004");
							sb.append("004/");

							savelocate(sb);
							if (Config.mIsForceTrans) {// 强制传输
								readLocate();// 执行读取本地保存文件方法
							}
							Log.d(TAG, "基站");
						}
					}

				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
					list.add("005");
					sb.append("005/");
					Log.d(TAG, "离线定位");
				}
			} else {
				GpsLocator();
			}
		}
	
	
     /**强制定位命令下达则会在用户开启GPS的前提下强制使用GPS定位*/
		private void GpsLocator() {

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Config.msetTime, 0, locationListener);

		}

		/** 获取gps定位信息 */
		private LocationListener locationListener = new LocationListener() {
			/**
			 * 位置信息变化时触发
			 */
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					sb.setLength(0);// 清空StringBuffer的内容
					list.clear();// 清空ArrayLis的内容
					dateString = null;
					dateString = formatter.format(new Date());
					list.add(dateString);// 1采集日期
					sb.append(dateString + ",");
					sb.append(location.getLatitude() + ",");
					list.add(location.getLatitude() + "");// 2纬度
					sb.append(location.getLongitude() + ",");
					list.add(location.getLongitude() + "");// 3经度
					list.add(deviceId);// 4设备序列号
					sb.append(deviceId + ",");
					list.add("001");
					sb.append("001/");
					Log.i(TAG, "GPS");
					if (info != null && info.isAvailable()) {
						name = info.getTypeName(); // 拿到GPS定位情况下的网络状况，为后续数据处理。
						info.getSubtypeName();
						if (name.equals("WIFI")) {
							sendHttp(list);// 执行发送http协议方法
						} else {
							savelocate(sb);
							if (Config.mIsForceTrans) {// 强制传输
								readLocate();// 执行读取本地保存文件方法
							}
						}
					} else {
						savelocate(sb);
					}
				}
				if (!Config.mIsOpenGps) {
					locationManager.removeUpdates(locationListener);
				}
			}

			/**
			 * GPS状态变化时触发
			 */
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				switch (status) {
				// GPS状态为可见时
				case LocationProvider.AVAILABLE:
					Log.i(TAG, "当前GPS状态为可见状态");
					break;
				// GPS状态为服务区外时
				case LocationProvider.OUT_OF_SERVICE:
					Log.i(TAG, "当前GPS状态为服务区外状态");
					break;
				// GPS状态为暂停服务时
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					Log.i(TAG, "当前GPS状态为暂停服务状态");
					break;
				}

			}

			/**
			 * GPS开启时触发
			 */
			@Override
			public void onProviderEnabled(String provider) {

			}

			/**
			 * GPS禁用时触发
			 */
			@Override
			public void onProviderDisabled(String provider) {

			}

		};



		private void readLocate() {
			/** 根据服务器是否下达强制传输的命令来开启读取本地文件的线程 */
			if (readDate != null) {
				listreadTask.remove(readDate);
				readDate = null;
			}
			readDate = new MyReadDate();
			readDate.start();
			listreadTask.add(readDate);
			Config.mIsForceTrans = false;
		}



		private void sendHttp(ArrayList<String> listdata) {
			/** 建立http连接发送数据到服务器，使用list来管理创建的http连接线程 */
			if (httpthread != null) {
				listhttp.remove(httpthread);
				httpthread = null;
			}
			httpthread = new HttpClientThread(Config.mURL, listdata);
			httpthread.start();
			listhttp.add(httpthread);

		}



		private void savelocate(StringBuffer sbf) {
			if (saveth != null) {
				listsaveTask.remove(saveth);
				saveth = null;
			}
			saveth = new MySaveTask(sbf.toString());
			saveth.start();
			listsaveTask.add(saveth);

		}



	@Override
	public void onReceivePoi(BDLocation arg0) {
		
	}
	
  }
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocClient.stop();
	}

	
}
