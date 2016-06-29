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
			//��������Ѿ��������˳�����
			finish();
		}
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		String imei = tm.getDeviceId();
		Log.d("geek", imei);
//		if (!imei.equals("352562072153169")) {// note5
//			VideoUtils.showDialog(this, "�����δ�ڴ˻�������Ȩ!");
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
	//��������
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
					//�л�����ͷ
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
					//��������
					ChoceShakeValue chocevlaue = new ChoceShakeValue(context);
					chocevlaue.requestWindowFeature(Window.FEATURE_NO_TITLE);
					chocevlaue.show();
				}
				if(position==6){
					//¼���ļ�����
					Intent intent =new Intent();
					intent.setClass(context, RecordFile.class);
					context.startActivity(intent);
				}
				if(position==7){
					//�޸�����
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
				map.put("title","���⹦��");
				map.put("status", "���⹦���Ѵ�");
			}else if(i==1){
				map.put("title1","����ͷ");
				map.put("status1", "��ѡ��ǰ������ͷ");
			}else if(i==2){
				map.put("title2","��ʾԤ��");
				map.put("status2", "����ǰ��Ԥ��");
			}else if(i==3){
				map.put("title3","¼��ʱ��");
				map.put("status3", "Ĭ��15���ӱ���һ���ļ�");
			}else if(i==4){
				map.put("title4","ʵʱУ��");
				map.put("status4", "�����ѹر�");
			}else if(i==5){
				map.put("title5","��������");
				map.put("status5", "��");
			}else if(i==6){
				map.put("title6","¼���ļ�����");
				map.put("status6", "����ɽ�����ϸ����");
			}else if(i==7){
				map.put("title7","�޸�����");
				map.put("status7", "����޸�����");
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
