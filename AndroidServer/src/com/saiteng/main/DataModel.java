package com.saiteng.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataModel extends DefaultTableModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object[][] arr ;//�������
	
	private Object[] name = new Object[] {"����ʱ��", "ip��ַ","��������","�ֻ�����" };
	
	private Object[] contact_name = new Object[] {"ID", "��ϵ������ ","��ϵ���ֻ���","��ϵ������" };
	
	private Object[] message_name = new Object[]{"ID","ʱ��","�ռ���","������","�Ƿ��Ķ�","��������","��������"};
	
	private List<String> list_name = new ArrayList<String>();
	
	private static DataModel mDataModel;
	
	public Object[] getName(){
		
		return name;
		
	}
	
	public static DataModel getDataModel(){
		if(mDataModel==null){
			
			mDataModel = new DataModel();
		}
		return mDataModel;
		
	}
	//����ÿһ������
	public void setList_name(String msg){
		
		list_name.add(msg);
		
		setArr();
	}
	
	public void removeList_name(String msg){
		
		for(int i=0;i<list_name.size();i++){
			
			if(list_name.get(i).contains(msg)){
				
				list_name.remove(i);
				
				setArr();
			}
		
		}
	}
	
	
	//��������ݶ�Ӧ��ÿ��ÿ��
	public void setArr(){
		
		 arr=new Object[20][4];
		 
		if (list_name.size() == 0) {
			
		} else {
			for (int i = 0; i < list_name.size(); i++) {
				
				for (int j = 0; j < name.length; j++) {
					
					arr[i][j] = list_name.get(i).split(",")[j];
					
				}
			}
		}
	}
	
	public Object[][] getArr(){
		
		return arr;
	}
	
	public void setContactArr(JSONObject json){
		
		arr = new Object[json.length()][4];
		
		if(json.length()==0){
			
		}else{
			
			Iterator iterator = json.keys();
			
            int i =0;
            
			while (iterator.hasNext()) {
				
				try {

					String key = (String) iterator.next();

					String value = json.getString(key);

					for (int j = 0; j < contact_name.length; j++) {
						if(j==0){
							arr[i][j] = i;
						}else if(j==1){
							arr[i][j] = key;
						}else if(j==2){
							arr[i][j] = value;
						}else{
							arr[i][j] = "null";
						}
					}
					i++;
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		}
	}
	
	public void setMessageArr(List<String> message_list){
		arr = new Object[message_list.size()][7];
		JSONObject jsonObject;
		try {
			for (int i = 0; i < message_list.size(); i++) {
			
				 jsonObject = new JSONObject(message_list.get(i));
				 
				 for (int j = 0; j < message_name.length; j++) {
					 if(j==0){
							arr[i][j] = i+1;
						}else if(j==1){
							arr[i][j] =  jsonObject.getString("date");
						}else if(j==2){
							if("1".equals(jsonObject.getString("type"))){
								arr[i][j] ="����";
							}else
								arr[i][j] = jsonObject.getString("receive");
						}else if(j==3){
							if("1".equals(jsonObject.getString("type"))){
								arr[i][j]=jsonObject.getString("receive");
							}else
								arr[i][j] = "����";
						}
                        else if(j==4){
							if("1".equals(jsonObject.getString("isread"))){
								arr[i][j] = "�Ѷ�";
							}else
								arr[i][j] = "δ��";
						}else if(j==5){
							if("1".equals(jsonObject.getString("type"))){
								arr[i][j] ="����" ;	
							}else
								arr[i][j] ="����" ;
						}else if(j==6){
							arr[i][j] = jsonObject.getString("context");
						}
				 }

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public Object[][] getContactArr(){
		
		return arr;
		
	}
	
    public Object[][] getMessageArr(){
		
		return arr;
		
	}
}
