package com.locate.location.conn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpClientThread extends Thread {
	
	private String url;
	private ArrayList<String> list;
	
	public HttpClientThread(String url,ArrayList<String> list){
		this.url=url;
		this.list = list;
	}
	private void doHttpClientPost(){
  		 NameValuePair pair0 = new BasicNameValuePair("CollectionTime",list.get(0));//采集时间
         NameValuePair pair1 = new BasicNameValuePair("Latitude",list.get(1));//纬度
         NameValuePair pair2 = new BasicNameValuePair("Longitude",list.get(2));//经度
         NameValuePair pair4 = new BasicNameValuePair("Equipment", list.get(3));//设备编号
         NameValuePair pair3 = new BasicNameValuePair("Transmission", list.get(4));//传输方式
        
         List<NameValuePair> pairList = new ArrayList<NameValuePair>();
         pairList.add(pair0);
         pairList.add(pair1);
         pairList.add(pair2);
         pairList.add(pair3);
         pairList.add(pair4);
         try {
             HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                     pairList);
             // URL使用基本URL即可，其中不需要加参数
             HttpPost httpPost = new HttpPost(url);
             // 将请求体内容加入请求中
             httpPost.setEntity(requestHttpEntity);
             // 需要客户端对象来发送请求
             HttpClient httpClient = new DefaultHttpClient();
             // 发送请求
				httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	

	@Override
	public void run() {
		doHttpClientPost();
	}
}
