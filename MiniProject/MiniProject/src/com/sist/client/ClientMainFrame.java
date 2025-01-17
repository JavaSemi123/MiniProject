package com.sist.client;
//AcrylLookAndFeel
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.sist.commons.Function;
import com.sist.dao.*;
import com.sist.vo.*;

import java.io.*;
import java.util.*;
import java.net.*;
/*
 * 	사용자 동작 => 로그인 / 채팅
 * 	메소드 => 무조건 종료 후 다른 메소드가 호출
 * 	
 */
public class ClientMainFrame extends JFrame
implements ActionListener,Runnable,MouseListener
{
	// 네트워크 통신
	Socket s;
	OutputStream out;
	BufferedReader in;
	// 로그인이 되면 서버에 등록
	
	MenuForm mf=new MenuForm();
	ControlPanel cp=new ControlPanel();
	Login login=new Login();
	int selectRow=-1;
	// 배치
	// 데이터베이스 가져오기
	MembershipDAO mDao=MembershipDAO.newInstance();
	public ClientMainFrame()
	{
		setLayout(null);//사용자정의=>직접배치
		mf.setBounds(10, 15, 800, 50);
		add(mf);
		cp.setBounds(10, 75, 820, 570);
		add(cp);
		setSize(850,750);
		// 등록
		// 로그인처리
		login.b1.addActionListener(this);
		login.b2.addActionListener(this);
		
		mf.b6.addActionListener(this); // 채팅
		mf.b1.addActionListener(this); // 홈
		mf.b2.addActionListener(this); // 브랜드
		mf.b3.addActionListener(this); // 검색
		mf.b7.addActionListener(this); // 상세보기
		// Chat => Socket 
		cp.cp.tf.addActionListener(this);
		cp.cp.table.addMouseListener(this);
		cp.cp.b2.addActionListener(this);
		cp.cp.b1.addActionListener(this);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				try
				{
					out.write((Function.EXIT+"|\n").getBytes());
				}catch(Exception ex) {}
			}
			
		});
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
		}catch(Exception ex) {}
		new ClientMainFrame();
	}
	// 서버에서 응답 받기 => 쓰레드
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			while(true)
			{
				String msg=in.readLine();
				// 서버에서 보낸값 받기
				StringTokenizer st=
						new StringTokenizer(msg,"|");
				int protocol=Integer.parseInt(st.nextToken());
				switch(protocol)
				{
					case Function.LOGIN:
					{
					String[] data= {
							st.nextToken(),
							st.nextToken(),
							st.nextToken()
						};
						cp.cp.model.addRow(data);
					}
					break;
					case Function.MYLOG:
					{
						String id=st.nextToken();
						setTitle(id);
						login.setVisible(false);
						setVisible(true);
					}
					break;
					case Function.WAITCHAT:
					{
						cp.cp.ta.append(st.nextToken()+"\n");
					}
					break;
					case Function.MYEXIT:
				  	{
				  		dispose();
				  		System.exit(0);
				  	}
				  	break;
				  	// 남아 있는 사람 처리 
				  	case Function.EXIT:
				  	{
					  	String yid=st.nextToken();
					  	for(int i=0;i<cp.cp.model.getRowCount();i++)
					  	{
						  	String id=cp.cp.model.getValueAt(i, 0).toString();
						  	if(yid.equals(id))
						  	{
							  	cp.cp.model.removeRow(i);
							  	break;
						  	}
					  	}
				  	}
				  	break;
				}
			}
		}catch(Exception ex) {}
	}
	// 서버요청 => 로그인 / 채팅
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==login.b2)
		{
			dispose(); // 윈도우 메모리 해제
			System.exit(0);
		}
		else if(e.getSource()==login.b1)
		{
			// 유효성 검사
			String id=login.tf.getText();
			if(id.trim().length()<1)
			{
				JOptionPane.showMessageDialog(this, "ID를 입력하세요");
				
				login.tf.requestFocus();
				return;
			}
			String pwd=String.valueOf(login.pf.getPassword());
			if(pwd.trim().length()<1)
			{
				JOptionPane.showMessageDialog(this, "PWD를 입력하세요");
				
				login.pf.requestFocus();
				return;
			}
			// 로그인검색
			MembershipVO vo=mDao.isLogin(id, pwd);
			if(vo.getMsg().equals("NO ID"))
			{
				JOptionPane.showMessageDialog(this, "ID가 존재하지 않습니다");
				login.tf.setText("");
				login.pf.setText("");
				login.tf.requestFocus();
			}
			else if(vo.getMsg().equals("NO PWD"))
			{
				JOptionPane.showMessageDialog(this, "비밀번호가 틀립니다");
				
				login.pf.setText("");
				login.pf.requestFocus();
			}
			else
			{
				//서버연결
				connection(vo);
			}
		}
		else if(e.getSource()==cp.cp.b2)
		{
			if(selectRow==-1)
			{
				JOptionPane.showMessageDialog(this,"정보 볼 대상을 선택하세요");
				return;
			}
			String id=cp.cp.model.getValueAt(selectRow, 0).toString();
			MembershipVO vo=mDao.memberInfo(id);
			
			String info="이름:"+vo.getName()+"\n"
						+"성별:"+vo.getSex()+"\n"
						+"이메일:"+vo.getEmail()+"\n"
						+"생년월일:"+vo.getBirthday().toString()+"\n"
						+"주소:"+vo.getAddress()+"\n";
			JOptionPane.showMessageDialog(this, info);
		}
		// chat처리 
		else if(e.getSource()==cp.cp.tf)
		{
			String msg=cp.cp.tf.getText();
			if(msg.trim().length()<1)
			{
				cp.cp.tf.requestFocus();
				return;
			}
			try
			{
			  out.write((Function.WAITCHAT+"|"+msg+"\n").getBytes());	
			}catch(Exception ex){}
			cp.cp.tf.setText("");
		}		
		else if(e.getSource()==mf.b6)
		{
			cp.card.show(cp,"CHAT");
		}
		else if(e.getSource()==mf.b1)
		{
			cp.card.show(cp,"HOME");
		}
		else if(e.getSource()==mf.b2)
		{
			cp.card.show(cp, "BRAND");
		}
		else if(e.getSource()==mf.b3)
		{
			cp.card.show(cp, "FIND");
		}
		else if(e.getSource()==mf.b7)
		{
			cp.card.show(cp, "DETAIL");
		}
	}
	public void connection(MembershipVO vo)
	{
		try
		{
			s=new Socket("localhost",1212);
			// 서버 연결 => s는 서버
			// 서버로 전송
			out=s.getOutputStream();		
			// 서버에서 값 받기
			in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			//  서버로 로그인 요청
			out.write((Function.LOGIN+"|"
					+vo.getId()+"|"
					+vo.getName()+"|"
					+vo.getSex()+"\n").getBytes());
			// => 반드시 \n을 전송해야 한다
		}catch(Exception ex) {}
		// 서버로부터 값을 받아서 출력
		new Thread(this).start(); // run() 호출
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==cp.cp.table)
		{
			int selectRow=cp.cp.table.getSelectedRow();
			String myId=getTitle();
			String id=cp.cp.model.getValueAt(selectRow, 0).toString();
			if(myId.equals(id))
			{
				cp.cp.b1.setEnabled(false);
				cp.cp.b2.setEnabled(false);
			}
			else
			{
				cp.cp.b1.setEnabled(true);
				cp.cp.b2.setEnabled(true);
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

