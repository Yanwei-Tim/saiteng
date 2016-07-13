package com.saiteng.gpsserver.frame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.saiteng.gpsserver.GPSServerClient;
import com.saiteng.gpsserver.GPSServerSocket;

public class GPSMainFrame extends JFrame implements ActionListener{
	
	private JLabel mlab_status;
	
	private JButton lbtn_send;
	
	private static JTextArea mtext_context;
	
	private JTextField mtext_send;
	
	private JPanel mpanel_01;
	
	private JScrollPane jsp;
	
	private static String context;
	
	
	public GPSMainFrame(){
		
		super("【赛腾定位服务】");
		
		mpanel_01 = new JPanel();
		
		mpanel_01.setLayout(null);
		
		this.add(mpanel_01);
		
		mlab_status = new JLabel("服务器状态:已开启");
		
		mlab_status.setBounds(20, 10, 120, 30);
		
		mpanel_01.add(mlab_status);
		
		lbtn_send = new JButton("发送指令");
		
		lbtn_send.setBounds(560, 10, 120, 30);
		
		lbtn_send.addActionListener(this);
		
		mpanel_01.add(lbtn_send);
		
		mtext_send = new JTextField();
		
		mtext_send.setBounds(200, 10, 300, 30);
		
		mpanel_01.add(mtext_send);
		
		mtext_context = new JTextArea();
		
		mtext_context.setBounds(20, 100, 900,500);
		
		mpanel_01.add(mtext_context);
		
		this.setVisible(true);
		
		GPSServerSocket gpsserver = new GPSServerSocket();
		
		gpsserver.start();
		
	}
	
	
	
	public static void setMessage(String msg){
		
		context = msg+"\n";
		
		mtext_context.append(context);
		
	}



	@Override
	public void actionPerformed(ActionEvent event) {
		
		Object obj = event.getSource();
		
		if(obj == lbtn_send){
			
			if(mtext_send.getText()!=null){
				
				GPSServerClient.sendmsg(mtext_send.getText());
				
				setMessage("发送命令:"+mtext_send.getText());
				
				
			}
		}
		
	}

}
