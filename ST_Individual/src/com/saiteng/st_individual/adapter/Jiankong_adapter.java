package com.saiteng.st_individual.adapter;

import java.util.List;
import java.util.Map;

import com.saiteng.st_individual.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Jiankong_adapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mdata;
	
	public Jiankong_adapter(Context context,List<Map<String, Object>> data){
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mviewHolder;
		if(convertView==null){
			mviewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listview_jiankong,null);
			mviewHolder.mView_imageview = (ImageView) convertView.findViewById(R.id.xinbiao_ico);
			mviewHolder.mView_phoneNum  = (TextView) convertView.findViewById(R.id.phoneNum);
		    mviewHolder.mView_phoneName = (TextView) convertView.findViewById(R.id.xiaobiao_name);
		    convertView.setTag(mviewHolder);
		}else{
			mviewHolder =(ViewHolder)convertView.getTag();
		}
		mviewHolder.mView_imageview.setBackgroundResource((Integer) mdata.get(position).get("image"));
		mviewHolder.mView_phoneNum.setText((String)mdata.get(position).get("phonenum"));
		mviewHolder.mView_phoneName.setText((String)mdata.get(position).get("phonename"));
		return convertView;
	}
	 /**´æ·Å¿Ø¼þ*/
    public final class ViewHolder{
    	public TextView mView_phoneName;
        public TextView mView_phoneNum;
        public ImageView mView_imageview;
    }

}
