package com.saiteng.st_individual.adapter;

import java.util.List;
import java.util.Map;

import com.saiteng.st_individual.R;
import com.saiteng.st_individual.adapter.Locus_adapter.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class LocusDtails_adapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mdata;
	public LocusDtails_adapter(Context context,List<Map<String, Object>> data){
		 this.mInflater = LayoutInflater.from(context);
		 this.mdata = data;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mviewHolder;
		if(convertView==null){
			mviewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listview_guijidetails,null);
			mviewHolder.mDateTime  = (TextView) convertView.findViewById(R.id.guiji_details);
		    convertView.setTag(mviewHolder);
		}else{
			mviewHolder =(ViewHolder)convertView.getTag();
		}
		
		mviewHolder.mDateTime.setText((String)mdata.get(position).get("DateTime"));
		return convertView;
	}
	 /**´æ·Å¿Ø¼þ*/
    public final class ViewHolder{
        public TextView mDateTime;;
      
    }

}
