package com.example.st_lc32xcam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTPFile;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.saiteng.lc32xcam.adapter.LocalAdapter;
import com.saiteng.lc32xcam.adapter.RemoteAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 测试Activity.
 * @author cui_tao
 *
 */
public class FTPActivity extends Activity {
	/**
	 * 标签.
	 */
	static final String TAG = "FTPActivity";

	/**
	 * FTP.
	 */
	private FTP ftp;

	/**
	 * FTP文件集合.
	 */
	private List<FTPFile> remoteFile;

	/**
	 * 本地文件集合.
	 */
	private List<File> localFile;

	/**
	 * 本地根目录.
	 */
	private static final String LOCAL_PATH = "/mnt/sdcard/CAM";

	/**
	 * 当前选中项.
	 */
	private int position = 0;

	/**
	 * ListView.
	 */
	private ListView listMain;

	/**
	 * 切换到本地按钮.
	 */
	private Button buttonChangeLocal,btn_break;

	/**
	 * 切换到FTP按钮.
	 */
	private Button buttonChangeRemote;


	/**
	 * 断开连接按钮.
	 */
	private Button buttonClose;

	/**
	 * 服务器名.
	 */
	private String hostName;

	/**
	 * 用户名.
	 */
	private String userName;

	/**
	 * 密码.
	 */
	private String password;
	
	private Context context;
	private String CurrentDIR ="";//当前目录
	private String ParentDir ="";//上级目录
    private List<String> Dirlist = new ArrayList<String>();
    private static Handler handler;
    private DisplayImageOptions mDisplayImageOptions;  

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp_main);
		context  =FTPActivity.this;
		// 初始化视图
		initView();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 关闭服务
		try {
			ftp.closeConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化视图.
	 */
	private void initView() {
		// 初始化控件
		listMain = (ListView) findViewById(R.id.list);
		buttonChangeLocal = (Button) findViewById(R.id.button_change_local);
		buttonChangeRemote = (Button) findViewById(R.id.button_change_remote);
		buttonClose = (Button) findViewById(R.id.button_close);
		// 获取登录信息
		loginConfig();
		// ListView单击
		listMain.setOnItemClickListener(listMainItemClick);
		//ListView长按
		listMain.setOnItemLongClickListener(longClickable);
		// ListView选中项改变
		listMain.setOnItemSelectedListener(listMainItemSelected);
		// 切换到本地
		buttonChangeLocal.setOnClickListener(buttonChangeLocalClick);
		// 切换到FTP
		buttonChangeRemote.setOnClickListener(buttonChangeRemoteClick);
		// 断开FTP服务
		buttonClose.setOnClickListener(buttonCloseClick);
		// 加载FTP视图
		loadRemoteView();
	}

	/**
	 * 获取登录信息.
	 */
	private void loginConfig() {
		Intent intent = getIntent();
		hostName = intent.getStringExtra("hostName");
		userName = intent.getStringExtra("userName");
		password = intent.getStringExtra("password");
	}

	/**
	 * 加载FTP视图.
	 */
	private void loadRemoteView() {
		try {
			if (ftp != null) {
				// 关闭FTP服务
				ftp.closeConnect();
			}
			// 初始化FTP
			Log.e("hostName", hostName);
			Log.e("userName", userName);
			Log.e("passwrod", password);
			ftp = new FTP(hostName, userName, password,context);
			// 打开FTP服务
			ftp.openConnect();
			// 初始化FTP列表
			remoteFile = new ArrayList<FTPFile>();
			// 更改控件可见
			buttonChangeLocal.setVisibility(Button.VISIBLE);
			buttonChangeRemote.setVisibility(Button.INVISIBLE);
			//buttonDownload.setVisibility(Button.VISIBLE);
			// 加载FTP列表
			remoteFile = ftp.listFiles(FTP.REMOTE_PATH);
			// FTP列表适配器
			RemoteAdapter adapter = new RemoteAdapter(this, remoteFile);
			// 加载数据到ListView
			listMain.setAdapter(adapter);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ListView单击事件.
	 */
	private OnItemClickListener listMainItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			if (buttonChangeLocal.getVisibility() == Button.VISIBLE) {
				//远端文件
				if(remoteFile.get(position).isDirectory()){
					//如果是文件夹 则进入当前点击的文件夹,
					String newname = remoteFile.get(position).getName();
					remoteFile.clear();
					RemoteAdapter adapter1 = new RemoteAdapter(context, remoteFile);
					// 加载数据到ListView
					listMain.setAdapter(adapter1);
					CurrentDIR = CurrentDIR+"/"+newname;
					Dirlist.add(CurrentDIR);
					remoteFile = ftp.listFiles(CurrentDIR);
					// FTP列表适配器
					RemoteAdapter adapter = new RemoteAdapter(context, remoteFile);
					// 加载数据到ListView
					listMain.setAdapter(adapter);
				}
			} else {
				//本地文件
				Toast.makeText(FTPActivity.this,
						localFile.get(position).getName(), Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
	/**
	  * ListView 长按事件 
	  */
	private OnItemLongClickListener longClickable = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			if (buttonChangeLocal.getVisibility() == Button.VISIBLE) {
				OperateView opview = new OperateView(FTPActivity.this);
				//去掉dialog顶部标题所占的view
				opview.requestWindowFeature(Window.FEATURE_NO_TITLE);
				opview.show();
				handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						String strRemote = remoteFile.get(position).getName();
						if(msg.what==0){
							//download
							ProgressView ProgressView = new ProgressView(context);
							ProgressView.requestWindowFeature(Window.FEATURE_NO_TITLE);
							ProgressView.setCanceledOnTouchOutside(true);
							ProgressView.show();
							ftp.download(CurrentDIR+"/"+strRemote, LOCAL_PATH,strRemote);
						}else if(msg.what==1){
							//delete
							if( ftp.deleteSingle(CurrentDIR, strRemote)){
								Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
							}
							
						}
					}
				};
				return false;
			}
			return false;
		}
	};
	
	public static Handler getHandler(){
		return handler;
		
	}

	/**
	 * ListView选中项改变事件.
	 */
	private OnItemSelectedListener listMainItemSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View view,
				int location, long arg3) {
			// 获取当前选中项
			FTPActivity.this.position = location;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	/**
	 * 切换到本地.
	 */
	private OnClickListener buttonChangeLocalClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 加载本地视图
			Intent intent = new Intent();
			intent.setClass(context, LocalFiles.class);
			startActivity(intent);

		}
	};

	/**
	 * 切换到FTP.
	 */
	private OnClickListener buttonChangeRemoteClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 加载FTP视图
			CurrentDIR="";
			buttonClose.setText("断开");
			loadRemoteView();
		}
	};
	/**
	 * 断开服务.
	 */
	private OnClickListener buttonCloseClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if("返回上一级".equals(buttonClose.getText())){
				Toast.makeText(context, Dirlist.get(position), Toast.LENGTH_SHORT).show();
			}else if("断开".equals(buttonClose.getText())){
				try {
					// 关闭FTP服务
					ftp.closeConnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finish();

			}
		}
	};

}