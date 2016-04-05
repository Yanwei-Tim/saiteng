package com.saiteng.st_forensice.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.saiteng.st_forensics.Config;
import com.saiteng.st_forensics.R;
import com.saiteng.st_forensics.view.VideoUtils;
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
import android.widget.Toast;

@SuppressWarnings("rawtypes")
public class FileAdapter<E> extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mdata;
	
	private List mlist= new ArrayList<E>(); ;
	ViewHolder holder = null;
	ViewHolder1 holder1 = null;
	private Handler handler=null;
	private Context context;
	
	public FileAdapter(Context context, List<Map<String, Object>> data) {
		this.mInflater = LayoutInflater.from(context);
		this.mdata=data;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mdata.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		if(handler==null){
			handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch(msg.what){
					case 0:
						Toast.makeText(context, "文件或文件夹不存在", Toast.LENGTH_SHORT).show();
						break;
					case 1:
						Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
						break;
					}
				}
			};
		}
		
		
		
		if(convertView==null){
			//设置样式
			if(position==0||position==1){
				holder = new ViewHolder();
				mlist.add(holder);
				convertView = mInflater.inflate(R.layout.item_recordfile, null);
				holder.txt_title = (TextView) convertView.findViewById(R.id.filename_title);
				holder.txt_status= (TextView) convertView.findViewById(R.id.filename_status);
				holder.boc_check = (CheckBox) convertView.findViewById(R.id.checkbox_recordfile);
				convertView.setTag(holder);
			}else{
				holder1 = new ViewHolder1();
				mlist.add(holder1);
				convertView = mInflater.inflate(R.layout.item_recordfile_path, null);
				holder1.txt_pathtitle  = (TextView) convertView.findViewById(R.id.filepath_title);
				holder1.txt_pathstatus = (TextView) convertView.findViewById(R.id.filepath_status);
				holder1.switch_path    = (Switch) convertView.findViewById(R.id.checkbox_filepath);
				convertView.setTag(holder1);
			}
			
		}else{
			if(position==0||position==1){
				holder = (ViewHolder) convertView.getTag();
				mlist.add(holder);
			}else
				holder1 = (ViewHolder1) convertView.getTag();
			    mlist.add(holder1);
		}
		//设置值
		if(position==0){
			holder.txt_title.setText((String)mdata.get(position).get("title"));
			holder.txt_status.setText((String)mdata.get(position).get("status"));
			holder.boc_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					holder = (FileAdapter<E>.ViewHolder) mlist.get(0);
					if(isChecked){
						//文件加密，生成隐藏文件
						Config.mIsencryption=true;
						holder.txt_status.setText("已加密所有文件");
					}else{
						//文件显示
						Config.mIsencryption=false;
						holder.txt_status.setText("已显示所有文件");
					}
				}
			});
		}
		if(position==1){
			holder.txt_title.setText((String)mdata.get(position).get("title1"));
			holder.txt_status.setText((String)mdata.get(position).get("status1"));
			holder.boc_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						
						VideoUtils.deleteFiles(handler);
					}else{
						
					}
				}
			});
		}
		if(position==2){
			holder1.txt_pathtitle.setText((String)mdata.get(position).get("title2"));
			holder1.txt_pathstatus.setText((String)mdata.get(position).get("status2"));
			holder1.switch_path.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						String path=VideoUtils.filePath();
						holder1.txt_pathstatus.setText(path);
					}else{
						
					}
				}
			});
		}
		return convertView;
	}

  class ViewHolder{
		TextView txt_title;
		TextView txt_status;
		CheckBox boc_check;
	}
	class ViewHolder1{
		TextView txt_pathtitle;
		TextView txt_pathstatus;
		Switch   switch_path;
	}
}
