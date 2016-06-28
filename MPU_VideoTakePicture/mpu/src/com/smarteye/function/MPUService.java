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
			// ִ�о������������
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
		String dialog = "�û�ID=" + userId;
		if (status == MPUDefine.BVCU_EVENT_DIALOG_OPEN) {
			dialog += " �Ự��";
		} else if (status == MPUDefine.BVCU_EVENT_DIALOG_UPDATE) {
			dialog += " �Ự����";
		} else if (status == MPUDefine.BVCU_EVENT_DIALOG_CLOSE) {
			dialog += " �Ự�ر�";
		}

		if (mediaDir != 0) {
			String desc = "";
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_VIDEOSEND) == MPUDefine.BVCU_MEDIADIR_VIDEOSEND) {
				desc += "������Ƶ, ";
			}
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_AUDIOSEND) == MPUDefine.BVCU_MEDIADIR_AUDIOSEND) {
				desc += "������Ƶ, ";
			}
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_AUDIORECV) == MPUDefine.BVCU_MEDIADIR_AUDIORECV) {
				desc += "������Ƶ, ";
			}
			if ((mediaDir & MPUDefine.BVCU_MEDIADIR_DATASEND) == MPUDefine.BVCU_MEDIADIR_DATASEND) {
				desc += "����GPS, ";
			}
			Toast.makeText(this,
					dialog + " : " + desc.substring(0, desc.length() - 2),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, dialog + " : " + "�Է��ر��˶Ի�", Toast.LENGTH_SHORT)
					.show();
		}
		MPUCoreSDK
				.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_MEDIADIR, mediaDir);
	}

	@Override
	public void onMPULoginMessage(int dwUserId, int dwErrorCode) {
		if (dwErrorCode == 0) {
			Toast.makeText(this, "ע��ɹ����û�ID=" + dwUserId, Toast.LENGTH_SHORT)
					.show();
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_APPLIERID,
					dwUserId);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_STATUS, 1);
		} else {
			Toast.makeText(this, "ע��ʧ��", Toast.LENGTH_SHORT).show();
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_STATUS, 0);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}
