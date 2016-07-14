package com.saiteng.st_master.fragments;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.Menu_TrackManageActivity;
import com.saiteng.st_master.Menu_VersionActivity;
import com.saiteng.st_master.R;
import com.saiteng.st_master.view.Param_Dialog;
import com.saiteng.st_master.view.ST_InfoDialog;
import com.saiteng.st_master.view.Shake_Dialog;
import com.saiteng.st_master.view.Utils;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
/**
 * 点击设备管理里面的 信标设备 的点选框时，底部弹出对应的导航栏
 * 
 * */
public class BottomDanBingFragment extends Fragment implements OnClickListener{
	private View view;
	private Button mdanbing_param,mdanbing_guiji,mdanbing_shock,mdanbing_holdon;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 view= inflater.inflate(R.layout.fragment_bottomdanbing,null);
		 return view;
	}
	 @Override
	public void onStart() {
		super.onStart();
		
		mdanbing_param = (Button) view.findViewById(R.id.danbing_param_divice);
		mdanbing_guiji = (Button) view.findViewById(R.id.danbing_guiji_divice);
		mdanbing_holdon = (Button) view.findViewById(R.id.danbing_HoldOn_divice);
		mdanbing_shock = (Button) view.findViewById(R.id.danbing_shock_divice);
		
	
		mdanbing_param.setOnClickListener(this);
		mdanbing_guiji.setOnClickListener(this);
		mdanbing_holdon.setOnClickListener(this);
		mdanbing_shock.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent  = new Intent();
		switch(v.getId()){
		case R.id.danbing_param_divice://参数设置
			Param_Dialog para_dialog = new Param_Dialog(Config.mManagecontext);
			para_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			para_dialog.show();
			break;
		case R.id.danbing_guiji_divice://轨迹管理
			intent.setClass(Config.mManagecontext,Menu_TrackManageActivity.class);
			Config.mManagecontext.startActivity(intent);
			break;
		case R.id.danbing_shock_divice://震动
			Shake_Dialog shake_dialog = new Shake_Dialog(Config.mManagecontext);
			shake_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			shake_dialog.show();
			break;
		case R.id.danbing_HoldOn_divice://待机
		
			break;
		
		}
		
	}


}
