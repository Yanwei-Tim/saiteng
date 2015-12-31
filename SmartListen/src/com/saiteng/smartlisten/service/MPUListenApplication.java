package com.saiteng.smartlisten.service;

import com.smarteye.adapter.BVPU_ServerParam;

import android.app.Application;

public class MPUListenApplication extends Application{
	private BVPU_ServerParam serverParam = new BVPU_ServerParam();
	
	public BVPU_ServerParam getServerParam() {
		return serverParam;
	}

}
