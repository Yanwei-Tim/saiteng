package com.saiteng.st_master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity{
	private ListView mView_menuListView;
	private SimpleAdapter menu_adapter;
	private List<Map<String,Object>> mlist;
	private Intent intent=new Intent();
	//数据源准备
	private String[] title={"设备管理","设备跟踪","手工定位","修改密码"};
	private int[] img_id={R.drawable.menu_management,R.drawable.menu_tracking,R.drawable.menu_location,
			R.drawable.menu_changepassword};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_listview);
		mView_menuListView = (ListView) findViewById(R.id.Menu_listview);
		mlist=initData();
		//绑定数据源
		SimpleAdapter simplead = new SimpleAdapter(this, mlist, R.layout.item_memu, 
				                  new String[]{"img","title"}, new int[]{R.id.Menu_img,R.id.Menu_text});
		mView_menuListView.setAdapter(simplead);
		mView_menuListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 TextView content=(TextView) view.findViewById(R.id.Menu_text);
			     if(title[0].equals(content.getText().toString())){
					 intent.setClass(MenuActivity.this, Menu_ManageActivity.class);
					 //Toast.makeText(MenuActivity.this,content.getText().toString(),Toast.LENGTH_LONG).show();
				 }
				 if(title[1].equals(content.getText().toString())){
					 intent.setClass(MenuActivity.this, Menu_TrackActivity.class);
					 //Toast.makeText(MenuActivity.this,content.getText().toString(),Toast.LENGTH_LONG).show();
				 }
				 if(title[2].equals(content.getText().toString())){
					 intent.setClass(MenuActivity.this, Menu_LocateActivity.class);
					 //Toast.makeText(MenuActivity.this,content.getText().toString(),Toast.LENGTH_LONG).show();
				 }
				 if(title[3].equals(content.getText().toString())){
					 intent.setClass(MenuActivity.this, Menu_ChangepwdActivity.class);
					 //Toast.makeText(MenuActivity.this,content.getText().toString(),Toast.LENGTH_LONG).show();
				 }
				 MenuActivity.this.startActivity(intent);
			}
		});
	}
	//这里的数据源为已知的四个。
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> initData() {
		List<Map<String,Object>> list  = new ArrayList<Map<String, Object>>();
		
		for(int i=0;i<4;i++){
			Map map=new HashMap<String, Object>();
			map.put("img",img_id[i]);
			map.put("title",title[i]);
			list.add(map);
		}
		return list;
	}
}
