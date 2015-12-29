package com.example.videotakepicture;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSeekBarChangeListener {

	private VideoView mSurfaceView;

	private ImageView mImageView;

	private TextView mTimeView;

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
	private String imei;
	private ConnectivityManager connManager;
	private NetworkInfo info;
	private Handler handler;

	private static final String ACTION_SIZE = "Action_Size";

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
	}

	public Handler getHandler() {
		return mHandler;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Ӧ������ʱ��������Ļ������������

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		// �������ӵĹ�����
		connManager = (ConnectivityManager) this
									.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo(); // ������Ϣ
	    imei = tm.getDeviceId();

		
		// ��¼��һ�ε�½����Ϣ
		SharedPreferences share = getSharedPreferences("lasthistory",
								Context.MODE_APPEND);
	    //�ж��Ƿ������°�װ
		boolean isAut = share.getBoolean("authentication",false);
		
		handler = new Handler() {
			 @Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				String str = msg.obj.toString();
				if("allow".equals(str)){//��֤ͨ��
					saveLastConfig();
					VideoUtils.showDialog(MainActivity.this, "��֤ͨ�������¿������ʹ��");
					
				}else if("forbidden".equals(str)){//��֤ʧ��
					VideoUtils.showDialog(MainActivity.this, "��֤ʧ����������ṩ����ϵ");
				
			   }else if("network".equals(str)){//������԰�ť������֤
					info = connManager.getActiveNetworkInfo(); // ������Ϣ
					if (info != null && info.isAvailable()) {
						 checkIMEI(imei);
				    }else
				    	VideoUtils.openNet(MainActivity.this, "��ȷ�������Ѿ�������������֤");
				}else if("serverDis".equals(str)){
					VideoUtils.showDialog(MainActivity.this, "��������æ....");
					//Toast.makeText(MainActivity.this, "��������æ", Toast.LENGTH_SHORT).show();
				}
					else{//������֤
						VideoUtils.showDialog(MainActivity.this, "��֤ʧ����������ṩ����ϵ");
				}
			}
		 };
		Config.mhandler=handler;
		if(!isAut){
			if(info != null && info.isAvailable()){
			    checkIMEI(imei);
			}else
				VideoUtils.openNet(MainActivity.this, "��ȷ�������Ѿ�������������֤");
		}else{
			setContentView(R.layout.main);
	
			new VideoUtils(this);
	
			VideoUtils.createDirectory2Store(this);
			VideoUtils.createFilePath(this);
	
			screenWidth = getWindowManager().getDefaultDisplay().getWidth();
			screenHeight = getWindowManager().getDefaultDisplay().getHeight();
	
			findView();
	
			sizeView.setText(VideoUtils.getAvailableSizeData() + "G/"
					+ VideoUtils.getTotalSizeData() + "G");
	
			changeZoom.setOnSeekBarChangeListener(this);
	
			// onAttachedToWindow();
	
			registerBoradcastReceiver();
	
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						sendEmptyMessageDelayed(1, 1000);
						duration += 1;
						if (duration >= 15 * 60) {
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
						mTimeView.setText(VideoUtils.time2String(duration));
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
					}else if(msg.what == 3){
						Intent intent = new Intent(ACTION_SIZE);
						sendBroadcast(intent);
						mSurfaceView.startRecord(); // ����¼��
						mTimeView.setVisibility(View.VISIBLE);
					}
				}
			};
			Handler handler1 = new Handler() {
				 @Override
			  	    public void handleMessage(Message msg) {
			  	      //  super.handleMessage(msg);
			  	      String srt = msg.obj.toString();
			  	      if("Start".equals(srt)){
			  	    	if(!mSurfaceView.isRecording()){
			  	    		try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			  	    		mSurfaceView.startRecord();
			  	    	}
			  	    	
			  	    	mTimeView.setText(VideoUtils.time2String(duration));
						changeZoom.setMax(maxChangeZomm);
			  	      }
				 }
			 };
			 Config.mStartHandler=handler1;
		}
	}

	
	public void saveLastConfig() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"lasthistory", Context.MODE_APPEND);
		Editor edit = sharedPreferences.edit(); // ��ȡ�༭��
		Config.mIsAuth=true;
		edit.putBoolean("authentication", Config.mIsAuth);
		edit.commit(); // �ύ����
		
	}
	private void checkIMEI(String eimi) {
		CheckSocketThread thread = new CheckSocketThread(eimi);
		thread.start();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSurfaceView.stopRecord();
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
				Config.mStop=false;
				mTimeView.setVisibility(View.INVISIBLE);
			} else {
				if (VideoUtils.getAvailableSizeData() <= 2) {
					VideoUtils.vibrateThrice(MainActivity.this);
					sizeView.setText("�洢�ռ䲻�㣡");
					mTimeView.setVisibility(View.INVISIBLE);
				} else {
					Intent intent = new Intent(ACTION_SIZE);
					sendBroadcast(intent);
					VideoUtils.vibrateOnce(this); // ��һ��
					mSurfaceView.startRecord(); // ����¼��
					mTimeView.setVisibility(View.VISIBLE);
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
