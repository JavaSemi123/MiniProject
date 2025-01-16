package com.sist.dao;
import java.util.*;
import java.sql.*;
import com.sist.vo.*;
public class ShoesDAO {
	private Connection conn;
	private PreparedStatement ps;
	private static ShoesDAO dao;
	private final String URL="jdbc:oracle:thin:@211.238.142.124:1521:XE"; //211.238.142.124
	
	// 드라이버 등록
	public ShoesDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
		}catch(Exception ex) {}
	}
	// conn 한개만 생성
	public static ShoesDAO newInstance()
	{
		if(dao==null)
			dao=new ShoesDAO();
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
	// 닫기
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null)conn.close();
		}catch(Exception ex) {}
	}
	public List<ShoesVO> shoesListData(int page)
	{
		List<ShoesVO>list
			= new ArrayList<ShoesVO>();
		try
		{
			getConnection();
			String sql="select goods_id,name_kor,img,num "
					+ "from (select goods_id,name_kor,img,rownum as num "
					+ "from (select /*+ index_asc(shoes sh_goods_id_pk)*/goods_id,name_kor,img "
					+ "from shoes)) "
					+ "where num between ? and ?";
			ps=conn.prepareStatement(sql);
			int rowsize=12;
			int start=(rowsize*page)-(rowsize-1);
			int end=rowsize*page;
			
			ps.setInt(1, start);
			ps.setInt(2, end);
			
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				ShoesVO vo=new ShoesVO();
				vo.setGoods_id(rs.getInt(1));
				vo.setName_kor(rs.getString(2));
				vo.setImg(rs.getString(3));
				list.add(vo);
			}
			rs.close();
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return list;
	}
	// 총페이지
	public int shoesTotalPage()
	{
		int total=0;
		try
		{
			getConnection();
			String sql="select ceil(count(*)/12.0) from shoes";
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
	// 2. 상세보기 => 조회수 증가
	
}
