package com.saiteng.st_master.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class ManageAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mdata;
	private Context context;
	
	// 用于记录每个RadioButton的状态，并保证只可选一个
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	
	public ManageAdapter(Context context,List<Map<String, Object>> data){
		this.mInflater = LayoutInflater.from(context);
		this.mdata     = data;
		this.context   = context;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mviewHolder;
		if(convertView==null){
			mviewHolder = new ViewHolder();
			convertView  = mInflater.inflate(R.layout.item_menu_manage, null);
			mviewHolder.mView_diviceimg    =  (ImageView) convertView.findViewById(R.id.menu_diviceimg);
			mviewHolder.mView_divicename   =  (TextView) convertView.findViewById(R.id.menu_divicename);
			mviewHolder.mView_divicanum    =  (TextView) convertView.findViewById(R.id.menu_divicenum);
			 convertView.setTag(mviewHolder);
		}else {
			mviewHolder=(ViewHolder) convertView.getTag();
		}
		mviewHolder.mView_diviceimg.setBackgroundResource((Integer) mdata.get(position).get("image"));
		mviewHolder.mView_divicename.setText((String) mdata.get(position).get("divicename"));
		mviewHolder.mView_divicanum.setText((String) mdata.get(position).get("divicenum"));
		final RadioButton radio =  (RadioButton) convertView.findViewById(R.id.menu_isCheck);
		mviewHolder.mRadio_radiobutton=radio;
		radio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 重置，确保最多只有一项被选中
		        for (String key : states.keySet()) {
		          states.put(key, false);
		        }
		        states.put(String.valueOf(position), radio.isChecked());
		        ManageAdapter.this.notifyDataSetChanged(); 
		        Message message = Config.mhandler.obtainMessage();
		        message.obj= mdata.get(position).get("image")+","+mdata.get(position).get("divicenum");
		        Config.mhandler.sendMessage(message);
			}
		});
		boolean res = false;
	    if (states.get(String.valueOf(position)) == null
	        || states.get(String.valueOf(position)) == false) {
	      res = false;
	      states.put(String.valueOf(position), false);
	    } else
	      res = true;

	    mviewHolder.mRadio_radiobutton.setChecked(res);
		return convertView;
	}
    public class ViewHolder{
    	public ImageView mView_diviceimg;
    	public TextView mView_divicename;
    	public TextView mView_divicanum;
    	public RadioButton mRadio_radiobutton;
    }

}
