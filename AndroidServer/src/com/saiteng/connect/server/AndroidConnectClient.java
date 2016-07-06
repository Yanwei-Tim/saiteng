package com.saiteng.connect.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	private String str_imei = null;
	
	private static List<Socket> client_list = new ArrayList<Socket>();
	
	private static Map<Socket,String> SocketMap = new HashMap<Socket,String>();
	
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
				//������
				oReader = new DataInputStream(msocket.getInputStream());
				
				byte[] l_aryBuf = new byte[niBufferSize];
				
				while(oReader.read(l_aryBuf)!=-1){
					
					msg = new String (l_aryBuf).trim();
					
					if("start_".equals(msg.substring(0, 6))){
						
						str_imei = msg.substring(6);
						
						SocketMap.put(msocket,str_imei);
						
						System.out.println(str_imei);
						
						DataModel.getDataModel().setList_name("1"+date+","+l_strIP+","+"0001,"+str_imei);
						
						mconnectFrame.updateModel();
						
					}else if("phone_info".equals(msg.substring(0, 10))){
						 //��һ��������Json�ַ�����ʽ��JSON����
                        try {
                        	
							JSONObject jsonObject=new JSONObject(msg.substring(10));
							
							mConnectServer.setJson(jsonObject);
							
							System.out.println(msg.substring(10));
							
						} catch (JSONException e) {

							e.printStackTrace();
						}
						
					}
					l_aryBuf=null; 
					
					l_aryBuf= new byte[niBufferSize]; 
					
				}
				
				oReader.close();
				
				msocket.close();
				
				SocketMap.remove(msocket);
				
				client_list.remove(msocket);
				
				mConnectServer.removeClient(this,str_imei);
				
			} catch (IOException e) {
				
				SocketMap.remove(msocket);
				
				client_list.remove(msocket);
				
				mConnectServer.removeClient(this,msg);
				
				e.printStackTrace();
			} 
			
		}
		
	}
	
	//�����ӵĿͻ��˷��͹㲥��Ϣ
	public static void sendMsg(String msg){
		
		try {
			
			for(int i=0;i<client_list.size();i++){
				
				//д����
				oWritter = new DataOutputStream(client_list.get(i).getOutputStream());
				
				oWritter.writeUTF(msg);
				
				oWritter.flush();
				
				System.out.println("��ͻ���"+i+"������Ϣ"+msg);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	//��ָ�������ӷ��ͻ�ȡ�ֻ���Ϣ����Ϣ
	public static void sendIphoneInfo(String IMEI,String msg) {

		Set<Socket> kset = SocketMap.keySet();

		for (Socket ks : kset) {

			if (IMEI.equals(SocketMap.get(ks))) {

				// д����
				try {
					oWritter = new DataOutputStream(ks.getOutputStream());

					oWritter.writeUTF(msg);

					oWritter.flush();
					
					System.out.println("��ͻ���"+ks+"������Ϣ:"+msg);

				} catch (IOException e) {
					e.printStackTrace();
				}

				

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
