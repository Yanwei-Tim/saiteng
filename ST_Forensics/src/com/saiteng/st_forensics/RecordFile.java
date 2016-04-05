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
        		map.put("title", "�ļ�����");
        		map.put("status","����ʾ�����ļ�");
        	}
        	if(i==1){
        		map.put("title1", "����ɾ��");
        		map.put("status1","ɾ������ȡ֤�ļ�����������");
        	}
        	if(i==2){
        		map.put("title2", "�ļ��洢λ��");
        		map.put("status2","");
        	}
        	mlist.add(map);
        }
		return mlist;
	}

}
