package com.locate.location.services;

public class Config {
	public static Boolean mIsForceLocate=false;
    public static Boolean mIsForceTrans=false;
    public static int msetTime =20000;//��ʼ��λʱ����20�롣
    public static boolean mIsOpenGps=false;
    public static boolean mIsDisconn=false;//���ÿͻ����Ƿ������Ͽ�webSocket����.Ĭ�ϲ��Ͽ�
    public static boolean mIsSetOption=false;//�Ƿ�Ҫ���¼�������
    public static String mURL = "http://192.168.0.63:8080/LocationWebsocket/login";
    public static String mWebsock = "ws://192.168.0.63:8080/LocationWebsocket/chat.ws?username=";
    public static boolean mIsDestroy=false;
	public static boolean mBaiduGps   =false;//ʹ�ðٶȶ�λ����ʱ��GPS����
}
