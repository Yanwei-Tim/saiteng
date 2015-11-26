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
			System.out.println("WGS坐标: " + latitude + "," + longitude);

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

		// 第一个参数是API key,
		// 第二个参数是常用事件监听，用来处理通常的网络错误，授权验证错误等，你也可以不添加这个回调接口
		mBMapManager.init("AFHzvvcpsZnIbI42CqvwnamE",
				new MKGeneralListenerImpl());

		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(new BDLocationListenerImpl());

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.disableCache(false);// 禁止启用缓存定位
		// option.setPoiNumber(5); //最多返回POI个数
		// option.setPoiDistance(1000); //poi查询距离
		// option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息

		mLocClient.setLocOption(option);
		mLocClient.start(); // 调用此方法开始定位

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
			Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
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
		Log.i(TAG, "摄像头有 " + m_cameraHelper.GetCameraNumber() + " 个");
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
					m_loginButton.setText("注销");
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
					builder.setTitle("确认注销？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									m_loginButton.setText("登录");
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
					builder.setNegativeButton("取消", null);
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
				builder.setTitle("历史登陆记录");
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
				builder.setNegativeButton("取消", null);
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
		// 退出应用调用BMapManager的destroy()方法
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}

		// 退出时销毁定位
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
					.setTitle("设置")
					.setView(layout)
					.setPositiveButton("确定",
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
							}).setNegativeButton("取消", null).show();
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
		 * 一些网络状态的错误处理回调函数
		 */
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				showToast("您的网络出错啦！");
			}
		}

		/**
		 * 授权错误的时候调用的回调函数
		 */
		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				showToast("API KEY错误, 请检查！");
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
		 * 接收异步返回的定位结果，参数是BDLocation类型参数
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
			System.out.println("百度坐标: " + location.getLatitude() + ","
					+ location.getLongitude());
			//MPUCoreSDK.InputGPSData(data);
		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}
}
