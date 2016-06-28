package com.smarteye.mpu;

import com.saiteng.smarteyempu.common.Config;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class MPUAudioHelper {
	private final static String TAG = "MPUAudioHelper";
	private AudioTrack mAudioTrack = null;
	private AudioRecord mAudioRecord = null;

	private PlayAudioThread mPlayAudioThread = null; // �����߳�
	private boolean mPlayThreadExitFlag = false; // �����߳��˳���־
	private int mMinPlayBufSize = 0;
	private boolean mAudioPlayReleased = false;

	private RecordAudioThread mRecordAudioThread = null; // �ɼ��߳�
	private boolean mRecordThreadExitFlag = false; // �ɼ��߳��˳���־
	private int mMinRecordBufSize = 0;
	private boolean mAudioRecordReleased = false;

	public MPUAudioHelper() {
	}

	// ��ʼ����Ƶ������
	@SuppressWarnings("deprecation")
	public int initAudioPlayer(int profile) {
		if (mAudioTrack != null)
			return 0;
		Log.d(TAG, "initAudioPlayer, profile: " + profile);
		int channel, samplerate, samplebit;
		// �����ϲ��趨��profile�����ò���
		if (profile == 1) {
			samplerate = 8000;
			channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
			samplebit = AudioFormat.ENCODING_PCM_16BIT;
		} else if (profile == 2) {
			samplerate = 44100;
			channel = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
			samplebit = AudioFormat.ENCODING_PCM_16BIT;
		} else {
			return -1;
		}
		try {
			mAudioPlayReleased = false;
			// ��ù����������С��������С
			mMinPlayBufSize = AudioTrack.getMinBufferSize(samplerate, channel,
					samplebit);
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplerate,
					channel, samplebit, mMinPlayBufSize, AudioTrack.MODE_STREAM);

			if (mPlayAudioThread == null) {
				mPlayThreadExitFlag = false;
				mPlayAudioThread = new PlayAudioThread();
				mPlayAudioThread.start();
			}
			Log.d(TAG, "mMinPlayBufSize = " + mMinPlayBufSize);
		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	// ������Ƶ������
	public void releaseAudioPlayer() {
		if (mAudioPlayReleased)
			return;
		mAudioPlayReleased = true;
		Log.d(TAG, "releaseAudioPlayer");
		if (mPlayAudioThread != null) {
			mPlayThreadExitFlag = true;
			mPlayAudioThread = null;
		}

		if (mAudioTrack != null) {
			try {
				mAudioTrack.stop();
				mAudioTrack.release();
				mAudioTrack = null;
			} catch (Exception e) {

			}
		}
	}

	/*
	 * ��Ƶ�����߳�
	 */
	class PlayAudioThread extends Thread {
		@Override
		public void run() {
			if (mAudioTrack == null)
				return;
			try {
				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			} catch (Exception e) {
				Log.d(TAG, "set play thread priority failed: " + e.getMessage());
			}
			try {
				mAudioTrack.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, "audio play....");
			while (!mPlayThreadExitFlag) {
				try {
					byte[] data = MPUCoreSDK.FetchAudioPlayBuffer(640);
					if (data == null || data.length <= 0)
						break;
					mAudioTrack.write(data, 0, data.length);
					//Log.d(TAG, "�յ���Ƶ����"+data.length+"�ֽ�");
				} catch (Exception e) {
					break;
				}
			}
			Log.d(TAG, "audio play stop....");
		}
	}

	// ��ʼ����Ƶ�ɼ��豸
	@SuppressWarnings("deprecation")
	public int initAudioRecorder(int profile) {
		if (mAudioRecord != null)
			return 0;
		Log.d(TAG, "initAudioRecorder, profile: " + profile);
		int channel, samplerate, samplebit;
		// �����ϲ��趨��profile�����ò���
		if (profile == 1) {
			samplerate = 8000;
			channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
			samplebit = AudioFormat.ENCODING_PCM_16BIT;
		} else if (profile == 2) {
			samplerate = 44100;
			channel = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
			samplebit = AudioFormat.ENCODING_PCM_16BIT;
		} else {
			return -1;
		}
		try {
			mAudioRecordReleased = false;
			// ��ù����������С��������С
			mMinRecordBufSize = AudioRecord.getMinBufferSize(samplerate,
					channel, samplebit);
			mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					samplerate, channel, samplebit, mMinRecordBufSize);

			// ����AnyChat���ⲿ��Ƶ�������
			MPUCoreSDK.SetInputAudioFormat(mAudioRecord.getChannelCount(),
					mAudioRecord.getSampleRate(), 16, 0);

			if (mRecordAudioThread == null) {
				mRecordThreadExitFlag = false;
				mRecordAudioThread = new RecordAudioThread();
				mRecordAudioThread.start();
			}
			Log.d(TAG, "mMinRecordBufSize = " + mMinRecordBufSize);
		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	// ������Ƶ�ɼ��豸
	public void releaseAudioRecorder() {
		if (mAudioRecordReleased)
			return;
		mAudioRecordReleased = true;
		Log.d(TAG, "releaseAudioRecorder");
		if (mRecordAudioThread != null) {
			mRecordThreadExitFlag = true;
			mRecordAudioThread = null;
		}
		if (mAudioRecord != null) {
			try {
				mAudioRecord.stop();
				mAudioRecord.release();
				mAudioRecord = null;
			} catch (Exception e) {

			}
		}
	}

	/*
	 * ��Ƶ�ɼ��߳�
	 */
	class RecordAudioThread extends Thread {
		@Override
		public void run() {
			if (mAudioRecord == null)
				return;
			try {
				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			} catch (Exception e) {
				Log.d(TAG,
						"set record thread priority failed: " + e.getMessage());
			}
			try {
				mAudioRecord.startRecording();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, "audio record....");
			byte[] recordbuf = new byte[640];
			while (!mRecordThreadExitFlag) {
				try {
					int ret = mAudioRecord.read(recordbuf, 0, recordbuf.length);
					if (ret == AudioRecord.ERROR_INVALID_OPERATION
							|| ret == AudioRecord.ERROR_BAD_VALUE)
						break;
					
					if(Config.mIsAudioUpload){
						// ͨ��MPU���ⲿ��Ƶ����ӿڽ���Ƶ�������ݴ����ں�
						MPUCoreSDK.InputAudioData(recordbuf, ret,
								System.currentTimeMillis());
					}
				} catch (Exception e) {
					break;
				}
			}
			Log.d(TAG, "audio record stop....");
			releaseAudioRecorder();
		}

	}
}
