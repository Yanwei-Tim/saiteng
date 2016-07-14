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
					//主控端请求指定设备的定位数据
					String diviceImei = msg.substring(20,30);
					
					Set<Socket> kset1 = SocketMap.keySet();

					for (Socket ks1 : kset1) {

						if (diviceImei.equals(SocketMap.get(ks1))) {
							// 写出流
							try {
								oWritter = new DataOutputStream(ks1.getOutputStream());
								
								oWritter.write("".getBytes("GB2312"));

								oWritter.flush();
								
								GPSMainFrame.setMessage("向客户端"+ks1+"发送："+msg.getBytes("GB2312"));

							} catch (IOException e) {
								
								e.printStackTrace();
							}
						}else 
							//向发送请求的主控端返回设备不在线的指令
							GPSMainFrame.setMessage("向客户端"+ks1+"发送："+msg.getBytes("GB2312"));
						}
					
					
					
				}else if("LK".equals(msg.substring(20, 22))){
					//链路保持命令
					SocketMap.put(msocket, msg.substring(4,14));
					
					oWritter.write("[3G*3916377609*0002*LK]".getBytes("UTF-8"));
					
					oWritter.flush();
					
					
					GPSMainFrame.setMessage("服务器：[3G*3916377609*0002*LK]");
					
				}else if("UD".equals(msg.substring(20, 22))){
					//位置数据上报，保存到
				}
				else{
					
					GPSMainFrame.setMessage("命令码是："+msg.substring(20, 22));
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
					
					GPSMainFrame.setMessage("向客户端"+ks+"发送："+msg.getBytes("GB2312"));

				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			}
		
		
	}

}
