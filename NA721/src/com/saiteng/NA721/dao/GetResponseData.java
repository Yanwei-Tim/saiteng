package com.saiteng.NA721.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.saiteng.NA721.utils.DBHelper;

//select * from table where id = (select max(id) from table)查询数据库中的最新数据
public class GetResponseData {
	public List<Object> list =new ArrayList<Object>();
	String divicename=null,phonenum=null,type=null,timedata=null,groupId=null,
			Latitude=null,Longitude=null,totaldata=null;
	private String result=null;
	static String sql = null;  
    static DBHelper db1 = null;  
    static ResultSet ret = null; 
    static String TCG="TCG";
	
	/**返回行动组表里的数据*/
	public  List<Object> getGroupData(String Phonenum){
		sql="select * from action_Group";
		db1 = new DBHelper(sql);
		//db1.doSelect();
		try {
			while(db1.rs.next()){
				divicename=null;phonenum=null;type=null;totaldata=null;
				groupId = db1.rs.getString("GroupId").toString();
				
				if(groupId.length()>11){
					int N=groupId.length()/11;
					String[] arr  =new String[N];
					//该算法实现每11位截取一个手机号码
					String pnum_str="";
					for(int i=0;i<N;i++){
						arr[i]=groupId.substring(11*i, 11*(i+1));
					}
					for(int i=0;i<arr.length;i++){
						if(Phonenum.equals(arr[i])){
							divicename = db1.rs.getString("DiviceName");
						    phonenum = db1.rs.getString("PhoneNum");
						    type = db1.rs.getString("DiviceType");
						    totaldata =divicename+"-"+phonenum+"-"+type;
							list.add(totaldata);
						}
					}
				}else if(Phonenum.equals(groupId)){
					divicename = db1.rs.getString("DiviceName");
				    phonenum = db1.rs.getString("PhoneNum");
				    type = db1.rs.getString("DiviceType");
				    totaldata =divicename+"-"+phonenum+"-"+type;
					list.add(totaldata);
				}
			}
		} catch (SQLException e) {
			db1.close();
		}
		db1.close();
		return list;
	}
	//返回该设备当前的最新经纬度
	public String getLatLngData(String phonenum){
		Longitude=null;Latitude=null;timedata=null;
		result="";
		sql="select * from beacon where id = (select max(id) from (select * from beacon where PhoneNum="+phonenum+")"+TCG+")";
		db1 = new DBHelper(sql);
		db1.doSelect();
		try {
			while(db1.rs.next()){
				Longitude = db1.rs.getString("Longitude"); 
				Latitude  = db1.rs.getString("Latitude");
				timedata  = db1.rs.getString("Time");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(Longitude!=null&&Latitude!=null){
			 result=Longitude+","+Latitude+","+timedata;
		}
		db1.close();
		return result;
	}
	/**返回该设备该设备所有时间段数据，填充轨迹详情列表*/
	public List<String> getLocusData(String phonenum){
		String time=null,Longitude=null,Latitude=null;
		List<String> mlist = new ArrayList<String>();
		result=null;
		sql="select * from beacon where PhoneNum = "+phonenum+"";
		db1 = new DBHelper(sql);
		db1.doSelect();
		try {
			while(db1.rs.next()){
				time = db1.rs.getString("Time");
				if(mlist.contains(time)){
					
				}else
					
					mlist.add(time);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			db1.close();
		}
		db1.close();
		return mlist;
		
	}
	/**返回该设备某个时间段内的所有经纬度数据，在地图上绘制轨迹。*/
	public List<Object> getDetailsLocus(String time){
		String id=null,Longitude=null,Latitude=null;
		sql="select * from beacon where Time="+time+"";
		List<Object> mlist = new ArrayList<Object>();
		db1 = new DBHelper(sql);
		db1.doSelect();
		try {
			while(db1.rs.next()){
				Longitude = db1.rs.getString("Longitude");
				Latitude  = db1.rs.getString("Latitude");
				mlist.add(Longitude+"&"+Latitude);
			}
		} catch (SQLException e) {
			db1.close();
		}
		db1.close();
		return mlist;
		
	}
	/*返回主控端软件请求群组的成员结果**/
	public List<Object> allGroupData(){
		List<Object> mlist = new ArrayList<Object>();
		sql="select * from action_Group";
		db1 = new DBHelper(sql);
		db1.doSelect();
		try {
			while(db1.rs.next()){
				divicename = db1.rs.getString("DiviceName");
			    phonenum = db1.rs.getString("PhoneNum");
			    type = db1.rs.getString("DiviceType");
			    totaldata =divicename+"-"+phonenum+"-"+type;
				mlist.add(totaldata);
				
			}
		} catch (SQLException e) {
			db1.close();
			e.printStackTrace();
		}
		db1.close();
		return mlist;
		
	}
	/*主控端写入数据。**/
	public void insertData(String[] info){
		List<String> list_groupid = new ArrayList<String>();
		String[] infoShow = info;
		/**如果添加的是单兵设备则从数据库中查找出有未写归入群组的信标设备若有则将改信标设备添加至该单兵设备的群组*/
		if("1".equals(infoShow[2])){
		 list_groupid.clear();
		 sql="select * from action_Group where DiviceType='0'";
		 db1 = new DBHelper(sql);
		 db1.doSelect();
		 try{
			 while(db1.rs.next()){
				 list_groupid.add(db1.rs.getString("GroupId"));
			 }
		 }catch(SQLException e){
			 db1.close();
			 e.printStackTrace();
		 }
		 for(int i=0;i<list_groupid.size();i++){
			 sql="update action_Group set GroupId="+list_groupid.get(i)+infoShow[1]+" where GroupId="+list_groupid.get(i)+"";
			 db1 = new DBHelper(sql);
			 db1.doUpdate();
		 }
		}else{
			/**如果添加的是前置信标，
			 * 如果分组中已经存在单兵终端，
			 * 则需主动给该信标添加到该单兵终端所在的群组*/
			
			 sql="select * from action_Group where DiviceType=1";
			 db1 = new DBHelper(sql);
			 db1.doSelect();
			 String groupid_str="";
			 try {
				while(db1.rs.next()){
					groupid_str = groupid_str+db1.rs.getString("PhoneNum");
				 }
			} catch (SQLException e) {
				db1.close();
				e.printStackTrace();
			}
			sql="insert into action_Group(DiviceName,PhoneNum,DiviceType,GroupId) values ('"+infoShow[0]+"',"+infoShow[1]+","+infoShow[2]+","+groupid_str+")";
			db1 = new DBHelper(sql);
			db1.doUpdate();
			db1.close();
		}
		
	}
	//主控端删除设备
	public void deleteData(String phonenum){
		sql="delete from action_Group where PhoneNum="+phonenum+"";
		db1 = new DBHelper(sql);
		db1.doUpdate();
		db1.close();
	}

	public void insertEimi(String eimi){
		sql="insert into Eimi(EIMI) values ('"+eimi+"')";
		db1 = new DBHelper(sql);
		db1.doUpdate();
		db1.close();
	}
	public int loginsercive(String name,String pwd){
		int result=0;
		sql="select * from user where UserName='"+name+"' and PassWord='"+pwd+"'";
		db1 = new DBHelper(sql);
	   db1.doSelect();
			try {
				while(db1.rs.next()){
					result=1;
				}
			} catch (SQLException e) {
				db1.close();
				e.printStackTrace();
			}
		db1.close();
		return result;
		
	}
	
	public int changepwd(String newpwd,String username){
		sql="update user set PassWord="+newpwd+" where UserName='"+username+"'";
		db1 = new DBHelper(sql);
		db1.doUpdate();
		return 1;
		
	}
	
}
