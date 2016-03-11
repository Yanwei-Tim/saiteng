package com.saiteng.st_individual;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

public class Config {

	public static Context mcontext              =null;
	public static Context Locus_mcontext        =null;
	public static Context Locusdetails_mcontext =null;
	public static Context Login_mcontext        =null;
	public static String  phoneNum              =null;
	public static String  ip                   =null;
	public static String  port                  =null;
	public static Handler mhandler              =null;
	public static String loginInfo              =null;
	public static boolean mIsLogined            =false;
	public static boolean mIsFristLogined       =false;
	public static Editor medit                           =null;
}
