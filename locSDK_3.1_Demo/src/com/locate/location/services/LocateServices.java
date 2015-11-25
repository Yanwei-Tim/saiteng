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
	private String deviceId; //��ǰ�豸��MIEI�š�
	private ConnectivityManager connManager;//�������ӹ�����
    private NetworkInfo info;
    private String name;
  //�������ݷ������ĵ�ַ��
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
		info = connManager.getActiveNetworkInfo(); // ������Ϣ
		//�������ڰٶ�api��LocationClient��
		mLocClient =new LocationClient( this );
		//��ȡ�ֻ�һЩ��ʶ��Ϣ��
		TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService( Context.TELEPHONY_SERVICE); 
		//��ȡ����MIEI���루���ֻ����ڣ�
		
		 deviceId = telephonyManager.getDeviceId();//��ȡ�豸id��
		 option = new LocationClientOption();//����������ز���
		 final IntentFilter filter = new IntentFilter();  
		    // ��Ļ�����㲥  
		 filter.addAction(Intent.ACTION_SCREEN_OFF);  
		    // ��Ļ�����㲥  
		 filter.addAction(Intent.ACTION_SCREEN_ON);  
         /**
           * ��̬ע����Ļ�����͹رչ㲥�¼�
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
	
	//������ز���
			private void setLocationOption(){
				option.setOpenGps(Config.mIsOpenGps);//�����Ƿ��gps��ʹ��gpsǰ�����û�Ӳ����gps��Ĭ���ǲ���gps�ġ�
				option.setCoorType("bd09ll");//�����������Ͱٶȼ����������͡�
				option.setServiceName("com.baidu.location.service_v2.9");
				option.setAddrType("all");//�����Ƿ�Ҫ���ص�ַ��Ϣ��Ĭ��Ϊ�޵�ַ��Ϣ��String ֵΪ allʱ����ʾ���ص�ַ��Ϣ������ֵ����ʾ�����ص�ַ��Ϣ��
				option.setScanSpan(Config.msetTime);//���ö�λģʽ��С��1����һ�ζ�λ;���ڵ���1����ʱ��λ
				//option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
				//option.setPriority(LocationClientOption.NetWorkFirst);//������������,/�����ã�Ĭ����gps����
				option.disableCache(true);		
				mLocClient.setLocOption(option);
				Log.d(TAG, "set LocOption");
			}
	/**
	  * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ��
	  */
	public class MyLocationListenner implements BDLocationListener {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onReceiveLocation(BDLocation location) {
			
			//���������˸ı����ò�������Ҫ���¼������ò���
		    if(Config.mIsSetOption){
		    	setLocationOption(); 
		    	Log.d(TAG, "set LocOption again"); 
		    	Config.mIsSetOption=false;
  		    }
			Log.d(TAG, "onReceive"); 
			if (location == null)
				return ;
			StringBuffer sb1 = new StringBuffer(256);//�����洢��Ҫ���浽���ص�����
			//����������Ҫ�����������˵����ݡ�
			 ArrayList<String> list=new ArrayList<String>();
			//�޸����ڸ�ʽ���浽���ݿ�
			 SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			 String dateString = formatter.format(new Date());
			list.add(dateString);//1�ɼ�����
			sb1.append(dateString);
			sb1.append(location.getLatitude()+",");
			list.add(location.getLatitude()+"");//2γ��
			sb1.append(location.getLongitude()+",");
			list.add(location.getLongitude()+"");//3����
			list.add(deviceId);//4�豸���к�
			sb1.append(deviceId+",");
			if (location.getLocType() == BDLocation.TypeGpsLocation){//GPS��λ
				list.add("001");//5��λ��ʽ
				sb1.append("001/");
				Log.d(TAG, "GPS"); 
				if (info != null && info.isAvailable()) {
	    			name = info.getTypeName(); // �õ�GPS��λ����µ�����״����Ϊ�������ݴ���
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
	    				}else{//������ǿ�ƴ�����������ݱ����ڱ��ء�
	    					new MySaveTask(sb1.toString()).execute("");  
	    				}
	    				}
	    				
	    		}else{//��gps��λ��û�п������������ݱ����ڱ��ء�
	    			
	    			new MySaveTask(sb1.toString()).execute("");  
	    		} 
				
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){//���綨λ
				if (info != null && info.isAvailable()) {
	    			name = info.getTypeName(); // �õ���ǰ���������
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
	    				}else{//������ǿ�ƴ��������ݱ����ڱ��أ�����wifi���������ϴ���
	    					new MySaveTask(sb1.toString()).execute("");  
	    				}
	    				}
	    				
	    			} 

			}else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
               
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
