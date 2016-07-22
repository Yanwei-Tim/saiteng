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
	
	private String msg=null;
	
	private String phoneImei =null;
	
	private String diviceImei =null;
	
	private static Set<Socket> kset1=null;
	
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
				
				if(msg!=null){
					
					msg=null;
				}
				
				msg=new String (l_aryBuf,0,len,"GB2312");
				
				GPSMainFrame.setMessage("客户端发送数据"+msg);
				
			   if(msg.length()>22){
				
				if(msg.contains("Connect")){
					//控制端链路保持命令
					
					phoneImei   = msg.substring(4,19);
					
					if(!SocketMap.containsValue(diviceImei)){
						
						SocketMap.put(msocket, phoneImei);
					}

					kset1 = SocketMap.keySet();
					
					for(Socket k1:kset1){
						
						if(phoneImei.equals(SocketMap.get(k1))){
							
							oWritter = new DataOutputStream(k1.getOutputStream());
							
							oWritter.write("[ST*Connect*OK]".getBytes("GB2312"));
							
							oWritter.flush();
							
							GPSMainFrame.setMessage("向客户端"+k1+"发送："+("[ST*Connect*OK]"));
						}
					}
					
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
					diviceImei = msg.substring(20,30);
					//主控端手机的IMEI号
					phoneImei   = msg.substring(4,19);
					
					P2PLocate.put(diviceImei,phoneImei);
					
					kset1 = SocketMap.keySet();

					for (Socket ks1 : kset1) {
						
						if(SocketMap.containsValue(diviceImei)){
							
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
							
						}else{
							//向主控端手机发送设备不在线
							if (phoneImei.equals(SocketMap.get(ks1))) {
								
								oWritter = new DataOutputStream(ks1.getOutputStream());
								
								oWritter.write(("[ST*"+diviceImei+"*NOTONLine]").getBytes("GB2312"));

								oWritter.flush();
								
								GPSMainFrame.setMessage("向客户端"+ks1+"发送："+("[ST*"+diviceImei+"*NOTONLine]"));
							}
							
						}
					}
				}else if(msg.contains("GetLocus")){
					//控制端请求返回轨迹数据文件夹
					//定位设备的设备号（根据IMEI号来下发命令参数）
					diviceImei = msg.substring(20,30);
					//主控端手机的IMEI号
					phoneImei   = msg.substring(4,19);
					
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.getLocus(phoneImei,diviceImei);
					
					
				}else if(msg.contains("LocusDetails")){
					//主控端请求轨迹数据
					diviceImei = msg.substring(20,30);
					//主控端手机的IMEI号
					phoneImei   = msg.substring(4,19);
					
					String date = msg.substring(31,43);
					
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.getLocusDetails(phoneImei, diviceImei, date);
					
			}else if("CR".equals(msg.substring(20, 22))){
					//设备返回开始定位的信息
			}else if("LK".equals(msg.substring(20, 22))){
					//链路保持命令
					String diviceIMI = msg.substring(4,14);
					
					if(!SocketMap.containsValue(diviceIMI)){
						
						SocketMap.put(msocket, diviceIMI);
						
					};
					
				    kset1 = SocketMap.keySet();
					
					for (Socket ks1 : kset1) {

						if (diviceIMI.equals(SocketMap.get(ks1))) {
							
							oWritter = new DataOutputStream(ks1.getOutputStream());
							
							oWritter.write(("[3G*"+diviceIMI+"*0002*LK]").getBytes("GB2312"));
							
							oWritter.flush();
							
							GPSMainFrame.setMessage("向客户端"+ks1+"发送:[3G*"+diviceIMI+"*0002*LK]");

						}
					}
					if (responsedata == null) {

						responsedata = new ResponseData();
					}
					responsedata.addOnline(diviceIMI);
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
			}
			
			responsedata=null;
			
			oReader.close();
			
			msocket.close();
			
			diviceImei =SocketMap.get(msocket);
			
			SocketMap.remove(msocket);
			
			mserversocket.removeClient(this);
			
			if (responsedata == null) {

				responsedata = new ResponseData();
			}
			responsedata.deleteOnline(diviceImei);
			
			
		 } catch (IOException e) {
			
			e.printStackTrace();
			
			mserversocket.removeClient(this);
		
		}
		
	}
	
	public static void sendmsg(String msg){
		//广播发送
		Set<Socket> kset1 = SocketMap.keySet();

		for (Socket ks : kset1) {
			
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
		kset1 = SocketMap.keySet();

		for (Socket ks : kset1) {

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
