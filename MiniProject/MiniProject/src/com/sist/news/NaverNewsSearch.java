package com.sist.news;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.text.*;

public class NaverNewsSearch {

    public static void main(String[] args) {

    	Scanner scan=new Scanner(System.in);
    	System.out.print("검색어 입력:");
    	String fd=scan.next();

        newsSearchData(fd);
    }
    public static List<NewsVO> newsSearchData(String fd)
    {
    	List<NewsVO> list=new ArrayList<NewsVO>();
    	
        String clientId = "0IIUnP4AIeOhZLEEiDr3"; //애플리케이션 클라이언트 아이디
        String clientSecret = "3YBqOpopHC"; //애플리케이션 클라이언트 시크릿


        String text = null;
        try {
            text = URLEncoder.encode(fd, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }


        String apiURL = "https://openapi.naver.com/v1/search/news.json?display=10&query=" + text;    // JSON 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // XML 결과


        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);
//      System.out.println(responseBody);
        try
        {
        	JSONParser jp=new JSONParser();
        	JSONObject root=(JSONObject)jp.parse(responseBody);
        	//System.out.println(root.toJSONString());
        	JSONArray items=(JSONArray)root.get("items");
        	//System.out.println(items.toJSONString());
        	for(int i=0;i<items.size();i++)
        	{
        		NewsVO vo=new NewsVO();
        		JSONObject obj=(JSONObject)items.get(i);
        		//System.out.println(obj.toJSONString());
        		String title=(String)obj.get("title");
        		System.out.println(title);
        		String description=(String)obj.get("description");
        		System.out.println(description);
        		String link=(String)obj.get("link");
        		System.out.println(link);
        		String pubDate=(String)obj.get("pubDate");
        		System.out.println(pubDate);
        		pubDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date(pubDate));
//        		System.out.println(pubDate);
        		vo.setTitle(title);
        	    vo.setDescription(description);
        		vo.setLink(link);
        		vo.setPubDate(pubDate);
        		list.add(vo);
        	}
        } catch (Exception ex) {
			// TODO: handle exception
		}

    	return list;
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}