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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		// 网络连接的管理器
		connManager = (ConnectivityManager) this
									.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo(); // 网络信息
	    imei = tm.getDeviceId();

		
		// 记录上一次登陆的信息
		SharedPreferences share = getSharedPreferences("lasthistory",
								Context.MODE_APPEND);
	    //判断是否是重新安装
		boolean isAut = share.getBoolean("authentication",false);
		
		handler = new Handler() {
			 @Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				String str = msg.obj.toString();
				if("allow".equals(str)){//验证通过
					saveLastConfig();
					VideoUtils.showDialog(MainActivity.this, "验证通过请重新开启软件使用");
					
				}else if("forbidden".equals(str)){//验证失败
					VideoUtils.showDialog(MainActivity.this, "验证失败请与软件提供商联系");
				
			   }else if("network".equals(str)){//点击重试按钮进行验证
					info = connManager.getActiveNetworkInfo(); // 网络信息
					if (info != null && info.isAvailable()) {
						 checkIMEI(imei);
				    }else
				    	VideoUtils.openNet(MainActivity.this, "请确认网络已经开启，进行验证");
				}else if("serverDis".equals(str)){
					VideoUtils.showDialog(MainActivity.this, "服务器繁忙....");
					//Toast.makeText(MainActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
				}
					else{//正在验证
						VideoUtils.showDialog(MainActivity.this, "验证失败请与软件提供商联系");
				}
			}
		 };
		Config.mhandler=handler;
		if(!isAut){
			if(info != null && info.isAvailable()){
			    checkIMEI(imei);
			}else
				VideoUtils.openNet(MainActivity.this, "请确认网络已经开启，进行验证");
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
								sizeView.setText("存储空间不足！");
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
						mSurfaceView.startRecord(); // 启动录像
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
		Editor edit = sharedPreferences.edit(); // 获取编辑器
		Config.mIsAuth=true;
		edit.putBoolean("authentication", Config.mIsAuth);
		edit.commit(); // 提交数据
		
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
	 * 屏蔽home键
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
					Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
				// RootManager.turnScreenOn(); // 关闭伪锁屏
			} else {
				mImageView.setVisibility(View.VISIBLE);
				VideoUtils.vibrateTwice(this);
				isDarkScreen = true;
				// RootManager.turnScreenOff(); // 进入伪锁屏状态
			}
			return true;
		}
		// 切换前后摄像头
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mSurfaceView.isRecording()) {
				if (mSurfaceView.getmCurrentCameraId() == 1) {
					mSurfaceView.setmCurrentCameraId(0); // 切换至后置摄像头
					VideoUtils.vibrateTwice(this); // 震动两次
				} else if (mSurfaceView.getmCurrentCameraId() == 0) {
					mSurfaceView.setmCurrentCameraId(1); // 切换至前置摄像头
					VideoUtils.vibrateOnce(this); // 震动一次
				}
				mSurfaceView.stopRecord();
				if (VideoUtils.getAvailableSizeData() <= 2) {
					VideoUtils.vibrateThrice(MainActivity.this);
					sizeView.setText("存储空间不足！");
					mTimeView.setVisibility(View.INVISIBLE);
				} else {
					Intent intent = new Intent(ACTION_SIZE);
					sendBroadcast(intent);
					mSurfaceView.startRecord();
				}
			} else {
				if (mSurfaceView.getmCurrentCameraId() == 1) {
					mSurfaceView.setmCurrentCameraId(0); // 切换至后置摄像头
					VideoUtils.vibrateTwice(this); // 震动两次
				} else if (mSurfaceView.getmCurrentCameraId() == 0) {
					mSurfaceView.setmCurrentCameraId(1); // 切换至前置摄像头
					VideoUtils.vibrateOnce(this); // 震动一次
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
				sizeView.setText("存储空间不足！");
			} else {
				mSurfaceView.takePicture();
				VideoUtils.vibrateOnce(this);
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mSurfaceView.isRecording()) {
				VideoUtils.vibrateTwice(this); // 震动两次
				mSurfaceView.stopRecord(); // 停止录像
				Config.mStop=false;
				mTimeView.setVisibility(View.INVISIBLE);
			} else {
				if (VideoUtils.getAvailableSizeData() <= 2) {
					VideoUtils.vibrateThrice(MainActivity.this);
					sizeView.setText("存储空间不足！");
					mTimeView.setVisibility(View.INVISIBLE);
				} else {
					Intent intent = new Intent(ACTION_SIZE);
					sendBroadcast(intent);
					VideoUtils.vibrateOnce(this); // 震动一次
					mSurfaceView.startRecord(); // 启动录像
					mTimeView.setVisibility(View.VISIBLE);
				}
			}
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	// 按下去与松开的X抽数据
	int downx, upx;

	// 点击屏幕右上角 进行录像屏幕的显示和隐藏 屏幕的触摸时间监听
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
			downx = (int) event.getX(); // 获取按下的X轴数据
			return false;
		} else if (!isDarkScreen && event.getAction() == MotionEvent.ACTION_UP
				&& event.getY() > screenHeight * 10 / 13
				&& event.getY() < screenHeight * 11 / 13) {
			upx = (int) event.getX();// 获取松开的X抽数据
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
		// 注册广播
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
