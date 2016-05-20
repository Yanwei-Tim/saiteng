package com.linkcard.media;

import android.util.Log;
public class LinkVideoCore 
{
	private static final String TAG = "DBG";

	static {
		
		System.loadLibrary("linkcardplayer");
		
		Log.d(TAG, "liblinkcardplayer.so");
	}
	
	public native int sysinit();

	public native int sysuninit();
}
