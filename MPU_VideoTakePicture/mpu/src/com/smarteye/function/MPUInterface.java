package com.smarteye.function;

import com.smarteye.mpu.MPUCameraHelper;
import com.smarteye.mpu.MPUCoreSDK;
import com.smarteye.mpu.MPUDefine;
import com.smarteye.mpu.bean.RegisterInfo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MPUInterface{

	private final String     TAG = "MPUInterface";
	
	private static MPUInterface sMPUInterface = null;		
	private SurfaceHolder        mCameraHolder;
	private Context              mParentContext;
	private MPUCameraHelper      m_cameraHelper = null;
	
	//创建Native实例
    public static MPUInterface getInstance(Context context) {
		synchronized (MPUInterface.class) {
			if(sMPUInterface == null){
				sMPUInterface = new MPUInterface(context);
			}
		}
		return sMPUInterface;
	}
    
    public MPUInterface(Context context) {
		super();
		
		//保存消息处理任务句柄
		this.mParentContext = context;
		
		mCameraHolder = null;
		
		InitialSDK();
		
		m_cameraHelper = new MPUCameraHelper();
		m_cameraHelper.SetContext(this.mParentContext);
		
		Log.i(TAG, "摄像头有 " + m_cameraHelper.GetCameraNumber() + " 个");
	}
    
    public void SetDisplay(SurfaceView view)
	{			
		mCameraHolder = view.getHolder();
		mCameraHolder.addCallback((SurfaceHolder.Callback)m_cameraHelper);
	}    
	
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
//		Intent startIntent = new Intent(mParentContext,MPUService.class);
//		mParentContext.startService(startIntent);
	}
	
	private void InitCodec() {
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOTS, 20000);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOBR, 300 * 1000);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOII, 1);
		MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOKT, 20000);
	}

	public int Login(String strServerAddr, String strServerPort,
			         String strDeviceName, String strDeviceID,
			         String strDeviceAlias){
		RegisterInfo registerInfo = new RegisterInfo();
		registerInfo.setServerAddr(strServerAddr);
		registerInfo.setServerPort(Integer.valueOf(strServerPort));
		registerInfo.setDeviceId(Integer.valueOf(strDeviceID));
		registerInfo.setDeviceName(strDeviceName);
		registerInfo.setServerAliasName(strDeviceAlias);
		
		if((strDeviceAlias == null) || (strDeviceAlias == ""))
		{
			registerInfo.setServerAliasName(registerInfo.getServerAddr());	
		}
		
		MPUCoreSDK.Register(registerInfo);
		
		return 1;
	}	
	
	public void Logout(){
		
}
}
