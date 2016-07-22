package com.saiteng.gpsserver;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.saiteng.gpsserver.frame.GPSMainFrame;

public class GPSServerSocket extends Thread{
	//服务器监听端口
	private int mport = 20086;
	//服务器对象
	private ServerSocket mgpsserversocket;
	//
	private List<GPSServerClient> mlist_click = new ArrayList<GPSServerClient>();
	
	@Override
	public void run() {
		
		super.run();
		
		try {
			
			mgpsserversocket = new ServerSocket(mport);
			
			//死循环连监听指定端口
			while(true){

				Socket a_socket = mgpsserversocket.accept();
				
				GPSMainFrame.setMessage("客户端请求连接");
				
				GPSServerClient gpssocket = new GPSServerClient(this,a_socket);
				
				gpssocket.start();
				
				mlist_click.add(gpssocket);
			}
			
		} catch (Exception e) {
			
		}
	}
	
	public void removeClient(GPSServerClient gps_clientsocket){
		
		if(gps_clientsocket!=null){
			
			synchronized (mlist_click) {
				
				if(mlist_click.contains(gps_clientsocket)){
					
					mlist_click.remove(gps_clientsocket);
					
				}
				
			}
		}
		mlist_click.remove(gps_clientsocket);
		
		GPSMainFrame.setMessage("设备"+gps_clientsocket+"断开连接");
	}
}


