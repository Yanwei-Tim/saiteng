package com.saiteng.st_forensics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saiteng.st_forensice.adapter.FileAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
@SuppressWarnings("rawtypes")
public class RecordFile<E> extends Activity{
	private List list = new ArrayList<E>();
	private FileAdapter myadapter;
	private ListView mlistview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recordfile);
		initView();
	}
	@SuppressWarnings("unchecked")
	private void initView() {
		mlistview = (ListView) findViewById(R.id.listview_recordfile);
		list = getData();
		myadapter = new FileAdapter<E>(RecordFile.this, list);
		mlistview.setAdapter(myadapter);
	}
	public List getData() {
		List<Map> mlist =new ArrayList<Map>();
        for(int i=0;i<3;i++){
        	Map<String, String> map =new HashMap<String, String>();
        	if(i==0){
        		map.put("title", "文件加密");
        		map.put("status","已显示所有文件");
        	}
        	if(i==1){
        		map.put("title1", "紧急删除");
        		map.put("status1","删除所有取证文件，谨慎操作");
        	}
        	if(i==2){
        		map.put("title2", "文件存储位置");
        		map.put("status2","");
        	}
        	mlist.add(map);
        }
		return mlist;
	}

}
