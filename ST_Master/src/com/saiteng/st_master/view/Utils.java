package com.saiteng.st_master.view;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.MainActivity;
import com.saiteng.st_master.conn.DelDiviceTask;
import com.saiteng.st_master.conn.DeleteTrackTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom.Document;  
import org.jdom.Element;  
import org.jdom.JDOMException;  
import org.jdom.output.Format;  
import org.jdom.output.XMLOutputter;  

public class Utils {
	private static Context mcontext=null;
	private static Handler mhandler=null;
	private static Map<String, String> map = new HashMap<String, String>();
	private static String path = Environment
			.getExternalStorageDirectory().getPath()+"/kml";
	public static void DeleteDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						//((Activity) context).finish();
						if(Config.phonenum==null){
							Toast.makeText(context, "无法删除!", Toast.LENGTH_SHORT).show();
						}else
							Toast.makeText(context, "删除"+Config.phonenum, Toast.LENGTH_SHORT).show();
							new DelDiviceTask().execute();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
					}).show();

	}
	public static void ExitDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						((MainActivity) context).finish();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();

	}
	public static void deleteTrack(final Context context, String message,final String tracktime) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(tracktime==null){
							Toast.makeText(context, "请先查看轨迹", Toast.LENGTH_SHORT).show();
						}else{
							new DeleteTrackTask().execute(tracktime);
							Toast.makeText(context, "轨技数据删除成功", Toast.LENGTH_SHORT).show();
						}
						}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
	}
	//创建kml文件，先从数据库查询到该设备的在指定日为期段的定位数据，再生成kml文件。
	public static void createKml(final Context context,String time){
		map.clear();
		mcontext = context;
	    new InitLatlngTask1().execute(time);
		mhandler = new Handler(){
			   @Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if("true".equals(msg.obj.toString())){
					createkmlfile(map);//创建kml文件
					
				}
			}
		 };
	}
	static class InitLatlngTask1 extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			String timedate = params[0];
			String result=null;
			HttpGet get = new HttpGet(Config.url+"locusdetails?time="+timedate+"&phonenum="+Config.phonenum);
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = null;
			try {
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStream = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					builder = new StringBuilder();
					String s = null;
					for (s = reader.readLine(); s != null; s = reader.readLine()) {
						builder.append(s);
					}
					result=builder.toString();
				}else{
					result ="NetworkException";
				}
			} catch (Exception e) {
				e.printStackTrace();
				result="Exception";
			}
			return result;
		}
		@Override
		public void onPostExecute(String result) {
			if("".equals(result)){
				Toast.makeText(mcontext, "暂无轨迹数据", Toast.LENGTH_SHORT).show();
			}if("NetworkException".equals(result)){
				Toast.makeText(mcontext, "服务器错误", Toast.LENGTH_SHORT).show();
			}if("Exception".equals(result)){
				Toast.makeText(mcontext, "访问出现问题", Toast.LENGTH_SHORT).show();
			}
			if(result!=null){
				String[] arr_LatLng=result.split(",");
				if(arr_LatLng.length<2){
					Toast.makeText(mcontext, "轨迹数据太少，无法绘制", Toast.LENGTH_SHORT).show();
				}
				else{
					for(int i=0;i<arr_LatLng.length;i++){
						//117.70401156231&32.544050387869
						String[] LatLng=arr_LatLng[i].split("&");
						map.put("point"+i, LatLng[0]+","+LatLng[1]);
					}
					Message message = new Message();
					message.obj ="true";
					mhandler.sendMessage(message);
				}
			}
		}
	}
	protected static void createkmlfile(Map map) {
		 // 创建根节点 并设置它的属性 ;     
		Element root = new Element("Document").setAttribute("count",map.size()+"");     
        // 将根节点添加到文档中；     
        Document Doc = new Document(root); 
        for (int i = 0; i < map.size(); i++) {    
            // 创建节点 book;     
            Element elements = new Element("Placemark");  
            Element point = new Element("Point");
            point.addContent(new Element("coordinates").setText(map.get("point"+i).toString()));
            // 给 book 节点添加子节点并赋值；     
            elements.addContent(new Element("name").setText("point"+i));    
            elements.addContent(point); 
           
            //    
            root.addContent(elements);    
        }    
        // 输出 books.xml 文件；    
        // 使xml文件 缩进效果  
        Format format = Format.getPrettyFormat();  
        XMLOutputter XMLOut = new XMLOutputter(format);  
        try {
			XMLOut.output(Doc, new FileOutputStream(path+"new.kml"));
			Toast.makeText(mcontext, "轨迹导出成功"+path, Toast.LENGTH_LONG).show();
		} catch (FileNotFoundException e) {
			Toast.makeText(mcontext, "轨迹导出失败"+path, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(mcontext, "轨迹导出失败"+path, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
}
