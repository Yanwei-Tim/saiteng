package com.saiteng.st_lc32xcam.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.st_lc32xcam.MainActivity;
import com.example.st_lc32xcam.R;
import com.saiteng.st_lc32xcam.utils.SmartCamDefine;

public class ActionBarRightFragment extends Fragment implements OnClickListener{
	private static LinearLayout actionbarview;
	private View rootView;
	private ImageView mImg_connect_status,mIng_file_browes,mImg_setting;
	private static Handler handler;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.actionbar_preview, null);
		actionbarview = (LinearLayout) rootView.findViewById(R.id.preview_actionbar_layout);
		mImg_connect_status = (ImageView) rootView.findViewById(R.id.connect_status);
		mIng_file_browes = (ImageView) rootView.findViewById(R.id.file_browes);
		mImg_setting = (ImageView) rootView.findViewById(R.id.setting);
		mImg_connect_status.setOnClickListener(this);
		mIng_file_browes.setOnClickListener(this);
		mImg_setting.setOnClickListener(this);
		return rootView;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==0){
					mImg_connect_status.setImageResource(R.drawable.wifi_enabled);
				}else if(msg.what==1){
					mImg_connect_status.setImageResource(R.drawable.wifi_disabled);
				}
			}
		};
		
	}
	
	public static LinearLayout getactionbarview(){
		return actionbarview;
		
	}
	
	public static Handler getHandler(){
		return handler;
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.file_browes:
			MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_FILE);
			break;
		case R.id.connect_status:
			
			break;
		case R.id.setting:
			MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_SETTING);
			break;

		default:
			break;
		}
	     
		
	}

}
