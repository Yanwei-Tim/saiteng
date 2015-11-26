package com.smarteye.function;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.smarteye.mpu.MPUAudioHelper;
import com.smarteye.mpu.MPUBaseEvent;
import com.smarteye.mpu.MPUCoreSDK;
import com.smarteye.mpu.MPUDefine;
import com.smarteye.mpu.MPUDialogEvent;

public class MPUService extends Service implements MPUBaseEvent, MPUDialogEvent {

	class MPUBinder extends Binder {
		public void startDownload() {
			Log.d("TAG", "startDownload() executed");
			// 执行具体的下载任务
		}
	}

	public static final String TAG = "MPUService";
	private MPUBinder binder = new MPUBinder();

	private MPUAudioHelper m_audioHelper;

	public MPUCoreSDK mpu = null;

	private void InitialSDK() {
		if (mpu == null) {
			mpu = new MPUCoreSDK();
			mpu.setBaseEvent(this);
			mpu.setDialogEvent(this);

			MPUCoreSDK.Initialize();

			m_audioHelper = new MPUAudioHelper();
			m_audioHelper.initAudioRecorder(1);
			m_audioHelper.initAudioPlayer(1);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		InitialSDK();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (m_audioHelper != null) {
			m_audioHelper.releaseAudioRecorder();
			m_audioHelper.releaseAudioPlayer();
		}
		m_audioHelper = null;
		MPUCoreSDK.Finish();
	}

	@Override
	public void onMPUDialogEvent(int userId, int status, int mediaDir) {
		String dialog = "用户ID=" + userId;
		if (status == MPUDefine.BVCU_EVENT_DIALOG_OPEN) {
			dialog += " 会话打开";
		} else if (status == MPUDefine.BVCU_EVENT_DIALOG_UPDATE) {
			dialog += " 会话更新";
		} else if (status == MPUDefine.BVCU_EVENT_DIALOG_CLOSE) {
			dialog += " 会话关闭";
		}

		if (mediaDir != 0) {
			String desc = "";
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_VIDEOSEND) == MPUDefine.BVCU_MEDIADIR_VIDEOSEND) {
				desc += "发送视频, ";
			}
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_AUDIOSEND) == MPUDefine.BVCU_MEDIADIR_AUDIOSEND) {
				desc += "发送音频, ";
			}
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_AUDIORECV) == MPUDefine.BVCU_MEDIADIR_AUDIORECV) {
				desc += "接收音频, ";
			}
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_DATASEND) == MPUDefine.BVCU_MEDIADIR_DATASEND) {
				desc += "接收GPS, ";
			}
			Toast.makeText(this,
					dialog + " : " + desc.substring(0, desc.length() - 2),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, dialog + " : " + "对方关闭了对话", Toast.LENGTH_SHORT)
					.show();
		}
		MPUCoreSDK
				.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_MEDIADIR, mediaDir);
	}

	@Override
	public void onMPULoginMessage(int dwUserId, int dwErrorCode) {
		if (dwErrorCode == 0) {
			Toast.makeText(this, "注册成功，用户ID=" + dwUserId, Toast.LENGTH_SHORT)
					.show();
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_APPLIERID,
					dwUserId);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_STATUS, 1);
		} else {
			Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_STATUS, 0);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}
