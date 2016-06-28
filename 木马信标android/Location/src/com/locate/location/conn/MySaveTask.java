package com.locate.location.conn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Environment;
import android.util.Log;
/**
 * 该类用于没有网络环境下将定位数据保存到本地
 * 等待有网络环境上传后，必须删除本地文件内容
 * */
public class MySaveTask extends  Thread{
	private String msg;
	public MySaveTask(String msg1){
		this.msg=msg1;
		 Log.d("readsuccess",msg);
	}
    @Override
    public void run() {
    	super.run();
    	 //保存在sd卡  
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
	          
			try {
				String sdCardDir = Environment.getExternalStorageDirectory()+"/system";// 获取SDCard目录
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
    }
	
}
