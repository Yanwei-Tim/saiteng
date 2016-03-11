package com.saiteng.st_master.adapter;

import java.util.List;
import java.util.Map;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.R;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ManageTrackAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mdata;
	
	public ManageTrackAdapter(Context context1,List<Map<String, Object>> mdata1) {
		this.mInflater = LayoutInflater.from(context1);
		this.mdata     = mdata1;
		this.context   = context1;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder;
		if(convertView == null){
			viewholder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_trackmanage, null);
			viewholder.mView_diviceimg  = (ImageView) convertView.findViewById(R.id.track_manage_img);
			viewholder.mView_divicename = (TextView) convertView.findViewById(R.id.track_manage_txt);
			
			 convertView.setTag(viewholder);
		}else
		viewholder = (ViewHolder)convertView.getTag();
		viewholder.mView_diviceimg.setBackgroundResource((Integer) mdata.get(position).get("image"));
		viewholder.mView_divicename.setText((String) mdata.get(position).get("title"));
		final CheckBox checkbox =  (CheckBox) convertView.findViewById(R.id.track_manage_check);
		viewholder.mcheckbox = checkbox;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Message message = new Message();
				message.obj = isChecked+"";
				Config.mTrackContext.sendMessage(message);
			}
		});
		return convertView;
	}
	public class ViewHolder{
		public ImageView mView_diviceimg;
    	public TextView mView_divicename;
    	public CheckBox mcheckbox;
		
	}

}
