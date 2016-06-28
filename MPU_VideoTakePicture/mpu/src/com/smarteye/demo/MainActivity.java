package com.smarteye.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera.CameraInfo;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.smarteye.demo.gps.GPSLocation;
import com.smarteye.mpu.MPUCameraHelper;
import com.smarteye.mpu.MPUCoreSDK;
import com.smarteye.mpu.MPUDefine;
import com.smarteye.mpu.R;
import com.smarteye.mpu.bean.GPSData;
import com.smarteye.mpu.bean.RegisterInfo;
import com.smarteye.mpu.bean.StorageInfo;
import com.smarteye.mpu.bean.WallTime;

public class MainActivity extends Activity implements LocationListener {

	@Override
	public void onLocationChanged(Location loc) {
		if (loc != null) {
			double latitude = loc.getLatitude();
			double longitude = loc.getLongitude();
			System.out.println("WGS����: " + latitude + "," + longitude);

			GPSData data = new GPSData();
			WallTime time = new WallTime();

			Calendar calendar = Calendar.getInstance();
			time.setDay((char) calendar.get(Calendar.DAY_OF_MONTH));
			time.setHour((char) calendar.get(Calendar.HOUR));
			time.setMinute((char) calendar.get(Calendar.MINUTE));
			time.setMonth((char) (calendar.get(Calendar.MONTH) + 1));
			time.setSecond((char) calendar.get(Calendar.SECOND));
			time.setYear((short) calendar.get(Calendar.YEAR));
			data.setTime(time);
			data.setLatitude((int) (latitude * 10000000));
			data.setLongitude((int) (longitude * 10000000));
			data.setOrientationState(1);
			data.setAntennaState(1);
			MPUCoreSDK.InputGPSData(data);
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	private static final String TAG = "MainActivity";
	private ArrayList<RegisterInfo> historyInfos = null;

	private boolean isLogin = true;
	private MPUCameraHelper m_cameraHelper;
	private EditText m_deviceIdText = null;

	private EditText m_deviceNameText = null;
	private long m_exitTime = 0;
	private Button m_hitoryButton = null;
	private EditText m_loginAliasText = null;
	private Button m_loginButton = null;
	private RadioButton m_radioButton1 = null;

	private RadioButton m_radioButton2 = null;
	private RadioButton m_radioButton3 = null;
	private RadioGroup m_radioGroup = null;
	private EditText m_serverAddrText = null;

	private EditText m_serverPortText = null;
	private SurfaceView m_surfaceView = null;

	private PowerManager powerManager = null;

	private CheckBox m_recordBox = null;

	private WakeLock wakeLock = null;
	private GPSLocation gps = null;

	private void InitialSDK() {
		Log.i(TAG, "Android MODEL: " + android.os.Build.MODEL);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOH, 352);
		MPUCoreSDK.SetSDKOptionString(MPUDefine.MPU_S_SYSTEM_MODEL,
				android.os.Build.MODEL);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_SYSTEM_APILEVEL,
				android.os.Build.VERSION.SDK_INT);
		MPUCoreSDK.SetSDKOptionString(MPUDefine.MPU_S_SYSTEM_MANUFACTURE,
				android.os.Build.MANUFACTURER);
		MPUCoreSDK.SetSDKOptionString(MPUDefine.MPU_S_SYSTEM_VERSION,
				android.os.Build.VERSION.RELEASE);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_SDK_MAINVERSION, 1);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_SDK_SUBVERSION, 4);
		MPUCoreSDK.SetSDKOptionString(MPUDefine.MPU_S_SDK_BUILDTIME,
				"2014-2-20 14:22:55");

		InitCodec();
		Intent startIntent = new Intent(this, MPUService.class);
		startService(startIntent);
	}

	private BMapManager mBMapManager;
	private LocationClient mLocClient;

	private void InitGPS() {
		gps = new GPSLocation(this);
		gps.openGPSSettings(this);
	}

	private void InitBaiduMap() {
		mBMapManager = new BMapManager(getApplicationContext());

		// ��һ��������API key,
		// �ڶ��������ǳ����¼���������������ͨ�������������Ȩ��֤����ȣ���Ҳ���Բ��������ص��ӿ�
		mBMapManager.init("AFHzvvcpsZnIbI42CqvwnamE",
				new MKGeneralListenerImpl());

		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(new BDLocationListenerImpl());

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��GPRS
		option.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(5000); // ���÷���λ����ļ��ʱ��Ϊ5000ms
		option.disableCache(false);// ��ֹ���û��涨λ
		// option.setPoiNumber(5); //��෵��POI����
		// option.setPoiDistance(1000); //poi��ѯ����
		// option.setPoiExtraInfo(true); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ

		mLocClient.setLocOption(option);
		mLocClient.start(); // ���ô˷�����ʼ��λ

		GeoPoint geoPt = new GeoPoint(31853088, 117197778);
		GeoPoint bundle = CoordinateConvert.fromWgs84ToBaidu(geoPt);
		System.out.println("------------ " + bundle.getLatitudeE6() + ","
				+ bundle.getLongitudeE6());
	}

	private void InitCodec() {
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOTS, 20000);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOBR, 700 * 1000);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOII, 1);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOKT, 20000);
	}

	public void onBackPressed() {
		if (System.currentTimeMillis() - m_exitTime > 2000) {
			Toast.makeText(this, "�ٰ�һ�η��ؼ��˳�", Toast.LENGTH_SHORT).show();
			m_exitTime = System.currentTimeMillis();
		} else {
			this.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InitialSDK();
		//InitBaiduMap();
		//InitGPS();

		setContentView(R.layout.activity_main);

		m_loginAliasText = (EditText) findViewById(R.id.history);
		m_hitoryButton = (Button) findViewById(R.id.arrow);
		m_serverAddrText = (EditText) findViewById(R.id.serveraddr);
		m_serverPortText = (EditText) findViewById(R.id.serverport);
		m_deviceNameText = (EditText) findViewById(R.id.devicename);
		m_deviceIdText = (EditText) findViewById(R.id.deviceid);

		m_radioGroup = (RadioGroup) findViewById(R.id.RG);
		m_radioButton1 = (RadioButton) findViewById(R.id.b1);
		m_radioButton2 = (RadioButton) findViewById(R.id.b2);
		m_radioButton3 = (RadioButton) findViewById(R.id.b3);

		m_recordBox = (CheckBox) findViewById(R.id.record);

		HistoryDatabase info = new HistoryDatabase(this);
		historyInfos = info.getAllRegisterInfo();
		info.close();

		if (historyInfos.size() > 0) {
			RegisterInfo loginInfo = historyInfos.get(0);
			m_loginAliasText.setText(loginInfo.getServerAliasName());
			m_serverAddrText.setText(loginInfo.getServerAddr());
			m_serverPortText.setText(String.valueOf(loginInfo.getServerPort()));
			m_deviceNameText.setText(loginInfo.getDeviceName());
			m_deviceIdText.setText(String.valueOf(loginInfo.getDeviceId()));
		} else {
			m_loginAliasText.setText("");
			m_serverAddrText.setText("");
			m_serverPortText.setText("");
			m_deviceNameText.setText("");
			m_deviceIdText.setText("");
		}

		m_surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		m_loginButton = (Button) findViewById(R.id.loginbtn);
		m_cameraHelper = new MPUCameraHelper();
		m_cameraHelper.SetContext(this);
		Log.i(TAG, "����ͷ�� " + m_cameraHelper.GetCameraNumber() + " ��");
		m_surfaceView.getHolder().addCallback(m_cameraHelper);

		m_radioButton3.setEnabled(false);

		m_loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				RegisterInfo registerInfo = new RegisterInfo();
				registerInfo.setServerAddr(m_serverAddrText.getText()
						.toString());
				registerInfo.setServerPort(Integer.valueOf(m_serverPortText
						.getText().toString()));
				registerInfo.setDeviceId(Integer.valueOf(m_deviceIdText
						.getText().toString()));
				registerInfo.setDeviceName(m_deviceNameText.getText()
						.toString());
				registerInfo.setServerAliasName(m_loginAliasText.getText()
						.toString());
				if (registerInfo.getServerAliasName() == null
						|| registerInfo.getServerAliasName().equals("")) {
					registerInfo.setServerAliasName(registerInfo
							.getServerAddr());
				}
				if (isLogin) {
					m_loginButton.setText("ע��");
					isLogin = false;
					m_loginAliasText.setEnabled(false);
					m_loginAliasText.setBackgroundColor(Color.GRAY);
					m_serverAddrText.setEnabled(false);
					m_serverAddrText.setBackgroundColor(Color.GRAY);
					m_serverPortText.setEnabled(false);
					m_serverPortText.setBackgroundColor(Color.GRAY);
					m_deviceIdText.setEnabled(false);
					m_deviceIdText.setBackgroundColor(Color.GRAY);
					m_deviceNameText.setEnabled(false);
					m_deviceNameText.setBackgroundColor(Color.GRAY);
					m_radioButton1.setEnabled(false);
					m_radioButton2.setEnabled(false);

					boolean isFound = false;
					for (RegisterInfo info : historyInfos) {
						if (info.getServerAliasName().equals(
								registerInfo.getServerAliasName())) {
							isFound = true;
							break;
						}
					}
					HistoryDatabase db = new HistoryDatabase(MainActivity.this);
					if (isFound) {
						db.update(registerInfo);
					} else {
						db.insert(registerInfo);
					}
					historyInfos = db.getAllRegisterInfo();
					db.close();

					MPUCoreSDK.Register(registerInfo);
				} else if (!isLogin) {
					Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("ȷ��ע����");
					builder.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									m_loginButton.setText("��¼");
									isLogin = true;
									m_loginAliasText.setEnabled(true);
									m_loginAliasText
											.setBackgroundColor(Color.WHITE);
									m_serverAddrText.setEnabled(true);
									m_serverAddrText
											.setBackgroundColor(Color.WHITE);
									m_serverPortText.setEnabled(true);
									m_serverPortText
											.setBackgroundColor(Color.WHITE);
									m_deviceIdText.setEnabled(true);
									m_deviceIdText
											.setBackgroundColor(Color.WHITE);
									m_deviceNameText.setEnabled(true);
									m_deviceNameText
											.setBackgroundColor(Color.WHITE);
									m_radioButton1.setEnabled(true);
									m_radioButton2.setEnabled(true);
								}
							});
					builder.setNegativeButton("ȡ��", null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});

		m_hitoryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				HistoryDatabase info = new HistoryDatabase(MainActivity.this);
				historyInfos = info.getAllRegisterInfo();
				info.close();
				Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("��ʷ��½��¼");
				String[] list = new String[historyInfos.size()];
				for (int i = 0; i < list.length; i++) {
					RegisterInfo loginInfo = historyInfos.get(i);
					list[i] = new String(loginInfo.getServerAliasName());
				}
				builder.setSingleChoiceItems(list, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								RegisterInfo loginInfo = historyInfos
										.get(which);
								m_loginAliasText.setText(loginInfo
										.getServerAliasName());
								m_serverAddrText.setText(loginInfo
										.getServerAddr());
								m_serverPortText.setText(String
										.valueOf(loginInfo.getServerPort()));
								m_deviceNameText.setText(loginInfo
										.getDeviceName());
								m_deviceIdText.setText(String.valueOf(loginInfo
										.getDeviceId()));
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("ȡ��", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		m_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				System.out.println("==================== onCheckedChanged");
				if (checkedId == m_radioButton1.getId()) {
					m_cameraHelper.SelectCamera(CameraInfo.CAMERA_FACING_BACK);
				}
				if (checkedId == m_radioButton2.getId()) {
					m_cameraHelper.SelectCamera(CameraInfo.CAMERA_FACING_FRONT);
				}
				if (checkedId == m_radioButton3.getId()) {
					MPUCoreSDK.SetSDKOptionInt(
							MPUDefine.MPU_I_USERSTATE_CAMERA_INDEX,
							MPUDefine.MPU_CAMERA_EXTERNAL_INDEX);
				}
			}

		});

		m_recordBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						StorageInfo info = new StorageInfo();

						try {
							File file = new File(filePathString);
							if (!file.exists())
								file.mkdir();
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						if (isChecked) {
							info.setFileName(fileNameString);
							info.setMediaType(MPUDefine.MPU_RECORD_MEDIA_AUDIO
									| MPUDefine.MPU_RECORD_MEDIA_VIDEO);
							info.setStatus("start");
							info.setFilePath(filePathString);
							info.setFileLenInSeconds(Integer
									.valueOf(secondsString));
							MPUCoreSDK.SetSDKOptionInt(
									MPUDefine.MPU_I_RECORD_FILESECONDS,
									Integer.valueOf(secondsString));
							MPUCoreSDK.Storage(info);
						} else {
							info.setStatus("stop");
							MPUCoreSDK.Storage(info);
						}
					}
				});

		this.powerManager = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		this.wakeLock = this.powerManager.newWakeLock(
				PowerManager.FULL_WAKE_LOCK, "My Lock");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		// �˳�Ӧ�õ���BMapManager��destroy()����
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}

		// �˳�ʱ���ٶ�λ
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onDestroy();
		/*
		 * if (m_cameraHelper != null) { m_cameraHelper.releaseCamera(); }
		 */
	}

	EditText filePath = null;
	EditText fileName = null;
	EditText seconds = null;
	String filePathString = "/sdcard/MPU/";
	String fileNameString = "du.mkv";
	String secondsString = "60";

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings: {
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.dialog,
					(ViewGroup) findViewById(R.id.dialog));
			filePath = (EditText) layout.findViewById(R.id.filepath);
			fileName = (EditText) layout.findViewById(R.id.filename);
			seconds = (EditText) layout.findViewById(R.id.seconds);
			filePath.setText(filePathString);
			fileName.setText(fileNameString);
			seconds.setText(secondsString);
			new AlertDialog.Builder(this)
					.setTitle("����")
					.setView(layout)
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									filePathString = filePath.getText()
											.toString();
									fileNameString = fileName.getText()
											.toString();
									secondsString = seconds.getText()
											.toString();
									System.out.println(filePathString
											+ fileNameString);
									try {
										File file = new File(filePathString);
										if (!file.exists())
											file.mkdir();
									} catch (Exception exception) {
										exception.printStackTrace();
									}
								}
							}).setNegativeButton("ȡ��", null).show();
		}
			break;
		case R.id.exit: {
			stopService(new Intent(MainActivity.this, MPUService.class));
			android.os.Process.killProcess(android.os.Process.myPid());
		}
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.wakeLock.release();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.wakeLock.acquire();
	}

	public class MKGeneralListenerImpl implements MKGeneralListener {

		/**
		 * һЩ����״̬�Ĵ�����ص�����
		 */
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				showToast("���������������");
			}
		}

		/**
		 * ��Ȩ�����ʱ����õĻص�����
		 */
		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				showToast("API KEY����, ���飡");
			}
		}

	}

	private Toast mToast;

	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public class BDLocationListenerImpl implements BDLocationListener {

		/**
		 * �����첽���صĶ�λ�����������BDLocation���Ͳ���
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			GPSData data = new GPSData();
			WallTime time = new WallTime();

			Calendar calendar = Calendar.getInstance();
			time.setDay((char) calendar.get(Calendar.DAY_OF_MONTH));
			time.setHour((char) calendar.get(Calendar.HOUR));
			time.setMinute((char) calendar.get(Calendar.MINUTE));
			time.setMonth((char) (calendar.get(Calendar.MONTH) + 1));
			time.setSecond((char) calendar.get(Calendar.SECOND));
			time.setYear((short) calendar.get(Calendar.YEAR));
			data.setTime(time);
			data.setLatitude((int) (location.getLatitude() * 10000000));
			data.setLongitude((int) (location.getLongitude() * 10000000));
			data.setOrientationState(1);
			data.setAntennaState(1);
			System.out.println("�ٶ�����: " + location.getLatitude() + ","
					+ location.getLongitude());
			//MPUCoreSDK.InputGPSData(data);
		}

		/**
		 * �����첽���ص�POI��ѯ�����������BDLocation���Ͳ���
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}
}
