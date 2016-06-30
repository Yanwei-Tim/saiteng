package com.saiteng.main;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class DataModel extends DefaultTableModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object[][] arr = new Object[20][4];//�������
	
	private Object[] name = new Object[] {"����ʱ��", "ip��ַ","��������","�ֻ�����" };
	
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
		 arr = new Object[20][4];
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

	
      

	

}
