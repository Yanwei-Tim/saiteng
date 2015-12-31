package com.saiteng.smartlisten;

import com.saiteng.smartlisten.service.MPUListenApplication;
import com.saiteng.smartlisten.service.MPUListenService;
import com.smarteye.adapter.BVPU_ServerParam;

import android.app.Activity;
import android.os.Bundle;

public class LoginActivity extends Activity{
	private BVPU_ServerParam param;

	private MPUListenService mpuListenService;
	private MPUListenApplication mpuListenApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		mpuListenService=new MPUListenService();
		mpuListenApplication=new MPUListenApplication();
		BVPU_ServerParam param = mpuListenApplication.getServerParam();
		mpuListenService.login(param);
	}
	public void initData(){
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

}
