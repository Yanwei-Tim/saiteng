package com.smarteye.function;

import java.io.File;
import java.util.Calendar;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.saiteng.smarteyempu.common.Config;
import com.smarteye.mpu.MPUCameraHelper;
import com.smarteye.mpu.MPUCoreSDK;
import com.smarteye.mpu.MPUDefine;
import com.smarteye.mpu.bean.GPSData;
import com.smarteye.mpu.bean.RegisterInfo;
import com.smarteye.mpu.bean.StorageInfo;
import com.smarteye.mpu.bean.WallTime;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera.CameraInfo;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class MPUInterface implements LocationListener {

	private final String TAG = "MPUInterface";

	private static MPUInterface sMPUInterface = null;
	private SurfaceHolder mCameraHolder;
	private Context mParentContext;
	private MPUCameraHelper m_cameraHelper = null;

	private GPSLocation mGPS = null;
	private BMapManager mBMapManager = null;
	private LocationClient mLocClient = null;

	private Intent mMPUServiceIntent = null;

	private int mVideoWidth = 352;
	private int mVideoHeight = 288;

	private boolean mIsBackCamera = true;

	private boolean mIsUploadLocation = true;
	private boolean mIsUploadAudio = true;

	private int mVideoBaudRate = 800 * 1000;
	private int mVideoIFrameInterval = 1;

	// 创建Native实例
	public static MPUInterface getInstance(Context context) {
		synchronized (MPUInterface.class) {
			if (sMPUInterface == null) {
				sMPUInterface = new MPUInterface(context);
			}
		}
		return sMPUInterface;
	}

	public MPUInterface(Context context) {
		super();

		// 保存消息处理任务句柄
		this.mParentContext = context;

		mCameraHolder = null;

		m_cameraHelper = new MPUCameraHelper();
		m_cameraHelper.SetContext(this.mParentContext);

		Log.i(TAG, "摄像头有 " + m_cameraHelper.GetCameraNumber() + " 个");
	}

	public void Destroy() {
		Log.i(Config.TAG, "MPUInterface Destroy");

		// 退出应用调用BMapManager的destroy()方法
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}

		// 退出时销毁定位
		if (mLocClient != null) {
			mLocClient.stop();
		}

		mParentContext.stopService(mMPUServiceIntent);
		mMPUServiceIntent = null;

		sMPUInterface = null;
	}

	public void Start() {

		mVideoWidth = m_cameraHelper.getPreviewWidth();
		mVideoHeight = m_cameraHelper.getPreviewHeight();

		InitialSDK();

		if (mIsUploadLocation == true) {
			InitGPS();
			InitBaiduMap();
		}
	}

	public void SetDisplay(SurfaceView view) {
		mCameraHolder = view.getHolder();
		mCameraHolder.addCallback((SurfaceHolder.Callback)m_cameraHelper);
	}    
	
    public void SetVideoSize(boolean isBackCamera, int iWidth, int iHeight)
	{	      	
    	if(isBackCamera){
    		if(m_cameraHelper != null){
        		m_cameraHelper.SettingPreviewSize(CameraInfo.CAMERA_FACING_BACK, iWidth, iHeight);
        	}
    		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_CAMERA_INDEX, MPUDefine.MPU_CAMERA_BACK_INDEX);
    	}else{
    		if(m_cameraHelper != null){
        		m_cameraHelper.SettingPreviewSize(CameraInfo.CAMERA_FACING_FRONT, iWidth, iHeight);
        	}
    		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_CAMERA_INDEX, MPUDefine.MPU_CAMERA_FRONT_INDEX);
    	}
	}  
    
    public void SetUploadLocation(boolean bIsEnable){
    	mIsUploadLocation = bIsEnable;
    }
    
    public void SetUploadAudio(boolean bIsEnable){
    	mIsUploadAudio = bIsEnable;
    }

    public void SetVideoCodec(int iBaudrate, int iFrameInterval){
    	mVideoBaudRate = iBaudrate;
    	mVideoIFrameInterval = iFrameInterval;
    }
    
    public void SelectCamera(boolean bIsBackCamera)
	{			
    	if(m_cameraHelper != null){
    		if(bIsBackCamera){
    			m_cameraHelper.SelectCamera(CameraInfo.CAMERA_FACING_BACK);
    		}else{
    			m_cameraHelper.SelectCamera(CameraInfo.CAMERA_FACING_FRONT);
    		}
    	} 
	} 
    
	public void ChangeZoomValue(int curZoomValue){
		if(m_cameraHelper != null){
			m_cameraHelper.ChangeZoom(curZoomValue);
		}
	}

	public int GetFrameCount() {
		return MPUCoreSDK.GetFrameCount();
	}

	public int GetUploadCount() {
		return MPUCoreSDK.GetUploadCount();
	}

	private void InitialSDK() {
		Log.i(TAG, "Android MODEL: " + android.os.Build.MODEL);

		Log.i(TAG, "Video width: " + mVideoWidth + " Height: " + mVideoHeight);

		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOH, mVideoHeight);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOW, mVideoWidth);

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

		mMPUServiceIntent = new Intent(mParentContext, MPUService.class);
		mParentContext.startService(mMPUServiceIntent);
	}

	private void InitCodec() {
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOTS, 20000);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOBR,
				mVideoBaudRate);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOII,
				mVideoIFrameInterval);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOKT, 20000);
	}

	public int Login(String strServerAddr, String strServerPort,
			String strDeviceName, String strDeviceID, String strDeviceAlias) {
		RegisterInfo registerInfo = new RegisterInfo();
		registerInfo.setServerAddr(strServerAddr);
		registerInfo.setServerPort(Integer.valueOf(strServerPort));
		registerInfo.setDeviceId(Integer.valueOf(strDeviceID));
		registerInfo.setDeviceName(strDeviceName);
		registerInfo.setServerAliasName(strDeviceAlias);

		if ((strDeviceAlias == null) || (strDeviceAlias == "")) {
			registerInfo.setServerAliasName(registerInfo.getServerAddr());
		}

		MPUCoreSDK.Register(registerInfo);

		return 1;
	}

	public void Logout() {
	}

	public void ZoonDown() {

	}

	public void ZoonUp() {

	}

	public void AutoFocus() {

	}

	public void StartRecord(String strFilePath, String strFileName,
			String strSeconds) {
		StorageInfo info = new StorageInfo();

		try {
			File file = new File(strFilePath);
			if (!file.exists())
				file.mkdir();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		info.setFileName(strFileName);
		info.setMediaType(MPUDefine.MPU_RECORD_MEDIA_AUDIO
				| MPUDefine.MPU_RECORD_MEDIA_VIDEO);
		info.setStatus("start");
		info.setFilePath(strFilePath);
		info.setFileLenInSeconds(Integer.valueOf(strSeconds));
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_FILESECONDS,
				Integer.valueOf(strSeconds));
		MPUCoreSDK.Storage(info);
	}

	public void StopRecord() {
		StorageInfo info = new StorageInfo();
		info.setStatus("stop");
		MPUCoreSDK.Storage(info);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
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
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	private void InitGPS() {
		mGPS = new GPSLocation(this.mParentContext);
		mGPS.openGPSSettings(this);
	}

	private void InitBaiduMap() {
		mBMapManager = new BMapManager(this.mParentContext);

		// 第一个参数是API key,
		// 第二个参数是常用事件监听，用来处理通常的网络错误，授权验证错误等，你也可以不添加这个回调接口
		mBMapManager.init("AFHzvvcpsZnIbI42CqvwnamE",
				new MKGeneralListenerImpl());

		mLocClient = new LocationClient(this.mParentContext);
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

	private Toast mToast;

	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(this.mParentContext, msg,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
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
			// MPUCoreSDK.InputGPSData(data);
		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}
}
