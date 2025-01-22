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
import java.util.Scanner;
// => 서버 연동 (X) 
public class ShoesFindPanel extends JPanel
implements ActionListener,MouseListener
{
     ControlPanel cp; // 상세보기 
     JTable table; // 모양관리 
     DefaultTableModel model; // 데이터 관리
     // MVC구조 
     JButton prev,next;
 	 JLabel la=new JLabel("0 page / 0 pages");
 	 JLabel[] imgs=new JLabel[12];
 	 int curpage=1;
 	 int totalpage=0;
 	 JTextField tf; 
     JButton b;// 검색 
     TableColumn column;
     ShoesDAO dao=ShoesDAO.newInstance();
     String name_kor="";
     public ShoesFindPanel(ControlPanel cp)
     {
    	 this.cp=cp;
    	 
    	 prev=new JButton("이전");
 		 next=new JButton("다음");
    	
 		 String[] col={
    		"번호","사진","이름","브랜드","색상","가격"
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
    	 table.getTableHeader().setReorderingAllowed(false);
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
    	 
    	 js1.setBounds(20, 60, 800, 450);
    	 
    	 add(tf); add(b); add(js1);
    	 
    	 b.addActionListener(this);
    	 tf.addActionListener(this);
    	 table.addMouseListener(this);

 		 JPanel p2=new JPanel();
 		 p2.add(prev); p2.add(la); p2.add(next);
 		 
		 setLayout(new BorderLayout());
 		 add("South",p2);
 		 
    	 prev.addActionListener(this);
 		 next.addActionListener(this);
     }
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == b || e.getSource() == tf) 
		{
			name_kor = tf.getText().trim();
            if (name_kor.length() < 1) 
            {
                tf.requestFocus();
                return;
            }
			print();
		} 

		else if (e.getSource() == prev)
		{
            if (curpage > 1) 
            {
                curpage--;
                print();
            }
        } 
		else if (e.getSource() == next) 
        {
            if (curpage < totalpage) 
            {
                curpage++;
                print();
            }
        }
    }
 	public void print()
 	{
 		// 테이블 데이터 지우기
        model.setRowCount(0);
 		
 		// 데이터 받기 
      	totalpage=dao.shoesFindTotalPage(name_kor);
 		List<ShoesVO> list=dao.shoesFindData(curpage, name_kor);
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
     	la.setText(curpage+" page / "+totalpage+" pages");
     	
     	prev.setEnabled(curpage > 1);
        next.setEnabled(curpage < totalpage);
 	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==table)
		{
			if(e.getClickCount()==2)
			{
				int row=table.getSelectedRow();
				String goods_id=model.getValueAt(row, 0).toString();
				ShoesVO vo=dao.shoesDetailData(Integer.parseInt(goods_id));
				cp.sdp.detailPrint(3, vo);
				cp.card.show(cp, "DETAIL");
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}