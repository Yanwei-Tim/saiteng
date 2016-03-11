package com.saiteng.st_individual;

import com.saiteng.st_individual.conn.AskLoginTask;
import com.saiteng.st_individual.view.DensityUtil;
import com.saiteng.st_individual.view.PreViewPopwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class LoginActivity extends Activity implements OnClickListener{
	private EditText mEditText_id,mEditText_port,mEditText_phoneNum;
	private Button mBtn_Ok,mBtn_Cancel;
	private Context context;
	private ConnectivityManager connManager;//网络连接管理器
	private NetworkInfo info;
	SharedPreferences sharedPreferences;
	Editor edit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_in);
		context=LoginActivity.this;
		Config.Login_mcontext = context;
		//当前网络信息
		connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo(); // 网络信息
		// dialog位置调整
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = DensityUtil.dip2px(this, 60);
		dialogWindow.setAttributes(lp);
		init();
	}

	private void init() {
		mEditText_id   = (EditText) findViewById(R.id.login_ip_edit);
		mEditText_port = (EditText) findViewById(R.id.login_port_edit);
		mEditText_phoneNum = (EditText) findViewById(R.id.login_phone_edit);
		
		mBtn_Ok = (Button) findViewById(R.id.login_server_ok);
		mBtn_Cancel = (Button) findViewById(R.id.login_server_cancel);
		mBtn_Ok.setOnClickListener(this);
		mBtn_Cancel.setOnClickListener(this);
		
		// 记录上一次登陆的信息
		SharedPreferences share = getSharedPreferences("lasthistory",
						Context.MODE_APPEND);
		boolean fristlogin = share.getBoolean("fristlogin",false);
		//如果不是第一次登录则将之前登录的信息填充到对应editText。
		if(fristlogin){
			mEditText_id.setText(share.getString("serverIP", ""));
			mEditText_port.setText(share.getString("serverPort", ""));
			mEditText_phoneNum.setText(share.getString("phoneNum", ""));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mBtn_Cancel.getId()) {
			finish();
		}
		if(v.getId() == mBtn_Ok.getId()){
			String ip = mEditText_id.getText().toString();
			String port = mEditText_port.getText().toString();
			String phone = mEditText_phoneNum.getText().toString();
			
			if (info != null && info.isConnected()) {
				if(!"".equals(ip)&&!"".equals(port)&&!"".equals(phone)){
					Config.phoneNum=phone;
					Config.ip=ip;
					Config.port=port;
					//用handler接收登录成功的消息，登录成功则把登录信息保存包SharedPreferences
					Handler handler = new Handler(){
						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							if("loginsuccess".equals(msg.obj.toString())){
								Config.mIsFristLogined=true;
								//提交登录成功信息更新导航栏PreViewPopwindow的信息
								Config.loginInfo = Config.ip+":"+Config.port;
								//标记登录成功
						         Config.mIsLogined=true;
						       //保存登录信息
								   sharedPreferences = getSharedPreferences(
											"lasthistory", Context.MODE_APPEND);
									edit = sharedPreferences.edit(); // 获取编辑器
									edit.putString("phoneNum", Config.phoneNum); //本机号码
									edit.putString("serverPort", Config.port); // 服务器端口号
									edit.putString("serverIP", Config.ip); // 设备ip
									edit.putBoolean("fristlogin",Config.mIsFristLogined);
									Config.medit=edit;
									edit.commit(); // 提交数据
								 finish();
							}
						}
					};
					Config.mhandler=handler;
					new AskLoginTask().execute(ip,port);
				}else{
					Toast.makeText(LoginActivity.this, getString(R.string.LoginToast), Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(LoginActivity.this, getString(R.string.NetWork), Toast.LENGTH_LONG).show();
			}
			
			
		}
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
