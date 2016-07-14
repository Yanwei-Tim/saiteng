package com.saiteng.st_master.fragments;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.Menu_TrackManageActivity;
import com.saiteng.st_master.Menu_VersionActivity;
import com.saiteng.st_master.R;
import com.saiteng.st_master.view.Param_Dialog;
import com.saiteng.st_master.view.ST_InfoDialog;
import com.saiteng.st_master.view.Utils;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.view.View.OnClickListener;
/**
 * 点击设备管理里面的 单兵设备 的点选框时，底部弹出对应的导航栏
 * 
 * */

public class BottonXinBiaoFragment extends Fragment implements OnClickListener{
	private View view;
	private Button mxinbiao_add,mxinbiao_delete,mxinbiao_param,mxinbiao_guiji,mxianbiao_about;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 view= inflater.inflate(R.layout.fragment_bottomxinbiao,null);
		 return view;
	}
	 @Override
	public void onStart() {
		super.onStart();
		mxinbiao_add = (Button) view.findViewById(R.id.xinbiao_add_divice);
		mxinbiao_delete = (Button) view.findViewById(R.id.xinbiao_delete_divice);
		mxinbiao_param = (Button) view.findViewById(R.id.xinbiao_param_divice);
		mxinbiao_guiji = (Button) view.findViewById(R.id.xinbiao_guiji_divice);
		mxianbiao_about = (Button) view.findViewById(R.id.xianbiao_about_divice);
		
		mxinbiao_add.setOnClickListener(this);
		mxinbiao_delete.setOnClickListener(this);
		mxinbiao_param.setOnClickListener(this);
		mxinbiao_guiji.setOnClickListener(this);
		mxianbiao_about.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		Intent intent  = new Intent();
		switch(v.getId()){
		case R.id.xinbiao_add_divice://添加设备
			ST_InfoDialog dialog = new ST_InfoDialog(Config.mManagecontext);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.show();
			break;
		case R.id.xinbiao_delete_divice://删除设备
			Utils.DeleteDialog(Config.mManagecontext, "是否确定删除选中的设备？");
			break;
		case R.id.xinbiao_param_divice://参数设置
			Param_Dialog para_dialog = new Param_Dialog(Config.mManagecontext);
			para_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			para_dialog.show();
			break;
		case R.id.xinbiao_guiji_divice://轨迹管理
			intent.setClass(Config.mManagecontext,Menu_TrackManageActivity.class);
			Config.mManagecontext.startActivity(intent);
			break;
		case R.id.xianbiao_about_divice://关于
			intent.setClass(Config.mManagecontext,Menu_VersionActivity.class);
			Config.mManagecontext.startActivity(intent);
			break;
		}
		
	}
}
