package com.smarteye.mpu;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.smarteye.mpu.bean.GPSData;
import com.smarteye.mpu.bean.RegisterInfo;
import com.smarteye.mpu.bean.StorageInfo;
import com.smarteye.mpu.bean.SystemInfo;

public class MPUCoreSDK {
	static class MainHandler extends Handler {
		WeakReference<MPUCoreSDK> mMPU;

		public MainHandler(Looper L) {
			super(L);
		}

		public MainHandler(MPUCoreSDK anychat) {
			mMPU = new WeakReference<MPUCoreSDK>(anychat);
		}

		public void handleMessage(Message nMsg) {
			MPUCoreSDK mpu = mMPU.get();
			if (mpu == null)
				return;
			super.handleMessage(nMsg);
			Bundle tBundle = nMsg.getData();
			int type = tBundle.getInt("HANDLETYPE");
			if (type == HANDLE_TYPE_LOGINMSG) {
				int dwUserId = tBundle.getInt("USERID");
				int dwErrorCode = tBundle.getInt("ERRORCODE");
				if (mpu.baseEvent != null)
					mpu.baseEvent.onMPULoginMessage(dwUserId, dwErrorCode);
			} else if (type == HANDLE_TYPE_DIALOGMSG) {
				int mediaDir = tBundle.getInt("MEDIADIR");
				int status = tBundle.getInt("STATUS");
				int userId = tBundle.getInt("USERID");
				if (mpu.dialogEvent != null)
					mpu.dialogEvent.onMPUDialogEvent(userId, status, mediaDir);
			}
		}
	}

	private static int HANDLE_TYPE_DIALOGMSG = 2;

	private static int HANDLE_TYPE_LOGINMSG = 1;

	static MainHandler mHandler;
	static {
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("yate");
		System.loadLibrary("sav");
		System.loadLibrary("framework");
		System.loadLibrary("network");	
		System.loadLibrary("rtp");
		System.loadLibrary("sip");
		System.loadLibrary("render");
		System.loadLibrary("mpucore");
		if(android.os.Build.VERSION.SDK_INT>= 21){
			System.loadLibrary("codec_5_0");
			System.loadLibrary("storage_5_0");
		}else{
			System.loadLibrary("codec");
			System.loadLibrary("storage");
		}
	}

	// ��ȡ��Ƶ��������
	public static native byte[] FetchAudioPlayBuffer(int size);

	public static native void Finish();

	// ��ѯSDK����������ֵ��
	public static native int GetSDKOptionInt(int optname);

	// ��ʼ��MPU��
	public static native int Initialize();

	// �ⲿ��Ƶ��������
	public static native int InputAudioData(byte[] lpSamples, int dwSize,
			long dwTimeStamp);

	// �ⲿ��Ƶ��������
	public static native int InputVideoData(byte[] lpVideoFrame, int dwSize,
			long dwTimeStamp);

	public static native int InputGPSData(GPSData data);

	public static native int Register(RegisterInfo info);

	public static native int ReStart();

	// �����ⲿ������Ƶ��ʽ
	public static native int SetInputAudioFormat(int dwChannels,
			int dwSamplesPerSec, int dwBitsPerSample, int dwFlags);

	// �����ⲿ������Ƶ��ʽ
	public static native int SetInputVideoFormat(int pixFmt, int dwWidth,
			int dwHeight, int dwFps, int dwFlags);

	// ����SDK����������ֵ��
	public static native int SetSDKOptionInt(int optname, int optvalue);

	public static native int Storage(StorageInfo info);
	
	public static native int GetFrameCount();
	
	public static native int GetUploadCount();

	private MPUBaseEvent baseEvent = null;

	private MPUDialogEvent dialogEvent = null;

	public MPUBaseEvent getBaseEvent() {
		return baseEvent;
	}

	public MPUDialogEvent getDialogEvent() {
		return dialogEvent;
	}

	public static native String GetSDKOptionString(int optname);

	private void onMPUDialogMessage(int userId, int status, int mediaDir) {
		Message tMsg = new Message();
		Bundle tBundle = new Bundle();
		tBundle.putInt("HANDLETYPE", HANDLE_TYPE_DIALOGMSG);
		tBundle.putInt("STATUS", status);
		tBundle.putInt("MEDIADIR", mediaDir);
		tBundle.putInt("USERID", userId);
		tMsg.setData(tBundle);
		mHandler.sendMessage(tMsg);
	}

	private void onMPULoginMessage(int dwUserId, int dwErrorCode) {
		Message tMsg = new Message();
		Bundle tBundle = new Bundle();
		tBundle.putInt("HANDLETYPE", HANDLE_TYPE_LOGINMSG);
		tBundle.putInt("USERID", dwUserId);
		tBundle.putInt("ERRORCODE", dwErrorCode);
		tMsg.setData(tBundle);
		mHandler.sendMessage(tMsg);
	}

	// ע����Ϣ֪ͨ
	public native int RegisterNotify();

	public void setBaseEvent(MPUBaseEvent baseEvent) {
		mHandler = new MainHandler(this);
		this.baseEvent = baseEvent;
		RegisterNotify();
	}

	public void setDialogEvent(MPUDialogEvent dialogEvent) {
		this.dialogEvent = dialogEvent;
	}

	public static native int SetSDKOptionString(int optname, String optvalue);
}
