package com.saiteng.smarteyempu.common;

import android.content.Context;
import android.os.Handler;

public class Config {
	public static boolean mStop=false;//是否是手动停止录像
	public static boolean isRecording=false;
	public static Handler mhandler;
	protected static boolean mIsAuth=false;
	protected static boolean IsConn=false;
	public static Handler mStartHandler;
	
	public static final String TAG = "MPU" ;	
	public static final int SERVICE_MESSAGE_EXIT                = 0x1001;
	public static final int SERVICE_MESSAGE_unregister          = 0x0000;
	public static final int SERVICE_MESSAGE_DarkScreen          = 0x0003;
	public static final int SERVICE_MESSAGE_NoDarkScreen        = 0x0002;
	public static final int SERVICE_MESSAGE_TAKEPICTRUE         = 0x0001;
	public static final int SERVICE_MESSAGE_REGISTER            = 0x1002;
	public static final int SERVICE_MESSAGE_START_RECORD        = 0x1003;
	public static final int SERVICE_MESSAGE_STOP_RECORD         = 0x1004; 
	public static final int SERVICE_MESSAGE_CHANGE_CAMERA       = 0x1005;
	public static final int SERVICE_MESSAGE_LED_SWITCH          = 0x1006;
	public static final int SERVICE_MESSAGE_AUTO_FOCUS          = 0x1007;
	public static final int SERVICE_MESSAGE_ZOOM_DOWN           = 0x1008;
	public static final int SERVICE_MESSAGE_ZOOM_UP             = 0x1009;
	public static final int SERVICE_MESSAGE_AUDIO_SWITCH        = 0x1010;
	public static final int SERVICE_MESSAGE_CHANGE_ZOOM         = 0x1011;
	public static final int SEND_MESSAGE_VISIBILITY_SEEKBAR     = 0x1012;
	public static final int SEND_MESSAGE_REFRESH_FRAME_RATE     = 0x1013;
	public static String  mStringServerAddr          = "211.144.85.109";
	public static String  mStringServerPort          = "9702";
	public static String  mStringDeviceName          = "Leo_Note5()";
	public static String  mStringDeviceID            = "975580";
	public static String  mStringDeviceAlias         = "Leo_Note5";
	public static boolean mIsLogin                   = false;
	public static boolean mIsAudioUpload             = false;
	public static boolean mIsLocationUpload          = false;
	public static boolean mVideoSwitch               = false;
	public static int     mVideoQuality              = 0;
	public static int     mStatus                    =-1;
	
	//调整预览界面的大小
	public static int viewWidth = 980;
	public static int viewHeight = 1160;
//	public static int viewWidth = 700;
//	public static int viewHeight = 860;
	
	public static boolean mIsBackCameraFirst = false; //判断启动程序时是否开启后置摄像头
	
	//请根据手机摄像头支持的分辨率逐一调整
	public static int     mBackCIFVideoWidth = 352;
	public static int     mBackCIFVideoHeight = 288;
	public static int     mBackCIFVideoBaudrate = 550 * 1000;
	public static int     mBackCIFVideoFrameInterval = 1;
	
	public static int     mBackVGAVideoWidth = 640;
	public static int     mBackVGAVideoHeight = 480;
	public static int     mBackVGAVideoBaudrate = 900 * 1000;
	public static int     mBackVGAVideoFrameInterval = 1;
	
	public static int     mBackD1VideoWidth = 960;
	public static int     mBackD1VideoHeight = 720;
	public static int     mBackD1VideoBaudrate = 1400 * 1000;
	public static int     mBackD1VideoFrameInterval = 1;
	
	public static int     mBack720PVideoWidth = 1280;
	public static int     mBack720PVideoHeight = 720;
	public static int     mBack720PVideoBaudrate = 1800 * 1000;
	public static int     mBack720PVideoFrameInterval = 1;
	
	
	
	public static int     mFrontCIFVideoWidth = 352;
	public static int     mFrontCIFVideoHeight = 288;
	public static int     mFrontCIFVideoBaudrate = 650 * 1000;
	public static int     mFrontCIFVideoFrameInterval = 1;
	
	public static int     mFrontVGAVideoWidth = 640;
	public static int     mFrontVGAVideoHeight = 480;
	public static int     mFrontVGAVideoBaudrate = 900 * 1000;
	public static int     mFrontVGAVideoFrameInterval = 1;
	
	public static int     mFrontD1VideoWidth = 960;
	public static int     mFrontD1VideoHeight = 720;
	public static int     mFrontD1VideoBaudrate = 1400 * 1000;
	public static int     mFrontD1VideoFrameInterval = 1;
	
	public static int     mFront720PVideoWidth = 1280;
	public static int     mFront720PVideoHeight = 720;
	public static int     mFront720PVideoBaudrate = 1800 * 1000;
	public static int     mFront720PVideoFrameInterval = 1;
	
	public static int     mVideoHeight = 0;
	public static int     mVideoWidth  = 0;
	public static int     mVideoBaudrate = 500 * 1000;
	public static int     mVideoFrameInterval = 1;
	
	
	public static final int     FRAME_RATE_REFRESH_TIME  = 1000;
	public static final int     RECORD_DURATION_TIME     = 15 * 60;
	
	public static Context mMian             =null;
}
