package com.location.dao;

import java.sql.ResultSet;  
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.location.utils.DBHelper;  
  
public class InsertData extends Thread{  
  
    static String sql = null;  
    static DBHelper db1 = null;  
    static ResultSet ret = null; 
    private String Transmission;
    private String CollectionTime;
    private String Latitude;
    private String Longitude;
    private String Equipment;
    private String TransWay;

    public InsertData(HttpServletRequest request){
    	if(request!=null){
	    	this.Transmission =request.getParameter("Transmission");
	    	this.CollectionTime = request.getParameter("CollectionTime");
	    	this.Latitude = request.getParameter("Latitude");
	    	this.Longitude = request.getParameter("Longitude");
	    	this.Equipment = request.getParameter("Equipment");
	    	if("001".equals(Transmission)){
	    		this.TransWay="GPS";
	    	}if("002".equals(Transmission)){
	    		this.TransWay="WIFI";
	    	}if("003".equals(Transmission)){
	    		this.TransWay="A_GPS";
	    	}if("004".equals(Transmission)){
	    		this.TransWay="Base_Station";
	    	}if("005".equals(Transmission)){
	    		this.TransWay="Outline";
	    	}
    	}
    	
    }
  
    public synchronized void  oInsert() {  
        sql = "insert into locatedata(Equipment,Latitude,Longitude,Transmission,CollectionTime) values ("+Equipment+","+Latitude+","+Longitude+","+Transmission+","+CollectionTime+")";
        db1 = new DBHelper(sql); 
        try {
			db1.pst.execute();
			System.out.println(Equipment+"正在写入数据");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			db1.close();
		}
    }  

	@Override
	public void run() {
		
		oInsert();
	}
}