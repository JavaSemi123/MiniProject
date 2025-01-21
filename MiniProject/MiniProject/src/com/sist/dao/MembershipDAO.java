package com.sist.dao;
import java.sql.*;
import com.sist.vo.*;
public class MembershipDAO {
	private Connection conn;
	private PreparedStatement ps;
	private static MembershipDAO dao;
	private final String URL="jdbc:oracle:thin:@211.238.142.124:1521:XE"; //211.238.142.124
	
	public MembershipDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
		}catch(Exception ex) {}
	}
	
	public static MembershipDAO newInstance()
	{
		if(dao==null)
			dao=new MembershipDAO();
		return dao;
	}
	
	public void getConnection()
	{
		try
		{
			conn=DriverManager.getConnection(URL,"hr_3","happy");
		}catch(Exception ex) {}
	}
	
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null)conn.close();
		}catch(Exception ex) {}
	}
	
	public MembershipVO isLogin(String id,String pwd)
	{
		MembershipVO vo=new MembershipVO();
		try
		{
			
			getConnection();
			
			String sql="select count(*) from membership where id=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			
			ResultSet rs=ps.executeQuery();
			rs.next();
			int count=rs.getInt(1);
			rs.close();
			
			if(count==0) // id가 없는 상태
			{
				vo.setMsg("NO ID");
			}
			else // id가 있는 상태
			{
				//비밀번호 확인
				sql="select id,pwd,name,sex from membership where id=?";
				ps=conn.prepareStatement(sql);
				// 실행 전 ? 값 채우기
				ps.setString(1, id);
				// 결과값
				rs=ps.executeQuery();
				rs.next(); // 한 줄 읽기
				//			 ------record
				vo.setId(rs.getString(1));
				vo.setName(rs.getString(3));
				vo.setSex(rs.getString(4));	
				String db_pwd=rs.getString(2);
				
				//비밀번호 검색
				if(db_pwd.equals(pwd)) // 로그인
				{
					vo.setMsg("OK");
				}
				else // 비번 틀린
				{
					vo.setMsg("NO PWD");
				}	
			}
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
	public MembershipVO memberInfo(String id)
	{
		MembershipVO vo=new MembershipVO();
		try
		{
			getConnection();
			String sql="SELECT name,sex,email,address,"
					+"birthday "
					+"FROM member "
					+"WHERE id=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs=ps.executeQuery();
			rs.next();
			vo.setName(rs.getString(1));
			vo.setSex(rs.getString(2));
			vo.setEmail(rs.getString(3));
			vo.setAddress(rs.getString(4));
			vo.setBirthday(rs.getDate(5));		
			rs.close();
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return vo;
	}
}
