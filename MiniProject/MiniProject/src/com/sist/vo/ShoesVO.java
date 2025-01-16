package com.sist.vo;
import java.util.*;
import lombok.Data;
/*
GOODS_ID         NUMBER(38)     
NAME_KOR         VARCHAR2(4000) 
IMG              VARCHAR2(1024) 
BRAND            VARCHAR2(4000) 
COLOR            VARCHAR2(4000) 
TYPE             VARCHAR2(26)    
RELEASE_PRICE    VARCHAR2(26)   
RT_PRICE         NUMBER(38)      
goods_id,name_kor,img,brand,color,type,release_price,rt_price
*/
@Data
public class ShoesVO {
	private int goods_id, rt_price;
	private String name_kor, img, brand, color, type, release_price;
}
