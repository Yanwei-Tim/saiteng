package com.saiteng.smartlisten.service;

import com.saiteng.smartlisten.process.AVDialogProcess;
import com.saiteng.smartlisten.process.ProcessManager;
import com.smarteye.adapter.BVPU_ServerParam;
import com.smarteye.bean.JNIMessage;
import com.smarteye.coresdk.AudioHelper;
import com.smarteye.coresdk.BVPU;
import com.smarteye.coresdk.JNIMessageEvent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MPUListenService extends Service implements JNIMessageEvent{
	private BVPU_ServerParam param;

	private ProcessManager mProcessManager = new ProcessManager();

	private MPUListenApplication mpuListenApplication;
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
		param.szDeviceName="MX4";
		param.szChannelName="video";
		param.iDeviceID=677791796;
		param.iMediaDir=12;
		param. bGPSEnable=1;
		
	}
	public void login(BVPU_ServerParam param) {
		BVPU.InitCoreSDK();
		BVPU.setMessageEvent(this);
		BVPU.Initialize();
		AudioHelper.GetAudioHelper().SetContext(this);
		AudioHelper.GetAudioHelper().InitAudioPlayer(1);
		AudioHelper.GetAudioHelper().InitAudioRecorder(1);
		JNIMessage message = new JNIMessage();
		message.addStrParam(JNIMessage.Key.JNIMESSAGE_KEY_S_ID.getName(),
				"login");
		message.setObj(param);
		BVPU.PostMessageToNative(message);
		
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mProcessManager.addProcess(new AVDialogProcess());
	}
	@Override
	public void onMessageFromNative(JNIMessage message) {
		
		
	}

}
