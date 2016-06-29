package com.saiteng.st_forensics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.saiteng.st_forensice.adapter.MyAdapter;
import com.saiteng.st_forensice.service.VideoService;
import com.saiteng.st_forensics.view.ChangeCamera;
import com.saiteng.st_forensics.view.ChoceShakeValue;
import com.saiteng.st_forensics.view.Comfir_Dialog;
import com.saiteng.st_forensics.view.RecordTime;
import com.saiteng.st_forensics.view.VideoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class MainActivity extends Activity {
	private Context context;
	private MyAdapter<?> myAdapter;
	private ListView my_listview;
	private VideoService videoservice;
	private static Handler handler;
	private static SharedPreferences shared;
	private List list = new ArrayList<HashMap<String, Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = MainActivity.this;
		videoservice = VideoService.getInstance(context);
		if(videoservice!=null){
			//如果服务已经开启则退出程序
			finish();
		}
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		String imei = tm.getDeviceId();
		Log.d("geek", imei);
//		if (!imei.equals("352562072153169")) {// note5
//			VideoUtils.showDialog(this, "该软件未在此机器上授权!");
//			return;
//		}
		shared  =context.getSharedPreferences("lasthistory", Context.MODE_APPEND);
		boolean login = shared.getBoolean("Login", false);
		if(login){
			initView();
		}else{
			Comfir_Dialog diaolog = new Comfir_Dialog(context);
			diaolog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			diaolog.show();
		}
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==1){
					initView();
				}else
					finish();
			}
		};
	}
	public static Handler getHandler(){
		return handler;
		
	}
	//启动拍摄
	private void startService() {
		new VideoUtils(this);
		VideoUtils.createDirectory2Store(this);
		VideoUtils.createFilePath(this);
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, VideoService.class);
		MainActivity.this.startService(intent);
	}
	
	@SuppressWarnings("unchecked")
	private void initView() {
		setContentView(R.layout.main_list);
		my_listview = (ListView) findViewById(R.id.main_list);
		list = getData();
		myAdapter = new MyAdapter(context,list);
		my_listview.setAdapter(myAdapter);
		my_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if(position==1){
					//切换摄像头
					ChangeCamera camera = new ChangeCamera(context);
					camera.requestWindowFeature(Window.FEATURE_NO_TITLE);
					camera.show();
				}

				if(position==3){
					RecordTime recorder = new RecordTime(context);
					recorder.requestWindowFeature(Window.FEATURE_NO_TITLE);
					recorder.show();
				}
				if(position==5){
					//动作调节
					ChoceShakeValue chocevlaue = new ChoceShakeValue(context);
					chocevlaue.requestWindowFeature(Window.FEATURE_NO_TITLE);
					chocevlaue.show();
				}
				if(position==6){
					//录像文件设置
					Intent intent =new Intent();
					intent.setClass(context, RecordFile.class);
					context.startActivity(intent);
				}
				if(position==7){
					//修改密码
					Intent intent = new Intent();
					intent.setClass(context, ChangePassWord.class);
					context.startActivity(intent);
				}
			}
		});
	}
	@SuppressWarnings("unchecked")
	private List<HashMap<String, Object>> getData() {
		Map<String, Object> map=null;
		for(int i=0;i<8;i++){
			map = new HashMap<String, Object>();
			if(i==0){
				map.put("title","特殊功能");
				map.put("status", "特殊功能已打开");
			}else if(i==1){
				map.put("title1","摄像头");
				map.put("status1", "已选择前置摄像头");
			}else if(i==2){
				map.put("title2","显示预览");
				map.put("status2", "摄像前无预览");
			}else if(i==3){
				map.put("title3","录像时间");
				map.put("status3", "默认15分钟保存一次文件");
			}else if(i==4){
				map.put("title4","实时校队");
				map.put("status4", "服务已关闭");
			}else if(i==5){
				map.put("title5","动作调节");
				map.put("status5", "大");
			}else if(i==6){
				map.put("title6","录像文件设置");
				map.put("status6", "点击可进行详细设置");
			}else if(i==7){
				map.put("title7","修改密码");
				map.put("status7", "点击修改密码");
			}
			list.add(map);
		}
		return list;
	}
    
	@SuppressWarnings("static-access")
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			if(Config.recording){
				startService();
				Config.recording=false;
			}
		  finish();
	    }
		return true;
	}
	

	
}
