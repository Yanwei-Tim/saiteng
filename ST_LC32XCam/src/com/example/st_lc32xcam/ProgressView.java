package com.example.st_lc32xcam;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressView extends Dialog{
	 private ProgressBar mProgress ;
	 private TextView mTextView;
	 private Button btn_status;
	 private static Handler mHandler;
	 private int progress=0;

	public ProgressView(Context context) {
		super(context);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_progress);
		 mProgress = (ProgressBar) findViewById(R.id.progressbar);
		 mTextView = (TextView) findViewById(R.id.tv_progress);
		 btn_status = (Button) findViewById(R.id.btn_break);
		 btn_status.setText("下载中...");
		 mHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 3:
						ProgressView.this.setCanceledOnTouchOutside(false);
						progress++;
						mProgress.setProgress((int) progress);
						mTextView.setText(progress + "/100");
						break;
					case 4:
						ProgressView.this.setCanceledOnTouchOutside(true);
						mTextView.setText("完成");
						break;
					case 5:
						//Toast.makeText(mContext, "下载已经取消", Toast.LENGTH_SHORT).show();
						//mButton.setText("继续");
					case 6:
						mProgress.setProgress(100);
						mTextView.setText("文件已经存在，请勿重复下载");
						
						break;
					default:
						break;
					}
				};
			};
		 
	}
	
		public static Handler getProgressHandler(){
			return mHandler;
		}
		
		

}
