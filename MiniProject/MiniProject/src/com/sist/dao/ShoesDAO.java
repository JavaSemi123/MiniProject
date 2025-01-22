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
	   public List<ShoesVO> shoesBrandData(int page,String brand)
	   {
		   List<ShoesVO> list=new ArrayList<ShoesVO>();
		   
		   try
		   {
			   getConnection();
			   String sql="select goods_id,name_kor,img,num "
						+ "from (select goods_id,name_kor,img,rownum as num "
						+ "from (select /*+ index_asc(shoes sh_goods_id_pk)*/goods_id,name_kor,img "
						+ "from shoes WHERE brand LiKE '%'||?||'%')) "
						+ "where num between ? and ?";
			   ps=conn.prepareStatement(sql);
			   ps.setString(1, brand);
			   int rowSize=12;
			   int start=(rowSize*page)-(rowSize-1);
			   int end=rowSize*page;
			   
			   ps.setInt(2, start);
			   ps.setInt(3, end);
			   
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
	public int shoesBrandTotalPage(String brand)
	{
		int total=0;
		try
		{
			getConnection();
			String sql="SELECT CEIL(COUNT(*)/12.0) "
					+"FROM shoes "
					+"WHERE brand LIKE '%'||?||'%'";
			ps=conn.prepareStatement(sql);
			ps.setString(1, brand);
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
//	+"FROM shoes "
//	+"WHERE name_kor LIKE '%'||?||'%' AND rownum<=10 "
//	+"ORDER BY goods_id ASC";
	public List<ShoesVO> shoesFindData(int page, String name_kor)
	{
		List<ShoesVO> list=new ArrayList<ShoesVO>();
		try
		{
			getConnection();
			String sql="SELECT goods_id,img,name_kor,brand,color,rt_price "
					+ "FROM (SELECT goods_id,img,name_kor,brand,color,rt_price,rownum as num "
					+ "FROM (SELECT /*+ index_asc(shoes sh_goods_id_pk)*/goods_id,img,name_kor,brand,color,rt_price "
					+ "FROM shoes WHERE name_kor LiKE '%'||?||'%')) "
					+ "WHERE num BETWEEN ? and ? "
					+ "ORDER BY goods_id ASC";
			ps=conn.prepareStatement(sql);
			ps.setString(1, name_kor);
			int rowSize=10;
			int start=(rowSize*page)-(rowSize-1);
			int end=rowSize*page;
   
			ps.setInt(2, start);
			ps.setInt(3, end);
		   
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				ShoesVO vo=new ShoesVO();
				vo.setGoods_id(rs.getInt(1));
				vo.setImg(rs.getString(2));
				vo.setName_kor(rs.getString(3));
				vo.setBrand(rs.getString(4));
				vo.setColor(rs.getString(5));
				vo.setRt_price(rs.getInt(6));
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
	public int shoesFindTotalPage(String name_kor)
	{
		int total=0;
		try
		{
			getConnection();
			String sql="SELECT CEIL(COUNT(*)/12.0) "
					+"FROM shoes "
					+"WHERE name_kor LIKE '%'||?||'%'";
			ps=conn.prepareStatement(sql);
			ps.setString(1, name_kor);
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
	public ShoesVO shoesDetailData(int goods_id)
	{
		ShoesVO vo=new ShoesVO();
		try
		{
			getConnection();
			String sql="SELECT name_kor,brand,type,color,img,rt_price FROM shoes WHERE goods_id="+goods_id;
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			rs.next();
			vo.setName_kor(rs.getString(1));
			vo.setBrand(rs.getString(2));
			vo.setType(rs.getString(3));
			vo.setColor(rs.getString(4));
			vo.setImg(rs.getString(5));
			vo.setRt_price(rs.getInt(6));
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
	public List<ShoesVO> shoesBrandDataEtc(int page) {
	    List<ShoesVO> list=new ArrayList<>();
	    try
	    {
	        getConnection();
	        String sql = "SELECT goods_id, name_kor, img, rnum " +
	                     "FROM ( " +
	                     "SELECT goods_id, name_kor, img, ROW_NUMBER() OVER (ORDER BY goods_id) AS rnum " +
	                     "FROM shoes " +
	                     "WHERE brand NOT IN ('Nike', 'New Balance', 'Asics', 'Adidas')) " +
	                     "WHERE rnum BETWEEN ? AND ?";
	        ps=conn.prepareStatement(sql);
	        int rowSize=12;
	        int start=(rowSize*page)-(rowSize-1);
	        int end=rowSize * page;
	        ps.setInt(1, start);
	        ps.setInt(2, end);
	        ResultSet rs=ps.executeQuery();
	        while (rs.next()) 
	        {
	            ShoesVO vo=new ShoesVO();
	            vo.setGoods_id(rs.getInt(1));
	            vo.setName_kor(rs.getString(2));
	            vo.setImg(rs.getString(3));
	            list.add(vo);
	        }
	        rs.close();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    finally
	    {
	        disConnection();
	    }
	    return list;
	}

	public int shoesBrandTotalPageEtc() 
	{
	    int total=0;
	    try 
	    {
	        getConnection();
	        String sql = "SELECT CEIL(COUNT(*) / 12.0) " +
	                     "FROM shoes " +
	                     "WHERE brand NOT IN ('Nike', 'New Balance', 'Asics', 'Adidas')";
	        ps = conn.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	        rs.next();
	        total = rs.getInt(1);
	        rs.close();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    finally
	    {
	        disConnection();
	    }
	    return total;
		}
}
