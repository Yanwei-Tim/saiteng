package com.saiteng.st_master.fragments;

/**添加设备页面*/
import com.saiteng.st_master.Config;
import com.saiteng.st_master.Menu_VersionActivity;
import com.saiteng.st_master.R;
import com.saiteng.st_master.view.ST_InfoDialog;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class BottomFragment extends Fragment implements OnClickListener{
	private Button mBtn_addDivice,mBtn_aboutDivice;
	private View view;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    view= inflater.inflate(R.layout.fragment_bottmpreview,null);
		return view;
	}
	@Override
	public void onStart() {
		super.onStart();
		mBtn_addDivice = (Button) view.findViewById(R.id.add_divice);
		mBtn_aboutDivice = (Button) view.findViewById(R.id.about_divice);
		mBtn_addDivice.setOnClickListener(this);
		mBtn_aboutDivice.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.add_divice:
			ST_InfoDialog dialog = new ST_InfoDialog(Config.mManagecontext);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.show();
			break;
		case  R.id.about_divice:
			Intent intent  = new Intent();
			intent.setClass(Config.mManagecontext,Menu_VersionActivity.class);
			Config.mManagecontext.startActivity(intent);
			break;
		}
		
	}

}
