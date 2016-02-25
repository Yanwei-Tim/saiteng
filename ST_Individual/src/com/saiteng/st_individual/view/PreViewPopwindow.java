package com.saiteng.st_individual.view;

import com.saiteng.st_individual.CleanActivity;
import com.saiteng.st_individual.Config;
import com.saiteng.st_individual.LocalActivity;
import com.saiteng.st_individual.LocusActivity;
import com.saiteng.st_individual.LoginActivity;
import com.saiteng.st_individual.MonitoringActivity;
import com.saiteng.st_individual.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class PreViewPopwindow extends PopupWindow implements OnClickListener{

	private View mView;
	private Activity mActivity;
	private RelativeLayout mLayoutLogin;
	private LinearLayout mLinearLayout, mLayoutMonitoring, mLayoutLocus,
			mLayoutClean, mLayoutLocal;
	public PreViewPopwindow(Context context){
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		mView = (View) inflater.inflate(R.layout.popwindow_preview_more, null);
		mActivity = (Activity) context;
		initAction();
		initView(mView);
		initDate();
		
	}
	/***/
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
		mLayoutLogin = (RelativeLayout) mView
				.findViewById(R.id.preview_popwindow_login);
		mLayoutMonitoring = (LinearLayout) mView
				.findViewById(R.id.preview_popwindow_jiankong);
		mLayoutLocus = (LinearLayout) mView
				.findViewById(R.id.preview_popwindow_guiji);
		mLayoutClean = (LinearLayout) mView
				.findViewById(R.id.preview_popwindow_clean);
		mLayoutLocal = (LinearLayout) mView
				.findViewById(R.id.preview_popwindow_locate);
		mLinearLayout.setFocusableInTouchMode(true);
	}
	private void initDate() {
		mLayoutMonitoring.setOnClickListener(this);
		mLayoutLocus.setOnClickListener(this);
		mLayoutClean.setOnClickListener(this);
		mLayoutLocal.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		if(v.getId()==mLayoutMonitoring.getId()){
			intent.setClass(mActivity, MonitoringActivity.class);
		}
		if(v.getId()==mLayoutLocus.getId()){
			intent.setClass(mActivity, LocusActivity.class);
		}
		if(v.getId()==mLayoutClean.getId()){
			intent.setClass(mActivity, CleanActivity.class);
		}
		if(v.getId()==mLayoutLocal.getId()){
			intent.setClass(mActivity, LocalActivity.class);
		}
		mActivity.startActivity(intent);
		//this.dismiss();
	}

}
