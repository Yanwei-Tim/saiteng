package com.saiteng.st_smartcam.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.linkcard.media.LinkVideoCore;
import com.linkcard.media.LinkVideoView;
import com.saiteng.st_smartcam.utils.SmartCamDefine;
import com.saiteng.st_smartcam.utils.ToastMsg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private LinkVideoCore linkStream;
    private LinkVideoView linkview;
    private RelativeLayout actionbarview;
    private TextView mTxtRecorderTimer;
    private boolean isVisible=false;
    private boolean isPausing = false;
    private boolean isRecording = false;
    private static Context context;
    private static Handler handler=null;
    private ToastMsg toastmsg;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toastmsg = new ToastMsg();
		context = MainActivity.this;
		linkStream = new LinkVideoCore();
		linkStream.sysinit();
		mTxtRecorderTimer = (TextView) findViewById(R.id.mRecorderTimer);
		linkview = (LinkVideoView) findViewById(R.id.mVideoView);
		
		linkview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				actionbarview = ActionBarFragment.getactionbarview();
				if(!isVisible && actionbarview!=null){
				    actionbarview.setVisibility(View.VISIBLE);
				    isVisible = true;
				}else if(isVisible && actionbarview!=null){
				    actionbarview.setVisibility(View.GONE);
				    isVisible=false;
				   
				}
				return false;
			}
		});
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==SmartCamDefine.SMARTCAM_TAKEPICTURE){
					//≈ƒ’’
					doTakePicture();
				}else if(msg.what ==SmartCamDefine.SMARTCAM_RECORDING){
					//¬ºœÒ
					doRecording();
				}else if(msg.what == SmartCamDefine.SMARTCAM_PALYSTOP){
				    //≤•∑≈‘›Õ£
					doPlay();
					
				}else if(msg.what == SmartCamDefine.SMARTCAM_FILE){
					//Œƒº˛‘§¿¿
					
				}else if(msg.what == SmartCamDefine.SMARTCAM_SETTING){
					//…Ë÷√
				}
				
			}
		};
		
		
	}
	private void doPlay() {
		if (!isPausing) {/* pause */
			if (linkview.pausePlayback()) {/* success */
				isPausing = true;
				/* update play status */
				updatePlayStatus(true);
			}
		} else {/* resume */
			if (linkview.resumePlayback()) {/* success */
				isPausing = false;
				/* update play status */
				updatePlayStatus(false);
			}
		}
	}
	/* update play button status */
	private void updatePlayStatus(boolean pausing) {
		if (pausing) {
			toastmsg.ToastShow(context,"‘›Õ£≤•∑≈.");
			//mBtnPlayPause.setImageResource(R.drawable.btn_play);
		} else {
			toastmsg.ToastShow(context,"ª÷∏¥≤•∑≈.");
			//mBtnPlayPause.setImageResource(R.drawable.pause);
		}
	}
	
	private void doRecording() {
		int ret = 0;
		if (!isRecording) {
			String filename = Environment.getExternalStorageDirectory().getAbsolutePath();
			//String filename = Environment.getExternalStorageDirectory().getAbsolutePath();
			long time = System.currentTimeMillis();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Date d1 = new Date(time);
			String date = format.format(d1);
			filename = filename + ("/" + "cam802/Video/video_" + date + ".mp4");
			ret = linkview.startRecord(filename);
//			if (ret == 0) {/* success */
				isRecording = true;
				/* set record status */
				updateRecordStatus(true);
//			} else {
//				showMsgBox("Warning", "Failed to start recording.");
//			}
		} else {
			ret = linkview.stopRecord();
//			if (ret == 0) {/* success */
				isRecording = false;
				updateRecordStatus(false);
//			}
		}
	}
	
	private Timer mRecordTimer;
	private int mRecordedSecond = 0;

	private void updateRecordStatus(boolean recording) {
		if (recording) {
			mRecordTimer = new Timer();
			mTxtRecorderTimer.setVisibility(View.VISIBLE);
			mRecordTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					int hour = mRecordedSecond / 3600;
					int min = mRecordedSecond % 3600 / 60;
					int sec = mRecordedSecond % 60;
					final String hourString = String.format("%02d", hour);
					final String minString = String.format("%02d", min);
					final String secString = String.format("%02d", sec);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mTxtRecorderTimer.setText(hourString + ":" + minString + ":" + secString);
						}
					});
					mRecordedSecond++;
				}
			}, 0, 1000);
		} else {
			mTxtRecorderTimer.setVisibility(View.INVISIBLE);
			mRecordedSecond = 0;
		    mTxtRecorderTimer.setText("00:00");
			if (mRecordTimer != null) {
				mRecordTimer.cancel();
			}
		}
	}
	
	protected void doTakePicture() {
		String filename = Environment.getExternalStorageDirectory().getAbsolutePath();
		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date d1 = new Date(time);
		String date = format.format(d1);

		filename = filename + ("/" + "cam802/img/img_" + date + ".jpg");

		if (linkview.takePicture(filename)) {
			toastmsg.ToastShow(context,"≈ƒ’’≥…π¶£¨±£¥Ê¬∑æ∂ «£∫ " + filename);
		} else {
			toastmsg.ToastShow(context,"≈ƒ’’ ß∞‹");
		}
	}
	
	public static Context getContext(){
		return context;
		
	}
	
	public static Handler getHandler(){
		return handler;
	}
	
}
