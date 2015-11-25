package com.locate.location.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.locate.location.HttpClientThread;
import com.locate.location.MyReadDate;
import com.locate.location.MySaveTask;
import com.locate.location.WebsocketThread;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class LocateServices extends Service{

	private LocationClient mLocClient;
	private String deviceId; //当前设备的MIEI号。
	private ConnectivityManager connManager;//网络连接管理器
    private NetworkInfo info;
    private String name;
  //保存数据服务器的地址。
	private LocationClientOption option;
  	private String url = "http://192.168.0.79:8080/LocationWebsocket/login";
  	private String TAG = "BroadcastReceiver";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo(); // 网络信息
		//声明基于百度api的LocationClient类
		mLocClient =new LocationClient( this );
		//获取手机一些标识信息。
		TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService( Context.TELEPHONY_SERVICE); 
		//获取本机MIEI号码（仅手机存在）
		
		 deviceId = telephonyManager.getDeviceId();//获取设备id号
		 option = new LocationClientOption();//定义设置相关参数
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
		            	 Config.mIsOpenGps=false;
		            	 setLocationOption();
		            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { 
		                 Log.d(TAG, "screen off");
		                 Config.mIsOpenGps=true;
		                 setLocationOption();
		                 
		            } 
		        }  
		    };  
		    registerReceiver(mBatInfoReceiver, filter);
		    mLocClient.registerLocationListener( new MyLocationListenner());
		    setLocationOption();
		    mLocClient.start();
		    Log.d(TAG, "start  new mLocClient");
		  
	}
	
	//设置相关参数
			private void setLocationOption(){
				option.setOpenGps(Config.mIsOpenGps);//设置是否打开gps，使用gps前提是用户硬件打开gps。默认是不打开gps的。
				option.setCoorType("bd09ll");//设置坐标类型百度加密坐标类型。
				option.setServiceName("com.baidu.location.service_v2.9");
				option.setAddrType("all");//设置是否要返回地址信息，默认为无地址信息。String 值为 all时，表示返回地址信息。其他值都表示不返回地址信息。
				option.setScanSpan(Config.msetTime);//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
				//option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
				//option.setPriority(LocationClientOption.NetWorkFirst);//设置网络优先,/不设置，默认是gps优先
				option.disableCache(true);		
				mLocClient.setLocOption(option);
				Log.d(TAG, "set LocOption");
			}
	/**
	  * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	  */
	public class MyLocationListenner implements BDLocationListener {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onReceiveLocation(BDLocation location) {
			
			//当服务器端改变设置参数是需要重新加载配置参数
		    if(Config.mIsSetOption){
		    	setLocationOption(); 
		    	Log.d(TAG, "set LocOption again"); 
		    	Config.mIsSetOption=false;
  		    }
			Log.d(TAG, "onReceive"); 
			if (location == null)
				return ;
			StringBuffer sb1 = new StringBuffer(256);//用来存储需要保存到本地的数据
			//用来保存需要传到服务器端的数据。
			 ArrayList<String> list=new ArrayList<String>();
			//修改日期格式保存到数据库
			 SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			 String dateString = formatter.format(new Date());
			list.add(dateString);//1采集日期
			sb1.append(dateString);
			sb1.append(location.getLatitude()+",");
			list.add(location.getLatitude()+"");//2纬度
			sb1.append(location.getLongitude()+",");
			list.add(location.getLongitude()+"");//3经度
			list.add(deviceId);//4设备序列号
			sb1.append(deviceId+",");
			if (location.getLocType() == BDLocation.TypeGpsLocation){//GPS定位
				list.add("001");//5定位方式
				sb1.append("001/");
				Log.d(TAG, "GPS"); 
				if (info != null && info.isAvailable()) {
	    			name = info.getTypeName(); // 拿到GPS定位情况下的网络状况，为后续数据处理。
	    			info.getSubtypeName();
	    			if (name.equals("WIFI")) {
	    				WebsocketThread thread =new WebsocketThread(list);
	    				thread.start();
	    				HttpClientThread httpthread =new HttpClientThread(url,list);
	    				httpthread.start();
	    				new MySaveTask(sb1.toString()).execute("");
	    			} else if (name.equalsIgnoreCase("MOBILE")) {
	    				if(Config.mIsForceTrans){
		    				list.add("003");
		    				WebsocketThread thread =new WebsocketThread(list);
		    				thread.start();
		    				Log.d(TAG, "MOBILE"); 
	    				}else{//不启用强制传输参数则将数据保存在本地。
	    					new MySaveTask(sb1.toString()).execute("");  
	    				}
	    				}
	    				
	    		}else{//在gps定位下没有开启网络则将数据保存在本地。
	    			
	    			new MySaveTask(sb1.toString()).execute("");  
	    		} 
				
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){//网络定位
				if (info != null && info.isAvailable()) {
	    			name = info.getTypeName(); // 拿到当前网络的名字
	    			info.getSubtypeName();
	    			if (name.equals("WIFI")) {
	    				list.add("002");
	    				sb1.append("002/");
	    				WebsocketThread thread =new WebsocketThread(list);
	    				thread.start();
	    				HttpClientThread httpthread =new HttpClientThread(url,list);
	    				httpthread.start();
	    				if(Config.mIsForceTrans){
	    					upLoadDate();
	    					Config.mIsForceTrans=false;
	    				}
	    				Log.d(TAG, "WIFI"); 
	    			} else if (name.equalsIgnoreCase("MOBILE")) {
	    				if(Config.mIsForceTrans){
		    				list.add("003");
		    				sb1.append("003/");
		    				WebsocketThread thread =new WebsocketThread(list);
		    				thread.start();
		    				Log.d(TAG, "MOBILE"); 
	    				}else{//不启用强制传输则将数据保存在本地，在有wifi环境下再上传。
	    					new MySaveTask(sb1.toString()).execute("");  
	    				}
	    				}
	    				
	    			} 

			}else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
               
               list.add("004");
               sb1.append("004/");
            } 
		}
	
	

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}
	
  }
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocClient.stop();
	}

	public void upLoadDate() {
		MyReadDate readdate = new MyReadDate();
		String data = readdate.saveDate();
		String[] arrData = data.split("/");
		for(int i=0;i<arrData.length;i++){
			ArrayList<String> dataList = new ArrayList<String>();
			String[] arrData1 = arrData[i].split(",");
			for(int j=0;j<arrData1.length;j++){
				dataList.add(arrData1[j]);
			}
			HttpClientThread httpthread =new HttpClientThread(url,dataList);
			httpthread.start();
		}
		
	}
}
