package com.sist.dao;
import java.util.*;
import java.sql.*;
import com.sist.vo.*;
public class ShoesDAO {
	private Connection conn;
	private PreparedStatement ps;
	private static ShoesDAO dao;
	private final String URL="jdbc:oracle:thin:@localhost:1521:XE";
	
	public ShoesDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch (Exception ex) {
			// TODO: handle exception
		}
	}
	
	public static ShoesDAO newInstance()
	{
		if(dao==null)
			dao=new ShoesDAO();
		return dao;
	}
	public void getConnection()
	{
		try
		{
			conn=DriverManager.getConnection(URL,"hr","happy");
		}catch (Exception ex) {
			// TODO: handle exception
		}
	}
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null) conn.close();
		}catch (Exception ex) {
			// TODO: handle exception
		}
	}
	public List<ShoesVO> foodListData(int page)
	{
		List<ShoesVO> list = new ArrayList<ShoesVO>();
		try
		{
			getConnection();
			String sql="SELECT fno,name,poster,num "
						+"FROM (SELECT fno,name,poster,rownum as num "
						+"FROM (SELECT /*+ INDEX_ASC(food_menupan fm_fno_pk)*/fno,name,poster "
						+"FROM food_menupan))"
						+"WHERE num BETWEEN ? AND ?";
			ps=conn.prepareStatement(sql);
			int rowSize=12;
			int start=(rowSize*page)-(rowSize-1);
			int end=rowSize*page;
			
			ps.setInt(1, start);
			ps.setInt(2, end);
			
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				ShoesVO vo=new ShoesVO();
				vo.setFno(rs.getInt(1));
				vo.setName(rs.getString(2));
				vo.setPoster("https://www.menupan.com"+rs.getString(3));
				list.add(vo);
			}
		   rs.close();

		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return list;
	}
	public int foodTotalPage()
	{
		int total=0;
		try
		{
			getConnection();
			String sql="SELECT CEIL(COUNT(*)/12.0) FROM food_menupan";
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			rs.next();
			total=rs.getInt(1);
			rs.close();
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return total;
	}
}
