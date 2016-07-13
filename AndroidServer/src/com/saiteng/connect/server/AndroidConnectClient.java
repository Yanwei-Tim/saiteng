package com.saiteng.connect.server;

import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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

import com.saiteng.Frame.ContactFrame;
import com.saiteng.Frame.MessageFrame;
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
	
	private List<String> list_message = new ArrayList<String>();
	
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
			
			//String msg="";
			
			StringBuilder msg = new StringBuilder();  
			
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
				
				 int len = 0;
				
				while ((len = oReader.read(l_aryBuf)) != -1) {

					// msg=new String (l_aryBuf,0,len,"UTF-8");

					msg.append(new String(l_aryBuf, 0, len, "UTF-8"));
					
					if("start_".equals(msg.substring(0, 6))){
						//接收到客户端连接上的数据
						
						Toolkit.getDefaultToolkit().beep();
						
						str_imei = msg.substring(6);
						
						SocketMap.put(msocket,str_imei);
						
						System.out.println(str_imei);
						
						DataModel.getDataModel().setList_name("1"+date+","+l_strIP+","+"0001,"+str_imei);
						
						mconnectFrame.updateModel();
						
						msg.delete(0, msg.length());
						
					}else if("phone_info".equals(msg.substring(0, 10))&&msg.toString().endsWith("}")){
						 //接收到手机信息数据
                        try {
                        	
							JSONObject jsonObject=new JSONObject(msg.substring(10));
							
							mConnectServer.setJson(jsonObject);
							
							System.out.println(msg.substring(10));
							
							msg.delete(0, msg.length());
							
						} catch (JSONException e) {

							e.printStackTrace();
						}
						
					}else if("contact_info".equals(msg.substring(0, 12))&&msg.toString().endsWith("}")){
                        //接收到通讯录数据
						try {
							
							JSONObject jsonObject = new JSONObject(msg.substring(12));
							
							DataModel.getDataModel().setContactArr(jsonObject);

							mConnectServer.setJson(jsonObject);
							
							ContactFrame.setProgress(true);
							
							ContactFrame.updateModel();;

							System.out.println("json数据长度为："+msg.substring(12));
							
							msg.delete(0, msg.length());
							
						} catch (JSONException e) {

							e.printStackTrace();
						}
						
					}else if("message_info".equals(msg.substring(0, 12))&&msg.toString().endsWith("}")){
						//"message_info".equals(msg.substring(0, 12))
						
						list_message.clear();
						
						String message = msg.substring(13);
						
						String message_json =message.substring(0,message.length()-1);
						
						String[] arr_message = message_json.split("e_n_d");
						
						for(int i=0;i<arr_message.length;i++){
							
							list_message.add(arr_message[i]);
							
						}
						
						DataModel.getDataModel().setMessageArr(list_message);
						
						MessageFrame.setProgress(true);
						
						//MessageFrame.updateModel();
						
						System.out.println(list_message);
						
						msg.delete(0, msg.length());
						
					}else if("call_info".equals(msg.substring(0, 9))&&msg.toString().endsWith("}")){
						
						System.out.println(msg.substring(10));
						
					}
					l_aryBuf = null;

					l_aryBuf = new byte[niBufferSize];

				}
					
				
				oReader.close();
				
				msocket.close();
				
				SocketMap.remove(msocket);
				
				client_list.remove(msocket);
				
				mConnectServer.removeClient(this,str_imei);
				
			} catch (IOException e) {
				
				SocketMap.remove(msocket);
				
				client_list.remove(msocket);
				
				mConnectServer.removeClient(this,msg.toString());
				
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
	//向指定的连接发送获取手机信息的消息
	public static void sendIphoneInfo(String IMEI,String msg) {

		Set<Socket> kset = SocketMap.keySet();

		for (Socket ks : kset) {

			if (IMEI.equals(SocketMap.get(ks))) {

				// 写出流
				try {
					oWritter = new DataOutputStream(ks.getOutputStream());

					oWritter.writeUTF(msg);

					oWritter.flush();
					
					System.out.println("向客户端"+ks+"发送信息:"+msg);

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
