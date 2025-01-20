package com.sist.client;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.awt.Color;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.sist.commons.ImageChange;
import com.sist.dao.*;
import com.sist.vo.*;
public class ShoesDetailPanel extends JPanel 
implements ActionListener
{
	JLabel img;
	JLabel name_korLa,brandLa,colorLa,typeLa,rt_priceLa;
	JLabel name_kor,brand,color,type,rt_price;
	JLabel[] images=new JLabel[6];
	JButton b1,b2,b3;
	int mode=0;
	
	ControlPanel cp;
	String[] link= {"","HOME","BRAND","FIND"};
	public ShoesDetailPanel(ControlPanel cp)
	{
		this.cp=cp;
		setLayout(null);
		
		img=new JLabel("");
		img.setBounds(20,20,300,500);
		add(img);
		
		name_korLa=new JLabel("제품명",JLabel.CENTER);
		name_korLa.setBorder(BorderFactory.createTitledBorder(""));
		name_kor=new JLabel();
		
		name_korLa.setBackground(Color.ORANGE);
		name_korLa.setOpaque(true);
		name_korLa.setBounds(330, 20, 80, 30);
		name_kor.setBounds(415, 20, 250, 30);
		add(name_korLa); add(name_kor);
		
		brandLa=new JLabel("브랜드",JLabel.CENTER);
		brandLa.setBorder(BorderFactory.createTitledBorder(""));
		brand=new JLabel();
		brandLa.setBackground(Color.ORANGE);
		brandLa.setOpaque(true);
		brandLa.setBounds(330, 80, 80, 30);
		brand.setBounds(415, 80, 250, 30);
		add(brandLa); add(brand);
		
		typeLa=new JLabel("타입",JLabel.CENTER);
		typeLa.setBorder(BorderFactory.createTitledBorder(""));
		type=new JLabel();
		typeLa.setBackground(Color.ORANGE);
		typeLa.setOpaque(true);
		typeLa.setBounds(330, 140, 80, 30);
		type.setBounds(415, 140, 350, 30);
		add(typeLa); add(type);
		
		colorLa=new JLabel("색상",JLabel.CENTER);
		colorLa.setBorder(BorderFactory.createTitledBorder(""));
		color=new JLabel();
		colorLa.setBackground(Color.ORANGE);
		colorLa.setOpaque(true);
		colorLa.setBounds(330, 200, 80, 30);
		color.setBounds(415, 200, 250, 30);
		add(colorLa); add(color);
		
		rt_priceLa=new JLabel("가격",JLabel.CENTER);
		rt_priceLa.setBorder(BorderFactory.createTitledBorder(""));
		rt_price=new JLabel();
		rt_priceLa.setBackground(Color.ORANGE);
		rt_priceLa.setOpaque(true);
		rt_priceLa.setBounds(330, 260, 80, 30);
		rt_price.setBounds(415, 260, 250, 30);
		add(rt_priceLa); add(rt_price);
			
		b1=new JButton("장바구니");
		b2=new JButton("구매하기");
		b3=new JButton("목록");
		
		JPanel p=new JPanel();
		p.add(b1);p.add(b2);p.add(b3);
		p.setBounds(330, 525, 435, 35);
		add(p);
		
		b3.addActionListener(this);
	}
	public void detailPrint(int mode,ShoesVO vo)
	{
		this.mode=mode;
		try
		{
			URL url=new URL(vo.getImg());
			Image imgs=ImageChange.getImage(new ImageIcon(url), 330, 400);
			img.setIcon(new ImageIcon(imgs));
			name_kor.setText(vo.getName_kor());
			brand.setText(vo.getBrand());
			type.setText(vo.getType());
			color.setText(vo.getColor());
			rt_price.setText(String.valueOf(vo.getRt_price()));		
		}catch (Exception ex) {
			// TODO: handle exception
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==b3)
		{
			cp.card.show(cp, link[mode]);
		}
	}
}
