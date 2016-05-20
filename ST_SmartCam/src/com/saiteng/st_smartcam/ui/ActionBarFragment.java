package com.saiteng.st_smartcam.ui;

import com.saiteng.st_smartcam.view.PreViewPopwindow;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;

public class ActionBarFragment extends Fragment{
	private static RelativeLayout actionbarview;
	private ImageView mimg_btn;
	private boolean isVisible = false;
	private boolean bTransfer = true;//
	private View rootView;
	private PreViewPopwindow preViewPopwindow;
	private RelativeLayout relativeLayout;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.actionbar_preview, null);
		actionbarview = (RelativeLayout) rootView.findViewById(R.id.preview_actionbar_layout);
		mimg_btn = (ImageView) rootView.findViewById(R.id.preview_actionbar_btn);
		relativeLayout = (RelativeLayout)rootView.findViewById(R.id.preview_actionbar_layout);
		mimg_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (bTransfer) {
					preViewPopwindow = new PreViewPopwindow(MainActivity.getContext());
					preViewPopwindow.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss() {
							bTransfer = true;
						}
					});
					int[] location = new int[2];
					relativeLayout.getLocationOnScreen(location);
					preViewPopwindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY,
							location[0] - preViewPopwindow.getWidth(), 0);
					bTransfer = false;
				}else{
					preViewPopwindow.dismiss();
					bTransfer = true;
				}
			}
		});
		return rootView;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	public static RelativeLayout getactionbarview(){
		return actionbarview;
		
	}

}
