package com.locate.location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("rawtypes")
public class MySaveTask extends  AsyncTask{
	private String msg;
	public MySaveTask(String msg1){
		this.msg=msg1;
		 Log.d("readsuccess",msg);
	}
	@Override
	protected Object doInBackground(Object... params) {
		
		 //保存在sd卡  
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
	          
			try {
				File sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录
				File sdFile = new File(sdCardDir, "systemLocation.txt");

				FileOutputStream outStream = new FileOutputStream(sdFile,true);

				outStream.write(msg.getBytes());
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	    }
		return params;  
	}

}
