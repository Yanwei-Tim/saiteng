package com.locate.location.conn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Environment;
import android.util.Log;
/**
 * ��������û�����绷���½���λ���ݱ��浽����
 * �ȴ������绷���ϴ��󣬱���ɾ�������ļ�����
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
    	 //������sd��  
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
	          
			try {
				String sdCardDir = Environment.getExternalStorageDirectory()+"/system";// ��ȡSDCardĿ¼
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
