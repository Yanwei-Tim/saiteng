package com.saiteng.st_lc32xcam.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ChangeCam {
	private static final String HOST = "192.168.11.123";
	private static final int PORT = 2001;
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private Context mcontext;
	private String msg = "000000";
	private String content = "";
	
	public ChangeCam(Context context){
		mcontext = context;
		initSocket();
	}
	private void initSocket() {
		new Thread(){
			public void run() {
			try{
				socket = new Socket(HOST, PORT);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())),
						true);
				if(socket.isConnected()){
					if (!socket.isOutputShutdown()) {
						out.println(msg);
					}
				}
				new Thread(runnable).start();
				}catch (IOException ex) {
					ex.printStackTrace();
					ShowDialog("login exception" + ex.getMessage());
				}
				
			};
		}.start();
		
	}
	/**
	 *接收返回值 
	 */
	public Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				while (true) {
					if (socket.isConnected()) {
						if (!socket.isInputShutdown()) {
							if ((content = in.readLine()) != null) {
								content += "\n";
							} 
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void ShowDialog(String msg) {

		new AlertDialog.Builder(mcontext).setTitle("notification").setMessage(msg)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}
	

}
