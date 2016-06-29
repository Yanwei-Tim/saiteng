package com.saiteng.connect.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.saiteng.main.ConnectServerFrame;
import com.saiteng.main.DataModel;

public class AndroidConnectClient extends Thread{
	
	private Socket msocket=null;
	//var: input stream 
	private DataInputStream oReader = null; 
	
	private DataOutputStream oWritter=null; 
	
	private final int niBufferSize =1024;
	
	private AndroidConnectServer mConnectServer=null;
	
	private ConnectServerFrame mconnectFrame;
	
	public AndroidConnectClient(Socket socket,AndroidConnectServer connectserver,ConnectServerFrame connectFrame){
		
		this.mconnectFrame = connectFrame;
		
		this.msocket = socket;
		
		this.mConnectServer = connectserver;
		
	}
	
	@Override
	public void run() {
		
		super.run();
		
		if(msocket!=null){
			String msg=null;
			long time = System.currentTimeMillis();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d1 = new Date(time);
			String date = format.format(d1);
			
			 String l_strIP= msocket.getRemoteSocketAddress().toString();
			 
			 if (l_strIP.startsWith("/")){
				 
				 l_strIP = l_strIP.substring(1); 
			 }
			
			try {
				//读入流
				oReader = new DataInputStream(msocket.getInputStream());
				//写出流
				oWritter = new DataOutputStream(msocket.getOutputStream());
				
				oWritter.writeUTF("ok");
				
				oWritter.flush();
			
				byte[] l_aryBuf = new byte[niBufferSize];
				
				while(oReader.read(l_aryBuf)!=-1){
					
					msg = new String (l_aryBuf).trim();
					
					DataModel.getDataModel().setList_name(date+","+l_strIP+","+"0001,"+msg);
					
					//System.out.println("来自"+l_strIP+"的消息："+msg);
					
					mconnectFrame.updateModel();
					
					l_aryBuf=null; 
					
					l_aryBuf= new byte[niBufferSize]; 
					
				}
				
				oReader.close();
				
				oWritter.close();
				
				msocket.close();
				
				mConnectServer.removeClient(this,msg);
				
			} catch (IOException e) {
				
				mConnectServer.removeClient(this,msg);
				
				e.printStackTrace();
			} 
			
		}
		
	}
	
	public void stopConnection (){
		
		if (msocket !=null && msocket.isConnected() && msocket.isClosed()==false){
			
			try {
				
				oReader.close();
				
				msocket.close(); 
				
			} catch (IOException e) {
            
				e.printStackTrace();
			}	
		}
	}
}
