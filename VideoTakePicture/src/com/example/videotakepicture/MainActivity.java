package com.example.videotakepicture;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.SimpleFormatter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSeekBarChangeListener {

	private VideoView mSurfaceView;

	private ImageView mImageView;
	
	private Button mBtn_start;

	private TextView mTimeView,mInfo;

	private int duration;

	private Handler mHandler;

	private int screenWidth, screenHeight;

	private long lastPressBack;

	private FrameLayout mVideoLayout;

	private boolean isDarkScreen;

	private boolean isLongPress;

	private SeekBar changeZoom;

	private int maxChangeZomm;

	private int count;

	private static final String ACTION_SIZE = "Action_Size";
	
	private TelephonyManager manager ;
	
	private String result = "�����绰״̬��/n";

	public void setMaxChangeZomm(int maxChangeZomm) {
		this.maxChangeZomm = maxChangeZomm;
	}

	public boolean isDarkScreen() {
		return isDarkScreen;
	}

	public void setDarkScreen(boolean isDarkScreen) {
		this.isDarkScreen = isDarkScreen;
	}

	private void findView() {
		mSurfaceView = (VideoView) findViewById(R.id.videoView);
		mImageView = (ImageView) findViewById(R.id.imageView);
		mTimeView = (TextView) findViewById(R.id.timeView);
		mVideoLayout = (FrameLayout) findViewById(R.id.videoFrame);
		changeZoom = (SeekBar) findViewById(R.id.changeZoom);
		sizeView = (TextView) findViewById(R.id.sizeView);
		mInfo = (TextView) findViewById(R.id.info);
		mBtn_start = (Button) findViewById(R.id.start);
	}

	public Handler getHandler() {
		return mHandler;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Ӧ������ʱ��������Ļ������������

//		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		String imei = tm.getDeviceId();
//		if (!imei.equals("358182063608685")) {
//			VideoUtils.showDialog(this, "�����δ�ڴ˻�������Ȩ!");
//			return;
//		}
		//��ȡ�绰����
        manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        // �ֶ�ע���PhoneStateListener�е�listen_call_state״̬���м���
       
		setContentView(R.layout.main);
		
		new VideoUtils(this);

		setTimer();
		VideoUtils.createDirectory2Store(this);
		VideoUtils.createFilePath(this);

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();

		findView();

		sizeView.setText(VideoUtils.getAvailableSizeData() + "G/"
				+ VideoUtils.getTotalSizeData() + "G");

		changeZoom.setOnSeekBarChangeListener(this);

		 //onAttachedToWindow();

		registerBoradcastReceiver();//�Զ�����ʾ�ڴ�㲥
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					sendEmptyMessageDelayed(1, 1000);
					duration += 1;
					if (duration >= 15 * 60) {//¼��ʱ�䳬��15���ӣ����sd���ڴ�����
						if (VideoUtils.getAvailableSizeData() <= 2) {
							VideoUtils.vibrateThrice(MainActivity.this);
							sizeView.setText("�洢�ռ䲻�㣡");
							mSurfaceView.stopRecord();
							mTimeView.setVisibility(View.INVISIBLE);
						} else {
							Intent intent = new Intent(ACTION_SIZE);
							sendBroadcast(intent);
							mSurfaceView.stopRecord();
							mSurfaceView.startRecord();
						}
					}
					if(mSurfaceView.isRecording()){
						mTimeView.setText(VideoUtils.time2String(duration));
					}else{
						mInfo.setText("�Ѿ�ֹͣ");
					    mTimeView.setVisibility(View.INVISIBLE);}
					
					changeZoom.setMax(maxChangeZomm);
				} else if (msg.what == 2) {
					changeZoom.setVisibility(View.INVISIBLE);
					timers = 0;
					count = 0;
					if (timer != null || task != null) {
						timer.cancel();
						task.cancel();
					}
					if (t != null || tasks != null) {
						t.cancel();
						tasks.cancel();
					}
				}if (msg.what == 3) {
					    mTimeView.setText(VideoUtils.time2String(duration));
					    mTimeView.setVisibility(View.VISIBLE);
						mInfo.setText("����¼��");
					 }
					
				
			}
		};
		mBtn_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ACTION_SIZE);
				sendBroadcast(intent);//�Զ�����ʾ�ڴ�㲥
				mSurfaceView.startRecord(); // ����¼��
				mInfo.setText("�ٴο�������¼��");
				mTimeView.setVisibility(View.VISIBLE);
			}
		});
		
	}
	
	
	
	private void setTimer() {
		
		Timer timer3= new Timer();
		
		TimerTask task3=new TimerTask() {
			
			@Override
			public void run() {
				if(Config.mIsCreate){
					if(mSurfaceView.isRecording()){
						mSurfaceView.stopRecord();
					}
					  mSurfaceView.startRecord(); // ����¼��
					  mHandler.sendEmptyMessage(3);
					  Config.mIsCreate=false;
				}
				
			}
		};
		timer3.schedule(task3, 1000,10000);
	}



	class MyPhoneStateListener extends PhoneStateListener{
	    @Override
	    public void onCallStateChanged(int state, String incomingNumber) {
	      switch (state) {
	      case TelephonyManager.CALL_STATE_IDLE:
	                result+=" �ֻ�����������  ";
			
	                if(!mSurfaceView.isRecording()){
                      Config.mIsCreate=true;
	                	
	                }
	              break;
	      case TelephonyManager.CALL_STATE_RINGING:
	              result+="  �ֻ��������ˣ��������:"+incomingNumber;
	              mInfo.setText("����ֹͣ¼��");
	              mTimeView.setVisibility(View.INVISIBLE);
	              break;
	      case TelephonyManager.CALL_STATE_OFFHOOK:
	    	      mSurfaceView.stopRecord(); //ֹͣ
	              result+=" �绰�������� ";
	      default:
	             break;
	      }
	    
	      super.onCallStateChanged(state, incomingNumber);
	   }
	  }
	
	
	
	/**
	 * ����home��
	 */
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
					mSurfaceView.stopRecord();
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				} else {
					Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
					lastPressBack = System.currentTimeMillis();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
 
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		isLongPress = true;
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			if (isDarkScreen) {
				mImageView.setVisibility(View.GONE);
				VideoUtils.vibrateOnce(this);
				isDarkScreen = false;
				// RootManager.turnScreenOn(); // �ر�α����
			} else {
				mImageView.setVisibility(View.VISIBLE);
				VideoUtils.vibrateTwice(this);
				isDarkScreen = true;
				// RootManager.turnScreenOff(); // ����α����״̬
			}
			return true;
		}
		// �л�ǰ������ͷ
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mSurfaceView.isRecording()) {
				if (mSurfaceView.getmCurrentCameraId() == 1) {
					mSurfaceView.setmCurrentCameraId(0); // �л�����������ͷ
					VideoUtils.vibrateTwice(this); // ������
				} else if (mSurfaceView.getmCurrentCameraId() == 0) {
					mSurfaceView.setmCurrentCameraId(1); // �л���ǰ������ͷ
					VideoUtils.vibrateOnce(this); // ��һ��
				}
				mSurfaceView.stopRecord();
				if (VideoUtils.getAvailableSizeData() <= 2) {
					VideoUtils.vibrateThrice(MainActivity.this);
					sizeView.setText("�洢�ռ䲻�㣡");
					mTimeView.setVisibility(View.INVISIBLE);
				} else {
					Intent intent = new Intent(ACTION_SIZE);
					sendBroadcast(intent);
					mSurfaceView.startRecord();
				}
			} else {
				if (mSurfaceView.getmCurrentCameraId() == 1) {
					mSurfaceView.setmCurrentCameraId(0); // �л�����������ͷ
					VideoUtils.vibrateTwice(this); // ������
				} else if (mSurfaceView.getmCurrentCameraId() == 0) {
					mSurfaceView.setmCurrentCameraId(1); // �л���ǰ������ͷ
					VideoUtils.vibrateOnce(this); // ��һ��
				}
				mSurfaceView.startPreview();
			}
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (isLongPress) {
			isLongPress = false;
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			if (VideoUtils.getAvailableSizeData() <= 2) {
				VideoUtils.vibrateThrice(MainActivity.this);
				sizeView.setText("�洢�ռ䲻�㣡");
			} else {
				mSurfaceView.takePicture();
				VideoUtils.vibrateOnce(this);
				
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mSurfaceView.isRecording()) {
				VideoUtils.vibrateTwice(this); // ������
				mSurfaceView.stopRecord(); // ֹͣ¼��
				mInfo.setText("¼��ֹͣ");
				mTimeView.setVisibility(View.INVISIBLE);
			} else {
				if (VideoUtils.getAvailableSizeData() <= 2) {
					VideoUtils.vibrateThrice(MainActivity.this);
					sizeView.setText("�洢�ռ䲻�㣡");
					mTimeView.setVisibility(View.INVISIBLE);
				} else {
					Intent intent = new Intent(ACTION_SIZE);
					sendBroadcast(intent);//�Զ�����ʾ�ڴ�㲥
					VideoUtils.vibrateOnce(this); // ��һ��
					mSurfaceView.startRecord(); // ����¼��
					mInfo.setText("����¼��");
					mTimeView.setVisibility(View.VISIBLE);
					manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
					
					}

			}
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	// ����ȥ���ɿ���X������
	int downx, upx;

	// �����Ļ���Ͻ� ����¼����Ļ����ʾ������ ��Ļ�Ĵ���ʱ�����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isDarkScreen && event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getX() > screenWidth * 1 / 3
				&& event.getY() < screenHeight * 1 / 2) {
			if (mVideoLayout.getVisibility() == View.VISIBLE) {
				mVideoLayout.setVisibility(View.GONE);
			} else {
				mVideoLayout.setVisibility(View.VISIBLE);
			}
			return true;
		}

		if (!isDarkScreen && event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getY() > screenHeight * 10 / 13
				&& event.getY() < screenHeight * 11 / 13) {
			downx = (int) event.getX(); // ��ȡ���µ�X������
			return false;
		} else if (!isDarkScreen && event.getAction() == MotionEvent.ACTION_UP
				&& event.getY() > screenHeight * 10 / 13
				&& event.getY() < screenHeight * 11 / 13) {
			upx = (int) event.getX();// ��ȡ�ɿ���X������
			if (upx - downx > 50) {
				if (changeZoom.getVisibility() == View.VISIBLE) {
					changeZoom.setVisibility(View.INVISIBLE);
					count = 0;
					timers = 0;
					if (t != null || tasks != null) {
						t.cancel();
						tasks.cancel();
					}
					if (timer != null || task != null) {
						timer.cancel();
						task.cancel();
					}

				} else if (changeZoom.getVisibility() == View.INVISIBLE) {
					changeZoom.setVisibility(View.VISIBLE);
					t = new Timer();
					tasks = new TimerTask() {

						@Override
						public void run() {
							count++;
							if (count >= 3) {
								mHandler.sendEmptyMessage(2);
							}
						}
					};
					t.schedule(tasks, 1000, 1000);
				}
				return true;
			} else if (downx - upx > 50) {
				return true;
			} else {
				Log.d("geek", "Hello");
			}
			return false;
		}
		return super.onTouchEvent(event);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	private int timers;

	private Timer timer;

	private TimerTask task;

	private Timer t;

	private TimerTask tasks;

	private TextView sizeView;

	private BroadcastReceiver MyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == ACTION_SIZE) {
				sizeView.setText(VideoUtils.getAvailableSizeData() + "G/"
						+ VideoUtils.getTotalSizeData() + "G");
			}
		}

	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_SIZE);
		// ע��㲥
		registerReceiver(MyReceiver, myIntentFilter);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		mSurfaceView.changerZoom(progress);
		timers = 0;
		count = 0;

		if (timer != null || task != null) {
			timer.cancel();
			task.cancel();
		}

		if (t != null || tasks != null) {
			t.cancel();
			tasks.cancel();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		timers = 0;
		count = 0;

		if (timer != null || task != null) {
			timer.cancel();
			task.cancel();
		}

		if (t != null || tasks != null) {
			t.cancel();
			tasks.cancel();
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				timers++;
				if (timers >= 3) {
					mHandler.sendEmptyMessage(2);
				}
			}
		};
		timer.schedule(task, 1000, 1000);
		if (t != null || tasks != null) {
			t.cancel();
			tasks.cancel();
		}
	}

}
