package com.sist.dao;
import java.util.*;
import java.sql.*;
import com.sist.vo.*;
public class ShoesDAO {
	private Connection conn;
	private PreparedStatement ps;
	private static ShoesDAO dao;
	private final String URL="jdbc:oracle:thin:@211.238.142.124:1212:XE";
	
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
			conn=DriverManager.getConnection(URL,"hr_3","happy");
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
	public List<ShoesVO> ShoesListData(int page)
	{
		List<ShoesVO> list = new ArrayList<ShoesVO>();
		try
		{
			getConnection();
			String sql="SELECT goods_id,name_kr,img "
					+ "FROM (SELECT goods_id, name_kor, img "
					+ "FROM (SELECT /*+ INDEX_ASC(shoes sh_goods_id_pk)*/ goods_id, name_kor, img"
					+ "FROM shoes))"
					+ "WHERE goods_id BETWEEN 1 AND 10";
						
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
				vo.setGoods_id(rs.getInt(1));
				vo.setName_kor(rs.getString(2));
				vo.setImg("https://kream-phinf.pstatic.net"+rs.getString(3));
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
	public int shoesTotalPage()
	{
		int total=0;
		try
		{
			getConnection();
			String sql="SELECT CEIL(COUNT(*)/12.0) FROM shoes";
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