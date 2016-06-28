package com.example.st_lc32xcam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saiteng.lc32xcam.adapter.SettingAdapter;
import com.saiteng.st_lc32xcam.control.ControlCmdHelper;
import com.saiteng.st_lc32xcam.control.ControlCmdHelper.ControlCmdListener;
import com.saiteng.st_lc32xcam.control.CustomDialog;
import com.saiteng.st_lc32xcam.control.FactoryDefaultCmdInfo;
import com.saiteng.st_lc32xcam.control.WifiPwdCmdInfo;
import com.saiteng.st_lc32xcam.control.WifiSSIDCmdInfo;
import com.saiteng.st_lc32xcam.utils.SmartCamDefine;
import com.saiteng.st_lc32xcam.utils.ToastMsg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SettingActivity extends Activity{
	private ListView mlistView;
	private List<Map<String,String>> list  = new ArrayList<Map<String,String>>();
	private Context context;
	private ToastMsg toastmsg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		context = SettingActivity.this;
		toastmsg = new ToastMsg();
		mlistView = (ListView) findViewById(R.id.listview_setting);
		getData();
		SettingAdapter myadapter = new SettingAdapter(context,list);
		mlistView.setAdapter(myadapter);
		mlistView.setOnItemClickListener(itemclick);
	}
	private List<Map<String, String>> getData() {
		Map<String, String> map;
		for(int i=0;i<4;i++){
			map = new HashMap<String, String>();
			if(i==0){
				map.put("itemname", "更换密码,连接名");
			}else if(i==1){
				map.put("itemname", "文件保存时间");
			}else if(i==2){
				map.put("itemname", "设备状态查看");
			}
			list.add(map);
		}
		return list;
	}
	OnItemClickListener itemclick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(position==0){
				doSetting();
			}
			
		}
	};
	private ControlCmdHelper mControlCmdHelper = new ControlCmdHelper();
	private CustomDialog mCustomDialog;
	@SuppressLint("InflateParams")
	private void doSetting(){
		
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("设置");
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.cus_alert_setting, null);
		final TextView wifiSSIDText = (EditText)textEntryView.findViewById(R.id.mSettingTextWiFiSSID);
		final TextView wifiSSIDBtn = (Button)textEntryView.findViewById(R.id.mSettingBtnWiFiSSID);
		final TextView wifiPWDText = (EditText)textEntryView.findViewById(R.id.mSettingTextWiFiPWD);
		final TextView wifiPWDBtn = (Button)textEntryView.findViewById(R.id.mSettingBtnWiFiPWD);
		
		wifiSSIDBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if (wifiSSIDText == null || wifiSSIDText.getText().toString().length() == 0){
					return;
				}
				
				wifiSSIDText.setEnabled(false);
				wifiPWDText.setEnabled(false);
				wifiSSIDBtn.setEnabled(false);
				wifiPWDBtn.setEnabled(false);
				
	            mControlCmdHelper.sendCmd(ControlCmdHelper.CONTROL_CMD_SET_WIFI_SSID + wifiSSIDText.getText().toString().trim(), new ControlCmdListener(){
					@Override
                    public void onFailure(int type) {
						toastmsg.ToastShow(context, "更改连接名失败.");	
						wifiSSIDText.setEnabled(true);
						wifiPWDText.setEnabled(true);
						wifiSSIDBtn.setEnabled(true);
						wifiPWDBtn.setEnabled(true);
                    }

					@Override
                    public void onSuccess(Object obj) {
						WifiSSIDCmdInfo info  = new WifiSSIDCmdInfo();
						if (info==null || info.getCode()!=ControlCmdHelper.CONTROL_CMD_CODE_SUCCESS){
							toastmsg.ToastShow(context, "更改连接名失败.");
						
							wifiSSIDText.setEnabled(true);
							wifiPWDText.setEnabled(true);
							wifiSSIDBtn.setEnabled(true);
							wifiPWDBtn.setEnabled(true);
							return;
						}
						mCustomDialog.dismiss();
						toastmsg.ToastShow(context,"更改连接名成功.");	
						wifiSSIDText.setEnabled(true);
						wifiPWDText.setEnabled(true);
						wifiSSIDBtn.setEnabled(true);
						wifiPWDBtn.setEnabled(true);
                    }	            	
	            }, WifiSSIDCmdInfo.class);
            }			
		});
		
		wifiPWDBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if (wifiPWDText == null || wifiPWDText.getText().toString().length() == 0){
					return;
				}
				
				wifiSSIDText.setEnabled(false);
				wifiPWDText.setEnabled(false);
				wifiSSIDBtn.setEnabled(false);
				wifiPWDBtn.setEnabled(false);
				
	            mControlCmdHelper.sendCmd(ControlCmdHelper.CONTROL_CMD_SET_WIFI_PWD + wifiPWDText.getText().toString().trim(), new ControlCmdListener(){
					@Override
                    public void onFailure(int type) {
						toastmsg.ToastShow(context, "更改密码失败.");	
						
						wifiSSIDText.setEnabled(true);
						wifiPWDText.setEnabled(true);
						wifiSSIDBtn.setEnabled(true);
						wifiPWDBtn.setEnabled(true);
                    }

					@Override
                    public void onSuccess(Object obj) {
						WifiSSIDCmdInfo info  = new WifiSSIDCmdInfo();
						if (info==null || info.getCode()!=ControlCmdHelper.CONTROL_CMD_CODE_SUCCESS){
							toastmsg.ToastShow(context, "更改密码失败.");
							
							wifiSSIDText.setEnabled(true);
							wifiPWDText.setEnabled(true);
							wifiSSIDBtn.setEnabled(true);
							wifiPWDBtn.setEnabled(true);
							return;
						}
						mCustomDialog.dismiss();
						toastmsg.ToastShow(context,"更改密码成功.");	
						wifiSSIDText.setEnabled(true);
						wifiPWDText.setEnabled(true);
						wifiSSIDBtn.setEnabled(true);
						wifiPWDBtn.setEnabled(true);
                    }	            	
	            }, WifiPwdCmdInfo.class);
			}
		});
		
		
		builder.setContentView(textEntryView);
		builder.setPositiveButton("恢复出厂设置", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {				
				wifiSSIDText.setEnabled(false);
				wifiPWDText.setEnabled(false);
				wifiSSIDBtn.setEnabled(false);
				wifiPWDBtn.setEnabled(false);
				
	            mControlCmdHelper.sendCmd(ControlCmdHelper.CONTROL_CMD_FACTORY_DEFAULT, new ControlCmdListener(){
					@Override
                    public void onFailure(int type) {
						toastmsg.ToastShow(context, "恢复出厂设置失败.");	
						
						wifiSSIDText.setEnabled(true);
						wifiPWDText.setEnabled(true);
						wifiSSIDBtn.setEnabled(true);
						wifiPWDBtn.setEnabled(true);
                    }

					@Override
                    public void onSuccess(Object obj) {
						WifiSSIDCmdInfo info  = new WifiSSIDCmdInfo();
						if (info==null || info.getCode()!=ControlCmdHelper.CONTROL_CMD_CODE_SUCCESS){
							toastmsg.ToastShow(context, "恢复出厂设置失败.");
							
							wifiSSIDText.setEnabled(true);
							wifiPWDText.setEnabled(true);
							wifiSSIDBtn.setEnabled(true);
							wifiPWDBtn.setEnabled(true);
							return;
						}
						mCustomDialog.dismiss();
						toastmsg.ToastShow(context, "恢复出厂设置成功");	
						wifiSSIDText.setEnabled(true);
						wifiPWDText.setEnabled(true);
						wifiSSIDBtn.setEnabled(true);
						wifiPWDBtn.setEnabled(true);
                    }	            	
	            }, FactoryDefaultCmdInfo.class);
			}		
		});
		
		mCustomDialog = builder.create();
		mCustomDialog.show();
	}

}
