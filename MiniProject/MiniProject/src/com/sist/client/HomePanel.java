package com.sist.client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.net.*;
import com.sist.commons.ImageChange;
import com.sist.dao.ShoesDAO;
import com.sist.vo.*;
import java.util.List;
public class HomePanel extends JPanel 
implements MouseListener, ActionListener{
	ControlPanel cp;
	JPanel pan=new JPanel();
	JButton b1,b2;
	JLabel la=new JLabel("0 page / 0 pages");
	JLabel[] imgs=new JLabel[12];
	
	int curpage=1;
	int totalpage=0;
	
	// 데이터베이스 연동 => FoodDAO
	ShoesDAO dao=ShoesDAO.newInstance();
	public HomePanel(ControlPanel cp)
	{
    	setLayout(new BorderLayout());
		this.cp=cp;
		pan.setLayout(new GridLayout(3,4,5,5));
		add("Center",pan);
		
		b1=new JButton("이전");
		b2=new JButton("다음");
		JPanel p=new JPanel();
		p.add(b1); p.add(la); p.add(b2);
		// add => 코딩 순서대로 배치
		add("South",p);
		print();
		
		b1.addActionListener(this); // 이전
		b2.addActionListener(this); // 다음
	}
	public void init()
	{
		for(int i=0;i<imgs.length;i++)
		{
			imgs[i]=new JLabel("");
		}
		pan.removeAll();
		pan.validate(); // 재배치
	}
	// 이미지 출력
	public void print()
	{
		totalpage=dao.foodTotalPage();
		List<ShoesVO> list=dao.foodListData(curpage);
		for(int i=0;i<list.size();i++)
		{
			ShoesVO vo=list.get(i);
			try
			{
				URL url=new URL(vo.getPoster());
				Image image=ImageChange.getImage(new ImageIcon(url),200,180);
				imgs[i]=new JLabel(new ImageIcon(image));
				imgs[i].setToolTipText(vo.getName()+"^"+vo.getFno());
				pan.add(imgs[i]);
				imgs[i].addMouseListener(this);
			}catch (Exception ex) {
				// TODO: handle exception
			}
		}
		la.setText(curpage+" page / "+totalpage+" pages");
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
		for(int i=0;i<imgs.length;i++)
		{
			if(e.getSource()==imgs[i])
			{
				imgs[i].setBorder(new LineBorder(Color.red,3));
			}
		}
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		for(int i=0;i<imgs.length;i++)
		{
			if(e.getSource()==imgs[i])
			{
				imgs[i].setBorder(new LineBorder(null));
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==b1) // 이전
		{
			if(curpage>1)
			{
				curpage--;
				init();
				print();
			}
		}
		else if(e.getSource()==b2) // 다음
		{
			if(curpage<totalpage)
			{
				curpage++;
				init();
				print();
			}
		}
	}
}
