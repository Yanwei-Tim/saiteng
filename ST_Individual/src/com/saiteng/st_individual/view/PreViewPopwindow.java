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
import android.widget.TextView;
import android.widget.Toast;

public class PreViewPopwindow extends PopupWindow implements OnClickListener{

	private View mView;
	private Activity mActivity;
	private RelativeLayout mLayoutLogin;
	private LinearLayout mLinearLayout, mLayoutMonitoring, mLayoutLocus,
			mLayoutClean, mLayoutLocal;
	private TextView mView_LodinInfo;
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
		mView_LodinInfo= (TextView) mView.findViewById(R.id.preview_popwindow_login_info);
		mView_LodinInfo.setText(Config.loginInfo);
	}
	private void initDate() {
		mLayoutLogin.setOnClickListener(this);
		mLayoutMonitoring.setOnClickListener(this);
		mLayoutLocus.setOnClickListener(this);
		mLayoutClean.setOnClickListener(this);
		mLayoutLocal.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		if (v.getId() == mLayoutLogin.getId()) {
			intent.setClass(mActivity, LoginActivity.class);
		}
		if (v.getId() == mLayoutMonitoring.getId()) {
			if (Config.mIsLogined) {
				intent.setClass(mActivity, MonitoringActivity.class);
			} else {
				Toast.makeText(mActivity, "ÇëÏÈµÇÂ¼", Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (v.getId() == mLayoutLocus.getId()) {

			if (Config.mIsLogined) {
				intent.setClass(mActivity, LocusActivity.class);
			} else {
				Toast.makeText(mActivity, "ÇëÏÈµÇÂ¼", Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (v.getId() == mLayoutClean.getId()) {
			if (Config.mIsLogined) {
			//	 intent.setClass(mActivity, CleanActivity.class);
				 BackInfoDialog dialog = new BackInfoDialog(mActivity,"±êÌâ");
				 dialog.show();
				 return;
			} else {
				Toast.makeText(mActivity, "ÇëÏÈµÇÂ¼", Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (v.getId() == mLayoutLocal.getId()) {
			if (Config.mIsLogined) {
			     intent.setClass(mActivity, LocalActivity.class);
			} else {
				Toast.makeText(mActivity, "ÇëÏÈµÇÂ¼", Toast.LENGTH_LONG).show();
				return;
			}
		}
		mActivity.startActivity(intent);
		this.dismiss();

	}

}
