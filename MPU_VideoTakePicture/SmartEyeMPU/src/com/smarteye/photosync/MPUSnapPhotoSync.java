package com.smarteye.photosync;

import java.io.IOException;

import com.saiteng.smarteyempu.common.Config;
import com.smarteye.ftp4j.Ftp4J;

import android.content.Context;
import android.util.Log;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class MPUSnapPhotoSync {
	private String TAG = "MPUSnapPhotoSync";
	private boolean mConnect = false;
	private Ftp4J mFtp4j = new Ftp4J();
	private int deviceId;
	private String deviceString;
	
//	public MPUSnapPhotoSync( MPU_FtpInfo bvpu_FtpInfo){
//		
//		this.bvpu_FtpInfo = bvpu_FtpInfo;
//	}
	private final String directoryRoot = "PU_";
	
	public boolean ConnectNRU() {
		mConnect = false;
		deviceId = 975580;
		deviceString  = String.format("%08X", deviceId);
		try {
			mFtp4j.connect(Config.mStringServerAddr, Integer.parseInt(Config.mStringServerPort));
			mFtp4j.login("","");
			mFtp4j.createDirectory(directoryRoot
					+ String.format("%08X", deviceId));
			mFtp4j.changeDirectory(directoryRoot
					+ String.format("%08X", deviceId));
			mConnect = true;
			
		}  catch (IOException e) {
			
			Log.e(TAG, "Á¬½ÓÊ§°Ü---->" + e.toString());
		 
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			e.printStackTrace();
		} catch (FTPException e) {
			e.printStackTrace();
		}
		return false;
		
	}

}
