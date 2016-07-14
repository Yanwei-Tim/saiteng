package com.saiteng.st_master.fragments;

import com.saiteng.st_master.R;
import com.saiteng.st_master.view.Utils;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


public class BottomTrackFragment extends Fragment implements OnClickListener{
	private Button mBtn_trackClean,mBtn_trackExport;
	private View view;
	private String trackTime;
	private Context context;
	public BottomTrackFragment(Context context1,String time_Track) {
		this.trackTime = time_Track;
		this.context = context1;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 view= inflater.inflate(R.layout.fragment_bottomtrack,null);
		 return view;
	}
	public void onStart() {
		super.onStart();
		
		mBtn_trackClean = (Button) view.findViewById(R.id.track_clean);
		mBtn_trackExport = (Button) view.findViewById(R.id.track_export);
		mBtn_trackClean.setOnClickListener(this);
		mBtn_trackExport.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.track_clean:
			//清除轨迹
			Utils.deleteTrack(context, "",trackTime);
			break;
		case R.id.track_export:
			//导出轨迹，生成kml文件。
			Utils.createKml(context,trackTime);
			break;
		}
		
	}
}
