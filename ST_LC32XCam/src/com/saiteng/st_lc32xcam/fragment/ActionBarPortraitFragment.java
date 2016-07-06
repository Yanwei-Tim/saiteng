package com.saiteng.st_lc32xcam.fragment;

import com.example.st_lc32xcam.MainActivity;
import com.example.st_lc32xcam.R;
import com.saiteng.st_lc32xcam.utils.SmartCamDefine;

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

public class ActionBarPortraitFragment extends Fragment implements OnClickListener{
	private View rootView;
	private static LinearLayout portraitview;
	private static ImageView mImg_play_off,mIng_takepicture,mImg_recording,mImg_connect_status,mIng_file_browes,mImg_setting;
	private boolean isStop=false;
	private static Handler handler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    rootView = inflater.inflate(R.layout.actionbarportrait_preview, null);
	    portraitview = (LinearLayout) rootView.findViewById(R.id.layout_portraitview);
	    mImg_play_off = (ImageView) rootView.findViewById(R.id.play_off);
    	mIng_takepicture = (ImageView) rootView.findViewById(R.id.takepicture);
    	mImg_recording = (ImageView) rootView.findViewById(R.id.recording);
    	mImg_connect_status = (ImageView) rootView.findViewById(R.id.connect_status);
		mIng_file_browes = (ImageView) rootView.findViewById(R.id.file_browes);
		mImg_connect_status.setOnClickListener(this);
		mIng_file_browes.setOnClickListener(this);
    	mImg_play_off.setOnClickListener(this);
    	mIng_takepicture.setOnClickListener(this);
    	mImg_recording.setOnClickListener(this);
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
	
	public static Handler getHandler(){
		return handler;
		
	}
	
	public static LinearLayout getActionPortraitBarview(){
		return portraitview;
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_off:
			if(!isStop){
				MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_PALYSTOP);
				isStop=true;
				mImg_play_off.setImageResource(R.drawable.btn_play);
			}else{
				MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_PALYSTOP);
				isStop=false;
				mImg_play_off.setImageResource(R.drawable.pause);
			}
			
			break;
		case R.id.takepicture:
			MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_TAKEPICTURE);
			break;
		case R.id.recording:
			MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_RECORDING);
			break;
		case R.id.connect_status:
			
			break;
		

		default:
			break;
		}
	}
	

}
