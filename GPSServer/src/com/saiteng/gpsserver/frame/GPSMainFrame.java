package com.saiteng.gpsserver.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.saiteng.gpsserver.GPSServerClient;
import com.saiteng.gpsserver.GPSServerSocket;

public class GPSMainFrame extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel mlab_status;
	
	private JButton lbtn_send,mbtn_delete;
	
	private static JTextArea mtext_context;
	
	private JTextField mtext_send;
	
	private JPanel mpanel_01;
	
	private static String context;
	
	private GPSServerSocket gpsserver;
	
	
	
	
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
		
		mbtn_delete = new JButton("清空接收区");
		
		mbtn_delete.setBounds(700, 10, 120, 30);
		
		mbtn_delete.addActionListener(this);
		
		mpanel_01.add(mbtn_delete);
		
		mtext_send = new JTextField();
		
		mtext_send.setBounds(200, 10, 300, 30);
		
		mpanel_01.add(mtext_send);
		
		mtext_context = new JTextArea();
		
		mtext_context.setBounds(20, 100, 900,500);
		
		mpanel_01.add(mtext_context);
		
		this.setVisible(true);
		
		gpsserver = new GPSServerSocket();
		
		gpsserver.start();

		// 监听窗口关闭
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// 向连接的客户端发送服务器关闭的消息
				if (gpsserver != null) {

					GPSServerClient.sendmsg("[ST*Server_close]");
				}
			}
		});
		
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
		}else if(obj==mbtn_delete){
			mtext_context.setText("");
		}
		
	}

}
