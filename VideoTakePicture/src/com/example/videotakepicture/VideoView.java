package com.example.videotakepicture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class VideoView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mSurfaceHolder;

	private MediaRecorder mediaRecorder;

	private String filePath = "/sdcard/route/routep";

	private Camera mCamera;

	private int mCurrentCameraId = 0; // 1 ����ǰ������ͷ 0 �����������ͷ

	private int width, height;

	private boolean isRecording;

	private int nCurZoomValue;

	private Camera.Parameters localParameters;

	private int nMaxZoomValue;

	private int[] isZoomSupportOfCamera = new int[2];
	
	public VideoView(Context context) {
		super(context);
		init();
	}

	public VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public int getmCurrentCameraId() {
		return mCurrentCameraId;
	}

	public void setmCurrentCameraId(int mCurrentCameraId) {
		this.mCurrentCameraId = mCurrentCameraId;
	}

	@SuppressWarnings("deprecation")
	private void init() {
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public boolean isRecording() {
		return isRecording;
	}

	// ������ʾ������С
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}
	
	/**
	 * ��ʼ¼����Ƶ
	 * @return
	 */
	public boolean startRecord() {
		init();
		mediaRecorder = new MediaRecorder(); //¼����Ƶ��
		mediaRecorder.reset();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (mCamera == null) {
			mCamera = Camera.open(mCurrentCameraId);
			// Log.d("geek", mCurrentCameraId+"   s");
			localParameters = this.mCamera.getParameters();
			localParameters.setPreviewFrameRate(5); // ����ÿ����ʾ4֡
			// localParameters.setPictureSize(width, height); // ���ñ����ͼƬ�ߴ�
			localParameters.setJpegQuality(80); // ������Ƭ����
			// ������Ƭ��ʽ
			localParameters.setPictureFormat(PixelFormat.JPEG);

			if (localParameters.isZoomSupported()) {
				nMaxZoomValue = localParameters.getMaxZoom(); // ��ȡ���Ľ���
				((MainActivity) getContext()).setMaxChangeZomm(nMaxZoomValue);
				nCurZoomValue = localParameters.getZoom(); // ��ȡ��ǰ�Ľ���
				isZoomSupportOfCamera[mCurrentCameraId] = 1;
			}

			mCamera.setDisplayOrientation(90);
		}
		mCamera.unlock();
		mediaRecorder.setCamera(mCamera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		CamcorderProfile localCamcorderProfile = null;
		if (this.mCurrentCameraId == CameraInfo.CAMERA_FACING_FRONT)
			localCamcorderProfile = CamcorderProfile.get(this.mCurrentCameraId,
					CamcorderProfile.QUALITY_HIGH);
		else
			localCamcorderProfile = CamcorderProfile
					.get(CamcorderProfile.QUALITY_1080P);
		localCamcorderProfile.duration = 86400;
		mediaRecorder.setProfile(localCamcorderProfile);
		
		mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		mediaRecorder.setOutputFile(VideoUtils.generateFileName());
        
		if (this.mCurrentCameraId == CameraInfo.CAMERA_FACING_FRONT) {
			mediaRecorder.setOrientationHint(270);
		} else {
			mediaRecorder.setOrientationHint(90);
		}
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			isRecording = true;
			((MainActivity) getContext()).setDuration(0);
			((MainActivity) getContext()).getHandler().sendEmptyMessage(1);
			new Handler(Looper.getMainLooper()) {

				@Override
				public void handleMessage(Message msg) {
					setBackgroundColor(Color.TRANSPARENT);
				}

			}.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * ֹͣ¼����Ƶ
	 * @return
	 */
	public boolean stopRecord() {
		try {
			if (mediaRecorder != null) {
				new Handler(Looper.getMainLooper()) {

					@Override
					public void handleMessage(Message msg) {
					 //setBackgroundColor(Color.BLACK);
					}

				}.sendEmptyMessage(1);
				((MainActivity) getContext()).getHandler().removeMessages(1);
				mediaRecorder.stop();
				mediaRecorder.reset();
				mediaRecorder.release();
				mediaRecorder = null;
			}
		} catch (Exception e) {
			return false;
		}
		isRecording = false;
		return true;
	}
	
	/**
	 * ¼����Ƶ�ĸı�״̬
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceHolder = holder;
	}
	
	
	/**
	 * ��ʼԤ������
	 */
	public void startPreview() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (mCamera == null) {
			mCamera = Camera.open(mCurrentCameraId);
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			localParameters = this.mCamera.getParameters();
			localParameters.setPreviewFrameRate(5); // ����ÿ����ʾ4֡
			// localParameters.setPictureSize(width, height); // ���ñ����ͼƬ�ߴ�
			localParameters.setJpegQuality(80); // ������Ƭ����
			// ������Ƭ��ʽ
			localParameters.setPictureFormat(PixelFormat.JPEG);

			if (localParameters.isZoomSupported()) {
				nMaxZoomValue = localParameters.getMaxZoom(); // ��ȡ���Ľ���
				((MainActivity) getContext()).setMaxChangeZomm(nMaxZoomValue);
				nCurZoomValue = localParameters.getZoom(); // ��ȡ��ǰ�Ľ���
				isZoomSupportOfCamera[mCurrentCameraId] = 1;
			}
			mCamera.startPreview();
			mCamera.setDisplayOrientation(90);
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
		new Thread() {

			@Override
			public void run() {
				startPreview();
			}

		}.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		stopRecord();
		mSurfaceHolder = null;
	}

	/**
	 * ��Զ����
	 */
	public void changerZoom(int progress) {
		if (isZoomSupportOfCamera[mCurrentCameraId] == 1 && mCamera != null) {
			if (progress >= this.nMaxZoomValue)
				return;
			localParameters = this.mCamera.getParameters();

			this.localParameters.isZoomSupported();
			this.nCurZoomValue = progress;
			nCurZoomValue = nCurZoomValue >= nMaxZoomValue ? nMaxZoomValue
					: progress;
			this.localParameters.setZoom(nCurZoomValue);
			this.mCamera.setParameters(this.localParameters);

		}
	}

	/**
	 * ��Զ����
	 */
	public void zoomIn() {
		if (isRecording && isZoomSupportOfCamera[mCurrentCameraId] == 1
				&& mCamera != null) {
			if (this.nCurZoomValue >= this.nMaxZoomValue)
				return;
			localParameters = this.mCamera.getParameters();

			this.localParameters.isZoomSupported();
			this.nCurZoomValue += 3;
			nCurZoomValue = nCurZoomValue >= nMaxZoomValue ? nMaxZoomValue
					: nCurZoomValue;
			this.localParameters.setZoom(nCurZoomValue);
			this.mCamera.setParameters(this.localParameters);
		}
	}

	/**
	 * ��������
	 */
	public void zoomOut() {
		if (isRecording && isZoomSupportOfCamera[mCurrentCameraId] == 1
				&& mCamera != null) {
			if (this.nCurZoomValue == 0)
				return;
			localParameters = this.mCamera.getParameters();
			this.localParameters.isZoomSupported();
			this.nCurZoomValue -= 3;
			nCurZoomValue = nCurZoomValue <= 0 ? 0 : nCurZoomValue;
			this.localParameters.setZoom(nCurZoomValue);
			this.mCamera.setParameters(this.localParameters);
		}
	}

	PictureCallback pictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File file = new File(VideoUtils.filePath(),
					VideoUtils.getDate(System.currentTimeMillis()) + ".rar");
			try {
				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(data); // д��sd����
				outputStream.close(); // �ر������
				if (!isRecording) {
					mCamera.startPreview();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * ����ķ������ص�����
	 */
	public void takePicture() {
		mCamera.takePicture(null, null, pictureCallback);
	}

}
