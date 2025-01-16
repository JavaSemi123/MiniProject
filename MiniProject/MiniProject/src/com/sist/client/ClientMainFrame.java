package com.sist.client;
//   AcrylLookAndFeel  
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.sist.vo.*;
import com.sist.commons.Function;
import com.sist.dao.*;
///////////////// 네트워크 통신 
import java.io.*;
import java.util.*;
import java.net.*;
public class ClientMainFrame extends JFrame 
implements ActionListener, Runnable{
	
	Socket s;
	OutputStream out;
	BufferedReader in;
	
	MenuForm mf=new MenuForm(); // 포함 클래스 => 있는 그래도 사용
	ControlPanel cp=new ControlPanel();
	Login login=new Login();
	MemberDAO mDao=MemberDAO.newInstance();
	public ClientMainFrame()
	{
		setLayout(null); // 사용자 정의 => 직접 배치
		mf.setBounds(10, 15, 800, 50);
		add(mf);
		cp.setBounds(10, 75, 820, 570);
		add(cp);
		setSize(850,700);
//		setVisible(true);
		
		login.b1.addActionListener(this);
		login.b2.addActionListener(this);
		
		mf.b6.addActionListener(this);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
		} catch(Exception ex) {}
		new ClientMainFrame();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			while(true)
			{
				String msg=in.readLine();
				StringTokenizer st=new StringTokenizer(msg,"|");
				int protocol=Integer.parseInt(st.nextToken());
				switch (protocol)
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
				}
			}
		}catch (Exception ex) {
			// TODO: handle exception
		}
	}
	// 서버에 요청 => 로그인 / 채팅 보내기 
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==login.b2)
		{
			dispose();
			System.exit(0);
		}
		else if(e.getSource()==login.b1)
		{
			String id=login.tf.getText();
			if(id.trim().length()<1)
			{
				JOptionPane.showMessageDialog(this, "아이디를 입력하세요");
				login.tf.requestFocus();
				return;
			}
			String pwd=String.valueOf(login.pf.getPassword());
			if(pwd.trim().length()<1)
			{
				JOptionPane.showMessageDialog(this, "비밀번호를 입력하세요");
				login.pf.requestFocus();
				return;
			}
			MemberVO vo=mDao.isLogin(id, pwd);
			if(vo.getMsg().equals("NOID"))
			{
				JOptionPane.showMessageDialog(this, "아이디가 존재하지 않습니다");
				login.tf.setText("");
				login.pf.setText("");
				login.tf.requestFocus();
			}
			else if(vo.getMsg().equals("NOPWD"))
			{
				JOptionPane.showMessageDialog(this, "비밀번호가 틀립니다");
				login.pf.setText("");
				login.pf.requestFocus();
			}
			else
			{
				connection(vo);
			}
		}
		else if(e.getSource()==mf.b6)
		{
			cp.card.show(cp,"CHAT");	
		}
		else if(e.getSource()==mf.b6)
		{
			cp.card.show(cp,"HOME");
		}
	}
	public void connection(MemberVO vo)
	{
		try
		{
			s=new Socket("localhost",3355);
			
			out=s.getOutputStream();
			in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			out.write((Function.LOGIN+"|"
					+vo.getId()+"|"
					+vo.getName()+"|"
					+vo.getSex()+"\n").getBytes());
		}catch (Exception ex) {
			// TODO: handle exception 
		}
		new Thread(this).start(); // run()메소드 호출 
	}
}
