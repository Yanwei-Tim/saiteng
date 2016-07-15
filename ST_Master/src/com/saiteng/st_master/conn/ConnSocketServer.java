package com.saiteng.st_master.conn;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.LocateActivity;
import com.saiteng.st_master.LoginActivity;
import com.saiteng.st_master.Menu_ManageActivity;
import com.saiteng.st_master.Menu_TrackActivity;

import android.os.Message;
import android.util.Log;

/**
 *与服务器建立socket连接来接收参数
 *和下发指令 
 */
public class ConnSocketServer extends Thread{
	private  Socket s = null;
	private static DataOutputStream oWritter;
	private DataInputStream oReader = null; 
	StringBuilder msg = new StringBuilder(); 
	private String imei=null;
    public ConnSocketServer(String strimei){
    	this.imei = strimei;
    }
	@Override
	public void run() {
		super.run();
		try {
			s = new Socket(Config.ip, Config.port);
			oWritter = new DataOutputStream(s.getOutputStream()); // 获取Socket对象的输出流，并且在外边包一层DataOutputStream管道，方便输出数
			oReader = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			LoginActivity.getHandler().sendEmptyMessage(4);
			oWritter.write(("[ST*"+imei+"*Connect]").getBytes("GB2312"));
			byte[] l_aryBuf = new byte[1024];
			int len = 0;
			while ((len=oReader.read(l_aryBuf)) != -1) {
				msg.append(new String(l_aryBuf, 0, len, "GB2312"));
				if("[ST*Login*OK]".equals(msg.toString())){
					LoginActivity.getHandler().sendEmptyMessage(1);
				}else if("[ST*Login*Fail]".equals(msg.toString())){
					LoginActivity.getHandler().sendEmptyMessage(0);
				}else if("[ST*Connect*OK]".equals(msg.toString())){
					//链路保持
				}else if("[ST*Server_close]".equals(msg.toString())){
					//服务器断开
					s=null;
					oWritter.close();
					oReader.close();
					LoginActivity.getHandler().sendEmptyMessage(2);
				}else if(msg.toString().contains("ST*SetDivice*OK")&&msg.toString().endsWith("]")){
					//从数据库获得设备列表数据
					if(Menu_ManageActivity.getHandler()!=null){
						Message message1 = Menu_ManageActivity.getHandler().obtainMessage();
					    message1.obj= (msg.toString());
					    Menu_ManageActivity.getHandler().sendMessage(message1);
					}
					if(Menu_TrackActivity.gethandler()!=null){
						Message message2 = Menu_TrackActivity.gethandler().obtainMessage();
						message2.obj =  (msg.toString());
				        Menu_TrackActivity.gethandler().sendMessage(message2);
					}
				}else if("[ST*Divice*NotOnline]".equals(msg.toString())){
					LocateActivity.setlatLng("");
				}else if(msg.toString().contains("ST*Divice*GetLatLng")){
					LocateActivity.setlatLng(msg.toString());
				}
				msg.delete(0, msg.length());		
			}
			s=null;
			oWritter.close();
			oReader.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			LoginActivity.getHandler().sendEmptyMessage(3);
		}
	}
	public static void sendOrder(String msg){
		try {
			oWritter.write(msg.getBytes("GB2312"));
			oWritter.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
