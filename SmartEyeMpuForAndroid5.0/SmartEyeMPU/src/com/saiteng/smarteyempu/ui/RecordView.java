package com.saiteng.smarteyempu.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.saiteng.smarteyempu.R;
import com.saiteng.smarteyempu.common.Config;
import com.smarteye.mpu.MPUDefine;
import com.smarteye.mpu.MPUDialogEvent;

public class RecordView extends RelativeLayout implements
		OnSeekBarChangeListener {
	private Context mContext = null;
	private Handler mParentHandler = null;
	private View viewRecordFrame;

	private RelativeLayout viewVideoViewControl;
	private RelativeLayout viewVideoView;

	private SurfaceView viewVideoDisplay;

	private TextView txtNetType;
	private TextView txtVideoQuality;
	private TextView txtNetRate;
	private TextView txtVideoFramerate;
	private int width;
	private int height;
	private SeekBar changeZoom;

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private LayoutParams mVideoViewParams;
	private int timers;
	private Timer timer;
	private TimerTask task;
	private int status;

	public RecordView(Context context, WindowManager wm,
			WindowManager.LayoutParams wmParams) {
		super(context);

		mContext = context;

		mWindowManager = wm;
		mWindowParams = wmParams;
		viewRecordFrame = View.inflate(context, R.layout.record_view, null);

		viewVideoViewControl = (RelativeLayout) viewRecordFrame
				.findViewById(R.id.id_video_view_control);

		viewVideoView = (RelativeLayout) viewRecordFrame
				.findViewById(R.id.id_video_view);

		txtNetType = (TextView) viewRecordFrame.findViewById(R.id.txtNetType);

		txtVideoQuality = (TextView) viewRecordFrame
				.findViewById(R.id.txtVideoQuality);

		txtNetRate = (TextView) viewRecordFrame.findViewById(R.id.txtNetRate);
		txtVideoFramerate = (TextView) viewRecordFrame
				.findViewById(R.id.txtVideoFramerate);
		changeZoom = (SeekBar) viewRecordFrame.findViewById(R.id.changeZoom);
		changeZoom.setOnSeekBarChangeListener(this);
		changeZoom.setMax(30); // 设置最大值

		viewVideoDisplay = (SurfaceView) viewRecordFrame
				.findViewById(R.id.id_video_display_view);

		viewVideoView.setOnTouchListener(viewOnTouchListener);
		viewVideoViewControl.setOnTouchListener(viewOnTouchListener);

		// 注册广播事件，显示当前的网络参数
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(receiver, mFilter);

		// 显示当前的分辨率
		Log.d("geek",Config.mVideoWidth +"  " +Config.mVideoHeight );
		getVideoQuality(Config.mVideoWidth, Config.mVideoHeight);
		this.addView(viewRecordFrame);
	}
	
	public SurfaceView getVideoDisplayView() {
		return viewVideoDisplay;
	}

	public void setHandler(Handler handler) {
		mParentHandler = handler;
	}

	public Handler getHandler() {
		return mHandler;
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Config.SEND_MESSAGE_VISIBILITY_SEEKBAR:
				if (changeZoom.getVisibility() == View.VISIBLE) {
					changeZoom.setVisibility(View.GONE);
					timers = 0;
					if (timer != null || task != null) {
						timer.cancel();
						task.cancel();
					}

				}
				break;
			case Config.SEND_MESSAGE_REFRESH_FRAME_RATE:
				setFrameRate(msg.getData().getString("framerate"));
				setBaudRate(msg.getData().getString("netrate"));
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	OnTouchListener viewOnTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			int action;
			float posx;
			float posy;
			if (null == event) {
				return false;
			}

			action = event.getAction();
			posx = event.getX();
			posy = event.getY();

			Log.i(Config.TAG, "posx:" + posx + "posy:" + posy);

			if (v.getId() == R.id.id_video_view_control) {
				if (action == MotionEvent.ACTION_DOWN) {
					viewVideoViewControl.setVisibility(View.GONE);
					viewVideoView.setVisibility(View.VISIBLE);
					updateViewPosition(Config.viewWidth, Config.viewHeight);
					mVideoViewParams = (LayoutParams) viewVideoDisplay
							.getLayoutParams();
					mVideoViewParams.width = Config.viewWidth;
					mVideoViewParams.height = Config.viewHeight;
					viewVideoDisplay.setLayoutParams(mVideoViewParams);

					return true;
				}
			} else if (v.getId() == R.id.id_video_view) {
				if (action == MotionEvent.ACTION_DOWN) {
					if ((posx < width / 4) && (posy < height / 6)) {
						changeZoom.setProgress(0);
						// 切换前后摄像头
						if (mParentHandler != null) {
							mParentHandler
									.sendEmptyMessage(Config.SERVICE_MESSAGE_CHANGE_CAMERA);
						}
					} else if ((posx >= (width * 3 / 4)) && (posy < height / 6)) {
						if (mParentHandler != null) {
							viewVideoViewControl.setVisibility(View.VISIBLE);
							viewVideoView.setVisibility(View.INVISIBLE);
							updateViewPosition(100, 100);
							mVideoViewParams = (LayoutParams) viewVideoDisplay
									.getLayoutParams();
							mVideoViewParams.width = 1;
							mVideoViewParams.height = 1;
							viewVideoDisplay.setLayoutParams(mVideoViewParams);
						}
					} else if ((posx < (width / 4)) && posy >= (height * 5 / 6)) {
						timers = 0;
						if (timer != null || task != null) {
							timer.cancel();
							task.cancel();
						}

						if (changeZoom.getVisibility() == View.GONE) {
							changeZoom.setVisibility(View.VISIBLE);

						} else if (changeZoom.getVisibility() == View.VISIBLE) {
							changeZoom.setVisibility(View.GONE);
						}
					} else if ((posx >= (width * 3 / 4))
							&& posy >= (height * 5 / 6)) {
						// 退出程序
//						if(Config.mStatus == MPUDefine.BVCU_EVENT_DIALOG_OPEN){
//							System.out.println("无法退出");
//						}else{
							if (mParentHandler != null) {
								mParentHandler
										.sendEmptyMessage(Config.SERVICE_MESSAGE_EXIT);
							}
							mContext.unregisterReceiver(receiver); // 取消注册
						//}
					}
				}
			}
			return true;
		}
	};

	private ConnectivityManager connManager;

	private void updateViewPosition(int width, int height) {
		// 更新浮动窗口位置参数,x是鼠标在屏幕的位置，mTouchStartX是鼠标在图片的位置
		mWindowParams.x = 0;
		mWindowParams.y = 0;
		mWindowParams.width = width;
		mWindowParams.height = height;
		mWindowManager.updateViewLayout(this, mWindowParams);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (mParentHandler != null) {
			Message msg = new Message();
			msg.what = Config.SERVICE_MESSAGE_CHANGE_ZOOM;
			Bundle bundle = new Bundle();
			bundle.putInt("progress", progress);
			msg.setData(bundle);
			mParentHandler.sendMessage(msg);
		}
		timers = 0;

		if (timer != null || task != null) {
			timer.cancel();
			task.cancel();
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		timers = 0;
		if (timer != null || task != null) {
			timer.cancel();
			task.cancel();
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
					mHandler.sendEmptyMessage(Config.SEND_MESSAGE_VISIBILITY_SEEKBAR);
				}
			}
		};
		timer.schedule(task, 1000, 1000);
	}

	// 用来显示当前的分辨率
	public void getVideoQuality(int videoWidth, int videoHeight) {
		
		if (videoWidth == 352 && videoHeight == 288) {
			txtVideoQuality.setText("CIF");
		} else if (videoWidth == 640 && videoHeight == 480) {
			txtVideoQuality.setText("VGA");
		} else if (videoWidth == 960 && videoHeight == 720) {
			txtVideoQuality.setText("720P");
		}
	}

	// 注册广播 实时监视网络的状态
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				// 网络连接的管理器
				connManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connManager.getActiveNetworkInfo(); // 网络信息

				if (info != null && info.isAvailable()) {
					String name = info.getTypeName(); // 拿到当前网络的名字
					info.getSubtypeName();
					if (name.equals("WIFI")) {
						txtNetType.setText("WIFI");
					} else if (name.equalsIgnoreCase("MOBILE")) {
						TelephonyManager telManager = (TelephonyManager) context
								.getSystemService(Context.TELEPHONY_SERVICE);
						int typeWork = telManager.getNetworkType();
						/*if(typeWork == TelephonyManager.NETWORK_TYPE_GPRS){
							txtNetType.setText("GPRS");
						}else if(typeWork == TelephonyManager.NETWORK_TYPE_EDGE){
							txtNetType.setText("2.5G");
						}else */
						if(typeWork == TelephonyManager.NETWORK_TYPE_LTE){
							txtNetType.setText("4G");
						}else{
							txtNetType.setText("3G");
						}
						
					}
				}
			}
		}
	};

	public void setFrameRate(String strFrameRate) {
		txtVideoFramerate.setText(strFrameRate);
	}

	public void setBaudRate(String strBaudRate) {
		txtNetRate.setText(strBaudRate);
	}

	
}