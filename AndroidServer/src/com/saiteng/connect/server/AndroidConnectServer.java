package com.saiteng.connect.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.saiteng.main.ConnectServerFrame;
import com.saiteng.main.DataModel;

public class AndroidConnectServer extends Thread{
	//服务器监听的端口
	private int mport = 20086;
	//服务器对象
	private ServerSocket mconnectServer;
	//list来控制客户端产生的连接
	private  List<AndroidConnectClient>  list_Client = new ArrayList<AndroidConnectClient>();
	
	private ConnectServerFrame mconnectFrame;
	
	public AndroidConnectServer(int port,ConnectServerFrame connectFrame){
		
	    this.mconnectFrame = connectFrame;
		
		this.mport  = port;
	}
	
	@Override
	public void run() {
		
		super.run();
		
		try {
			int i=0;
			mconnectServer = new ServerSocket(mport);
			//死循环连监听指定端口
			while(true){
				
				Socket a_socket = mconnectServer.accept();
				
				System.out.println("第" + list_Client.size() + "个客户端成功连接！");
				
				AndroidConnectClient mconnectClient = new AndroidConnectClient(a_socket,this,mconnectFrame);
				
				mconnectClient.start();
				
				list_Client.add(mconnectClient);
				
			}
			
		} catch (IOException e) {


			e.printStackTrace();
		}
		
	}
	//当客户端连接断开时移除连接
	public void removeClient(AndroidConnectClient a_connectClient,String msg){
		
		if(a_connectClient!=null){
			
			synchronized(list_Client){
				
				if(list_Client.contains(a_connectClient)){
					
					list_Client.remove(a_connectClient);
					
					a_connectClient=null;
					
					DataModel.getDataModel().removeList_name(msg);
					
					mconnectFrame.updateModel();
					System.out.println("客户端断开连接！");
					
				}
				
			}
			
		}
		
	}
	
	public void stopClient(){
		
		synchronized(list_Client){
			
			for(AndroidConnectClient a_connectClient:list_Client){
				
				if(a_connectClient.isAlive()){
					
					a_connectClient.stopConnection();
					
					a_connectClient.interrupt();
					
					System.out.println("客户端停止连接！");
					
				}
			}
		}
		
	}
	
	public void Boardcast(String msg){
		
		for(int i=0;i<list_Client.size();i++){
		
			list_Client.get(i).sendMsg(msg);

		}
	}

}
