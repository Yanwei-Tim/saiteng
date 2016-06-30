package com.saiteng.connect.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.saiteng.main.ConnectServerFrame;
import com.saiteng.main.DataModel;

public class AndroidConnectClient extends Thread{
	
	private Socket msocket=null;
	//var: input stream 
	private DataInputStream oReader = null; 
	
	private static DataOutputStream oWritter=null; 
	
	private final int niBufferSize =1024;
	
	private AndroidConnectServer mConnectServer=null;
	
	private ConnectServerFrame mconnectFrame;
	
	private static List<Socket> client_list = new ArrayList<Socket>();
	
	public AndroidConnectClient(Socket socket,AndroidConnectServer connectserver,ConnectServerFrame connectFrame){
		
		this.mconnectFrame = connectFrame;
		
		this.msocket = socket;
		
		this.mConnectServer = connectserver;
		
	}
	
	@Override
	public void run() {
		
		super.run();
		
		if(msocket!=null){
			
			client_list.add(msocket);
			
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
				
				byte[] l_aryBuf = new byte[niBufferSize];
				
				while(oReader.read(l_aryBuf)!=-1){
					
					msg = new String (l_aryBuf).trim();
					
					System.out.println(msg);
					
					DataModel.getDataModel().setList_name("1"+date+","+l_strIP+","+"0001,"+msg);
					
					mconnectFrame.updateModel();
					
					l_aryBuf=null; 
					
					l_aryBuf= new byte[niBufferSize]; 
					
				}
				
				oReader.close();
				
				msocket.close();
				
				client_list.remove(msocket);
				
				mConnectServer.removeClient(this,msg);
				
			} catch (IOException e) {
				
				client_list.remove(msocket);
				
				mConnectServer.removeClient(this,msg);
				
				e.printStackTrace();
			} 
			
		}
		
	}
	
	//向连接的客户端发送广播消息
	public static void sendMsg(String msg){
		
		try {
			
			for(int i=0;i<client_list.size();i++){
				
				//写出流
				oWritter = new DataOutputStream(client_list.get(i).getOutputStream());
				
				oWritter.writeUTF(msg);
				
				oWritter.flush();
				
				System.out.println("向客户端"+i+"发送信息"+msg);
			}
			
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
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
