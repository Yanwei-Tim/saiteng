package com.saiteng.gpsserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.saiteng.gpsserver.frame.GPSMainFrame;

public class GPSServerClient extends Thread{
	
	private Socket msocket=null;
	//var: input stream 
	private DataInputStream oReader = null; 
	
	private static DataOutputStream oWritter=null; 
	
	private final int niBufferSize =1024;
	
	public GPSServerClient(Socket socket){
		
		this.msocket = socket;
	}
	
	@Override
	public void run() {

		super.run();

		// 读入流
		try {
			oReader = new DataInputStream(msocket.getInputStream());
			//写入流
			oWritter = new DataOutputStream(msocket.getOutputStream());

			byte[] l_aryBuf = new byte[niBufferSize];

			int len = 0;

			while ((len = oReader.read(l_aryBuf)) != -1) {
				
				String msg=new String (l_aryBuf,0,len,"UTF-8");
				
				GPSMainFrame.setMessage("客户端发送数据"+msg.substring(20, 22));
				
				if("LK".equals(msg.substring(20, 22))){
					
					oWritter.writeUTF(msg);
					
					oWritter.flush();
				}
			}
			
			oReader.close();
			
			msocket.close();

		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
	}
	
	public static void sendmsg(String msg){
		
		try {
			oWritter.writeUTF(msg);
			
		    oWritter.flush();
		    
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
