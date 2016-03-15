package com.saiteng.st_master;

import com.baidu.location.LocationClient;
import android.content.Context;
import android.os.Handler;

public class Config{
	public static LocationClient mlocationClient = null;
	public static String url                     = "http://192.168.0.56:8080/NA721/";
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
} 
