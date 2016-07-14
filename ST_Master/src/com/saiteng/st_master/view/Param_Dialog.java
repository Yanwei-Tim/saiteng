package com.saiteng.st_master.view;

import java.util.ArrayList;
import java.util.List;

import com.saiteng.st_master.R;
import com.saiteng.st_master.conn.SentParamTask;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
/**
 * 参数设置对话框
 * */
public class Param_Dialog extends Dialog{
	private EditText editText;
	private Button positiveButton, negativeButton;
	private TextView title;
	private Spinner spinner_item;
	private List<String> data_list;
	private ArrayAdapter<String> arr_adapter;
	private Context context;

	public Param_Dialog(Context context) {
		super(context);
		this.context = context;
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_normal_layout);
		initView();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		spinner_item = (Spinner) findViewById(R.id.setting_param);
		positiveButton = (Button) findViewById(R.id.positiveButton);
		negativeButton = (Button) findViewById(R.id.negativeButton);
		
	
		positiveButton.setOnClickListener(new View.OnClickListener(){
            //参数上传到服务器，再又服务器将参数下发到指定手机或信标控制端
			@Override
			public void onClick(View v) {
				String interval = spinner_item.getSelectedItem().toString();
				if("5秒".equals(interval)){
					interval ="5";
				}else if("10秒".equals(interval)){
					interval ="10";
				}else if("30秒".equals(interval)){
					interval ="30";
				}else if("1分钟".equals(interval)){
					interval ="60";
				}
				new SentParamTask().execute(interval);
			    dismiss();
			}
			
		});
		negativeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Param_Dialog.this.dismiss();
				
			}
		});
	}

}
