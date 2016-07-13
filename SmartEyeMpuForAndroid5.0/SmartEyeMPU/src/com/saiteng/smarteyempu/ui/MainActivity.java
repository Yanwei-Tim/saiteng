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
	private ConnectivityManager connManager;//�������ӹ�����
    private NetworkInfo info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo(); // ������Ϣ
		
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		
		// ��¼��һ�ε�½����Ϣ
	    SharedPreferences share = getSharedPreferences("lasthistory",
						Context.MODE_APPEND);
		
			String imei =tm.getDeviceId(); 
			//Log.d("geek", imei);
	         //if (!imei.equals("359850055306133")) {//note3
			 //if(!imei.equals("352575070964312")){//note5
			  if(!imei.equals("352562072153169")) {//note4
			//if (!imei.equals("357504057093866")) {//S4
			//if(!imei.equals("867489023948778")){//lenovo
				Utils.showDialog(this, "�����δ�ڴ˻�������Ȩ!"); 
				return; 
				
			}
		
		// ����Ƿ��Ѿ����������������ֱ�ӷ���
		mContext = getApplicationContext();
		mFloatViewService = FloatViewService.getInstance(mContext);
		if (mFloatViewService != null) {
			this.finish();
		}

		// ���ؽ��沼��
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

		// ��������ֵ
		LoadConfigure();

		// ��ʾ���ص�����

		//switchAudioUpload.setChecked(Config.mIsAudioUpload);
		//switchLocationUpload.setChecked(Config.mIsLocationUpload);

		btnSetting.setOnClickListener(viewClickListenser);
		btnSignin.setOnClickListener(viewClickListenser);

		String serverAliasName = share.getString("serverAliasName", "");
		if (!serverAliasName.equals("")) {
			// �õ��ϴε�½����Ϣ������ʾ����
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
			 * ��ʼ������
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
	 * ��ť�ĵ���¼�
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
				// ��������ֵ
				Config.mStringServerAddr = editServerAddr.getText().toString();
				Config.mStringServerPort = editServerPort.getText().toString();

				Config.mStringDeviceName = editDeviceName.getText().toString();

				Config.mStringDeviceID = editDeviceId.getText().toString();

				Config.mStringDeviceAlias = editDeviceAlias.getText()
						.toString();
				//Config.mIsAudioUpload = switchAudioUpload.isChecked();
				//Config.mIsLocationUpload = switchLocationUpload.isChecked();
				//Config.mVideoSwitch = saveVideoSwitch.isChecked();
                GetAudioUpload();//��ȡ��Ƶ�Ƿ��ϴ�����
                GetLocationUpload();//��ȡλ���Ƿ��ϴ�����
                saveVideo();//��ȡ�Ƿ�¼�����
				GetVideoSize();
				// �����ϴμ�¼
				saveLastConfig();
                //���������� �����������
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
	 * ѡ��video�ֱ���
	 */
	private void GetVideoSize() {
		// ��ȡ������ѡ�����ID
		int radioButtonId = radioVideoQuality.getCheckedRadioButtonId();
		// ����ID��ȡRadioButton��ʵ��
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
	 * �Ƿ�¼��
	 */
	protected void saveVideo() {
		// ��ȡ������ѡ�����ID
		int saveVideoButtonId = saveVideoSwitch.getCheckedRadioButtonId();
		// ����ID��ȡRadioButton��ʵ��
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
	 * �Ƿ�Ҫ�ϴ�λ��
	 */
	protected void GetLocationUpload() {
		// ��ȡ������ѡ�����ID
		int LocationButtonId = switchLocationUpload.getCheckedRadioButtonId();
		// ����ID��ȡRadioButton��ʵ��
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
	 * �Ƿ�Ҫ�ϴ���Ƶ
	 */
	protected void GetAudioUpload() {
		// ��ȡ������ѡ�����ID
		int AudioButtonId = switchAudioUpload.getCheckedRadioButtonId();
		// ����ID��ȡRadioButton��ʵ��
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
	 * ����½�����ݱ��浽sharedPreferences ����
	 * 
	 * @param regisInfo
	 */
	public void saveLastConfig() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"lasthistory", Context.MODE_APPEND);
		Editor edit = sharedPreferences.edit(); // ��ȡ�༭��
		edit.putString("serverAddr", Config.mStringServerAddr); // ��������ַ
		edit.putString("serverPort", Config.mStringServerPort); // �������˿ں�
		edit.putString("deviceId", Config.mStringDeviceID); // �豸id
		edit.putString("deviceName", Config.mStringDeviceName); // �豸��
		edit.putString("serverAliasName", Config.mStringDeviceAlias); // ����������
		edit.putInt("videoSize", Config.mVideoQuality); // ���Ƶķֱ���
		edit.putBoolean("audioUpload", Config.mIsAudioUpload);
		edit.putBoolean("locationUpload", Config.mIsLocationUpload);
		edit.putBoolean("saveVideo", Config.mVideoSwitch);
		// Log.d("geek", Config.mVideoQuality + "");
		edit.commit(); // �ύ����

	}

}
