package com.saiteng.gpsserver.frame;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class GPSMain {
	
	public static void main(String[] args){
		
       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
       GPSMainFrame serverFrame = new GPSMainFrame();
		
		serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		serverFrame.setBounds((screenSize.width) / 6, (screenSize.height) / 6, 1000, 600);
		
		serverFrame.setVisible(true);
		
	}

}
