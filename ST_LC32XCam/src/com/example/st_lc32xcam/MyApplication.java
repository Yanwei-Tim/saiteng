package com.example.st_lc32xcam;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class MyApplication extends Application{
	
	private MyApplication myapplication;
	
	public MyApplication getApplication() {
		return myapplication;
		
	} 
	
	@Override
	public void onCreate() {
		myapplication = this;
		super.onCreate();
		initImageLoader(getApplicationContext());
	}
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	private Handler handler;
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public Handler getHandler(){
		return handler;
		
	}

}
