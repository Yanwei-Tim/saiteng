package com.saiteng.st_master.view;
import com.saiteng.st_master.Config;
import com.saiteng.st_master.Menu_ManageActivity;
import com.saiteng.st_master.R;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.conn.SaveinfoTask;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
/**添加设备对话框*/
public class ST_InfoDialog extends Dialog{
	private Context context;
	private Button mButtonOk, mButtonCancle;
	private EditText mEdit_mdivicename,mEdit_mdivicenum;
	//private Spinner mSpinner_type;
	public ST_InfoDialog(Context context1) {
		super(context1);
		this.context = context1;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_adddivice_dialog);
		
		initView();
		Config.InfoDialog=ST_InfoDialog.this;
	}
	private void initView() {
		mButtonOk = (Button) findViewById(R.id.base_dialog_set_ok);
		mButtonCancle = (Button) findViewById(R.id.base_dialog_set_cancel);
		mEdit_mdivicename = (EditText) findViewById(R.id.Dialog_divice_name);
		
		mEdit_mdivicenum  = (EditText) findViewById(R.id.Dialog_divice_num);
	//	mSpinner_type     = (Spinner) findViewById(R.id.Dialog_divice_typechoose);
		mButtonOk.setOnClickListener(new View.OnClickListener() {
		/**添加设备再添加之前要判断该设备是否在线即 所添加单兵设备必须登录单兵软件
		 *信标设备必须正常开启。这里用online数据库中是否存在改号码的设备来判断
		 **/
			@Override
			public void onClick(View v) {
				String mdivicename = mEdit_mdivicename.getText().toString();
				String mdivicenum  = mEdit_mdivicenum.getText().toString();
				//String mdivicetype = mSpinner_type.getSelectedItem().toString();
				if(mdivicename==null||mdivicenum==null){
					Toast.makeText(context,"设备名称、移动号码不能为空", Toast.LENGTH_LONG).show();
				}else{
					//添加设备
					ConnSocketServer.sendOrder("[ST*"+Config.imei+"*ADDDivce,"+mdivicename+","+mdivicenum+"]");
				}
				//添加新设备后更新UI
			//	Menu_ManageActivity.getGroupnumTask();
				dismiss();
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
