package com.z.po;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.R.integer;

public class Global {
	public static String UserName;
	public static String PassWord;
	public static String urlString="http://222.30.32.10";
	public static String cookie;
	public static String checkcode_text;
	public static HttpURLConnection conn=null;
	
	//已修课程
	public static int item=0;                    //每一个课程对应的详情
	public static int course_page=1;             //当前跳转页
	public static int course_total=0;            //已修课程总数
	public static int course_total_page=0;       //已修课程总页数
	public static  CourseDetail coursedetail;
	public static ArrayList<CourseDetail> courses;
	
	//已选课程
	public static int course_item=0;
	public static int course_selected_total=0;   //已选课程总数
	public static int course_selected_page=0;    //已选课程页数
	public static  CourseSelectedDetail course_selected_detail;
	public static ArrayList<CourseSelectedDetail> selected_courses;
	
	//考试安排
	public static int test_information=0;           //考试信息个数
	public static TestDetail test_infor_detial;     //单个考试信息
	public static ArrayList<TestDetail> test_infor_details;    //开始信息安排
	
	//选课退课
	public static int course_number;                //选课总数
	public static ChooseCourse choose_course;             //单个选课退课
	public static ArrayList<ChooseCourse>choose_courses;    //选课退课
	
	//查找课程
	public static int find_num;                  //结果数量
	public static int page_num=0;                  //page页数
	public static CourseQueryDetail courseQueryDetail;       //单个查询结果
	public static ArrayList<CourseQueryDetail>courseQueryDetails;    //所有查询结果
	
	//rsa publicKey
	public static String publicKeyString="00b6b7f8531b19980c66ae08e3061c6295a1dfd9406b32b202a59737818d75dea03de45d44271a1473af8062e8a4df927f031668ba0b1ec80127ff323a24cd0100bef4d524fdabef56271b93146d64589c9a988b67bc1d7a62faa6c378362cfd0a875361ddc7253aa0c0085dd5b17029e179d64294842862e6b0981ca1bde29979";
	public static BigInteger publicKey;
	
	
}
