package com.saiteng.smarteyempu.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.saiteng.smarteyempu.R;
import com.saiteng.smarteyempu.common.Config;
import com.saiteng.smarteyempu.common.Utils;
import com.saiteng.smarteyempu.logic.FloatViewService;

public class MainActivity extends Activity {
	private FloatViewService mFloatViewService = null;

	private Button btnSetting = null;
	private Button btnSignin = null;
	private View viewSetting = null;

	private EditText editServerAddr = null;
	private EditText editServerPort = null;

	private EditText editDeviceName = null;
	private EditText editDeviceId = null;
	private EditText editDeviceAlias = null;
	private RadioGroup switchAudioUpload = null;
	private RadioGroup switchLocationUpload = null;
	private RadioGroup saveVideoSwitch = null;
	private RadioGroup radioVideoQuality = null;

	private boolean mIsShowSetting = false;
	private boolean mIsSignIn = false;
	private Context mContext = null;
	private ConnectivityManager connManager;//网络连接管理器
    private NetworkInfo info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo(); // 网络信息
		
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		
		// 记录上一次登陆的信息
	    SharedPreferences share = getSharedPreferences("lasthistory",
						Context.MODE_APPEND);
		
			String imei =tm.getDeviceId(); 
			//Log.d("geek", imei);
	         //if (!imei.equals("359850055306133")) {//note3
			 //if(!imei.equals("352575070964312")){//note5
			  if(!imei.equals("352562072153169")) {//note4
			//if (!imei.equals("357504057093866")) {//S4
			//if(!imei.equals("867489023948778")){//lenovo
				Utils.showDialog(this, "该软件未在此机器上授权!"); 
				return; 
				
			}
		
		// 检测是否已经开启，如果开启则直接返回
		mContext = getApplicationContext();
		mFloatViewService = FloatViewService.getInstance(mContext);
		if (mFloatViewService != null) {
			this.finish();
		}

		// 加载界面布局
		setContentView(R.layout.main);

		btnSetting = (Button) findViewById(R.id.setting_button);
		btnSignin = (Button) findViewById(R.id.signin_button);

		viewSetting = (View) findViewById(R.id.settings);

		editServerAddr = (EditText) findViewById(R.id.serveraddr_edit);
		editServerPort = (EditText) findViewById(R.id.serverport_edit);

		editDeviceName = (EditText) findViewById(R.id.devicename_edit);
		editDeviceId = (EditText) findViewById(R.id.deviceid_edit);
		editDeviceAlias = (EditText) findViewById(R.id.devicealais_edit);

		switchAudioUpload = (RadioGroup) findViewById(R.id.audioUploadSwitch);
		switchLocationUpload = (RadioGroup) findViewById(R.id.locationUploadSwitch);
		saveVideoSwitch = (RadioGroup) findViewById(R.id.videoSwitch);

		radioVideoQuality = (RadioGroup) findViewById(R.id.videoQuality_group);

		// 加载配置值
		LoadConfigure();

		// 显示加载的配置

		//switchAudioUpload.setChecked(Config.mIsAudioUpload);
		//switchLocationUpload.setChecked(Config.mIsLocationUpload);

		btnSetting.setOnClickListener(viewClickListenser);
		btnSignin.setOnClickListener(viewClickListenser);

		String serverAliasName = share.getString("serverAliasName", "");
		if (!serverAliasName.equals("")) {
			// 拿到上次登陆的信息将其显示出来
			editServerAddr.setText(share.getString("serverAddr", ""));
			editDeviceAlias.setText(serverAliasName);
			editDeviceName.setText(share.getString("deviceName", ""));
			editDeviceId.setText(share.getString("deviceId", ""));
			editServerPort.setText(share.getString("serverPort", ""));
			//switchAudioUpload
					//.setChecked(share.getBoolean("audioUpload", false));
			boolean AudioUpload = share.getBoolean("audioUpload", false);
			if(AudioUpload){
				switchAudioUpload.check(R.id.audioUploadyes);
			}else{
				switchAudioUpload.check(R.id.audioUploadno);
			}
			//switchLocationUpload.setChecked(share.getBoolean("locationUpload",
					//false));
			boolean LocationUpload = share.getBoolean("locationUpload", false);
			if(LocationUpload){
				switchLocationUpload.check(R.id.locationUploadyes);
			}else{
				switchLocationUpload.check(R.id.locationUploadno);
			}
			//saveVideoSwitch.setChecked(share.getBoolean("saveVideo", false));
			boolean saveVideo = share.getBoolean("saveVideo", false);
			if(saveVideo){
				saveVideoSwitch.check(R.id.videoSwitchyes);
			}else{
				saveVideoSwitch.check(R.id.videoSwitchno);
			}
			
			int videoSize = share.getInt("videoSize", 0);

			if (videoSize == 0) {
				radioVideoQuality.check(R.id.videoQuality_low_btn);
			} else if (videoSize == 1) {
				radioVideoQuality.check(R.id.videoQuality_common_btn);
			} else if (videoSize == 2) {
				radioVideoQuality.check(R.id.videoQuality_high_btn);
			}

		} else {
			/**
			 * 初始化参数
			 */
			editServerAddr.setText(Config.mStringServerAddr);
			editServerPort.setText(Config.mStringServerPort);
			editDeviceName.setText(Config.mStringDeviceName);
			editDeviceId.setText(Config.mStringDeviceID);
			editDeviceAlias.setText(Config.mStringDeviceAlias);
		}
		mIsShowSetting = false;

	}

	/**
	 * 按钮的点击事件
	 */
	private OnClickListener viewClickListenser = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_button: {
				if (!mIsShowSetting) {
					viewSetting.setVisibility(View.VISIBLE);
				} else {
					viewSetting.setVisibility(View.GONE);
				}

				mIsShowSetting = !mIsShowSetting;
			}
				break;
			case R.id.signin_button: {
				// 更新设置值
				Config.mStringServerAddr = editServerAddr.getText().toString();
				Config.mStringServerPort = editServerPort.getText().toString();

				Config.mStringDeviceName = editDeviceName.getText().toString();

				Config.mStringDeviceID = editDeviceId.getText().toString();

				Config.mStringDeviceAlias = editDeviceAlias.getText()
						.toString();
				//Config.mIsAudioUpload = switchAudioUpload.isChecked();
				//Config.mIsLocationUpload = switchLocationUpload.isChecked();
				//Config.mVideoSwitch = saveVideoSwitch.isChecked();
                GetAudioUpload();//获取音频是否上传参数
                GetLocationUpload();//获取位置是否上传参数
                saveVideo();//获取是否录像参数
				GetVideoSize();
				// 保存上次记录
				saveLastConfig();
                //加载配置项 启动拍摄服务。
				Intent recordFloatViewService = new Intent(mContext,
						FloatViewService.class);
				mContext.startService(recordFloatViewService);
				Log.i(Config.TAG, "Start floatViewService");

				finish();
			}
				break;

			}

		}
	};

	/**
	 * 选择video分辨率
	 */
	private void GetVideoSize() {
		// 获取变更后的选中项的ID
		int radioButtonId = radioVideoQuality.getCheckedRadioButtonId();
		// 根据ID获取RadioButton的实例
		switch (radioButtonId) {
		case R.id.videoQuality_low_btn:
			if (Config.mIsBackCameraFirst == true) {
				Config.mVideoWidth = Config.mBackCIFVideoWidth;
				Config.mVideoHeight = Config.mBackCIFVideoHeight;
				Config.mVideoBaudrate = Config.mBackCIFVideoBaudrate;
				Config.mVideoFrameInterval = Config.mBackCIFVideoFrameInterval;
			} else {
				Config.mVideoWidth = Config.mFrontCIFVideoWidth;
				Config.mVideoHeight = Config.mFrontCIFVideoHeight;
				Config.mVideoBaudrate = Config.mFrontCIFVideoBaudrate;
				Config.mVideoFrameInterval = Config.mFrontCIFVideoFrameInterval;
			}
			Config.mVideoQuality = 0;
			break;
		case R.id.videoQuality_common_btn:
			if (Config.mIsBackCameraFirst == true) {
				Config.mVideoWidth = Config.mBackVGAVideoWidth;
				Config.mVideoHeight = Config.mBackVGAVideoHeight;
				Config.mVideoBaudrate = Config.mBackVGAVideoBaudrate;
				Config.mVideoFrameInterval = Config.mBackVGAVideoFrameInterval;
			} else {
				Config.mVideoWidth = Config.mFrontVGAVideoWidth;
				Config.mVideoHeight = Config.mFrontVGAVideoHeight;
				Config.mVideoBaudrate = Config.mFrontVGAVideoBaudrate;
				Config.mVideoFrameInterval = Config.mFrontVGAVideoFrameInterval;
			}
			Config.mVideoQuality = 1;
			break;
		case R.id.videoQuality_high_btn:
			if (Config.mIsBackCameraFirst == true) {
				Config.mVideoWidth = Config.mBackD1VideoWidth;
				Config.mVideoHeight = Config.mBackD1VideoHeight;
				Config.mVideoBaudrate = Config.mBackD1VideoBaudrate;
				Config.mVideoFrameInterval = Config.mBackD1VideoFrameInterval;
			} else {
				Config.mVideoWidth = Config.mFrontD1VideoWidth;
				Config.mVideoHeight = Config.mFrontD1VideoHeight;
				Config.mVideoBaudrate = Config.mFrontD1VideoBaudrate;
				Config.mVideoFrameInterval = Config.mFrontD1VideoFrameInterval;
			}
			Config.mVideoQuality = 2;
			break;
		}
	}
	/**
	 * 是否录像
	 */
	protected void saveVideo() {
		// 获取变更后的选中项的ID
		int saveVideoButtonId = saveVideoSwitch.getCheckedRadioButtonId();
		// 根据ID获取RadioButton的实例
		switch (saveVideoButtonId) {
		case R.id.videoSwitchyes:
			Config.mVideoSwitch = true;
			break;
		case R.id.videoSwitchno:
			Config.mVideoSwitch = false;
			break;
		}
		
	}
	/**
	 * 是否要上传位置
	 */
	protected void GetLocationUpload() {
		// 获取变更后的选中项的ID
		int LocationButtonId = switchLocationUpload.getCheckedRadioButtonId();
		// 根据ID获取RadioButton的实例
		switch (LocationButtonId) {
		case R.id.locationUploadyes:
			Config.mIsLocationUpload = true;
			break;
		case R.id.locationUploadno:
			Config.mIsLocationUpload = false;
			break;
		}

	}

	/**
	 * 是否要上传音频
	 */
	protected void GetAudioUpload() {
		// 获取变更后的选中项的ID
		int AudioButtonId = switchAudioUpload.getCheckedRadioButtonId();
		// 根据ID获取RadioButton的实例
		switch (AudioButtonId) {
		case R.id.audioUploadyes:
			Config.mIsAudioUpload=true;
			break;
		case R.id.audioUploadno:
			Config.mIsAudioUpload=false;
			break;
		}
		
	}

	public void LoadConfigure() {

	}

	/**
	 * 将登陆的数据保存到sharedPreferences 里面
	 * 
	 * @param regisInfo
	 */
	public void saveLastConfig() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"lasthistory", Context.MODE_APPEND);
		Editor edit = sharedPreferences.edit(); // 获取编辑器
		edit.putString("serverAddr", Config.mStringServerAddr); // 服务器地址
		edit.putString("serverPort", Config.mStringServerPort); // 服务器端口号
		edit.putString("deviceId", Config.mStringDeviceID); // 设备id
		edit.putString("deviceName", Config.mStringDeviceName); // 设备名
		edit.putString("serverAliasName", Config.mStringDeviceAlias); // 服务器别名
		edit.putInt("videoSize", Config.mVideoQuality); // 上推的分辨率
		edit.putBoolean("audioUpload", Config.mIsAudioUpload);
		edit.putBoolean("locationUpload", Config.mIsLocationUpload);
		edit.putBoolean("saveVideo", Config.mVideoSwitch);
		// Log.d("geek", Config.mVideoQuality + "");
		edit.commit(); // 提交数据

	}

}
