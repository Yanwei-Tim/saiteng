package com.saiteng.st_forensice.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.saiteng.st_forensics.Config;
import com.saiteng.st_forensics.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
@SuppressWarnings("rawtypes")
public class MyAdapter<E> extends BaseAdapter{
	    final int VIEW_TYPE = 3;  
	    final int TYPE_1 = 0;  
	    final int TYPE_2 = 1;  
	    final int TYPE_3 = 2; 
	    final int TYPE_4 = 3;  
	    final int TYPE_5 = 4;  
	    final int TYPE_6 = 5;
	    final int TYPE_7 = 6;  
	    final int TYPE_8 = 7;  
	    final int TYPE_9 = 8; 
	    private LayoutInflater mInflater;
	    private List<Map<String, Object>> mdata;
	    private Handler handler=null;
	 
		private List mlist= new ArrayList<E>(); ;
	    ViewHolder  holder = null;  
        ViewHolder1 holder1 = null;  
        ViewHolder2 holder2 = null;
	   
	    
	public MyAdapter(Context context,List<Map<String, Object>> data){
		 this.mInflater = LayoutInflater.from(context);
		 this.mdata = data;
	}
	@Override
	public int getCount() {
		
		return mdata.size();
	}

	@Override
	public Object getItem(int position) {
		return mdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	
	@SuppressLint("InflateParams")
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(handler==null){
			handler = new Handler(){
			
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch(msg.what){
					case 0://����ѡ��ǰ������ͷ
						//��list������ÿһ��ViewHolder�����ڸ��½���ֵʱ������
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(1);
						holder1.status1.setText("��ѡ���������ͷ");
						break;
					case 1:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(1);
						holder1.status1.setText("��ѡ��ǰ������ͷ");
						break;
					case 2:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(5);
						holder1.status1.setText("С");
						break;
					case 3:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(5);
						holder1.status1.setText("��");
						break;
					case 4:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(5);
						holder1.status1.setText("��");
						break;
					case 5:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("Ĭ��5���ӱ���һ���ļ�");
						break;
					case 6:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("Ĭ��15���ӱ���һ���ļ�");
						break;
					case 7:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("Ĭ��30���ӱ���һ���ļ�");
						break;
					case 8:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("Ĭ��1Сʱ����һ���ļ�");
						break;
					}
				}
			};
			Config.madpterhandler = handler;
		}
    
        //���ò�����ʽ
        if(convertView==null){
        	if(position==TYPE_1){
        		convertView = mInflater.inflate(R.layout.type_item,null);
        		holder= new ViewHolder();
        		mlist.add(holder);
        		holder.title = (TextView) convertView.findViewById(R.id.main_title);
        		holder.status = (TextView) convertView.findViewById(R.id.main_status);
        	    holder.switchon = (Switch) convertView.findViewById(R.id.switch_open);
        	    convertView.setTag(holder1); 
        	}else if(position==TYPE_3||position==TYPE_5){
        		convertView = mInflater.inflate(R.layout.type2_item,null);
        		holder2= new ViewHolder2();
        		mlist.add(holder2);
        		holder2.title2 = (TextView) convertView.findViewById(R.id.type2_title);
        		holder2.status2 = (TextView) convertView.findViewById(R.id.type2_status);
        		holder2.chenckbox = (CheckBox) convertView.findViewById(R.id.checkbox_open);
        		convertView.setTag(holder2); 
        	}else{
        		convertView = mInflater.inflate(R.layout.type1_item,null);
        		holder1= new ViewHolder1();
        		mlist.add(holder1);
        		holder1.title1 = (TextView) convertView.findViewById(R.id.type1_title);
        		holder1.status1 = (TextView) convertView.findViewById(R.id.type1_status);
        	    convertView.setTag(holder1); 
        	}
        //������Դ
        }else{
        	if(position==TYPE_1){
        		holder = (ViewHolder) convertView.getTag();
        	}else if(position==TYPE_3||position==TYPE_5){
        		holder1 = (ViewHolder1) convertView.getTag();
        	}else
        		holder2 = (ViewHolder2) convertView.getTag();
        }
        	switch(position){
        	case TYPE_1:
        		holder.title.setText((String)mdata.get(position).get("title"));
        		holder.status.
        		setText((String)mdata.get(position).get("status"));
        		//switch��ť�����¼�
        		holder.switchon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							Config.recording=true;
							holder.status.setText("�ѿ������⹦��");
						}else{
							Config.recording=false;
							holder.status.setText("�ѹر����⹦��");
						}
					}
				});
        		break;
        	case TYPE_2:
        		holder1.title1.setText((String)mdata.get(position).get("title1"));
        		holder1.status1.setText((String)mdata.get(position).get("status1"));
        		break;
        	case TYPE_3:
        		
        		holder2.title2.setText((String)mdata.get(position).get("title2"));
        		holder2.status2.setText((String)mdata.get(position).get("status2"));
        		holder2.chenckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				//¼��ǰ�Ƿ���ʾԤ��
        			@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						holder2 = (MyAdapter<E>.ViewHolder2) mlist.get(2);
						if(isChecked){//����Ԥ��
							Config.mstartPreview=true;
							holder2.status2.setText("����ǰ��Ԥ��");
						}else{
							Config.mstartPreview=false;
							holder2.status2.setText("����ǰ��Ԥ��");
						}
					}
				});
        		break;
        	case TYPE_4:
        		holder1.title1.setText((String)mdata.get(position).get("title3"));
        		holder1.status1.setText((String)mdata.get(position).get("status3"));
        		break;
        	case TYPE_5:
        		holder2.title2.setText((String)mdata.get(position).get("title4"));
        		holder2.status2.setText((String)mdata.get(position).get("status4"));
        		holder2.chenckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						holder2 = (MyAdapter<E>.ViewHolder2) mlist.get(4);
						if(isChecked){//ʵʱУ��
							Config.mproofread=true;
							holder2.status2.setText("�����ѿ���");
						}else{
							Config.mproofread=false;
							holder2.status2.setText("�����ѹر�");
						}
						
					}
				});
        		break;
        	case TYPE_6:
        		holder1.title1.setText((String)mdata.get(position).get("title5"));
        		holder1.status1.setText((String)mdata.get(position).get("status5"));
        		break;
        	case TYPE_7:
        		holder1.title1.setText((String)mdata.get(position).get("title6"));
        		holder1.status1.setText((String)mdata.get(position).get("status6"));
        		break;
        	case TYPE_8:
        		holder1.title1.setText((String)mdata.get(position).get("title7"));
        		holder1.status1.setText((String)mdata.get(position).get("status7"));
        		break;
        	case TYPE_9:
        		holder1.title1.setText((String)mdata.get(position).get("title8"));
        		holder1.status1.setText((String)mdata.get(position).get("status8"));
        		break;
        	}
		return convertView;
	}
	
  public final class ViewHolder{
		private TextView title;
		private TextView status;
		private Switch switchon;
	}
  public final class ViewHolder1{
		private TextView title1;
		private TextView status1;
	}
  public final class ViewHolder2{
		private TextView title2;
		private TextView status2;
		private CheckBox chenckbox;
	}

}
