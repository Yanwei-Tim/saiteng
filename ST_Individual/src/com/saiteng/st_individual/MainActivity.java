package com.saiteng.st_individual;

import com.saiteng.st_individual.view.PreViewPopwindow;
import com.saiteng.st_individual.view.Utils;
import com.baidu.mapapi.SDKInitializer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
	public void exitSystem() {
		
		finish();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stubs
		super.onDestroy();
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			Utils.ExitDialog(context, "确定退出？");
		}
		return true;
	}
	
}
