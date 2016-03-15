package com.saiteng.st_individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saiteng.st_individual.adapter.LocusDtails_adapter;
import com.saiteng.st_individual.conn.AskDetailsTask;
import com.saiteng.st_individual.view.PreViewPopwindow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class LocusDatilsActivity extends Activity implements OnClickListener{
	private ImageView mImage2;
	private RelativeLayout relativeLayout2;
	private boolean bTransfer = true;
	private Context context;
	private PreViewPopwindow preViewPopwindow;
	private LocusDtails_adapter madapter;
	private List<Map<String, Object>> data;
	private ListView mlistView;
	private ProgressDialog dialog;//提示框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guijidetails);
		mlistView = (ListView) findViewById(R.id.listview_guijidetails);
		context = LocusDatilsActivity.this;
		Config.Locusdetails_mcontext=context;
		//顶部标题栏
		mImage2 = (ImageView) findViewById(R.id.preview_guijidetails_btn);
		mImage2.setOnClickListener(this);
		relativeLayout2 = (RelativeLayout)findViewById(R.id.guijidetails_layout);
		/**提示框*/
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示！");
		dialog.setMessage("正在加载中...");
		
	     //接收intent传递过来的值
		 Intent intent1 = getIntent();
	     String[] arr_details = intent1.getStringArrayExtra("details");
	     data=initData(arr_details);
	     madapter = new LocusDtails_adapter(context,data);
	     mlistView.setAdapter(madapter);
		 mlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView content=(TextView) view.findViewById(R.id.guiji_details);
				new AskDetailsTask().execute(content.getText().toString());
				//Toast.makeText(context,"绘制轨迹",Toast.LENGTH_LONG).show();
				
			}
		});
		
	}
	//设置数据。
	private List<Map<String, Object>> initData(String[] arr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map ;
		for(int i=0;i<arr.length;i++){
			map= new HashMap<String, Object>();
			map.put("DateTime", arr[i]+".trk");
			list.add(map);
		}
		return list;
		
	}
	@Override
	public void onClick(View v) {
		if (bTransfer) {
			preViewPopwindow = new PreViewPopwindow(context);
			preViewPopwindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					bTransfer = true;
				}
			});
			int[] location = new int[2];
			relativeLayout2.getLocationOnScreen(location);
			preViewPopwindow.showAtLocation(relativeLayout2, Gravity.NO_GRAVITY,
					location[0] - preViewPopwindow.getWidth(), 0);
			bTransfer = false;
		}else{
			preViewPopwindow.dismiss();
			bTransfer = true;
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	public void exitSystem() {
		Config.mIsLogined=false;
		Config.mIsFristLogined=false;
		Config.ip=null;
		Config.port=null;
		Config.phoneNum=null;
		Config.loginInfo=null;
		Config.medit.clear();
		Config.medit.commit();
		finish();
	}
}
