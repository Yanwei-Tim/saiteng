package com.locate.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import android.os.AsyncTask;
import android.os.Environment;

@SuppressWarnings("rawtypes")
public class MyReadDate{
	public StringBuffer sb;

	public String saveDate(){
		 //¶ÁÈ¡sd¿¨  ÎÄ¼þ
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
	          
	        try {  
	            File file = new File(Environment.getExternalStorageDirectory(),  
	            		"systemLocation.txt");  
	            BufferedReader br = new BufferedReader(new FileReader(file));  
	            String readline = "";  
	            sb = new StringBuffer();  
	            while ((readline = br.readLine()) != null) {  
	                System.out.println("readline:" + readline);  
	                sb.append(readline);  
	            }  
	            br.close();
	            file.delete(); 
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }    
	             
	    }
		return sb.toString(); 
		
	}

}
