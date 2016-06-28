package com.example.st_lc32xcam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OperateView extends Dialog implements android.view.View.OnClickListener{
	private Context mcontext;
	private Button mBtn_downlaod,mBtn_downlaodall,mBtn_delete,mBtn_deleteall,mBtn_cannel;

	public OperateView(Context context) {
		super(context);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.operateview);
		initView();
	}
	private void initView() {
		mBtn_downlaod = (Button) findViewById(R.id.download_single);
		mBtn_downlaodall = (Button) findViewById(R.id.download_all);
		mBtn_downlaodall.setVisibility(View.GONE);
		mBtn_delete = (Button) findViewById(R.id.delete_single);
		mBtn_deleteall = (Button) findViewById(R.id.delete_all);
		mBtn_deleteall.setVisibility(View.GONE);
		mBtn_cannel  = (Button) findViewById(R.id.cannel);
		mBtn_downlaod.setOnClickListener(this);
		mBtn_downlaodall.setOnClickListener(this);
		mBtn_delete.setOnClickListener(this);
		mBtn_deleteall.setOnClickListener(this);
		mBtn_cannel.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.download_single:
			FTPActivity.getHandler().sendEmptyMessage(0);
			this.dismiss();
			break;
       case R.id.download_all:
    		this.dismiss();
			break;
       case R.id.delete_single:
    	    FTPActivity.getHandler().sendEmptyMessage(1);
    		this.dismiss();
			break;
       case R.id.delete_all:
    		this.dismiss();
			break;
       case R.id.cannel:
    		this.dismiss();
			break;
		default:
			this.dismiss();
			break;
		}
	}

}
