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
	//保存需要请求实时定位数据的空锥端和设备连接信息
	private static Map<String,String> P2PLocate = new HashMap<String,String>();
	
	public GPSServerClient(GPSServerSocket serversocket,Socket socket){
		
		this.msocket = socket;
		
		this.mserversocket = serversocket;
		
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
				
				String msg=new String (l_aryBuf,0,len,"GB2312");
				
				GPSMainFrame.setMessage("客户端发送数据"+msg);
				
				if(msg.contains("Connect")){
					//控制端链路保持命令
					SocketMap.put(msocket, msg.substring(4,19));
					
					oWritter.write("[ST*Connect*OK]".getBytes("GB2312"));
					
					oWritter.flush();
					
				}else if(msg.contains("Login")){
					//验证控制端登录信息
					if(responsedata==null){
						
						responsedata = new ResponseData();
					}
					responsedata.CheckLogin(msg.substring(4,19),msg.split(",")[1],msg.split(",")[2].replace("]", ""));
				} else if (msg.contains("ADDDivice")) {
					// 主控端添加监控的设备
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.addDivice(msg);
				}else if(msg.contains("GetDivice")){
					// 主控端请求获得设备
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.getDivice(msg);
				}else if(msg.contains("GetlatLng")){
					//主控端请求指定设备的实时定位数据
					//定位设备的设备号（根据IMEI号来下发命令参数）
					String diviceImei = msg.substring(20,30);
					//主控端手机的IMEI号
					String phoneImei   = msg.substring(4,19);
					
					P2PLocate.put(diviceImei,phoneImei);
					
					Set<Socket> kset1 = SocketMap.keySet();

					for (Socket ks1 : kset1) {

						if (diviceImei.equals(SocketMap.get(ks1))) {
							// 写出流
							try {
								oWritter = new DataOutputStream(ks1.getOutputStream());
								
								oWritter.write(("[3G*"+diviceImei+"*0002*CR]").getBytes("GB2312"));

								oWritter.flush();
								
								GPSMainFrame.setMessage("向客户端"+ks1+"发送："+("[3G*"+diviceImei+"*0002*CR]"));

							} catch (IOException e) {
								
								e.printStackTrace();
							}
						} 
						}
				}else if("CR".equals(msg.substring(20, 22))){
					//设备返回开始定位的信息
				}else if("LK".equals(msg.substring(20, 22))){
					//链路保持命令
					SocketMap.put(msocket, msg.substring(4,14));
					
					oWritter.write(("[3G*"+msg.substring(4,14)+"*0002*LK]").getBytes("GB2312"));
					
					oWritter.flush();
					
					GPSMainFrame.setMessage("服务器：[3G*"+msg.substring(4,14)+"*0002*LK]");
					
				}else if("UD".equals(msg.substring(20, 22))){
					//位置数据上报，保存到数据库
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.saveData(msg);
				}
				else{
					
					GPSMainFrame.setMessage("命令码是："+msg.substring(20, 22));
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
		//广播发送
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
		//向指定客户端发送
		Set<Socket> kset = SocketMap.keySet();

		for (Socket ks : kset) {

			if (imei.equals(SocketMap.get(ks))) {
				// 写出流
				try {
					oWritter = new DataOutputStream(ks.getOutputStream());
					
					oWritter.write(msg.getBytes("GB2312"));

					oWritter.flush();
					
					GPSMainFrame.setMessage("向客户端"+ks+"发送："+msg);

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

						GPSMainFrame.setMessage("向客户端" + ks + "发送：" + msg);
						
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		}
	}
}
