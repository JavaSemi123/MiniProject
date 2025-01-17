package com.sist.server;
import java.util.*;

import com.sist.commons.Function;

import java.net.*;
import java.io.*;
/*
 * 	서버
 * 	 => 접속 담당
 * 		=> 접속한 클라이언트의 정보 저장
 *   => 통신 담당 : 각 클라이언트당 따로 통신
 *      -------쓰레드 이용
 *      => 저장된 클라이언트 정보 이용
 *      => 멤번클래스(내부클래스)
 *     ------------------------------------------------ 
 *     class server
 *     {
 *     	 Vector waitVc;
 *     
 *     	 class client extends thread
 *       {
 *      	 // 클래스간 공유가 많은 경우 ( 변수,메소드)
 *      	 // 내부 클래스 이용
 *       }
 *     }
 *     ---------------------서버/빅데이터
 */
public class Server implements Runnable {
	// 클라이언트 정보저장
	Vector<Client> waitVc=new Vector<Client>();
	// 동기화
	// 접속 받기
	ServerSocket ss;
	// 연결
	final int PORT=1212; // 조별 포트번호 1212
	// port => 0~65535
	// 0~1023 알려진 포트
	
	//1.서버구동
	public Server()
	{
		try
		{
			ss=new ServerSocket(PORT);
			// 자동인식 => ip
			// 연결 => ip+port가 매칭 => bind()
			// 대기상태 listen()
			// 단점 : 50명만 접근 가능
			// ServerSocket(100,PORT); => 접근자 늘릴 수 있음
			System.out.println("Server Start..");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	// thread메소드 => 동작
	// 접속시마다 처리
	public void run()
	{
		while(true)
		{
			try
			{
				Socket s=ss.accept(); //대기상태
				//			접속 => 클라이언트가 접속시 호출되는 특수메소드
				//발신자정보 => 접속한 클라이언트 ip , port
				// => 통신시작
				Client client=new Client(s);
				client.start();
			}catch( Exception ex) {}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 시작점
		Server server=new Server();
		//서버 설정 => 서버 구동
		new Thread(server).start(); // 접속처리 => run() 호출
	}
	// 같은 서버에서 같은 port 이용시 서버는 1번만 수행
	// 통신담당
	class Client extends Thread
	{
		// client의 모든 정보
		String id,name,sex;
		// 연결
		Socket s;
		// 송수신
		//송신
		OutputStream out;
		//수신
		BufferedReader in;
		
		public Client(Socket s)
		{
			try
			{
				this.s=s;
				// 클라이너트 컴퓨터 메모리
				out=s.getOutputStream();
				
				// 접속한 클라이언트로 요청 처리값을 보내는 경우
				in=new BufferedReader(
						new InputStreamReader(s.getInputStream()));
				// 클라이언트로부터 요청을 받을 경우 사용
				// 한글처리 => 2byte
				// inputstream => 1byte => 한글깨짐
				// ====> 2byte변경 => reader
				// 웹에서 전송 : 1byte / 받는 경우 : 2byte 변경
				// => encoding			decoding(utf-8)
				//------------			=> setCharacterEncoding
				
			}catch(Exception ex) {}
		}
		
		// 통신 => start() => run() 호출
		public void run()
		{
			try
			{
				while(true)
				{
					// 사용자 전송한 메시지 받기
					String msg=in.readLine();
					System.out.println("Client =>"+msg);
					
					StringTokenizer st=new StringTokenizer(msg,"|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					case Function.LOGIN:
					{
						id=st.nextToken();
						name=st.nextToken();
						sex=st.nextToken();
						
						// 전체적으로 로그인 정보 전송
						messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex);
						// 입장메시지 전송
						messageAll(Function.WAITCHAT+"|[알림]"+name+"님이 입장했습니다");
						// waitVc 저장
						waitVc.add(this);
						// 메인화면 이동
						messageTo(Function.MYLOG+"|"+id);
						// 먼저 접속한 사람의 정보 전송
						for(Client client:waitVc)
						{
							messageTo(Function.LOGIN+"|"
									+client.id+"|"
									+client.name+"|"
									+client.sex);
						}
					}
					break;
					 case Function.WAITCHAT:
					  {
						  messageAll(Function.WAITCHAT+"|["
								  +name+"] "+st.nextToken());
					  }
					  break;
					  case Function.EXIT:
					  {
						  messageAll(Function.EXIT+"|"+id);
						  messageAll(Function.WAITCHAT+"|[알림]"
								  +name+"님이 퇴장하셨습니다");
						  for(int i=0;i<waitVc.size();i++)
						  {
							  Client c=waitVc.get(i);
							  if(c.id.equals(id))
							  {
								  messageTo(Function.MYEXIT+"|");
								  waitVc.remove(i);
								  // 접속자명단 제거 
								  try
								  {
									  // 통신 중단 
									  in.close();
									  out.close();
								  }catch(Exception ex) {}
							  }
						  }
					  }
					  break;
					}
				}
			}catch(Exception ex) {}
		}
		// => 개인별 전송
		// synchronized => 동기화
		// => async function
		// => sync function
		public synchronized void messageTo(String msg)
		{
			// 신뢰성이 좋은 프로그램 => 안정성 / 속도가 느리다
			// => smtp / ftp
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex) {}
		}
		// => 접속자 전체 전송
		public synchronized void messageAll(String msg)
		{
			try
			{
				for(Client client:waitVc)
				{
					client.messageTo(msg);
				}
			}catch(Exception ex) {}
		}
	}

}
