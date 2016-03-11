package com.saiteng.st_master.view;
import com.saiteng.st_master.R;
import com.saiteng.st_master.R.id;
import com.saiteng.st_master.conn.SaveinfoTask;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ST_InfoDialog extends Dialog{
	private Context context;
	private String title;
	private Button mButtonOk, mButtonCancle;
	private EditText mEdit_mdivicename,mEdit_mdivicenum;
	private Spinner mSpinner_type;
	public ST_InfoDialog(Context context1,String title1) {
		super(context1);
		this.title = title1;
		this.context = context1;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_adddivice_dialog);
		initView();
		
	}
	private void initView() {
		mButtonOk = (Button) findViewById(R.id.base_dialog_set_ok);
		mButtonCancle = (Button) findViewById(R.id.base_dialog_set_cancel);
		mEdit_mdivicename = (EditText) findViewById(R.id.Dialog_divice_name);
		
		mEdit_mdivicenum  = (EditText) findViewById(R.id.Dialog_divice_num);
		mSpinner_type     = (Spinner) findViewById(R.id.Dialog_divice_typechoose);
		mButtonOk.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				String mdivicename = mEdit_mdivicename.getText().toString();
				String mdivicenum  = mEdit_mdivicenum.getText().toString();
				String mdivicetype = mSpinner_type.getSelectedItem().toString();
				if("前置信标".equals(mdivicetype)){
					mdivicetype="0";
				}
				if("单兵终端".equals(mdivicetype)){
					mdivicetype="1";
				}
				if(mdivicename==null||mdivicenum==null){
					Toast.makeText(context,"设备名称、移动号码不能为空", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(context,"保存到数据库", Toast.LENGTH_LONG).show();
					new SaveinfoTask().execute(mdivicename+","+mdivicenum+","+mdivicetype);
				}
				
			}
		});
		mButtonCancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ST_InfoDialog.this.dismiss();
				
			}
		});
		
	};

}
