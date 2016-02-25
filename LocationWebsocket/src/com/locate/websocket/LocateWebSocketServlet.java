package com.locate.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import com.location.dao.InsertData;

public class LocateWebSocketServlet extends WebSocketServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 911879078000755859L;
	//private final Map<Integer, WsOutbound> map = new HashMap<Integer, WsOutbound>();
    private final Map<String,WsOutbound> users = new HashMap<String,WsOutbound>();
    int i=0;
	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest request) {
		String username = null;
		try {
			username = request.getParameter("username");
			System.out.println(username);
			if(username != null){
				username = new String(username.getBytes("ISO8859_1"),"UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(username+" 请求连接：");
		return new ChatMessageInbound(username);
	}
	//MessageInbound子类，完成收到WebSocket消息后的逻辑处理 
		class ChatMessageInbound extends MessageInbound {
			
			private String userName = "user";
			
			public ChatMessageInbound(String userName) {
					this.userName = userName;
			}

			/**
			 *有客户端建立连接的时候调用 
			 */
			@Override
			public void onOpen(WsOutbound outbound) {
				if("".equals(userName)){
					
				}
				if("server".equals(userName)){
					if(i>0){
						int j=i-1;
						users.remove("server"+j+"");	
					}
					
					userName = userName+i+"";
					i++;
				}
				if(users.containsKey(userName)){
					System.out.println(userName+"已经在线了");
				//	users.remove(userName);
				}else{
					users.put(userName, outbound);
					System.out.println(userName+"上线了,总共"+users.size()+"人在线");}
				super.onOpen(outbound);
				
			}

			/**
			 *客户端断开连接的时候调用 
			 */
			@Override
			protected void onClose(int status) {
				users.remove(userName);
				System.out.println("下线："+userName);
				super.onClose(status);
				String msg =userName+",disconn";
				int size = users.size();
				Set<String> set = users.keySet();//取所有已经连接上客户端的name
				for (String name : set) 
				{
					
					if(name.contains("server")){
						   WsOutbound outbound = users.get(name);
						   CharBuffer buffer = CharBuffer.wrap(users.size()+","+msg);
							try {
									outbound.writeTextMessage(buffer);
									outbound.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
					   }}
			}

			//处理二进制消息
			@Override
			protected void onBinaryMessage(ByteBuffer buffer) throws IOException {
			
			}
			//处理文本消息
			@Override
			protected void onTextMessage(CharBuffer buffer) throws IOException {

				String msg = buffer.toString();
				if(!"server".equals(userName)){
				   // new InsertData(msg).start();
				}
				System.out.println(userName+"发送数据："+msg);
				if(msg!=null && !"".equals(msg)){
					broadcast(msg);
				}
				
			}

			// 向所有已连接的客户端发送文本消息(广播)  
			private void broadcast(String msg) {
				String[] clientName=msg.split(",");
				Set<String> set = users.keySet();//取所有已经连接上客户端的name
				for (String name : set) 
				{
					if(name.contains("server")){
					  // if("server0".equals(name)||"server1".equals(name)){
						   WsOutbound outbound = users.get(name);
							CharBuffer buffer = CharBuffer.wrap(users.size()+","+msg);
							try {
									outbound.writeTextMessage(buffer);
									outbound.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
					   }else{
						   if(clientName.length!=5){
								WsOutbound outbound = users.get(clientName[0]);
								CharBuffer buffer = CharBuffer.wrap(clientName[1]);
								try {
										outbound.writeTextMessage(buffer);
										outbound.flush();
								} catch (Exception e) {
									    e.printStackTrace();
								}
						   }
					   }
				}
			}
		}
	
	

}
