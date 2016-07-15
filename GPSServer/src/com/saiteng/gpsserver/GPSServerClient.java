package com.saiteng.gpsserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.saiteng.gpsserver.db.ResponseData;
import com.saiteng.gpsserver.frame.GPSMainFrame;

public class GPSServerClient extends Thread{
	
	private Socket msocket=null;
	
	private GPSServerSocket mserversocket=null;
	//var: input stream 
	private DataInputStream oReader = null; 
	
	private static DataOutputStream oWritter=null; 
	
	private final int niBufferSize =1024;
	
	private ResponseData responsedata = new ResponseData();
	
	private static Map<Socket,String> SocketMap = new HashMap<Socket,String>();
	//������Ҫ����ʵʱ��λ���ݵĿ�׶�˺��豸������Ϣ
	private static Map<String,String> P2PLocate = new HashMap<String,String>();
	
	public GPSServerClient(GPSServerSocket serversocket,Socket socket){
		
		this.msocket = socket;
		
		this.mserversocket = serversocket;
		
	}
	
	@Override
	public void run() {

		super.run();

		// ������
		try {
			oReader = new DataInputStream(msocket.getInputStream());
			//д����
			oWritter = new DataOutputStream(msocket.getOutputStream());

			byte[] l_aryBuf = new byte[niBufferSize];

			int len = 0;

			while ((len = oReader.read(l_aryBuf)) != -1) {
				
				String msg=new String (l_aryBuf,0,len,"GB2312");
				
				GPSMainFrame.setMessage("�ͻ��˷�������"+msg);
				
				if(msg.contains("Connect")){
					//���ƶ���·��������
					SocketMap.put(msocket, msg.substring(4,19));
					
					oWritter.write("[ST*Connect*OK]".getBytes("GB2312"));
					
					oWritter.flush();
					
				}else if(msg.contains("Login")){
					//��֤���ƶ˵�¼��Ϣ
					if(responsedata==null){
						
						responsedata = new ResponseData();
					}
					responsedata.CheckLogin(msg.substring(4,19),msg.split(",")[1],msg.split(",")[2].replace("]", ""));
				} else if (msg.contains("ADDDivice")) {
					// ���ض���Ӽ�ص��豸
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.addDivice(msg);
				}else if(msg.contains("GetDivice")){
					// ���ض��������豸
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.getDivice(msg);
				}else if(msg.contains("GetlatLng")){
					//���ض�����ָ���豸��ʵʱ��λ����
					//��λ�豸���豸�ţ�����IMEI�����·����������
					String diviceImei = msg.substring(20,30);
					//���ض��ֻ���IMEI��
					String phoneImei   = msg.substring(4,19);
					
					P2PLocate.put(diviceImei,phoneImei);
					
					Set<Socket> kset1 = SocketMap.keySet();

					for (Socket ks1 : kset1) {

						if (diviceImei.equals(SocketMap.get(ks1))) {
							// д����
							try {
								oWritter = new DataOutputStream(ks1.getOutputStream());
								
								oWritter.write(("[3G*"+diviceImei+"*0002*CR]").getBytes("GB2312"));

								oWritter.flush();
								
								GPSMainFrame.setMessage("��ͻ���"+ks1+"���ͣ�"+("[3G*"+diviceImei+"*0002*CR]"));

							} catch (IOException e) {
								
								e.printStackTrace();
							}
						} 
						}
				}else if("CR".equals(msg.substring(20, 22))){
					//�豸���ؿ�ʼ��λ����Ϣ
				}else if("LK".equals(msg.substring(20, 22))){
					//��·��������
					SocketMap.put(msocket, msg.substring(4,14));
					
					oWritter.write(("[3G*"+msg.substring(4,14)+"*0002*LK]").getBytes("GB2312"));
					
					oWritter.flush();
					
					GPSMainFrame.setMessage("��������[3G*"+msg.substring(4,14)+"*0002*LK]");
					
				}else if("UD".equals(msg.substring(20, 22))){
					//λ�������ϱ������浽���ݿ�
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.saveData(msg);
				}
				else{
					
					GPSMainFrame.setMessage("�������ǣ�"+msg.substring(20, 22));
				}
			}
			
			oReader.close();
			
			msocket.close();
			
			SocketMap.remove(msocket);
			
			mserversocket.removeClient(this);

		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
	}
	
	public static void sendmsg(String msg){
		//�㲥����
		Set<Socket> kset = SocketMap.keySet();

		for (Socket ks : kset) {
			
			try {
				oWritter = new DataOutputStream(ks.getOutputStream());
				
				oWritter.write(msg.getBytes("GB2312"));
				
			    oWritter.flush();
			    
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void specificsendmsg(String imei,String msg){
		//��ָ���ͻ��˷���
		Set<Socket> kset = SocketMap.keySet();

		for (Socket ks : kset) {

			if (imei.equals(SocketMap.get(ks))) {
				// д����
				try {
					oWritter = new DataOutputStream(ks.getOutputStream());
					
					oWritter.write(msg.getBytes("GB2312"));

					oWritter.flush();
					
					GPSMainFrame.setMessage("��ͻ���"+ks+"���ͣ�"+msg);

				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			}
	}

	public static void specificLatLng(String imei, String msg) {

		Set<Socket> kset = SocketMap.keySet();

		Set<String> kset1 = P2PLocate.keySet();

		for (String ks1 : kset1) {

			for (Socket ks : kset) {

				if (P2PLocate.get(ks1).equals(SocketMap.get(ks))) {
					try {
						oWritter = new DataOutputStream(ks.getOutputStream());

						oWritter.write(msg.getBytes("GB2312"));

						oWritter.flush();

						GPSMainFrame.setMessage("��ͻ���" + ks + "���ͣ�" + msg);
						
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		}
	}
}
