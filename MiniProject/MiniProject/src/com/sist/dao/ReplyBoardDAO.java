package com.sist.dao;
import java.util.*;
import java.sql.*;
import com.sist.vo.*;
public class ReplyBoardDAO {
    // 연결 => 오라클 
	private Connection conn;
	// 송수신 => SQL문장 전송 , 결과값 받기 
	private PreparedStatement ps;
	// 메모리 절약 / Connection객체 생성 조절
	private static ReplyBoardDAO dao;
	// 오라클 연결 주소 
	private final String URL="jdbc:oracle:thin:@211.238.142.124:1521:XE";
	
	// 드라이버 등록 => 한번만 사용
	public ReplyBoardDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 리플렉션 => 클래스이름으로 메모리할당 / 메소드 제어 ..
			// new 역할 => 메소드명을 모르는 경우에도 호출 가능 
		}catch(Exception ex) {}
	}
	// 싱글턴 
	public static ReplyBoardDAO newInstance()
	{
		if(dao==null)
			dao=new ReplyBoardDAO();
		return dao;
	}
	// 연결 
	public void getConnection()
	{
		try
		{
			conn=DriverManager.getConnection(URL,"hr_3","happy");
		}catch(Exception ex) {}
	}
	// 해제 
	public void disConnection()
	{
		try
		{
			if(ps!=null)
				ps.close();
			if(conn!=null)
				conn.close();
		}catch(Exception ex) {}
	}
	/////////////////////// 자바에서 오라클 연동 기본 (공통) 
	/////////////////////// ---------------- JDBC 
	//// JDBC (원시소스) => DBCP => ORM (MyBatis,Hibernate => JPA)
	/// 기능 
	// 1. 목록 출력 => 인라인뷰 => 1페이지에 10개 
	// => 최신순 
	// => INDEX사용은 X => 추가,수정,삭제가 많은 경우에는 
	// INDEX가 속도가 저하 => ORDER BY를 이용한다 
	public List<ReplyBoardVO> boardListData(int page)
	{
		List<ReplyBoardVO> list=
				new ArrayList<ReplyBoardVO>();
		try
		{
			// 1. 연결 
			getConnection();
			// 2. SQL문장 제작 
			String sql="SELECT no,subject,name,TO_CHAR(regdate,'YYYY-MM-DD'),hit,group_tab,num "
					  +"FROM (SELECT no,subject,name,regdate,hit,group_tab,rownum as num "
					  +"FROM (SELECT no,subject,name,regdate,hit,group_tab "
					  +"FROM replyBoard ORDER BY group_id DESC,group_step ASC)) "
					  +"WHERE num BETWEEN ? AND ?";
			ps=conn.prepareStatement(sql);
			int rowSize=10;
			int start=(rowSize*page)-(rowSize-1);
			int end=rowSize*page;
			
			ps.setInt(1, start);
			ps.setInt(2, end);
			
			// 실행 요청 
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				ReplyBoardVO vo=new ReplyBoardVO();
				vo.setNo(rs.getInt(1));
				vo.setSubject(rs.getString(2));
				vo.setName(rs.getString(3));
				vo.setDbday(rs.getString(4));
				vo.setHit(rs.getInt(5));
				vo.setGroup_tab(rs.getInt(6));
				
				list.add(vo);
			}
			rs.close();
			
		}catch(Exception ex)
		{
			// 오류 확인 
			ex.printStackTrace();
		}
		finally
		{
			// 연결 해제 
			disConnection();
		}
		return list;
	}
	// 1-1. 총페이지 
	public int boardTotalPage()
	{
		int total=0;
		try
		{
			getConnection();
			String sql="SELECT CEIL(COUNT(*)/10.0) FROM replyBoard";
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			rs.next();
			total=rs.getInt(1);
			rs.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return total;
	}
	// 2. 글쓰기 => INSERT 
	// 매개변수가 3개이상 => 배열 / VO를 이용한다 
	public void boardInsert(ReplyBoardVO vo)
	{
		try
		{
			getConnection();
			// 새로운 그룹 생성 => 그룹번호 : group_id 
			String sql="INSERT INTO replyBoard(no,name,subject,content,pwd,group_id) "
					+ "VALUES(rb_no_seq.nextval,?,?,?,?,"
					+ "(SELECT NVL(MAX(group_id)+1,1) FROM replyBoard))";
			ps=conn.prepareStatement(sql);
			ps.setString(1, vo.getName());
			ps.setString(2, vo.getSubject());
			ps.setString(3, vo.getContent());
			ps.setString(4, vo.getPwd());
			ps.executeUpdate(); // commit()을 수행 
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
	}
	// 3. 상세보기 => WHERE 
	// 입고 => 재고 
	// 
	public ReplyBoardVO boardDetailData(int type,int no)
	{
		// => 상세보기 : 반드시 사용자로부터 Primary key 
		ReplyBoardVO vo=new ReplyBoardVO();
		try
		{
			// 1. 연결 
			getConnection();
			// 2. SQL문장 제작 
			// SQL 1. 조회수 증가 
			if(type==1)
			{
				String sql="UPDATE replyBoard SET "
						  +"hit=hit+1 "
						  +"WHERE no="+no;
				ps=conn.prepareStatement(sql);
				ps.executeUpdate(); // => COMMIT
			}
			// SQL 2. 상세보기 내용 읽기 
			String sql="SELECT no,name,subject,content,"
			   +"TO_CHAR(regdate,'YYYY-MM-DD HH24:MI:SS'),hit "
			   +"FROM replyBoard "
			   +"WHERE no="+no;
			ps=conn.prepareStatement(sql);
			// 한 기능을 수행할때 => SQL은 여러개일 수 있다
			ResultSet rs=ps.executeQuery();
			rs.next();
			vo.setNo(rs.getInt(1));
			vo.setName(rs.getString(2));
			vo.setSubject(rs.getString(3));
			vo.setContent(rs.getString(4));
			vo.setDbday(rs.getString(5));
			vo.setHit(rs.getInt(6));
			// * => DESC순서 
			rs.close();
		}catch(Exception ex)
		{
			// 복구 => 오류체크 
			ex.printStackTrace();
		}
		finally
		{
			// 닫기 
			disConnection();
		}
		return vo;
	}
	// 4. 수정 => UPDATE 
	public ReplyBoardVO boardUpdateData(int no)
	{
		ReplyBoardVO vo=new ReplyBoardVO();
		try
		{
			getConnection();
			String sql="SELECT no,name,subject,content "
					  +"FROM replyBoard "
					  +"WHERE no="+no;
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			rs.next();
			vo.setNo(rs.getInt(1));
			vo.setName(rs.getString(2));
			vo.setSubject(rs.getString(3));
			vo.setContent(rs.getString(4));
			rs.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return vo;
	}
	// 4-1. 실제 수정 
	public boolean boardUpdate(ReplyBoardVO vo)
	{
		boolean bCheck=false;// 비밀번호 처리 
		try
		{
			getConnection();
			// 1. SQL => 비밀번호 
			String sql="SELECT pwd FROM replyBoard "
					  +"WHERE no="+vo.getNo();
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			rs.next();
			String db_pwd=rs.getString(1);
			rs.close();
			
			// 오라클에 존재    사용자가 보내준 비밀번호
			if(db_pwd.equals(vo.getPwd()))
			{
				bCheck=true;
				// 실제 수정 
				sql="UPDATE replyBoard SET "
				   +"name=?,subject=?,content=? "
				   +"WHERE no=?";
				ps=conn.prepareStatement(sql);
				ps.setString(1, vo.getName()); // '홍길동'
				ps.setString(2, vo.getSubject());
				ps.setString(3, vo.getContent());
				ps.setInt(4, vo.getNo());
				
				// 수정해라 
				ps.executeUpdate();
				// INSERT / UPDATE / DELETE가 여러개인 경우 => 트랜잭션
				// SELECT : 검색 => COMMIT이 필요 없다 
				
			}
			else
			{
				bCheck=false;
			}
			
		}catch(Exception ex)
		{
			
		}
		finally
		{
			
		}
		return bCheck;
	}
	// 5. 답변 => 트랜잭션 
	// 6. 삭제 => 트랜잭션 
	
	
}