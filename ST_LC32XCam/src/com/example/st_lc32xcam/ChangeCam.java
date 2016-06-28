package com.example.st_lc32xcam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ChangeCam {
	public Handler scHandler;
	private static final String HOST = "192.168.11.123";
	private static final int PORT = 2001;
	private static final String  ChangeCam= "000000";
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	
	public ChangeCam(){
		initData();
		initSocket();
	}
	private void initData() {
		scHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 0) {
					//连接上再发命令参数
					if (socket.isConnected()) {
						if (!socket.isOutputShutdown()) {
							out.println(ChangeCam);
						}
					}
					
				}
			}
		};
		
	}

	private void initSocket() {
		new Thread() {
			@Override
			public void run() {
				try {
					socket = new Socket(HOST, PORT);
					in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);
					Message msg=scHandler.obtainMessage();
					msg.what=0;
					scHandler.sendMessage(msg);
				} catch (IOException ex) {
					ex.printStackTrace();
					Log.i("Socket","login exception" + ex.getMessage());
				}
			}
		}.start();
		
	}
}
