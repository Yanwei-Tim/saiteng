package com.saiteng.smarteyempu.common;

public class Config {
	public static final String TAG = "MPU" ;	
	public static final int SERVICE_MESSAGE_EXIT                = 0x1001;
	
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
	public static String  mStringServerPort          = "9910";
	public static String  mStringDeviceName          = "Leo_Note3";
	public static String  mStringDeviceID            = "975580";
	public static String  mStringDeviceAlias         = "Leo_Note3";
	public static boolean mIsAudioUpload             = false;
	public static boolean mIsLocationUpload          = false;
	public static boolean mVideoSwitch               = false;
	public static int     mVideoQuality              = 0;
	public static int     mStatus                    =-1;
	
	//调整预览界面的大小
	public static int viewWidth = 880;
	public static int viewHeight = 1060;
	
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
}
