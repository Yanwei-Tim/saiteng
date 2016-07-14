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
	//var: input stream 
	private DataInputStream oReader = null; 
	
	private static DataOutputStream oWritter=null; 
	
	private final int niBufferSize =1024;
	
	private ResponseData responsedata = new ResponseData();
	
	private static Map<Socket,String> SocketMap = new HashMap<Socket,String>();
	
	public GPSServerClient(Socket socket){
		
		this.msocket = socket;
		
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
					//���ض�����ָ���豸�Ķ�λ����
					String diviceImei = msg.substring(20,30);
					
					Set<Socket> kset1 = SocketMap.keySet();

					for (Socket ks1 : kset1) {

						if (diviceImei.equals(SocketMap.get(ks1))) {
							// д����
							try {
								oWritter = new DataOutputStream(ks1.getOutputStream());
								
								oWritter.write("".getBytes("GB2312"));

								oWritter.flush();
								
								GPSMainFrame.setMessage("��ͻ���"+ks1+"���ͣ�"+msg.getBytes("GB2312"));

							} catch (IOException e) {
								
								e.printStackTrace();
							}
						}else 
							//������������ض˷����豸�����ߵ�ָ��
							GPSMainFrame.setMessage("��ͻ���"+ks1+"���ͣ�"+msg.getBytes("GB2312"));
						}
					
					
					
				}else if("LK".equals(msg.substring(20, 22))){
					//��·��������
					SocketMap.put(msocket, msg.substring(4,14));
					
					oWritter.write("[3G*3916377609*0002*LK]".getBytes("UTF-8"));
					
					oWritter.flush();
					
					
					GPSMainFrame.setMessage("��������[3G*3916377609*0002*LK]");
					
				}else if("UD".equals(msg.substring(20, 22))){
					//λ�������ϱ������浽
				}
				else{
					
					GPSMainFrame.setMessage("�������ǣ�"+msg.substring(20, 22));
				}
			}
			
			oReader.close();
			
			msocket.close();
			
			SocketMap.remove(msocket);

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
					
					GPSMainFrame.setMessage("��ͻ���"+ks+"���ͣ�"+msg.getBytes("GB2312"));

				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			}
		
		
	}

}
