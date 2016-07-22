package com.saiteng.gpsserver;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.saiteng.gpsserver.frame.GPSMainFrame;

public class GPSServerSocket extends Thread{
	//�����������˿�
	private int mport = 20086;
	//����������
	private ServerSocket mgpsserversocket;
	//
	private List<GPSServerClient> mlist_click = new ArrayList<GPSServerClient>();
	
	@Override
	public void run() {
		
		super.run();
		
		try {
			
			mgpsserversocket = new ServerSocket(mport);
			
			//��ѭ��������ָ���˿�
			while(true){

				Socket a_socket = mgpsserversocket.accept();
				
				GPSMainFrame.setMessage("�ͻ�����������");
				
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
		
		GPSMainFrame.setMessage("�豸"+gps_clientsocket+"�Ͽ�����");
	}
}


