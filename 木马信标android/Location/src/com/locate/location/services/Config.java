package com.locate.location.services;

public class Config {
	public static Boolean mIsForceLocate=false;
    public static Boolean mIsForceTrans=false;
    public static int msetTime =20000;//初始定位时间间隔20秒。
    public static boolean mIsOpenGps=false;
    public static boolean mIsDisconn=false;//设置客户端是否主动断开webSocket连接.默认不断开
    public static boolean mIsSetOption=false;//是否要重新加载设置
    public static String mURL = "http://192.168.0.63:8080/LocationWebsocket/login";
    public static String mWebsock = "ws://192.168.0.63:8080/LocationWebsocket/chat.ws?username=";
    public static boolean mIsDestroy=false;
	public static boolean mBaiduGps   =false;//使用百度定位服务时的GPS开关
}
