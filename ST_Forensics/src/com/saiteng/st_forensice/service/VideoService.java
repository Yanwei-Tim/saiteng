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
	private int mCurrentCameraId = 1; // 1 ����ǰ������ͷ 0 �����������ͷ ;
	private int mStartId;
	private NotificationManager notificationManager;
	private Notification notification;
	private StopForensics mReceiver;
	private String ACTION_STOP="com.saiteng.st_forensics.stopservice";
	private String ACTION_CHANGE="com.saiteng.st_forensics.change";
	public final static String INTENT_NAME = "videoService";
    public final static int INTENT_BTN_LOGIN = 1;
	private Handler handler;
	private boolean isRecording=false;//�Ƿ���¼��
	private boolean isFullwm = false;//�Ƿ���ʾ��Ԥ��
	private SensorManager sensorManager;  //�����ֻ��ζ����� 
    private Vibrator vibrator;     
    private static final int SENSOR_SHAKE = 10; 
    private static VideoService svideoService;
    private int currentVolume;//��ǰ����
	public AudioManager mAudioManager;//�����������
	private int maxVolume;//ϵͳ�������
    private Thread volumeChangeThread;//���������������߳�
	private boolean isDestroy;//ȷ������رպ��̹߳ر�
	private int duration;//¼���ʱ��ÿ��15���ӱ���һ��¼��
 	
    //����ʵ��
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
        // ���AudioManager����
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//��������,���Ҫ�������������仯�����ΪAudioManager.STREAM_RING
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        // ע�������   
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);   
        // ��һ��������Listener���ڶ������������ô��������ͣ�����������ֵ��ȡ��������Ϣ��Ƶ��   
        
		mcontext = getApplicationContext();
	    surfaceview = new SurfaceView(mcontext);
	    surfaceholder = surfaceview.getHolder();
	    surfaceholder.addCallback(this);
	    surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    //���Ԥ��������Ӧ�¼�
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
						//�������õ�Ԥ��ʱ������Զ������ļ�
						if (VideoUtils.getAvailableSizeData() <= 2) {
							VideoUtils.vibrateThrice(mcontext);
							notification.contentView.setTextViewText(R.id.tv_down,"�洢�ռ䲻��");
							notificationManager.notify(100, notification);
							stopRecorder();
						}
					}
					notification.contentView.setTextViewText(R.id.tv_down,"����¼��"+ VideoUtils.time2String(duration));
					notificationManager.notify(100, notification);
	    		}
	    	}
	    };
	    
	    
	}
	 /**
	     * �������������仯 ˵���� ��ǰ�����ı�ʱ��������ֵ����Ϊ���ֵ��2��Ϊ��Ӧ����ң��
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
	                        // ������ʱ����
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
	                                // ������������ maxVolume-2��ԭ���ǣ�������ֵ�����ֵ����Сֵʱ���������ӻ��û�иı䣬����ÿ�ζ�����Ϊ�̶���ֵ��
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
	                        	//�����Ӱ�������¼��ʼ������
	                        	if(isRecording){
	                        		stopRecorder();
		        					isRecording=false;
		        					notification.contentView.setTextViewText(R.id.tv_down,"¼��ֹͣ"+ VideoUtils.time2String(0));
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
	                            	//����������������ر�
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
	     * ������Ӧ��������Ӧ�ֻ�ҡ��
	     */   
	    private SensorEventListener sensorEventListener = new SensorEventListener() {   
	        @Override   
	        public void onSensorChanged(SensorEvent event) {   
	            // ��������Ϣ�ı�ʱִ�и÷���   
	            float[] values = event.values;   
	            float x = values[0]; // x�᷽����������ٶȣ�����Ϊ��   
	            float y = values[1]; // y�᷽����������ٶȣ���ǰΪ��   
	            float z = values[2]; // z�᷽����������ٶȣ�����Ϊ��   
	            
	            // һ����������������������ٶȴﵽ40�ʹﵽ��ҡ���ֻ���״̬��   
	            int medumValue = Config.medumValue;// ��������������е��͸���ֵ,����10�Ļ��Ͳ�����,��Ϊz���ϵļ��ٶȱ�����Ѿ��ﵽ10��                
	        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {   
	        	if(Config.mproofread){
	        		//ѡ����ʵʱУ�Բ���ִ�����¼�
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
	      * ��Ӧҡ���ֻ�
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
    /**�����������ڣ�����surfaceView��ӵ��Ĵ���*/
	private void createWM() {
		wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();

		//����window type
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

		wmParams.format = PixelFormat.RGBA_8888; // ����ͼƬ��ʽ��Ч��Ϊ����͸��

		//����Window flag
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		//����ѡ��¼��ǰ�Ƿ���Ԥ�� �����������ĳ��ÿ�
		if(Config.mstartPreview){
			wmParams.width = 500;
			wmParams.height = 760;
			isFullwm=true;
			Config.mstartPreview=false;
		}else{
			wmParams.width = 1;
			wmParams.height =1;
		}

		// ���������������Ͻ�
		wmParams.gravity = Gravity.TOP | Gravity.RIGHT;
		wm.addView(surfaceview, wmParams);
		
	}
	/**����֪ͨ��*/
	public void CreateInform() {
		unregeisterReceiver();
		intiReceiver();
		//��ȡNotificationManager
	    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		// ����δ����֪ͨ��ʱ��������ʾ
		// ֪ͨʱ��״̬����ʾ������
		notification.tickerText = "������ʡ�����";
		notification.icon = R.drawable.bulb_front;
		// ��������֪ͨ����ʾ������
		// ����һ��Զ��View����Notification��contentView
		notification.contentView = new RemoteViews(getPackageName(), R.layout.notification);

		notification.contentView.setTextViewText(R.id.tv_up, "ʡ��ģʽ��������");
		notification.contentView.setTextViewText(R.id.tv_down, "����ɿ�ʼ¼��");
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
	 /**ע��֪ͨ���㲥*/
	 private void intiReceiver() {
		mReceiver = new StopForensics();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_STOP);
		getApplicationContext().registerReceiver(mReceiver, intentFilter);

		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction(ACTION_CHANGE);
		getApplicationContext().registerReceiver(mReceiver, intentFilter1);
	}
	 /**����֪ͨ��*/
	 public void updateNotification(){
		 
	 }

	private void unregeisterReceiver() {
		if (mReceiver != null) {
			getApplicationContext().unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}
	public class StopForensics extends BroadcastReceiver{
       // ��Ӧ֪ͨ�����¼�
		@Override
		public void onReceive(Context context, Intent intent) {
			if(ACTION_STOP.equals(intent.getAction())){
				if(isRecording){
					stopRecorder();
					isRecording=false;
					notification.contentView.setTextViewText(R.id.tv_down,"¼��ֹͣ"+ VideoUtils.time2String(0));
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
				//�л�����ͷҪ����¼��
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
	
	 /**����¼����*/
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
			localParameters.setPreviewFrameRate(5); // ����ÿ����ʾ4֡
			// localParameters.setPictureSize(width, height); // ���ñ����ͼƬ�ߴ�
			localParameters.setJpegQuality(80); // ������Ƭ����
			// ������Ƭ��ʽ
			localParameters.setPictureFormat(PixelFormat.JPEG);

//			if (localParameters.isZoomSupported()) {
//				nMaxZoomValue = localParameters.getMaxZoom(); // ��ȡ���Ľ���
//				((MainActivity) getContext()).setMaxChangeZomm(nMaxZoomValue);
//				nCurZoomValue = localParameters.getZoom(); // ��ȡ��ǰ�Ľ���
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
	/**ֹͣ¼��*/
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
	/**ֹͣ�����������*/
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
