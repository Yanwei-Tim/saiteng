package com.saiteng.main;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class ConnectMain {

	public static void main(String args[]) {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		ConnectServerFrame serverFrame = new ConnectServerFrame();
		
		serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		serverFrame.setBounds((screenSize.width) / 6, (screenSize.height) / 6, 1000, 600);
		
		serverFrame.setVisible(true);
	}
}
