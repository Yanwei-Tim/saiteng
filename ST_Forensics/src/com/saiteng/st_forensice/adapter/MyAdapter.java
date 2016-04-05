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
					case 0://更新选择前后摄像头
						//用list来保存每一个ViewHolder，用在更新界面值时的区别
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(1);
						holder1.status1.setText("已选择后置摄像头");
						break;
					case 1:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(1);
						holder1.status1.setText("已选择前置摄像头");
						break;
					case 2:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(5);
						holder1.status1.setText("小");
						break;
					case 3:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(5);
						holder1.status1.setText("中");
						break;
					case 4:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(5);
						holder1.status1.setText("大");
						break;
					case 5:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("默认5分钟保存一次文件");
						break;
					case 6:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("默认15分钟保存一次文件");
						break;
					case 7:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("默认30分钟保存一次文件");
						break;
					case 8:
						holder1=(MyAdapter<E>.ViewHolder1) mlist.get(3);
						holder1.status1.setText("默认1小时保存一次文件");
						break;
					}
				}
			};
			Config.madpterhandler = handler;
		}
    
        //设置布局样式
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
        //设置资源
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
        		//switch按钮监听事件
        		holder.switchon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							Config.recording=true;
							holder.status.setText("已开启特殊功能");
						}else{
							Config.recording=false;
							holder.status.setText("已关闭特殊功能");
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
				//录像前是否显示预览
        			@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						holder2 = (MyAdapter<E>.ViewHolder2) mlist.get(2);
						if(isChecked){//开启预览
							Config.mstartPreview=true;
							holder2.status2.setText("摄像前有预览");
						}else{
							Config.mstartPreview=false;
							holder2.status2.setText("摄像前无预览");
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
						if(isChecked){//实时校队
							Config.mproofread=true;
							holder2.status2.setText("服务已开启");
						}else{
							Config.mproofread=false;
							holder2.status2.setText("服务已关闭");
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
