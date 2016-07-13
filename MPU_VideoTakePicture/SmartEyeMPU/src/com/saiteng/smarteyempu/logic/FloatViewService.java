package com.saiteng.smarteyempu.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.saiteng.smarteye.videotakepicture.VideoUtils;
import com.saiteng.smarteyempu.R;
import com.saiteng.smarteyempu.common.Config;
import com.saiteng.smarteyempu.common.Utils;
import com.saiteng.smarteyempu.ui.RecordView;
import com.smarteye.function.MPUInterface;
import com.smarteye.mpu.MPUCoreSDK;

public class FloatViewService extends Activity {
	private Intent mStartIntent = null;
	private long lastPressBack;
    private boolean isLongPress=false;
    private boolean isDarkScreen=false;
	private Context mContext = null;
	private RecordView recordView;
	private Handler    recordViewHandler = null;
	private static WindowManager wm;
	private static WindowManager.LayoutParams wmParams;
	private ImageView mImageView;
	private MPUInterface mPUInterface = null;
	private static FloatViewService sFloatViewService = null;
	private boolean bIsBackCamera = true;
	//��ʱ�����
    private RefreshFrameRateTimerThread  timerRfreshFrameRateThread = null;
    private boolean bIsNeedStopRefreshTimer = false;
    private RecordTimerThread  timerRecordThread = null;
    private boolean bIsNeedStopRecordTimer = false;
    private String  strStoreagePath = "";
    /**
      * ���������Ķ���
      */
    public AudioManager mAudioManager;
    // ����Nativeʵ��
	public static FloatViewService getInstance(Context context) {
		
		return sFloatViewService;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Ӧ������ʱ��������Ļ������������
		
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.main_mpu);
	    
	    mImageView = (ImageView)findViewById(R.id.imageView1);
	    
        sFloatViewService = this;
	}
	
	/**
     * �������������仯 ˵���� ��ǰ�����ı�ʱ��������ֵ����Ϊ���ֵ��2
     */

	@Override
	public void onDestroy() {
		
		//super.onDestroy();
		
		 mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_STOP_RECORD);
		 
		 mPUInterface.Destroy();
		 
		 mPUInterface = null;

		sFloatViewService = null;

		mStartIntent = null;

		StopRefreshFrameRateTimer();
		
		if(Config.mVideoSwitch){
			
			StopRecordTimer();
		}
		
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	@Override
	protected void onStart() {
	
		super.onStart();
	
		mContext = getBaseContext().getApplicationContext();
		

		// ����MPU
		mPUInterface = MPUInterface.getInstance(mContext);
		if (mPUInterface == null) {
		
			mContext = null;
			return;
		}

		createFloatView();
		mPUInterface.SetDisplay(recordView.getVideoDisplayView());

		mPUInterface.SetUploadLocation(Config.mIsLocationUpload);
		mPUInterface.SetUploadAudio(Config.mIsAudioUpload);//��������ֵ��ʱ��ı䡣
		
		bIsBackCamera = Config.mIsBackCameraFirst;
		mPUInterface.SetVideoSize(Config.mIsBackCameraFirst, Config.mVideoWidth, Config.mVideoHeight);
		mPUInterface.SetVideoCodec(Config.mVideoBaudrate, Config.mVideoFrameInterval);
		mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_REGISTER);
		mPUInterface.Start();
		
		//mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_REGISTER);
		recordViewHandler = recordView.getHandler();
		
		
		StartRefreshFrameRateTimer();
		
		if(Config.mVideoSwitch){//�Ƿ���Ҫ����ý���ļ�������
			strStoreagePath = Utils.getStoragePath(mContext);//�ж��Ƿ��������SD��
			if(strStoreagePath.equals("") == false){
				StartRecordTimer();
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){
			
			switch (msg.what) {
			case Config.SERVICE_MESSAGE_DarkScreen:
				mImageView.setVisibility(View.GONE);
				break;
			case Config.SERVICE_MESSAGE_NoDarkScreen:
				mImageView.setVisibility(View.VISIBLE);
				break;
			case Config.SERVICE_MESSAGE_EXIT:
				if (recordView != null) {
					wm.removeView(recordView);
					recordView = null;
					finish();
				}
				break;
			case Config.SERVICE_MESSAGE_REGISTER:
				if (mPUInterface != null) {
					mPUInterface.Login(Config.mStringServerAddr,
							Config.mStringServerPort, Config.mStringDeviceName,
							Config.mStringDeviceID, Config.mStringDeviceAlias);
				}
				break;
			case Config.SERVICE_MESSAGE_unregister:
				if (mPUInterface != null) {
					mPUInterface.Login(Config.mStringServerAddr,
							"9703", Config.mStringDeviceName,
							Config.mStringDeviceID, Config.mStringDeviceAlias);
					bIsNeedStopRefreshTimer=true;
				}
				break;
			case Config.SERVICE_MESSAGE_CHANGE_CAMERA:
				if (bIsBackCamera == false) {
					bIsBackCamera = true;
					Log.d("MPU", "��������ͷ");
					switch(Config.mVideoQuality){
						case 0:
							Config.mVideoWidth = Config.mBackCIFVideoWidth;
							Config.mVideoHeight = Config.mBackCIFVideoHeight;
							Config.mVideoBaudrate = Config.mBackCIFVideoBaudrate;
							Config.mVideoFrameInterval = Config.mBackCIFVideoFrameInterval;
							break;
						case 1:
							Config.mVideoWidth = Config.mBackVGAVideoWidth;
							Config.mVideoHeight = Config.mBackVGAVideoHeight;
							Config.mVideoBaudrate = Config.mBackVGAVideoBaudrate;
							Config.mVideoFrameInterval = Config.mBackVGAVideoFrameInterval;
							break;
						case 2:
							Config.mVideoWidth = Config.mBackD1VideoWidth;
							Config.mVideoHeight = Config.mBackD1VideoHeight;
							Config.mVideoBaudrate = Config.mBackD1VideoBaudrate;
							Config.mVideoFrameInterval = Config.mBackD1VideoFrameInterval;
							break;
					}
				} else {
					bIsBackCamera = false;
					Log.d("MPU", "ǰ������ͷ");
					switch(Config.mVideoQuality){
					case 0:
						Config.mVideoWidth = Config.mFrontCIFVideoWidth;
						Config.mVideoHeight = Config.mFrontCIFVideoHeight;
						Config.mVideoBaudrate = Config.mFrontCIFVideoBaudrate;
						Config.mVideoFrameInterval = Config.mFrontCIFVideoFrameInterval;
						break;
					case 1:
						Config.mVideoWidth = Config.mFrontVGAVideoWidth;
						Config.mVideoHeight = Config.mFrontVGAVideoHeight;
						Config.mVideoBaudrate = Config.mFrontVGAVideoBaudrate;
						Config.mVideoFrameInterval = Config.mFrontVGAVideoFrameInterval;
						break;
					case 2:
						Config.mVideoWidth = Config.mFrontD1VideoWidth;
						Config.mVideoHeight = Config.mFrontD1VideoHeight;
						Config.mVideoBaudrate = Config.mFrontD1VideoBaudrate;
						Config.mVideoFrameInterval = Config.mFrontD1VideoFrameInterval;
						break;
					}
				}
				if (mPUInterface != null) {
					mPUInterface.SelectCamera(bIsBackCamera);
				}
				break;
			case Config.SERVICE_MESSAGE_AUTO_FOCUS:
				break;
			case Config.SERVICE_MESSAGE_LED_SWITCH:
				break;
			case Config.SERVICE_MESSAGE_START_RECORD:
				if (mPUInterface != null) {
					Log.i(Config.TAG,"StartRecord");
					if("".equals(strStoreagePath)){
						strStoreagePath = Utils.getStoragePath(mContext);
					}
					mPUInterface.StartRecord(strStoreagePath, "mpu.mkv",//�����ļ���Ϊ��.��Ϊ�����ļ���
							("" + Config.RECORD_DURATION_TIME));
					if(recordViewHandler != null){
						recordView.setDuration(0);
						recordViewHandler.sendEmptyMessage(1);
					}
					Config.mVideoSwitch=true;
				}
				break;
			case Config.SERVICE_MESSAGE_STOP_RECORD:
				if (mPUInterface != null) {
					Log.i(Config.TAG,"StopRecord");
					mPUInterface.StopRecord();
					if(recordViewHandler != null){
						recordViewHandler.sendEmptyMessage(2);
						recordViewHandler.removeMessages(1);
					}
					Config.mVideoSwitch=false;
				}
				break;
			case Config.SERVICE_MESSAGE_ZOOM_DOWN:
				if (mPUInterface != null) {
				}
				break;
			case Config.SERVICE_MESSAGE_ZOOM_UP:
				if (mPUInterface != null) {
				}
				break;
			case Config.SERVICE_MESSAGE_AUDIO_SWITCH:
				if (mPUInterface != null) {
				}
				break;
			case Config.SERVICE_MESSAGE_CHANGE_ZOOM:
				if(mPUInterface != null){
					int curZoomValue = msg.getData().getInt("progress");
					mPUInterface.ChangeZoomValue(curZoomValue);
					break;
				}
			case Config.SERVICE_MESSAGE_TAKEPICTRUE:
				if(mPUInterface != null){
					mPUInterface.TakePictrue();
					VideoUtils.vibrateOnce(getApplicationContext()); // ��һ��
				}
				break;
			
			}
		}
	};

	public Handler getHandler() {
		return mHandler;
	}

	public Intent getStartIntent() {
		return mStartIntent;
	}

	private boolean createFloatView() {

		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();

		// ����window type
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

		wmParams.format = PixelFormat.RGBA_8888; // ����ͼƬ��ʽ��Ч��Ϊ����͸��

		// ����Window flag
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// �����������ĳ��ÿ�
		wmParams.width = Config.viewWidth;
		wmParams.height = Config.viewHeight;

		// ���������������Ͻ�
		wmParams.gravity = Gravity.TOP | Gravity.RIGHT;

		recordView = new RecordView(mContext, wm, wmParams);
		if (recordView == null) {
			return false;
		}
		recordView.setHandler(mHandler);
		
		wm.addView(recordView, wmParams);

		return true;
	}
	
	private void StartRefreshFrameRateTimer(){
		bIsNeedStopRefreshTimer = false;
		timerRfreshFrameRateThread = new RefreshFrameRateTimerThread();
		timerRfreshFrameRateThread.start();
	}	
		
	private void StopRefreshFrameRateTimer(){
		bIsNeedStopRefreshTimer = true;
	}
	
	class RefreshFrameRateTimerThread extends Thread {
        public void run() {
        	int lastFrameCount = 0;
        	int lastUploadCount = 0;
        	int currFrameCount = 0;
        	int currUploadCount = 0;
        	float fFrameRate =0;
        	float fBaudRate  =0;
            try {
            	while(false == bIsNeedStopRefreshTimer) {
            		Thread.sleep(Config.FRAME_RATE_REFRESH_TIME);
            		
            		if(bIsNeedStopRefreshTimer){
            			break;
            		}
            		currFrameCount = mPUInterface.GetFrameCount();
            		currUploadCount = mPUInterface.GetUploadCount();
					if(lastFrameCount == 0){
						
						lastFrameCount  = currFrameCount;
						lastUploadCount = currUploadCount;
								
						continue;
					}
					
					fFrameRate = ((currFrameCount - lastFrameCount) * 1000) / (float)Config.FRAME_RATE_REFRESH_TIME;
					fBaudRate  = ((currUploadCount - lastUploadCount) * 8) / (float)Config.FRAME_RATE_REFRESH_TIME;
					
					lastFrameCount  = currFrameCount;
					lastUploadCount = currUploadCount;
					
					String strFrameRate = "" + fFrameRate + "fps";
					String strBaudRate = "" + fBaudRate + "k/bps";
									
					if(recordViewHandler != null){
						Message msg = recordViewHandler.obtainMessage();
						Bundle data = new Bundle();
						
						data.putString("framerate", strFrameRate);
						data.putString("netrate", strBaudRate);
						
						msg.what = Config.SEND_MESSAGE_REFRESH_FRAME_RATE;
						msg.setData(data);
						
						recordViewHandler.sendMessage(msg);
					}
				 }
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }	 	             	 
         }
    }
	
	private void StartRecordTimer(){
		bIsNeedStopRefreshTimer = false;
		timerRecordThread = new RecordTimerThread();
		timerRecordThread.start();
	}	
		
	private void StopRecordTimer(){
		bIsNeedStopRecordTimer = true;
	}
	
	class RecordTimerThread extends Thread {
        public void run() {
        	boolean bIsStartRecord = false;
            try {
            	while(false == bIsNeedStopRecordTimer) {
					if(mHandler != null){
						if(bIsStartRecord == false){
							Message msg = mHandler.obtainMessage();
							msg.what = Config.SERVICE_MESSAGE_START_RECORD;
							mHandler.sendMessage(msg);
							bIsStartRecord = true;
						}else{
							Message msg = mHandler.obtainMessage();
							msg.what = Config.SERVICE_MESSAGE_STOP_RECORD;
							mHandler.sendMessage(msg);
							bIsStartRecord = false;
							Thread.sleep(100);
							
						    continue;
						}
					}
					
					Thread.sleep(Config.RECORD_DURATION_TIME * 1000);
					
					if(bIsNeedStopRecordTimer){
            			break;
            		}
				 }
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }	 	             	 
         }
    }

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			event.startTracking();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			event.startTracking();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			event.startTracking();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isDarkScreen) {
			    if (System.currentTimeMillis() - lastPressBack <= 3000) {
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				} else {
					Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
					lastPressBack = System.currentTimeMillis();
				}
			return true;
		    }
		}
		return super.onKeyDown(keyCode, event);
	}
	/**��������*/
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		isLongPress = true;
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			if (isDarkScreen) {
				//�ر�α��Ƶ
				mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_DarkScreen);
				recordViewHandler.sendEmptyMessage(3);
				isDarkScreen = false;
			} else {
				//��α��Ƶ
				mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_NoDarkScreen);
				recordViewHandler.sendEmptyMessage(4);
				isDarkScreen = true;
			}
			return true;
		}
		// �л��ش�������
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_ENTER) {
			if(Config.mIsLogin){
				mHandler
						.sendEmptyMessage(Config.SERVICE_MESSAGE_unregister);
				VideoUtils.vibrateOnce(this);
				if(Config.mVideoSwitch){
					recordViewHandler.sendEmptyMessage(7);
				}else
					recordViewHandler.sendEmptyMessage(8);
				Config.mIsLogin=false;
			}else{
				mHandler
				      .sendEmptyMessage(Config.SERVICE_MESSAGE_REGISTER);
				VideoUtils.vibrateOnce(this);
				if(Config.mVideoSwitch){
					recordViewHandler.sendEmptyMessage(5);
				}else
					recordViewHandler.sendEmptyMessage(6);
				Config.mIsLogin=true;
			}
		}
		return super.onKeyLongPress(keyCode, event);
	}
	
	 /**����̧��*/
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {

			if (isLongPress) {
				isLongPress = false;
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
				if (VideoUtils.getAvailableSizeData() <= 2) {
					//�洢�ռ䲻��
					VideoUtils.vibrateThrice(this);
					recordViewHandler.sendEmptyMessage(5);
				} else {
					//����
					VideoUtils.vibrateOnce(this);
					mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_TAKEPICTRUE);
				}
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
					|| keyCode == KeyEvent.KEYCODE_ENTER) {
				if(Config.mVideoSwitch){
					mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_STOP_RECORD);
					
					VideoUtils.vibrateTwice(this);
				}else{
					mHandler.sendEmptyMessage(Config.SERVICE_MESSAGE_START_RECORD);
					
					VideoUtils.vibrateOnce(this);
				}
				return true;
			}
			return super.onKeyUp(keyCode, event);
		}
	
}
