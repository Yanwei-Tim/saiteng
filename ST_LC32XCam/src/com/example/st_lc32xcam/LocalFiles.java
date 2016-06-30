package com.example.st_lc32xcam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.saiteng.st_lc32xcam.utils.SmartCamDefine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocalFiles extends Activity{
	private List<String> mMediaFilesList = new ArrayList<String>();
	private MyFileAsyncTask mMyFileAsyncTask;
	private String mFileDir = android.os.Environment.getExternalStorageDirectory() + "/CAM/video";
	private Context context;
	private ListView mlistview;
	private DisplayImageOptions mDisplayImageOptions;  
	private int type;
	private TextView mPopText;
	private Button mBtnPopDelete;
	private Button mBtnPopDeleteAll;
	private Button mBtnPopCancel;
	private Button btn_ChangeLocal;
	private Button btn_Close,btn_image,btn_video;
	private MediaFilesListAdapter mMediaFilesListAdapter;
	private int mMediaFileCurItem = 0;
	private PopupWindow mPopupWindow;
	private LinearLayout mLookbackLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lookback);
		initView();
		mDisplayImageOptions = new DisplayImageOptions.Builder()	
		.showImageOnLoading(R.drawable.ic_stub) 
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		context = LocalFiles.this;
		initClick();
		updateFileList();
		mMediaFilesListAdapter = new MediaFilesListAdapter(context);
		mlistview.setAdapter(mMediaFilesListAdapter);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		mLookbackLayout = (LinearLayout)findViewById(R.id.mLookbackLayout);
		btn_ChangeLocal = (Button) findViewById(R.id.button_change_remote1);
		btn_image = (Button) findViewById(R.id.button_img);
		btn_image.setVisibility(View.VISIBLE);
		btn_video = (Button) findViewById(R.id.button_video);
		btn_video.setVisibility(View.VISIBLE);
		btn_video.setEnabled(false);
		btn_video.setBackgroundResource(R.color.blue);
		btn_Close = (Button) findViewById(R.id.button_close1);
		btn_Close.setVisibility(View.GONE);
		mlistview = (ListView) findViewById(R.id.mLookbackGridView);
		LayoutInflater inflater = LayoutInflater.from(this);
		View popView = inflater.inflate(R.layout.activity_pop, null);
		mPopText = (TextView) (popView.findViewById(R.id.mPopText));
		mBtnPopDelete = (Button) (popView.findViewById(R.id.mPopDelete));
		btn_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFileDir = android.os.Environment.getExternalStorageDirectory() + "/CAM/img";
				btn_video.setBackgroundResource(R.color.transparent);
				btn_image.setBackgroundResource(R.color.blue);
				btn_image.setEnabled(false);
				btn_video.setEnabled(true);
				 updateFileList();
				 mMediaFilesListAdapter = new MediaFilesListAdapter(context);
				 mlistview.setAdapter(mMediaFilesListAdapter);
			}
		});
		btn_video.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFileDir = android.os.Environment.getExternalStorageDirectory() + "/CAM/video";
				btn_image.setBackgroundResource(R.color.transparent);
				btn_video.setBackgroundResource(R.color.blue);
				btn_video.setEnabled(false);
				btn_image.setEnabled(true);
				 updateFileList();
				 mMediaFilesListAdapter = new MediaFilesListAdapter(context);
				 mlistview.setAdapter(mMediaFilesListAdapter);
			}
		});
		btn_ChangeLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(SmartCamDefine.isconn){
					Intent intent = new Intent(LocalFiles.this, FTPActivity.class);
					intent.putExtra("hostName", SmartCamDefine.hostName);
					intent.putExtra("userName", SmartCamDefine.userName);
					intent.putExtra("password", SmartCamDefine.password);
					startActivity(intent);
					finish();
				}else
					Toast.makeText(context, "FTP服务没连接", Toast.LENGTH_SHORT).show();
			}
		});
		mBtnPopDelete.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				String file = mMediaFilesList.get(mMediaFileCurItem).substring("file://".length());
				File f = new File(file);
				if (f==null || !f.exists()){
					return;
				}
				
				f.delete();
				updateFileList();
				mPopupWindow.dismiss();
			}
		});
		
		mBtnPopDeleteAll = (Button)(popView.findViewById(R.id.mPopDeleteAll));
	
		mBtnPopDeleteAll.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				File f = new File(mFileDir);
				if (f==null || !f.exists()){
					return;
				}

				if (f.isFile()){
					f.delete();
				}else if (f.isDirectory()){
					File[] subfile = f.listFiles();
					for (int i=0; i<subfile.length;i++){
						if (subfile[i].isFile()){
							subfile[i].delete();
						}						
					}
				}
				
				mMediaFilesList.clear();
				mMediaFilesListAdapter.notifyDataSetChanged();
				mPopupWindow.dismiss();
				
			}
		});
		
		mBtnPopCancel = (Button)(popView.findViewById(R.id.mPopCancel));
	  
		mBtnPopCancel.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
		
		mPopupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false); 
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable()); 
		mPopupWindow.setOutsideTouchable(true); 
		mPopupWindow.setFocusable(true);
	}
	private void initClick() {
		
		mlistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mMediaFileCurItem=position;
				String filepath = mMediaFilesList.get(position);
				if (filepath.endsWith(".jpg")){
					openImageFileIntent(filepath);
				}else if (filepath.endsWith(".mp4")||filepath.endsWith(".avi")){
					openVideoFileIntent(filepath);
				}
			}
		});
		//listview长按事件
		mlistview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mMediaFileCurItem = position;
				int start = mMediaFilesList.get(position).lastIndexOf("/");
				String filepath = mMediaFilesList.get(position).substring(start+1);
				mPopupWindow.showAtLocation(mLookbackLayout, Gravity.CENTER, 0, 0);
				return true;
			}
		});
		
	}
	private void updateFileList() {
		mMyFileAsyncTask = new MyFileAsyncTask();
		mMyFileAsyncTask.execute(mFileDir);
	}
	
	
	private class MyFileAsyncTask extends AsyncTask<String, Integer, List<String>>{
        /**
          * 
          */
		@Override
        protected List<String> doInBackground(String... param) {
	        if (param==null || param.length==0){
	        	return null;
	        }
	        
	        File dir = new File(param[0]);
	        if (dir==null || !dir.exists()){
	        	return null;
	        }
	        File[] subdir = dir.listFiles();
	        if (subdir==null || subdir.length==0){
	        	return null;
	        }
	        mMediaFilesList.clear();
	        for (int i=0; i<subdir.length; i++){
	        	File subfile = subdir[i];
	        	if (subfile != null && subfile.exists() && !subfile.isDirectory()){
	        		mMediaFilesList.add("file://" + subfile.getAbsolutePath());
	        	}
	        }
	        return mMediaFilesList;
        }
	}
	/**点击预览图片*/
	public void openImageFileIntent( String param ) {  
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        Uri uri = Uri.parse(param);  
        intent.setDataAndType(uri, "image/*");    
        LocalFiles.this.startActivity(intent);
    }  
	/**点击播放视频*/
    public void openVideoFileIntent( String param ) {  
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        Uri uri = Uri.parse(param);  
        intent.setDataAndType(uri, "video/*");    
        LocalFiles.this.startActivity(intent); 
    }
	
	
	/**������ע����ʹ��ImageLoaderʱҪ��application�н�������
	 * ����ʹ�÷������ĵ�
	 * */
	public class MediaFilesListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public MediaFilesListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mMediaFilesList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				holder.textView = (TextView) view.findViewById(R.id.textview);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			ImageLoader.getInstance()
					.displayImage(mMediaFilesList.get(position), holder.imageView, mDisplayImageOptions, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.progressBar.setProgress(0);
							holder.progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							if (imageUri != null && imageUri.endsWith(".avi")){
								Bitmap bitmap2 = ((BitmapDrawable) getResources().getDrawable(R.drawable.video)).getBitmap();
								holder.imageView.setImageBitmap(bitmap2);
							}
							holder.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							if (imageUri != null && imageUri.endsWith(".avi")){								
								Bitmap bitmap2 = ((BitmapDrawable) getResources().getDrawable(R.drawable.media_play_new_normal)).getBitmap();
								Bitmap tmp = drawLogo(loadedImage, bitmap2);
								holder.imageView.setImageBitmap(tmp);
							}
							holder.progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view, int current, int total) {
							holder.progressBar.setProgress(Math.round(100.0f * current / total));
						}
					});
			 // 获取文件
			int index = mMediaFilesList.get(position).indexOf("2");
			//file:///storage/emulated/0/CAM/Video20160601102829_0.avi
			holder.textView.setText(mMediaFilesList.get(position).substring(index));

			return view;
		}
	}
	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		TextView textView;
	}
	private Bitmap drawLogo(Bitmap bg, Bitmap logo) {
		if (!bg.isMutable()) {
			bg = bg.copy(Bitmap.Config.ARGB_8888, true);
		}

		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setAlpha(200);
		
		Canvas canvas = new Canvas(bg);
		//Rect rect = new Rect(0, (bg.getHeight() - logo.getHeight()), bg.getWidth(), bg.getHeight());
		Rect rect = new Rect((bg.getWidth()-logo.getWidth())/2, (bg.getHeight()-logo.getHeight())/2, (bg.getWidth()+logo.getWidth())/2, (bg.getHeight()+logo.getHeight())/2);
		// canvas.drawBitmap(logo, null, rect,p);
		canvas.drawBitmap(logo, null, rect, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return bg;
	}
	

}
