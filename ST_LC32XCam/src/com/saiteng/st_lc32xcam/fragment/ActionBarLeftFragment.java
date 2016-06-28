package com.saiteng.st_lc32xcam.fragment;

import com.example.st_lc32xcam.MainActivity;
import com.example.st_lc32xcam.R;
import com.saiteng.st_lc32xcam.utils.SmartCamDefine;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ActionBarLeftFragment extends Fragment implements OnClickListener{
	private View rootView;
	private static ImageView mImg_play_off,mIng_takepicture,mImg_recording;
	private static LinearLayout leftview;
	private boolean isStop=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = inflater.inflate(R.layout.actionbarleft_preview, null);
    	leftview = (LinearLayout) rootView.findViewById(R.id.preview_actionbarleft_layout);
    	mImg_play_off = (ImageView) rootView.findViewById(R.id.play_off);
    	mIng_takepicture = (ImageView) rootView.findViewById(R.id.takepicture);
    	mImg_recording = (ImageView) rootView.findViewById(R.id.recording);
    	mImg_play_off.setOnClickListener(this);
    	mIng_takepicture.setOnClickListener(this);
    	mImg_recording.setOnClickListener(this);
    	return rootView;
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

		default:
			break;
		}
		
	}
	public static LinearLayout getLeftActionbarview(){
		return leftview;
		
	}

	public static ImageView getBtnView(){
		return mImg_recording;
	}


}
