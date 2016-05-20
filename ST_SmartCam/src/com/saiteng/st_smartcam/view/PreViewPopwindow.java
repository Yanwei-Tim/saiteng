package com.saiteng.st_smartcam.view;
import com.saiteng.st_smartcam.ui.MainActivity;
import com.saiteng.st_smartcam.ui.R;
import com.saiteng.st_smartcam.utils.SmartCamDefine;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PreViewPopwindow extends PopupWindow implements OnClickListener{
	private View mView;
	private Activity mActivity;
	private Handler handler;
	private LinearLayout wifistatus;
	private LinearLayout mLinearLayout, mLayout_play_stop, mLayout_setting,
	mLayout_takepicture, mLayout_recording,mLayout_file;

	public PreViewPopwindow(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mView = (View) inflater.inflate(R.layout.popwindow_preview_more, null);
		mActivity = (Activity) context;
		handler = MainActivity.getHandler();
		initAction();
		initView(mView);
		initData();
	}

	private void initAction() {
		this.setContentView(mView);
		DisplayMetrics metrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		this.setHeight(metrics.heightPixels / 2);
		this.setWidth(metrics.widthPixels / 3);
		this.setFocusable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setAnimationStyle(R.style.PopupPreview);
	}
	private void initView(View mView) {
		mLinearLayout = (LinearLayout) mView
				.findViewById(R.id.popwindow_preview_layout);
		wifistatus = (LinearLayout) mView
				.findViewById(R.id.wifi_status);
		mLayout_takepicture = (LinearLayout) mView
				.findViewById(R.id.takepicture);
		mLayout_recording = (LinearLayout) mView
				.findViewById(R.id.recording);
		mLayout_play_stop = (LinearLayout) mView
				.findViewById(R.id.play_stop);
		mLayout_file = (LinearLayout) mView
				.findViewById(R.id.file);
		mLayout_setting = (LinearLayout) mView
				.findViewById(R.id.setting);
		mLinearLayout.setFocusableInTouchMode(true);
	}
	private void initData() {
		wifistatus.setOnClickListener(this);
		mLayout_takepicture.setOnClickListener(this);
		mLayout_recording.setOnClickListener(this);
		mLayout_play_stop.setOnClickListener(this);
		mLayout_file.setOnClickListener(this);
		mLayout_setting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.wifi_status){
			//Á¬½Ó×´Ì¬
			
		}else if(v.getId()==R.id.takepicture){
			//ÅÄÕÕ
			if(handler!=null){
				handler.sendEmptyMessage(SmartCamDefine.SMARTCAM_TAKEPICTURE);
			}
			
		}else if(v.getId()==R.id.recording){
			//Â¼Ïñ
			if(handler!=null){
				handler.sendEmptyMessage(SmartCamDefine.SMARTCAM_RECORDING);
			}
			
		}else if(v.getId()==R.id.play_stop){
			//²¥·ÅÔÝÍ£
			if(handler!=null){
				handler.sendEmptyMessage(SmartCamDefine.SMARTCAM_PALYSTOP);
			}
			
			
		}else if(v.getId()==R.id.setting){
			//ÉèÖÃ
			if(handler!=null){
				handler.sendEmptyMessage(SmartCamDefine.SMARTCAM_SETTING);
			}
			
		}else if(v.getId()==R.id.file){
			//ÎÄ¼þä¯ÀÀ
			if(handler!=null){
				handler.sendEmptyMessage(SmartCamDefine.SMARTCAM_FILE);
			}
		}
		
	}
}
