package com.locate.location.conn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import com.locate.location.services.Config;

import android.os.Environment;
/**
  * �����ǵȴ���wifi����ʱ��ȡ������ļ�����ȡ�����������ϴ���������
  * ����Ҫ��ȡ�ļ���Ҫ���úö�ȡ����������Ƶ��������
  * �ü̳�Callable �ӿ���ʵ�ִ��з���ֵ���߳�
  */
public class MyReadDate extends Thread{
	public StringBuffer sb = new StringBuffer();
	ArrayList<HttpClientThread> listhttp  = new ArrayList<HttpClientThread>();
	private HttpClientThread httpthread;

	
	@Override
	public void run() {
		super.run();
		 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ 
		        try {  
		            File file = new File(Environment.getExternalStorageDirectory()+"/system",  
		            		"systemLocation.txt");  
		            BufferedReader br = new BufferedReader(new FileReader(file));  
		            String readline = "";  
		            while ((readline = br.readLine()) != null) {  
		                sb.append(readline); //������������ 
		            }  
		            br.close();
		            file.delete(); 
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        } 
		        String[] arrData = sb.toString().split("/");
				for(int i=0;i<arrData.length;i++){
					ArrayList<String> dataList = new ArrayList<String>();
					String[] arrData1 = arrData[i].split(",");
					for(int j=0;j<arrData1.length;j++){
						dataList.add(arrData1[j]);
					}
					if(httpthread!=null){
						listhttp.remove(httpthread);
						httpthread=null;
					}
					httpthread =new HttpClientThread(Config.mURL,dataList);
					httpthread.start();
					listhttp.add(httpthread);
				}
		 }
	} 
	
}
