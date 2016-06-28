package com.saiteng.lc32xcam.adapter;

import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.example.st_lc32xcam.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<Map<String,String>> mlist;
	
	 public SettingAdapter(Context context,List<Map<String,String>> list) {
		 this.inflater = LayoutInflater.from(context);
		 this.mlist = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return  mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderView holderview;
		if(convertView==null){
			holderview = new HolderView();
			convertView = inflater.inflate(R.layout.item_setting, null);
			holderview.itemname = (TextView) convertView.findViewById(R.id.setting_name);
		    convertView.setTag(holderview);
		}else
			holderview = (HolderView) convertView.getTag();
		holderview.itemname.setText(mlist.get(position).get("itemname"));
		return convertView;
	}
	
	class HolderView{
		private TextView itemname;
	}

}
