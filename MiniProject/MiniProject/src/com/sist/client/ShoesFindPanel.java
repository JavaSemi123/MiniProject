package com.sist.client;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;//Image , Layout
import java.awt.event.*;
import java.net.URL;

import com.sist.vo.*;
import com.sist.commons.ImageChange;
import com.sist.dao.*;
import java.util.List;
// => 서버 연동 (X) 
public class ShoesFindPanel extends JPanel
implements ActionListener
{
     ControlPanel cp; // 상세보기 
     JTable table; // 모양관리 
     DefaultTableModel model; // 데이터 관리
     // MVC구조 
     JTextField tf; 
     JButton b;// 검색 
     TableColumn column;
     ShoesDAO dao=ShoesDAO.newInstance();
     public ShoesFindPanel(ControlPanel cp)
     {
    	 this.cp=cp;
    	 String[] col={
    		"번호","","이름","브랜드","색상","가격"
    	 };
    	 Object[][] row=new Object[0][6];
    	 model=new DefaultTableModel(row,col)
    	 {
            // 클릭시 => 편집 방지 
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
            // 이미지 출력 <?> => 와일드카드 : 어떤 클래스든 사용이 가능
			/*
			 *   Class<Integer> => ?
			 *   Class<String>
			 *   Class<ImageIcon>
			 *   Class<Double>
			 */
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				// TODO Auto-generated method stub
				return getValueAt(0, columnIndex).getClass();
			}
    		// 오버라이딩 사용 => 라이브러리 변경 
			// 상속없이 오버라이딩이 가능 => 익명의 클래스 (내부 클래스의 일종) 
    	 };
    	 table=new JTable(model);
    	 table.setRowHeight(40);
    	 JScrollPane js1=new JScrollPane(table);
    	 
    	 for(int i=0;i<col.length;i++)
     	 {
    		column=table.getColumnModel().getColumn(i);
     			if(i==0)
     		column.setPreferredWidth(80);
     		else if(i==1)
     			column.setPreferredWidth(80);
     		else if(i==2)
     			column.setPreferredWidth(180);
     		else if(i==3)
     			column.setPreferredWidth(350);
     		else if(i==4)
     			column.setPreferredWidth(160);
     		else if(i==5)
     			column.setPreferredWidth(80);
     	 }
    	 
    	 b=new JButton("검색");
    	 tf=new JTextField();
    	 
    	 setLayout(null);
    	 
    	 tf.setBounds(20, 20, 200, 30);
    	 b.setBounds(225, 20, 80, 30);
    	 
    	 js1.setBounds(20, 60, 800, 500);
    	 
    	 add(tf); add(b); 
    	 add(js1);
    	 
    	 b.addActionListener(this);
    	 tf.addActionListener(this);
     }
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==b || e.getSource()==tf)
		{
			// 검색어 읽기
			String name_kor=tf.getText();
			if(name_kor.trim().length()<1)
			{
				tf.requestFocus();
				return;
			}
			print(name_kor);
		}
	}
	public void print(String name_kor)
	{
		// 테이블 데이터 지우기 
		for(int i=model.getRowCount()-1;i>=0;i--)
		{
			model.removeRow(i);
		}
		
		// 데이터 받기 
		List<ShoesVO> list=dao.shoesFindData(name_kor);
		for(ShoesVO vo:list)
		{
			try
			{
				URL url=new URL(vo.getImg());
				Image image=ImageChange.getImage(new ImageIcon(url), 200, 180);
				Object[] data={
					vo.getGoods_id(),
					new ImageIcon(image),
					vo.getName_kor(),
					vo.getBrand(),
					vo.getColor(),
					vo.getRt_price()
				};
				model.addRow(data);
			}catch(Exception ex){}
		}
	}
}