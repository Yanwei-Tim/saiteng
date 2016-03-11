package com.saiteng.st_master.fragments;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.LocateActivity;
import com.saiteng.st_master.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
/**设备跟踪菜单里面点击按钮的底部弹框*/
public class BottomGenzongFragment extends Fragment implements OnClickListener{
	private Button mBtn_navigation,mBtn_Genzong;
	private View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 view= inflater.inflate(R.layout.fragment_bottomgenzong,null);
		 return view;
	}
	@Override
	public void onStart() {
		super.onStart();
		mBtn_navigation = (Button) view.findViewById(R.id.navigation);
		mBtn_Genzong    = (Button) view.findViewById(R.id.genzong);
		mBtn_navigation.setOnClickListener(this);
		mBtn_Genzong.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()){
		case R.id.navigation:
			break;
		case R.id.genzong:
			intent.setClass(Config.mManagecontext, LocateActivity.class);
			Config.mManagecontext.startActivity(intent);
			break;
		}
	}
}
