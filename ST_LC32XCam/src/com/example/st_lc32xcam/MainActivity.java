package com.example.st_lc32xcam;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import com.linkcard.media.LinkVideoCore;
import com.linkcard.media.LinkVideoView;
import com.saiteng.st_lc32xcam.control.ControlCmdHelper;
import com.saiteng.st_lc32xcam.control.ControlCmdHelper.ControlCmdListener;
import com.saiteng.st_lc32xcam.control.CustomDialog;
import com.saiteng.st_lc32xcam.control.RecordCmdInfo;
import com.saiteng.st_lc32xcam.control.TimeCmdInfo;
import com.saiteng.st_lc32xcam.control.VersionCmdInfo;
import com.saiteng.st_lc32xcam.fragment.ActionBarLeftFragment;
import com.saiteng.st_lc32xcam.fragment.ActionBarPortraitFragment;
import com.saiteng.st_lc32xcam.fragment.ActionBarRightFragment;
import com.saiteng.st_lc32xcam.fragment.ActionBarTopFragment;
import com.saiteng.st_lc32xcam.utils.ChangeCam;
import com.saiteng.st_lc32xcam.utils.RecordingSDCard;
import com.saiteng.st_lc32xcam.utils.SmartCamDefine;
import com.saiteng.st_lc32xcam.utils.ToastMsg;
import com.saiteng.st_lc32xcam.utils.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private LinkVideoCore linkStream;
    private LinkVideoView linkview;
    private static Handler handler,handlerupdate;
    private TextView mTxtRecorderTimer;
    private LinearLayout actionbarview;
    private LinearLayout leftview;
    private LinearLayout portraitview,topview;
    private boolean isVisible=false;
    private boolean isPausing = false;
    private boolean isRecording = false;
    private boolean  isForceExit =false;
    private boolean isStreaming = false;
    private boolean orientation =false;
    private boolean portraiVisible = false;
    private AudioTrack mAudioTrack;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = MainActivity.this;
		Util.CreateFile();
		linkview = (LinkVideoView) findViewById(R.id.mVideoView);
		mTxtRecorderTimer = (TextView) findViewById(R.id.mRecorderTimer);
		linkStream = new LinkVideoCore();
		linkStream.sysinit();
		linkview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				actionbarview = ActionBarRightFragment.getactionbarview();
				leftview = ActionBarLeftFragment.getLeftActionbarview();
				portraitview = ActionBarPortraitFragment.getActionPortraitBarview();
				topview = ActionBarTopFragment.getActionBarTopFragment();
				if(orientation&&!isVisible && actionbarview!=null&&leftview!=null){
				    actionbarview.setVisibility(View.VISIBLE);
				    leftview.setVisibility(View.VISIBLE);
				    
				    isVisible = true;
				}else if(orientation&&isVisible && actionbarview!=null&&leftview!=null){
				    actionbarview.setVisibility(View.GONE);
				    leftview.setVisibility(View.GONE);
				    isVisible=false;
				}else if(!orientation&&!portraiVisible&&portraitview!=null){
					portraitview.setVisibility(View.VISIBLE);
					topview.setVisibility(View.VISIBLE);
					portraiVisible=true;
				}else if(!orientation&&portraiVisible&&portraitview!=null){
					portraitview.setVisibility(View.GONE);
					topview.setVisibility(View.GONE);
					portraiVisible=false;
				}
				return false;
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==SmartCamDefine.SMARTCAM_TAKEPICTURE){
					//拍照
					doTakePicture();
				}else if(msg.what ==SmartCamDefine.SMARTCAM_RECORDING){
					//录像
					doRecording();
				}else if(msg.what == SmartCamDefine.SMARTCAM_PALYSTOP){
				    //播放暂停
					doPlay();
				}else if(msg.what == SmartCamDefine.SMARTCAM_FILE){
					//切换摄像头
					doChangeCam();
				}else if(msg.what == SmartCamDefine.SMARTCAM_SETTING){
					Intent intent = new Intent(MainActivity.this, SettingActivity.class);
					startActivity(intent);
				}else if(msg.what ==SmartCamDefine.SMARTCAM_LOOKFILE){
					Intent intent = new Intent(MainActivity.this, FTPActivity.class);
					intent.putExtra("hostName", SmartCamDefine.hostName);
					intent.putExtra("userName", SmartCamDefine.userName);
					intent.putExtra("password", SmartCamDefine.password);
					startActivity(intent);
				}
				}
		};
		checkValidation();
		audio_init();
		audio_startPlay();
		doSetTime();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//竖屏
		if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
			  orientation=false;
			  isVisible=false;
			  actionbarview.setVisibility(View.GONE);
			  leftview.setVisibility(View.GONE);
		}

		//切换为横屏

		else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
			orientation = true;
			portraitview.setVisibility(View.GONE);
			topview.setVisibility(View.GONE);
			portraiVisible=false;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (!isStreaming) {
			linkview.startPlayback();
		}
		isStreaming = true;
	
	}
	/**
	 *音频播放 
	 */
	private void audio_startPlay() {
		if (mPlayAudioThread == null)
		{
			mThreadExitFlag = false;
			mPlayAudioThread = new PlayAudioThread();
			mPlayAudioThread.start();
		}
		
	}
	/**
	 * 初始化音频播放设置
	 */
	@SuppressLint("InlinedApi")
	@SuppressWarnings({ "deprecation" })
	private void audio_init() {
		 if (mAudioTrack != null)
	        { 
	        	audio_release(); 
	        } 

			// 获得构建对象的最小缓冲区大小 
			int minBufSize = AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT); 
			minBufSize = minBufSize*4;
			//STREAM_ALARM：警告声 
			//STREAM_MUSCI：音乐声，例如music等 
			//STREAM_RING：铃声 
			//STREAM_SYSTEM：系统声音 
			//STREAM_VOCIE_CALL：电话声音 
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 48000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize, AudioTrack.MODE_STREAM); 
			//AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
			//STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。
			//这个和我们在socket中发送数据一样，应用层从某个地方获取数据，
			//例如通过编解码得到PCM数据，然后write到audiotrack。
			//这种方式的坏处就是总是在JAVA层和Native层交互，效率损失较大。
			//而STATIC的意思是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
			//后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。 
			//这种方法对于铃声等内存占用较小，延时要求较高的声音来说很适用。 
	        mAudioTrack.play(); 
		
	}
	
	private void audio_release() {
		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();
		}
		
	}
	//停止播放声音
		private void audio_stopPlay()
		{
			if (mPlayAudioThread != null)
			{
				mThreadExitFlag = true;
				mPlayAudioThread = null;
			}
		}
	
	/*
	 *  播放音频的线程
	 */
    private boolean mThreadExitFlag = false;
    private PlayAudioThread mPlayAudioThread;
    /**
     *内部类播放音频线程 
     */
	class PlayAudioThread extends Thread
	{
		@Override
		public void run() {
			int AudiobufLen = 48000*1*2*4;
			short[] Audiobuf = new short[AudiobufLen];
			
			int retsize = 0;
			int convertUnsignedToSigned = 1;
			
			mAudioTrack.play();	
			linkview.startPlayback();
			while(true)
			{											
				if (mThreadExitFlag == true)
				{
					break;
				}		
				try 
				{
					if((AudiobufLen = linkview.getAudioFrame(Audiobuf)) > 0)
					{
						retsize = mAudioTrack.write(Audiobuf, 0, AudiobufLen);
					}
				}
				catch (Exception e) 
				{
				
					e.printStackTrace();
					break;
				}
			}	
			
			Log.d("MainActivity", "PlayAudioThread complete...");				
		}
	}
	/**
	 *实时检查wifi的连接状态 
	 */
	private void checkValidation() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(!isForceExit){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							doRequestWiFiStatus();
						}						
					});
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
	}
	public static Handler getHandler(){
		return handler;
	}
	public static Handler getUpdateHandler(){
		return handlerupdate;
	}
	protected void doPlay() {
		if (!isPausing) {
			if (linkview.pausePlayback()) {
				audio_stopPlay();
				isPausing = true;
				updatePlayStatus(true);
			}
		} else {
			if (linkview.resumePlayback()) {
				audio_startPlay();
				isPausing = false;
				updatePlayStatus(false);
			}
		}
	}
   /**
    * 更新播放按钮的状态 
    */
	private void updatePlayStatus(boolean Pausing) {
		if (Pausing) {
			Toast.makeText(context, "暂停", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "播放", Toast.LENGTH_SHORT).show();
		}
		
	}
	protected void doRecording() {
		//本地录像
		long time = System.currentTimeMillis();
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		final Date d1 = new Date(time);
		String date = format.format(d1);
		final String filename = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ ("/" + "CAM/video/video_" + date);
		final String videoname = "Video"+format.format(d1);
		if(SmartCamDefine.isconn){
			if(!isRecording){
				new RecordingSDCard().execute();
				isRecording = true;	        	
	        	updateRecordStatus(true);
				//ControlCmdHelper.videoname = "Video"+format.format(d1);
			}else{
				new RecordingSDCard().execute();
				isRecording = false;	        	
	        	updateRecordStatus(false);
			}
			
		
//			{
//			this.mControlCmdHelper.sendCmd(ControlCmdHelper.CONTROL_CMD_START_RECORD+ControlCmdHelper.videoname, new ControlCmdListener(){
//
//				@Override
//				public void onFailure(int type) {
//					
//					
//				}
//
//				@Override
//				public void onSuccess(Object obj) {
//					
//					RecordCmdInfo localTimeCmdInfo = (RecordCmdInfo)obj;
//					
//			        if ((localTimeCmdInfo != null) && (videoname.equals(localTimeCmdInfo.getValue()))){
//			        	
//			        	linkview.Record(filename);
//			        	
//			        	isRecording = true;
//			        	
//			        	updateRecordStatus(true);
//						
//			        }else if((localTimeCmdInfo != null) && ("Stop record".equals(localTimeCmdInfo.getValue()))){
//			        	
//                         updateRecordStatus(false);
//                         
//                         linkview.stopRecord();
//                         
//                         isRecording=false;
//                         
//			        }else{
//			        	
//			        	updateRecordStatus(false);
//                        
//                        linkview.stopRecord();
//                        
//                        isRecording=false;
//			        }
//				}
//				} , RecordCmdInfo.class);
//		}
		}else
			Toast.makeText(context, "设备没连接", Toast.LENGTH_SHORT).show();
	}
    /**
     *更新录像时间
     */
	private Timer mRecordTimer;
	private int mRecordedSecond = 0;
	private void updateRecordStatus(final boolean recording) {
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
										mTxtRecorderTimer.setText("录像"+hourString + ":" + minString + ":" + secString);
									}
								});
								mRecordedSecond++;
								if(mRecordedSecond%60==0){
									//录像时间达到指定录像时间则自动保存
									mRecordedSecond=0;
								}
							}
						}, 0, 1000);
						}else{
							mTxtRecorderTimer.setVisibility(View.INVISIBLE);
							mRecordedSecond = 0;
						    mTxtRecorderTimer.setText("00:00");
							if (mRecordTimer != null) {
								mRecordTimer.cancel();
						   }
						}	
	}
	/**
	 *
	 */
	protected void doTakePicture() {
		//本地拍照
		String filename = Environment.getExternalStorageDirectory().getAbsolutePath();

		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date d1 = new Date(time);
		String date = format.format(d1);

		filename = filename + ("/" + "CAM/img/img_" + date + ".jpg");

		if (linkview.takePicture(filename)) {
		
		} else {
			Toast.makeText(context, "拍照失败", Toast.LENGTH_SHORT).show();
		}
		
		//SD卡拍照
		{
		 new DateFormat();
		 String str = (String)DateFormat.format("yyyyMMddHHmmss", Calendar.getInstance(Locale.CHINA));
		 str = "pic"+str;
		 this.mControlCmdHelper.sendCmd(ControlCmdHelper.CONTROL_CMD_TAKEPIC + str, new ControlCmdListener()
		    {
		      public void onFailure(int paramAnonymousInt)
		      {
		      }

		      public void onSuccess(Object paramAnonymousObject)
		      {
		        TimeCmdInfo localTimeCmdInfo = (TimeCmdInfo)paramAnonymousObject;
		        if ((localTimeCmdInfo != null) && (localTimeCmdInfo.getCode() == 0)){
		        	Toast.makeText(context, "拍照成功", Toast.LENGTH_SHORT).show();
		        }
		      }
		    }
		    , TimeCmdInfo.class);
		}
	}
	/**
	 *同步手机时间给设备 
	 */
	private void doSetTime()
	  {
	    new DateFormat();
	    String str = (String)DateFormat.format("yyyy-MM-dd hh:mm:ss", Calendar.getInstance(Locale.CHINA));
	    this.mControlCmdHelper.sendCmd("http://192.168.11.123/api/trio_tmr?val=" + str, new ControlCmdHelper.ControlCmdListener()
	    {
	      public void onFailure(int paramAnonymousInt)
	      {
	      }

	      public void onSuccess(Object paramAnonymousObject)
	      {
	        TimeCmdInfo localTimeCmdInfo = (TimeCmdInfo)paramAnonymousObject;
	        if ((localTimeCmdInfo != null) && (localTimeCmdInfo.getCode() != 0));
	      }
	    }
	    , TimeCmdInfo.class);
	  }
	
	/**
	 *切换摄像头，发送命令给串口 
	 */
	protected void doChangeCam() {
		
		//ChangeCam change = new ChangeCam(context);
	}
	/**
	 *响应wifi状态检查 
	 */
	private ControlCmdHelper mControlCmdHelper = new ControlCmdHelper();
	private void doRequestWiFiStatus(){
		if (isForceExit){
			return;
		}
		
        mControlCmdHelper.sendCmd(ControlCmdHelper.CONTROL_CMD_VERSION, new ControlCmdListener(){
			@Override
            public void onFailure(int type) {
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						ActionBarRightFragment.getHandler().sendEmptyMessage(1);
						ActionBarPortraitFragment.getHandler().sendEmptyMessage(1);
						SmartCamDefine.isconn=false;
						if (isRecording) {
							mTxtRecorderTimer.setVisibility(View.INVISIBLE);
							mRecordedSecond = 0;
						    mTxtRecorderTimer.setText("00:00");
							if (mRecordTimer != null) {
								mRecordTimer.cancel();
						   }
							isRecording=false;
						}
					}					
				});
            }

			@Override
            public void onSuccess(Object obj) {
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						ActionBarRightFragment.getHandler().sendEmptyMessage(0);
						ActionBarPortraitFragment.getHandler().sendEmptyMessage(0);
                        SmartCamDefine.isconn=true;
					}					
				});
            }	            	
        }, VersionCmdInfo.class);
	}
	/**
	 *响应点击返回键 
	 */
	@Override
	public void onBackPressed() {
		final CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
		final CustomDialog dialog;
		builder.setTitle("警告");
		if (isRecording) {
			builder.setMessage("你将要停止录像并且与设备断开连接?");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					isForceExit = true;
					if (linkview != null) {
						audio_stopPlay();
						audio_release();
						linkview.stopRecord();
						linkview.stopPlayback();
						dialog.dismiss();
						MainActivity.this.finish();
					}
				}
			});
		} else {
			builder.setMessage("确定退出程序？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (linkview != null) {
						audio_stopPlay();
						audio_release();
						linkview.stopPlayback();
						dialog.dismiss();
						MainActivity.this.finish();
					}
				}
			});
		}
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
		dialog.show();
	}
	
}
