package com.saiteng.st_individual;

import com.saiteng.st_individual.view.PreViewPopwindow;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements OnClickListener{
	private ImageView mImage;
	private RelativeLayout relativeLayout;
	private boolean bTransfer = true;
	private PreViewPopwindow preViewPopwindow;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏  
		context = MainActivity.this;
		mImage = (ImageView) findViewById(R.id.preview_actionbar_btn);
		mImage.setOnClickListener(this);
		relativeLayout = (RelativeLayout)findViewById(R.id.preview_actionbar_layout);
	}
     /**点击按钮弹出窗口
      * 根据窗口按钮再做其他操作
      * */
	@Override
	public void onClick(View v) {
		if (bTransfer) {
			
			preViewPopwindow = new PreViewPopwindow(context);
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
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		preViewPopwindow.dismiss();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		preViewPopwindow.dismiss();
	}

	
}
