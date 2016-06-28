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
	private StringBuffer sb = new StringBuffer(256);//�����洢��Ҫ���浽���ص�����
	//����������Ҫ�����������˵����ݡ�
    private ArrayList<String> list=new ArrayList<String>();
    
    private ArrayList<WebsocketThread> listsocket = new ArrayList<WebsocketThread>();//�������������websocket����
	private ArrayList<HttpClientThread> listhttp  = new ArrayList<HttpClientThread>();
	private ArrayList<MySaveTask> listsaveTask    = new ArrayList<MySaveTask>();
	private ArrayList<MyReadDate> listreadTask    = new ArrayList<MyReadDate>();
    
	//�޸����ڸ�ʽ���浽���ݿ�
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	private LocationClient mLocClient;
	private String deviceId; //��ǰ�豸��MIEI�š�
	private ConnectivityManager connManager;//�������ӹ�����
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
		info = connManager.getActiveNetworkInfo(); // ������Ϣ

		// �������ڰٶ�api��LocationClient��
		mLocClient = new LocationClient(this);
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// ��ȡ�ֻ�һЩ��ʶ��Ϣ��
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// ��ȡ����MIEI���루���ֻ����ڣ�
		mcontext = LocateServices.this;
		deviceId = telephonyManager.getDeviceId();// ��ȡ�豸id��
		option = new LocationClientOption();// ����������ز���
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
	
	// ������ز���
	private void setLocationOption() {
		option.setOpenGps(Config.mBaiduGps);// �����Ƿ��gps��ʹ��gpsǰ�����û�Ӳ����gps��Ĭ���ǲ���gps�ġ�
		option.setCoorType("bd09ll");// �����������Ͱٶȼ�����������
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");// �����Ƿ�Ҫ���ص�ַ��Ϣ��Ĭ��Ϊ�޵�ַ��Ϣ��String ֵΪ
									// allʱ����ʾ���ص�ַ��Ϣ������ֵ����ʾ�����ص�ַ��Ϣ��
		option.setScanSpan(Config.msetTime);// ���ö�λģʽ��С��1����һ�ζ�λ;���ڵ���1����ʱ��λ
		// option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		// option.setPriority(LocationClientOption.NetWorkFirst);//������������,/�����ã�Ĭ����gps����
		option.disableCache(true);
		mLocClient.setLocOption(option);
		Log.d(TAG, "set LocOption");
	}
	/**
	  * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ��
	  */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);

			if (td != null) {
				listsocket.remove(td);
				td = null;
			}
			if (info != null && info.isConnected()) {// �������������²Ż�ȥ����websocket��������
				td = new WebsocketThread(list);
				td.start();
				listsocket.add(td);
			}
			if (Config.mIsDestroy) {
				onDestroy();
			}
			// ���������˸ı����ò�������Ҫ���¼������ò���
			if (Config.mIsSetOption) {
				setLocationOption();
				Log.d(TAG, "set LocOption again");
				Config.mIsSetOption = false;
			}
			/**
			 * ���û��ǿ�ƶ�λ�����´�Ͱ��հٶȶ�λ���������ж�λ ���������GPS�����»�����ѡ��λ������õķ��ض�λ���
			 */
			if (!Config.mIsOpenGps) {
				Log.d(TAG, "onReceive");
				if (location == null)
					return;
				sb.setLength(0);// ���StringBuffer������
				list.clear();// ���ArrayLis������
				dateString = null;
				dateString = formatter.format(new Date());
				list.add(dateString);// 1�ɼ�����
				sb.append(dateString + ",");
				sb.append(location.getLatitude() + ",");
				list.add(location.getLatitude() + "");// 2γ��
				sb.append(location.getLongitude() + ",");
				list.add(location.getLongitude() + "");// 3����
				list.add(deviceId);// 4�豸���к�
				sb.append(deviceId + ",");
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS��λ
					if (info != null && info.isConnected()) {
						name = info.getTypeName(); // �õ�GPS��λ����µ�����״����Ϊ�������ݴ���
						info.getSubtypeName();
						if (name.equals("WIFI")) {
							list.add("002");
							sb.append("002/");
							sendHttp(list);// ִ�з���httpЭ�鷽��
							Log.i(TAG, "WIFI");
						} else if (name.equalsIgnoreCase("MOBILE")) {
							Log.i(TAG, "A-GPS");
							list.add("003");
							sb.append("003/");
							savelocate(sb);// ִ�б��ر��淽��
							if (Config.mIsForceTrans) {
								readLocate();// ִ�ж�ȡ���ر����ļ�����
							}
						}

					} else {// ��gps��λ��û�п������������ݱ����ڱ��ء�
						list.add("001");// 5��λ��ʽ
						sb.append("001/");
						Log.d(TAG, "GPS");
						savelocate(sb);// ִ�б��ر��淽��
					}

				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// ���綨λ
					if (info != null && info.isConnected()) {
						name = info.getTypeName(); // �õ���ǰ���������
						info.getSubtypeName();
						if (name.equals("WIFI")) {
							sendHttp(list);// ����httpЭ��
							list.add("002");
							sb.append("002/");
							Log.d(TAG, "WIFI");
						} else if (name.equalsIgnoreCase("MOBILE")) {
							list.add("004");
							sb.append("004/");

							savelocate(sb);
							if (Config.mIsForceTrans) {// ǿ�ƴ���
								readLocate();// ִ�ж�ȡ���ر����ļ�����
							}
							Log.d(TAG, "��վ");
						}
					}

				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
					list.add("005");
					sb.append("005/");
					Log.d(TAG, "���߶�λ");
				}
			} else {
				GpsLocator();
			}
		}
	
	
     /**ǿ�ƶ�λ�����´�������û�����GPS��ǰ����ǿ��ʹ��GPS��λ*/
		private void GpsLocator() {

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Config.msetTime, 0, locationListener);

		}

		/** ��ȡgps��λ��Ϣ */
		private LocationListener locationListener = new LocationListener() {
			/**
			 * λ����Ϣ�仯ʱ����
			 */
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					sb.setLength(0);// ���StringBuffer������
					list.clear();// ���ArrayLis������
					dateString = null;
					dateString = formatter.format(new Date());
					list.add(dateString);// 1�ɼ�����
					sb.append(dateString + ",");
					sb.append(location.getLatitude() + ",");
					list.add(location.getLatitude() + "");// 2γ��
					sb.append(location.getLongitude() + ",");
					list.add(location.getLongitude() + "");// 3����
					list.add(deviceId);// 4�豸���к�
					sb.append(deviceId + ",");
					list.add("001");
					sb.append("001/");
					Log.i(TAG, "GPS");
					if (info != null && info.isAvailable()) {
						name = info.getTypeName(); // �õ�GPS��λ����µ�����״����Ϊ�������ݴ���
						info.getSubtypeName();
						if (name.equals("WIFI")) {
							sendHttp(list);// ִ�з���httpЭ�鷽��
						} else {
							savelocate(sb);
							if (Config.mIsForceTrans) {// ǿ�ƴ���
								readLocate();// ִ�ж�ȡ���ر����ļ�����
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
			 * GPS״̬�仯ʱ����
			 */
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				switch (status) {
				// GPS״̬Ϊ�ɼ�ʱ
				case LocationProvider.AVAILABLE:
					Log.i(TAG, "��ǰGPS״̬Ϊ�ɼ�״̬");
					break;
				// GPS״̬Ϊ��������ʱ
				case LocationProvider.OUT_OF_SERVICE:
					Log.i(TAG, "��ǰGPS״̬Ϊ��������״̬");
					break;
				// GPS״̬Ϊ��ͣ����ʱ
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					Log.i(TAG, "��ǰGPS״̬Ϊ��ͣ����״̬");
					break;
				}

			}

			/**
			 * GPS����ʱ����
			 */
			@Override
			public void onProviderEnabled(String provider) {

			}

			/**
			 * GPS����ʱ����
			 */
			@Override
			public void onProviderDisabled(String provider) {

			}

		};



		private void readLocate() {
			/** ���ݷ������Ƿ��´�ǿ�ƴ����������������ȡ�����ļ����߳� */
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
			/** ����http���ӷ������ݵ���������ʹ��list����������http�����߳� */
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
