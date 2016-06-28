package com.saiteng.st_lc32xcam.fragment;

import com.example.st_lc32xcam.MainActivity;
import com.example.st_lc32xcam.R;
import com.saiteng.st_lc32xcam.utils.SmartCamDefine;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ActionBarTopFragment extends Fragment implements OnClickListener{
	private View rootView;
	private static LinearLayout topview;
	private ImageView mediaBtn;
	private ImageView settingBtn;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.topportrait_preview, null);
		topview = (LinearLayout) rootView.findViewById(R.id.layout_topportraitview);
		settingBtn = (ImageView) rootView.findViewById(R.id.setting);
		mediaBtn = (ImageView) rootView.findViewById(R.id.filelook);
		settingBtn.setOnClickListener(this);
		mediaBtn.setOnClickListener(this);
		return rootView;
	}
	
	public static LinearLayout getActionBarTopFragment(){
		return topview;
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting:
			MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_SETTING);
			break;
		case R.id.filelook:
			MainActivity.getHandler().sendEmptyMessage(SmartCamDefine.SMARTCAM_LOOKFILE);
			break;
		default:
			break;
		}
		
	}
}
