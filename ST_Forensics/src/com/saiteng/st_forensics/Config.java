package com.saiteng.st_forensics;


import android.os.Handler;

public class Config {

	public static boolean mStop=false;//是否是手动停止录像
	public static Handler mhandler=null;
	public static boolean mIsAuth=false;
	public static boolean IsConn=false;
	public static boolean recording=false;
	public static boolean stoping=false;
	public static boolean defaultcamera=false; //默认前置摄像头
	public static boolean mstartPreview = false;//开启预览
	public static boolean mproofread    =false;//开启实时校队
	public static int medumValue = 19;//触发手机震动的阀值，越大越难触发
	public static Handler madpterhandler=null;
	public static int recordtime = 15;
	public static boolean mIsencryption=false;//是否加密保存
	public static String password ="123456";
}