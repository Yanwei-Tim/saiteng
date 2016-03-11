package com.saiteng.st_master.fragments;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.Menu_VersionActivity;
import com.saiteng.st_master.R;
import com.saiteng.st_master.view.ST_InfoDialog;
import com.saiteng.st_master.view.Utils;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class BottomDanBingFragment extends Fragment implements OnClickListener{
	private View view;
	private Button mdanbing_add,mdanbing_delete,mdanbing_param,mdanbing_guiji,mdanbing_shock,mdanbing_about;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 view= inflater.inflate(R.layout.fragment_bottomdanbing,null);
		 return view;
	}
	 @Override
	public void onStart() {
		super.onStart();
		mdanbing_add = (Button) view.findViewById(R.id.danbing_add_divice);
		mdanbing_delete = (Button) view.findViewById(R.id.danbing_delete_divice);
		mdanbing_param = (Button) view.findViewById(R.id.danbing_param_divice);
		mdanbing_guiji = (Button) view.findViewById(R.id.danbing_guiji_divice);
		mdanbing_about = (Button) view.findViewById(R.id.danbing_about_divice);
		mdanbing_shock = (Button) view.findViewById(R.id.danbing_shock_divice);
		
		mdanbing_add.setOnClickListener(this);
		mdanbing_delete.setOnClickListener(this);
		mdanbing_param.setOnClickListener(this);
		mdanbing_guiji.setOnClickListener(this);
		mdanbing_about.setOnClickListener(this);
		mdanbing_shock.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.danbing_add_divice://添加设备
			ST_InfoDialog dialog = new ST_InfoDialog(Config.mManagecontext,"标题");
			dialog.show();
			break;
		case R.id.danbing_delete_divice://删除设备
			
			Utils.DeleteDialog(Config.mManagecontext, "是否确定删除选中的设备？");
			break;
		case R.id.danbing_param_divice://参数设置
			break;
		case R.id.danbing_guiji_divice://轨迹管理
			break;
		case R.id.danbing_shock_divice://关于
			
			break;
		case R.id.xianbiao_about_divice://关于
			Intent intent  = new Intent();
			intent.setClass(Config.mManagecontext,Menu_VersionActivity.class);
			Config.mManagecontext.startActivity(intent);
			break;
		
		}
		
	}


}
