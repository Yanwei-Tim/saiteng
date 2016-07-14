package com.saiteng.st_master;

import com.baidu.location.LocationClient;
import com.saiteng.st_master.view.ST_InfoDialog;

import android.content.Context;
import android.os.Handler;

public class Config{
	public static LocationClient mlocationClient = null;
	public static String url                     = "http://192.168.0.63:8080/NA721/";
    public static Context mManagecontext         = null;
    public static Handler mhandler               = null;
    public static String phonenum                = null;
    public static Handler mTrackContext          = null;
    public static Handler mGenzongContext        = null;
    public static double mLatitude               = 0;  
    public static double mLongitude              = 0;
    public static double mGZLatitude             = 0;
    public static double mGZLongitude            = 0;
    public static String pwd                     = null;
    public static String username                = null;
    public static ST_InfoDialog InfoDialog       =null;
    public static String tacketime               =null;
    
    
    public static String ip = "192.168.0.63";
    public static int port  = 20086;
    public static boolean disconn = false;
    public static String imei = null;
} 
