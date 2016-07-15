package com.saiteng.st_master;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application{
	
	//运用list来保存们每一个activity是关键  
    private List<Activity> mList = new LinkedList<Activity>();  
    
    private static MyApplication myapplication;
    
    //实例化一次  
    public synchronized static MyApplication getInstance(){   
        if (null == myapplication) {   
        	myapplication = new MyApplication();   
        }   
        return myapplication;   
    }   
    // add Activity    
    public void addActivity(Activity activity) {   
        mList.add(activity);   
    } 
    //关闭每一个list内的activity  
    public void exit() {   
        try {   
            for (Activity activity:mList) {   
                if (activity != null)   
                    activity.finish();   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            System.exit(0);   
        }   
    }  
    public void serverexit() {   
        try {   
            for (int i=1;i<mList.size();i++) {   
                if (mList.get(i) != null)   
                	mList.get(i).finish();   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            System.exit(0);   
        }   
    } 
    //杀进程  
    public void onLowMemory() {   
        super.onLowMemory();       
        System.gc();   
    }    

}
