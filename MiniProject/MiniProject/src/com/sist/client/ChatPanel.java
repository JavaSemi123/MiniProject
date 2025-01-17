package com.sist.client;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
public class ChatPanel extends JPanel{
	JTextArea ta;
	JTextField tf;
	JTable table;
	DefaultTableModel model;
	JButton b1,b2;
	
	ControlPanel cp;
	public ChatPanel(ControlPanel cp)
	{
		this.cp=cp;
		ta=new JTextArea();
		JScrollPane js=new JScrollPane(ta);
		ta.setEditable(false);
		
		tf=new JTextField();
		b1=new JButton("쪽지보내기");
		b2=new JButton("정보보기");
		
		String[] col= {"아이디","이름","성별"};
		String[][] row=new String[0][3];
    	model=new DefaultTableModel(row,col)
    	{
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
    		
    	};
    	table=new JTable(model);
    	JScrollPane js1=new JScrollPane(table);
	
		setLayout(null);
		
		js.setBounds(100, 20, 400, 450);
		add(js);
		
		tf.setBounds(100, 475, 400, 30);
		add(tf);
		
		js1.setBounds(510, 20, 280, 300);
		add(js1);
		
		JPanel p=new JPanel();
		p.add(b1); p.add(b2);
		
		p.setBounds(510, 330, 280, 35);
		add(p);
		
	}
}
