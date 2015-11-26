package com.smarteye.mpu;

import java.util.List;

import com.smarteye.mpu.bean.SystemInfo;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

// MPU Camera��װ�࣬ʵ�ֱ�����Ƶ�ɼ�
public class MPUCameraHelper implements SurfaceHolder.Callback {
	private final static String TAG = "MPU";
	private Camera mCamera = null;
	private boolean bIfPreview = false;
	private boolean bNeedCapture = true;
	private int iCurrentCameraId = -1;
	private SurfaceHolder currentHolder = null;
	private int mVideoPixfmt = -1;
	private final int iCaptureBuffers = 3;

	private Context mContext = null;
	private int mCameraOrientation = 0;
	private int mCameraFacing = 0;
	private int mDeviceOrientation = 0;

	public final int CAMERA_FACING_BACK = 0;
	public final int CAMERA_FACING_FRONT = 1;
	
	//���÷ֱ���
	int iSettingsWidth = 720;
	int iSettingsHeight = 480;

	// ���ø����ھ��
	public void SetContext(Context ctx) {
		mContext = ctx;
	}

	// ��ʼ�����������surfaceCreated�е���
	private void initCamera() {
		if (null == mCamera)
			return;
		try {
			if (bIfPreview) {
				mCamera.stopPreview();// stopCamera();
				mCamera.setPreviewCallbackWithBuffer(null);
			}
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(iCurrentCameraId, cameraInfo);

			mCameraOrientation = cameraInfo.orientation;
			mCameraFacing = cameraInfo.facing;
			mDeviceOrientation = getDeviceOrientation();
			Log.i(TAG, "allocate: device orientation=" + mDeviceOrientation
					+ ", camera orientation=" + mCameraOrientation
					+ ", facing=" + mCameraFacing);

			setCameraDisplayOrientation();

			/* Camera Service settings */
			Camera.Parameters parameters = mCamera.getParameters();

			// ��ȡcamera֧�ֵ���ز������ж��Ƿ��������
			List<Size> previewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();

			for (int i = 0; i < previewSizes.size(); i++) {
				Size s = previewSizes.get(i);
				Log.i(TAG, "width=" + s.width + " height=" + s.height);
			}

			// ��ȡ��ǰ���õķֱ��ʲ���
			boolean bSetPreviewSize = false;
			for (int i = 0; i < previewSizes.size(); i++) {
				Size s = previewSizes.get(i);
				if (s.width == iSettingsWidth && s.height == iSettingsHeight) {
					bSetPreviewSize = true;
					parameters.setPreviewSize(iSettingsWidth, iSettingsHeight);
					break;
				}
			}
			// parameters.setPreviewFrameRate(25);
			// ָ���ķֱ��ʲ�֧��ʱ����Ĭ�ϵķֱ������
			if (!bSetPreviewSize)
				parameters.setPreviewSize(640, 480);

			// ������Ƶ���ݸ�ʽ
			// parameters.setPreviewFormat(ImageFormat.NV21);
			// ����������Ч
			try {
				mCamera.setParameters(parameters);
			} catch (Exception e) {

			}
			Camera.Size captureSize = mCamera.getParameters().getPreviewSize();
			int bufSize = captureSize.width * captureSize.height
					* ImageFormat.getBitsPerPixel(ImageFormat.NV16) / 8;
			for (int i = 0; i < iCaptureBuffers; i++) {
				mCamera.addCallbackBuffer(new byte[bufSize]);
			}
			// ������Ƶ����ص�������ͨ��MPU���ⲿ��Ƶ����ӿڴ���MPU�ں˽��д���
			mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					if (data.length != 0 && bNeedCapture) {
						MPUCoreSDK.InputVideoData(data, data.length,
								System.currentTimeMillis());
					}
					mCamera.addCallbackBuffer(data);
				}
			});
			mCamera.startPreview(); // ��Ԥ������
			bIfPreview = true;

			// ��ȡ���ú����ز���
			if (mCamera.getParameters().getPreviewFormat() == ImageFormat.NV21)
				mVideoPixfmt = MPUDefine.MPU_PIX_FMT_NV21;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.YV12)
				mVideoPixfmt = MPUDefine.MPU_PIX_FMT_YV12;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.NV16)
				mVideoPixfmt = MPUDefine.MPU_PIX_FMT_NV16;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.YUY2)
				mVideoPixfmt = MPUDefine.MPU_PIX_FMT_YUY2;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.RGB_565)
				mVideoPixfmt = MPUDefine.MPU_PIX_FMT_RGB565;
			else
				Log.e(TAG, "unknow camera privew format:"
						+ mCamera.getParameters().getPreviewFormat());

			Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
			MPUCoreSDK.SetInputVideoFormat(mVideoPixfmt, previewSize.width,
					previewSize.height, mCamera.getParameters()
							.getPreviewFrameRate(), 0);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOW,
					previewSize.width);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOH,
					previewSize.height);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOFR, 15);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOII, 1);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOBR,
					800 * 1000 * 2);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_MEDIA, 3);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOTS, 320);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOKT, 20000);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_AUDIOTS, 320);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ����ͷ�ɼ�����
	public void CaptureControl(boolean bCapture) {
		bNeedCapture = bCapture;
		if (bNeedCapture && mVideoPixfmt != -1) {
			try {
				Camera.Size previewSize = mCamera.getParameters()
						.getPreviewSize();
				MPUCoreSDK.SetInputVideoFormat(mVideoPixfmt, previewSize.width,
						previewSize.height, mCamera.getParameters()
								.getPreviewFrameRate(), 0);
			} catch (Exception ex) {

			}
		}
	}

	// ��ȡϵͳ������ͷ������
	public int GetCameraNumber() {
		try {
			return Camera.getNumberOfCameras();
		} catch (Exception ex) {
			return 0;
		}
	}

	// �Զ��Խ�
	public void CameraAutoFocus() {
		if (mCamera == null || !bIfPreview)
			return;
		try {
			mCamera.autoFocus(null);
		} catch (Exception ex) {

		}
	}

	// ��������ͷ�ķ���ѡ������ͷ��ǰ�á����ã�
	public void SelectVideoCapture(int facing) {
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == facing) {
				iCurrentCameraId = i;
				break;
			}
		}
	}

	// ��������ͷ�����ѡ������ͷ��0 - GetCameraNumber()��
	public void SelectCamera(int iCameraId) {
		String dd[] = { "��������ͷ", "ǰ������ͷ" };
		System.out.println(dd[iCameraId]);
		try {
			if (iCurrentCameraId == iCameraId
					|| Camera.getNumberOfCameras() <= iCameraId
					|| currentHolder == null)
				return;
			iCurrentCameraId = iCameraId;

			if (iCurrentCameraId == CameraInfo.CAMERA_FACING_BACK)
				MPUCoreSDK.SetSDKOptionInt(
						MPUDefine.MPU_I_USERSTATE_CAMERA_INDEX,
						MPUDefine.MPU_CAMERA_BACK_INDEX);
			else if (iCurrentCameraId == CameraInfo.CAMERA_FACING_FRONT)
				MPUCoreSDK.SetSDKOptionInt(
						MPUDefine.MPU_I_USERSTATE_CAMERA_INDEX,
						MPUDefine.MPU_CAMERA_FRONT_INDEX);

			if (null != mCamera) {
				mCamera.stopPreview();
				mCamera.setPreviewCallbackWithBuffer(null);
				bIfPreview = false;
				mVideoPixfmt = -1;
				mCamera.release();
				mCamera = null;
			}

			mCamera = Camera.open(iCameraId);
			mCamera.setPreviewDisplay(currentHolder);
			initCamera();
		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				mVideoPixfmt = -1;
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			System.out.println("��Ƶ���ֱ�������...............");
			currentHolder = holder;
			int index = MPUCoreSDK
					.GetSDKOptionInt(MPUDefine.MPU_I_USERSTATE_CAMERA_INDEX);
			if (index == MPUDefine.MPU_CAMERA_FRONT_INDEX)
				SelectCamera(CameraInfo.CAMERA_FACING_FRONT);
			else
				SelectCamera(CameraInfo.CAMERA_FACING_BACK);
		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				mVideoPixfmt = -1;
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("��Ƶ���汻������.............");
		if (null != mCamera) {
			try {
				mCamera.stopPreview();
				mCamera.setPreviewCallbackWithBuffer(null);
				bIfPreview = false;
				mCamera.release();
				mCamera = null;
			} catch (Exception ex) {
				mCamera = null;
				bIfPreview = false;
			}
		}
		currentHolder = null;
		mVideoPixfmt = -1;
	}

	private int getDeviceOrientation() {
		int orientation = 0;
		if (mContext != null) {
			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			// Log.i(TAG, "wm.getDefaultDisplay().getRotation():" +
			// wm.getDefaultDisplay().getRotation());
			switch (wm.getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_90:
				orientation = 90;
				break;
			case Surface.ROTATION_180:
				orientation = 180;
				break;
			case Surface.ROTATION_270:
				orientation = 270;
				break;
			case Surface.ROTATION_0:
			default:
				orientation = 0;
				break;
			}
		}
		return orientation;
	}

	public void setCameraDisplayOrientation() {
		if (mContext == null)
			return;
		try {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(iCurrentCameraId, cameraInfo);

			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			int rotation = wm.getDefaultDisplay().getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}

			int result;
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (cameraInfo.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (cameraInfo.orientation - degrees + 360) % 360;
			}

			mCamera.setDisplayOrientation(result);
		} catch (Exception ex) {

		}
	}

}