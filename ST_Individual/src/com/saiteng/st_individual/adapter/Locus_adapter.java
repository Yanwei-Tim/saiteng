package com.saiteng.st_individual.adapter;

import java.util.List;
import java.util.Map;
import com.saiteng.st_individual.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class Locus_adapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mdata;
	public Locus_adapter(Context context,List<Map<String, Object>> data){
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
			convertView = mInflater.inflate(R.layout.listview_guiji,null);
			mviewHolder.mView_imageview = (ImageView) convertView.findViewById(R.id.wenjian_guiji);
			mviewHolder.mView_phoneNum  = (TextView) convertView.findViewById(R.id.phoneNum_guiji);
			//mviewHolder.mBox_checkbox   = (CheckBox) convertView.findViewById(R.id.checkbox);
		    convertView.setTag(mviewHolder);
		}else{
			mviewHolder =(ViewHolder)convertView.getTag();
		}
		mviewHolder.mView_imageview.setBackgroundResource((Integer) mdata.get(position).get("image"));
		mviewHolder.mView_phoneNum.setText((String)mdata.get(position).get("phonenum"));
		return convertView;
	}
	 /**´æ·Å¿Ø¼þ*/
    public final class ViewHolder{
        public TextView mView_phoneNum;
        public ImageView mView_imageview;
        public CheckBox mBox_checkbox;
    }
}
