package com.saiteng.gpsserver.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.saiteng.gpsserver.GPSServerClient;


public class ResponseData {
	
	static String sql = null;  
    static DBHelper db1 = null;  
    static ResultSet ret = null; 
    
	String imei = null;
	
	String divicename = null;
	
	String divicenum = null;
	
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
		 imei = msg.substring(4,19);
		
		 divicename = msg.split(",")[1];
		
		 divicenum = msg.split(",")[2].replace("]","");
		
		sql ="Insert into divicegroup(IMEI,DiviceName,DiviceNum)VALUES('"+imei+"','"+divicename+"','"+divicenum+"')";
		 
		db1 = new DBHelper(sql);
		
		db1.doUpdate();
		
		db1.close();
	
		db1=null;
		
	}
	
	public void getDivice(String msg) {
		// 主控端请求设备
		String diviceName,diviceNum;
		
		String online =null;
		
		StringBuilder msgbuilder = new StringBuilder(); 
		
		 imei = msg.substring(4, 19);

		sql = "select * from divicegroup where IMEI = '" + imei + "'";

		db1 = new DBHelper(sql);

		db1.doSelect();
		
		msgbuilder.append("[ST*SetDivice*OK,");

		try {
			while (db1.rs.next()) {
				
				diviceName = db1.rs.getString("DiviceName");
				
				diviceNum = db1.rs.getString("DiviceNum");
				
				online = db1.rs.getInt("DiviceOnLine")+"";
				
				msgbuilder.append(diviceName+"#"+diviceNum+"#"+online+",");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			db1.close();
		}
		
		msgbuilder.append("]");
		
		GPSServerClient.specificsendmsg(imei,msgbuilder.toString());
		
		db1.close();
		
		db1=null;
		
        msgbuilder.delete(0, msgbuilder.length());
		
		msgbuilder=null;
	}
	
	public void addOnline(String IMEI){
		//添加设备在线
		sql = "update divicegroup set DiviceOnLine='1' where DiviceNum='"+IMEI+"'";
		
		db1 = new DBHelper(sql);
		
		db1.doUpdate();
		
		
	}
	
	public void deleteOnline(String IMEI){
		//设备设备在线标记
        sql = "update divicegroup set DiviceOnLine='0' where DiviceNum='"+IMEI+"'";
		
		db1 = new DBHelper(sql);
		
		db1.doUpdate();
		
	}
	
	public void saveData(String msg){
		
		// 保存设备上传定位数据
		/**
		 [3G*3916377609*016B*UD,150716,020644,V,31.200925,N,121.4601900,E,2.16,184.9,0.0,5,100,91,0,0,00000010,7,255,460,0,6243,53825,165, 6243,53363,155,6243,53826,154,6243,53827,153,6243,53361,149,6243,55377,148,6243,53362,148,5,jamestplink,c0:61:18:b:95:ce,-37,abcd,1a:ee:65:30:0:53,-68,TP-LINK_F6702A,8c:21:a:f6:70:2a,-73,VANS_PRIVATE,c0:3f:e:b:46:35,-78,EUFOTON,78:44:76:94:50:24,-80,35.6] 
		 * 
		 */
		String data[] = msg.split(",");
		
		 imei = msg.substring(4, 14);
        
		sql = "Insert into locationdata(Latitude,Longitude,DiviceIMEI,Date) VALUES('"+data[4]+"','"+data[6]+"','"+imei+"','"+data[1]+"')";
		
        db1 = new DBHelper(sql);
		
    	db1.doUpdate();
    	
		db1.close();
	
		db1=null;
		
		GPSServerClient.specificLatLng(imei,"[ST*Divice*GetLatLng,"+msg);
	}
	
	public void getLocus(String imei,String diviceIMEI){
		
		StringBuilder msgbuilder = new StringBuilder(); 
		
		sql = "select Date from locationdata where DiviceIMEI='"+diviceIMEI+"'";
		
		db1 = new DBHelper(sql);

		db1.doSelect();
		
		msgbuilder.append("[ST*GetLocus*OK");
		
		try {
			while(db1.rs.next()){
				
				String date = db1.rs.getString("Date");
				
				if(msgbuilder.toString().contains(date)){
					
				}else
					msgbuilder.append(","+date);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        msgbuilder.append("]");
		
		GPSServerClient.specificsendmsg(imei,msgbuilder.toString());
		
		db1.close();
		
		db1=null;
		
		msgbuilder.delete(0, msgbuilder.length());
		
		msgbuilder=null;
		
	}
	
	public void getLocusDetails(String imei,String diviceIMEI,String date){
		
		String Longitude=null,Latitude=null;
		
		StringBuilder msgbuilder = new StringBuilder(); 
		
		sql = "select * from locationdata where DiviceIMEI='"+diviceIMEI+"' and Date ='"+date.substring(0,6)+"'";
		
		db1 = new DBHelper(sql);

		db1.doSelect();
		
		msgbuilder.append("[ST*LocusDetails*OK");
		
		try {
			while(db1.rs.next()){
				
				Longitude = db1.rs.getString("Longitude");
				
				Latitude  = db1.rs.getString("Latitude");
				
				msgbuilder.append(","+Longitude+"&"+Latitude);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 msgbuilder.append("]");
			
			GPSServerClient.specificsendmsg(imei,msgbuilder.toString());
			
			db1.close();
			
			db1=null;
			
			msgbuilder.delete(0, msgbuilder.length());
			
			msgbuilder=null;
		
	}
	


}
