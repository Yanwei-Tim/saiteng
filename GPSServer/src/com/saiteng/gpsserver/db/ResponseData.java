package com.saiteng.gpsserver.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.saiteng.gpsserver.GPSServerClient;


public class ResponseData {
	
	static String sql = null;  
    static DBHelper db1 = null;  
    static ResultSet ret = null; 
	
	public void CheckLogin(String imei,String username,String password){
		//主控端登录检查
		sql = "select * from user where UserName='"+username+"' and UserPassword = '"+password+"'";
		
		db1 = new DBHelper(sql);
		
		db1.doSelect();
		
		try {
			if(db1.rs.next()){
				GPSServerClient.specificsendmsg(imei,"[ST*Login*OK]");
			}else
				GPSServerClient.specificsendmsg(imei,"[ST*Login*Fail]");
		} catch (SQLException e) {
			e.printStackTrace();
			db1.close();
		}
		db1.close();
		
		db1=null;
	}
	
	public void addDivice(String msg){
		//主控端添加设备
		String imei = msg.substring(4,19);
		
		String divicename = msg.split(",")[1];
		
		String divicenum = msg.split(",")[2].replace("]","");
		
		sql ="Insert into Group(IMEI,DiviceName,DiviceNum)values("+imei+","+divicename+","+divicenum+")";
		 
		db1 = new DBHelper(sql);
		
		db1.doUpdate();
		
		db1.close();
	
		db1=null;
		
	}
	
	public void getDivice(String msg) {
		// 主控端请求设备
		String diviceName,diviceNum;
		
		StringBuilder msgbuilder = new StringBuilder(); 
		
		String imei = msg.substring(4, 19);

		sql = "select * from divicegroup where IMEI = '" + imei + "'";

		db1 = new DBHelper(sql);

		db1.doSelect();
		
		msgbuilder.append("[ST*SetDivice*OK,");

		try {
			while (db1.rs.next()) {
				
				diviceName = db1.rs.getString("DiviceName");
				
				diviceNum = db1.rs.getString("DiviceNum");
				
				msgbuilder.append(diviceName+"#"+diviceNum+",");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			db1.close();
		}
		
		msgbuilder.append("]");
		
		GPSServerClient.specificsendmsg(imei,msgbuilder.toString());
		
		db1.close();
		
		db1=null;
	}
	


}
