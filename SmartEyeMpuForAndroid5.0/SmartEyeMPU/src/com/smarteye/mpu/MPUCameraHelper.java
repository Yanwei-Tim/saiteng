package com.smarteye.mpu;

import java.io.IOException;
import java.util.List;

import com.saiteng.smarteyempu.common.Config;
import com.saiteng.smarteyempu.common.Utils;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

// MPU Camera包装类，实现本地视频采集
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

	int iSettingsWidth ;
	int iSettingsHeight ;

	int iBestSettingsWidth = 320;
	int iBestSettingsHeight = 240;
	
	private int nMaxZoomValue;
	private int nCurZoomValue;
	
	private MediaRecorder mediaRecorder;
	// 设置父窗口句柄
	public void SetContext(Context ctx) {
		mContext = ctx;
	}

	public void SettingPreviewSize(int iCameraId,int iWidth, int iHeight){
		
		iSettingsWidth  = iWidth;
		iSettingsHeight = iHeight;
		mCamera = Camera.open(iCameraId);
	
		Log.d(TAG, iCameraId+" =");
		// 获取camera支持的分辨率相关参数，判断是否可以设置
		List<Size> previewSizes = mCamera.getParameters()
				.getSupportedPreviewSizes();
		for (int i = 0; i < previewSizes.size(); i++) {
			Size s = previewSizes.get(i);
			if((s.width > 600) && (s.width < 700)){
				iBestSettingsWidth = s.width;
				iBestSettingsHeight = s.height;
			}
			Log.i(TAG, "width=" + s.width + " height=" + s.height);
		}
		Log.i(TAG, "Best Preview Size:width=" + iBestSettingsWidth + " height=" + iBestSettingsHeight);

		// 获取当前设置的分辩率参数
		boolean bSetPreviewSize = false;
		for (int i = 0; i < previewSizes.size(); i++) {
			Size s = previewSizes.get(i);
			if (s.width ==iSettingsWidth  && s.height == iSettingsHeight) {
				bSetPreviewSize = true;
				break;
			}
		}
		// 指定的分辩率不支持时，用默认的分辩率替代
		if (!bSetPreviewSize){
			iSettingsWidth = iBestSettingsWidth;
			iSettingsHeight = iBestSettingsHeight;
		}
		mCamera.release();
		mCamera = null;
		
		
		
		Log.i(TAG,  "Camera width: " + iSettingsWidth + " Height: " + iSettingsHeight);
	}
	// 初始化摄像机，在surfaceCreated中调用
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
			nMaxZoomValue = parameters.getMaxZoom(); //获取最大焦距
			nCurZoomValue = parameters.getZoom(); //获取当前的焦距
			
			// 设置分辩率参数
			parameters.setPreviewSize(iSettingsWidth, iSettingsHeight);
					
			// 设置视频数据格式
			//parameters.setPreviewFormat(ImageFormat.NV21);
			// 参数设置生效
			try {
				mCamera.setParameters(parameters);
			} catch (Exception e) {

			}
			Camera.Size captureSize = mCamera.getParameters().getPreviewSize();
			int bufSize = captureSize.width * captureSize.height
					* ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
			for (int i = 0; i < iCaptureBuffers; i++) {
				mCamera.addCallbackBuffer(new byte[bufSize]);
			}
			// 设置视频输出回调函数，通过MPU的外部视频输入接口传入MPU内核进行处理
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
			mCamera.startPreview(); // 打开预览画面
			bIfPreview = true;

			// 获取设置后的相关参数
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
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_CODEC_VIDEOFR, 25);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOII, 1);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOBR,
					500 * 1000 * 2 * 4);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_MEDIA, 3);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOTS, 320);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_VIDEOKT, 20000);
			MPUCoreSDK.SetSDKOptionInt(MPUDefine.MPU_I_RECORD_AUDIOTS, 320);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 摄像头采集控制
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

	// 获取系统中摄像头的数量
	public int GetCameraNumber() {
		try {
			return Camera.getNumberOfCameras();
		} catch (Exception ex) {
			return 0;
		}
	}

	// 自动对焦
	public void CameraAutoFocus() {
		if (mCamera == null || !bIfPreview)
			return;
		try {
			mCamera.autoFocus(null);
		} catch (Exception ex) {

		}
	}

	// 根据摄像头的方向选择摄像头（前置、后置）
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
	
	// 根据摄像头的序号选择摄像头（0 - GetCameraNumber()）
	public void SelectCamera(int iCameraId) {
		String dd[] = { "后置摄像头", "前置摄像头" };
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
	
	/**
	 * 滑动变焦
	 * @param zoomValue
	 */
	public void ChangeZoom(int zoomValue) {
		changeZoom(zoomValue);
	}

	/**
	 * 改变焦距
	 */
	public void changeZoom(int progress) {

		Camera.Parameters parameters;
		
		if (progress >= this.nMaxZoomValue)
			return;
		parameters = this.mCamera.getParameters();

		parameters.isZoomSupported();
		this.nCurZoomValue = progress;
		nCurZoomValue = nCurZoomValue >= nMaxZoomValue ? nMaxZoomValue
				: progress;
		parameters.setZoom(nCurZoomValue);
		this.mCamera.setParameters(parameters);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			System.out.println("视频区又被创建了...............");
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
		System.out.println("视频表面被销毁了.............");
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
	
	public int getPreviewWidth(){
		return iSettingsWidth;
	}
	
	public int getPreviewHeight(){
		return iSettingsHeight;
	}
}