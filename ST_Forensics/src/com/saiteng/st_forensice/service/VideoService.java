package com.saiteng.st_forensice.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.saiteng.st_forensics.Config;
import com.saiteng.st_forensics.R;
import com.saiteng.st_forensics.view.VideoUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class VideoService extends Service implements SurfaceHolder.Callback{
	private Context mcontext=null;
	private SurfaceView surfaceview;
	private SurfaceHolder surfaceholder;
	private WindowManager wm;
	private WindowManager.LayoutParams wmParams;
	private MediaRecorder mediaRecorder;
	private Camera mCamera;
	private Camera.Parameters localParameters;
	private int mCurrentCameraId = 1; // 1 代表前置摄像头 0 代表后置摄像头 ;
	private int mStartId;
	private NotificationManager notificationManager;
	private Notification notification;
	private StopForensics mReceiver;
	private String ACTION_STOP="com.saiteng.st_forensics.stopservice";
	private String ACTION_CHANGE="com.saiteng.st_forensics.change";
	public final static String INTENT_NAME = "videoService";
    public final static int INTENT_BTN_LOGIN = 1;
	private Handler handler;
	private boolean isRecording=false;//是否在录像
	private boolean isFullwm = false;//是否显示了预览
	private SensorManager sensorManager;  //监听手机晃动功能 
    private Vibrator vibrator;     
    private static final int SENSOR_SHAKE = 10; 
    private static VideoService svideoService;
    private int currentVolume;//当前音量
	public AudioManager mAudioManager;//音量管理对象
	private int maxVolume;//系统最大音量
    private Thread volumeChangeThread;//监听音量按键的线程
	private boolean isDestroy;//确保程序关闭后线程关闭
	private int duration;//录像计时，每隔15分钟保存一次录像
 	
    //创建实例
    public static VideoService getInstance(Context context) {
		return svideoService;
	}
	 @SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		svideoService=this;
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);   
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
        isDestroy = false;
        // 获得AudioManager对象
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//音乐音量,如果要监听铃声音量变化，则改为AudioManager.STREAM_RING
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        // 注册监听器   
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);   
        // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率   
        
		mcontext = getApplicationContext();
	    surfaceview = new SurfaceView(mcontext);
	    surfaceholder = surfaceview.getHolder();
	    surfaceholder.addCallback(this);
	    surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    //点击预览画面响应事件
	    surfaceview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				wmParams.x = 0;
				wmParams.y = 0;
				wmParams.width = 1;
				wmParams.height = 1;
				wm.updateViewLayout(v, wmParams);
				isFullwm=false;
				return false;
			}
		});
	    createWM();
	    onVolumeChangeListener();
	    if(Config.defaultcamera){
	    	mCurrentCameraId=0;
	    }else
	    	mCurrentCameraId=1;
	    handler = new Handler(){
	    	
	    	@Override
	    	public void handleMessage(Message msg) {
	    		// TODO Auto-generated method stub
	    		super.handleMessage(msg);
	    		if(msg.what==1){
	    			sendEmptyMessageDelayed(1, 1000);
					duration += 1;
					if (duration >= Config.recordtime * 60) {
						//根据设置的预设时间进行自动保存文件
						if (VideoUtils.getAvailableSizeData() <= 2) {
							VideoUtils.vibrateThrice(mcontext);
							notification.contentView.setTextViewText(R.id.tv_down,"存储空间不足");
							notificationManager.notify(100, notification);
							stopRecorder();
						}
					}
					notification.contentView.setTextViewText(R.id.tv_down,"正在录像"+ VideoUtils.time2String(duration));
					notificationManager.notify(100, notification);
	    		}
	    	}
	    };
	    
	    
	}
	 /**
	     * 持续监听音量变化 说明： 当前音量改变时，将音量值重置为最大值减2，为响应蓝牙遥控
	     */
	    public void onVolumeChangeListener()
	    {
	        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
	        volumeChangeThread = new Thread()
	        {
	            public void run()
	            {
	                while (!isDestroy)
	                    {
	                        int count = 0;
	                        boolean isDerease = false;
	                        // 监听的时间间隔
	                        try
	                            {
	                                Thread.sleep(20);
	                            } catch (InterruptedException e)
	                            {
	                               
	                            }
	 
	                        if (currentVolume < mAudioManager.getStreamVolume(AudioManager.STREAM_RING))
	                            {
	                                count++;
	                                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
	                                // 设置音量等于 maxVolume-2的原因是：当音量值是最大值和最小值时，按音量加或减没有改变，所以每次都设置为固定的值。
	                                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume - 2,
	                                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	                            }
	                        if (currentVolume > mAudioManager.getStreamVolume(AudioManager.STREAM_RING))
	                            {
	                                count++;
	                                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
	                                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume - 2,
	                                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	                                if (count == 1)
	                                    {
	                                        isDerease = true;
	                                    }
	                            }
	                        if (count == 2) {
	                        	//音量加按键触发录像开始结束。
	                        	if(isRecording){
	                        		stopRecorder();
		        					isRecording=false;
		        					notification.contentView.setTextViewText(R.id.tv_down,"录像停止"+ VideoUtils.time2String(0));
		        					notificationManager.notify(100, notification);
	                        	}else{
	                        		new Thread() {
	            						@Override
	            						public void run() {
	            							startRecorder();
	            						}
	            					}.start();
	            					isRecording=true;
	                        	}
	                        	Log.i("VideoService","+++++");
	 
	                            } else if (isDerease)
	                            {
	                            	//音量减键触发服务关闭
	                            	stopRecorder();
	                            	stopservice();
	                            	Log.i("VideoService","-----");
	                            }
	 
	                }
	            };
	        };
	        volumeChangeThread.start();
	    }
	   /** 
	     * 重力感应监听，响应手机摇动
	     */   
	    private SensorEventListener sensorEventListener = new SensorEventListener() {   
	        @Override   
	        public void onSensorChanged(SensorEvent event) {   
	            // 传感器信息改变时执行该方法   
	            float[] values = event.values;   
	            float x = values[0]; // x轴方向的重力加速度，向右为正   
	            float y = values[1]; // y轴方向的重力加速度，向前为正   
	            float z = values[2]; // z轴方向的重力加速度，向上为正   
	            
	            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。   
	            int medumValue = Config.medumValue;// 如果不敏感请自行调低该数值,低于10的话就不行了,因为z轴上的加速度本身就已经达到10了                
	        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {   
	        	if(Config.mproofread){
	        		//选择了实时校对才能执行震动事件
	        	    vibrator.vibrate(200);   
	                Message msg = new Message();   
	                msg.what = SENSOR_SHAKE;   
	                handler1.sendMessage(msg); 
	        	 }
	            }   
	        }   
	        @Override   
	        public void onAccuracyChanged(Sensor sensor, int accuracy) {   
	   
	        }   
	    };   
	    /** 
	      * 响应摇晃手机
	      */   
	  Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SENSOR_SHAKE:
				if(!isFullwm){
					wmParams.x =0;
					wmParams.y = 0;
					wmParams.width = 500;
					wmParams.height = 760;
					wm.updateViewLayout(surfaceview, wmParams);
					isFullwm=true;
                 }else{
                	wmParams.x =0;
 					wmParams.y = 0;
 					wmParams.width = 1;
 					wmParams.height = 1;
 					wm.updateViewLayout(surfaceview, wmParams);
 					isFullwm=false;
				 }
					break;
				
			}
		}
	}; 
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		mStartId = startId;
		CreateInform();
		
	}
    /**创建悬浮窗口，并将surfaceView添加到改窗口*/
	private void createWM() {
		wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();

		//设置window type
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

		//设置Window flag
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		//根据选择录像前是否有预览 设置悬浮窗的长得宽
		if(Config.mstartPreview){
			wmParams.width = 500;
			wmParams.height = 760;
			isFullwm=true;
			Config.mstartPreview=false;
		}else{
			wmParams.width = 1;
			wmParams.height =1;
		}

		// 调整悬浮窗到右上角
		wmParams.gravity = Gravity.TOP | Gravity.RIGHT;
		wm.addView(surfaceview, wmParams);
		
	}
	/**创建通知栏*/
	public void CreateInform() {
		unregeisterReceiver();
		intiReceiver();
		//获取NotificationManager
	    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		// 设置未下拉通知栏时弹出的提示
		// 通知时在状态栏显示的内容
		notification.tickerText = "已启动省电服务";
		notification.icon = R.drawable.bulb_front;
		// 设置下拉通知栏显示的内容
		// 构建一个远程View赋给Notification的contentView
		notification.contentView = new RemoteViews(getPackageName(), R.layout.notification);

		notification.contentView.setTextViewText(R.id.tv_up, "省电模式正在启用");
		notification.contentView.setTextViewText(R.id.tv_down, "点击可开始录像");
		notification.contentView.setImageViewResource(R.id.icon_t, R.drawable.bulb_front);

		Intent intent = new Intent(ACTION_CHANGE);
		intent.putExtra(INTENT_NAME, INTENT_BTN_LOGIN);
		PendingIntent intentpi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentView.setOnClickPendingIntent(R.id.btn_login, intentpi);

		Intent intent2 = new Intent(ACTION_STOP);
		intent2.putExtra(INTENT_NAME, INTENT_BTN_LOGIN);
		PendingIntent intentContent = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent =intentContent;
    	notificationManager.notify(100, notification);
	}  
	 /**注册通知栏广播*/
	 private void intiReceiver() {
		mReceiver = new StopForensics();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_STOP);
		getApplicationContext().registerReceiver(mReceiver, intentFilter);

		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction(ACTION_CHANGE);
		getApplicationContext().registerReceiver(mReceiver, intentFilter1);
	}
	 /**更新通知栏*/
	 public void updateNotification(){
		 
	 }

	private void unregeisterReceiver() {
		if (mReceiver != null) {
			getApplicationContext().unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}
	public class StopForensics extends BroadcastReceiver{
       // 响应通知栏的事件
		@Override
		public void onReceive(Context context, Intent intent) {
			if(ACTION_STOP.equals(intent.getAction())){
				if(isRecording){
					stopRecorder();
					isRecording=false;
					notification.contentView.setTextViewText(R.id.tv_down,"录像停止"+ VideoUtils.time2String(0));
					notificationManager.notify(100, notification);
				}else{
					new Thread() {
						@Override
						public void run() {
							startRecorder();
						}
					}.start();
					isRecording=true;
					}
				
			}if(ACTION_CHANGE.equals(intent.getAction())){
				//切换摄像头要重新录像
				if(mCurrentCameraId==1){
					mCurrentCameraId=0;
					notification.contentView.setImageViewResource(R.id.icon_t, R.drawable.bulm_back);
					notificationManager.notify(100, notification);
				}else{
					mCurrentCameraId=1;
					notification.contentView.setImageViewResource(R.id.icon_t, R.drawable.bulb_front);
					notificationManager.notify(100, notification);	
				}
				if(isRecording){
					stopRecorder();
				}
				new Thread() {
					@Override
					public void run() {
						startRecorder();
					}
				}.start();
				isRecording=true;
			}
		}
	 }
	
	 /**开启录像功能*/
	@SuppressWarnings("deprecation")
	private boolean startRecorder(){
		mediaRecorder = new MediaRecorder();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (mCamera == null) {
			mCamera = Camera.open(mCurrentCameraId);
			// Log.d("geek", mCurrentCameraId+"   s");
			localParameters = this.mCamera.getParameters();
			localParameters.setPreviewFrameRate(5); // 设置每秒显示4帧
			// localParameters.setPictureSize(width, height); // 设置保存的图片尺寸
			localParameters.setJpegQuality(80); // 设置照片质量
			// 设置照片格式
			localParameters.setPictureFormat(PixelFormat.JPEG);

//			if (localParameters.isZoomSupported()) {
//				nMaxZoomValue = localParameters.getMaxZoom(); // 获取最大的焦距
//				((MainActivity) getContext()).setMaxChangeZomm(nMaxZoomValue);
//				nCurZoomValue = localParameters.getZoom(); // 获取当前的焦距
//				isZoomSupportOfCamera[mCurrentCameraId] = 1;
//			}
			
			mCamera.setDisplayOrientation(0);
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
		mediaRecorder.setPreviewDisplay(surfaceholder.getSurface());
		mediaRecorder.setOutputFile(VideoUtils.generateFileName());
		if (this.mCurrentCameraId == CameraInfo.CAMERA_FACING_FRONT) {
			mediaRecorder.setOrientationHint(270);
		} else {
			mediaRecorder.setOrientationHint(90);
		}
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			isRecording=true;
			setDuration(0);
			handler.sendEmptyMessage(1);
			VideoUtils.vibrateOnce(mcontext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	/**停止录像*/
	public boolean stopRecorder(){
		handler.removeMessages(1);
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
			mCamera.release();
			mCamera=null;
			isRecording=false;
			VideoUtils.vibrateOnce(mcontext);
		}
		return false;
	}
	/**停止整个服务服务*/
	public void stopservice(){
		notificationManager.cancel(100);
		sensorManager.unregisterListener(sensorEventListener);
		unregeisterReceiver();
		isDestroy = true;
		wm.removeView(surfaceview);
		surfaceview=null;
		stopSelf(mStartId);
		svideoService=null;
		onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceholder = holder;
//		new Thread() {
//			@Override
//			public void run() {
//				startRecorder();
//			}
//		}.start();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		surfaceholder = holder;
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 public void setDuration(int duration) {
		this.duration = duration;
	}
	 PictureCallback pictureCallback = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				File file = new File(VideoUtils.filePath(),
						VideoUtils.getDate(System.currentTimeMillis()) + ".rar");
				try {
					FileOutputStream outputStream = new FileOutputStream(file);
					outputStream.write(data); // 写入sd卡中
					outputStream.close(); // 关闭输出流
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
		 * 照相的方法，回调函数
		 */
		public void takePicture() {
			mCamera.takePicture(null, null, pictureCallback);
		}

	
	
}
