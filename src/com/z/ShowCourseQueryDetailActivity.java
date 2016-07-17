package com.z;

import java.util.ArrayList;
import java.util.HashMap;

import com.z.po.CourseQueryDetail;
import com.z.po.Global;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShowCourseQueryDetailActivity extends Activity {
	
	private ListView listView;
	
	//添加数据所准备。
	ArrayList<HashMap<String, Object>> listItem ;
	
	String[] from  = new String[]{"CourseCode","CourseTeachername","TestWeek","TestWeekDay","TestBeginCourse","TestEndCourse","TestBeginTime","TestEndTime"};
	int[] to = new int[]{};
	
	SimpleAdapter listItemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_course_query_detail);
		
		
		listView =(ListView)findViewById(R.id.scda_list);
		
	    listItem = new ArrayList<HashMap<String, Object>>();  
	    
	    AddData(Global.courseQueryDetails);
	     
	   
	    listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
	            R.layout.show_course_query_deatil_serve,//ListItem的XML实现  
	            //动态数组与ImageItem对应的子项          
	            new String[] {"CourseCode","CourseTeacherName","CourseName","CourseBeginEndWeek","CourseWeekDay","CourseTime","CourseType","CourseRoom"},   
	            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
	            new int[] {R.id.scqd_coursecode,R.id.scqd_courseteachername,R.id.scqd_coursename,R.id.scqd_coursebeginendweek,R.id.scqd_courseweekday,R.id.scqd_coursetime,R.id.scqd_coursetype,R.id.scqd_courseroom}  
	        ); 
	    
		
        listView.setAdapter(listItemAdapter);         
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub		
				Toast.makeText(getApplicationContext(), "item="+arg2, 0).show();
				//JumpToShowGrade(arg2);
			}
		});
	}
	
	
	/*
	private void AddCourseDetail(){
		CourseQueryDetail x = new CourseQueryDetail();
		x.setCourseName("大学语文");
		x.setCourseType("主讲");
		x.setCourseCode("0097");
		x.setCourseTeacherName("张张张");
		x.setCourseWeekDay("7");
		x.setCourseBeginEndWeek("1~16");
		x.setCourseRoom("津南5D215");
		x.setCourseTime("2/3/4");
	    courses.add(x);		
	}*/
	
	private void AddData(ArrayList<CourseQueryDetail> courses)
	{	
		for(int i = 0;i<courses.size();i++)
		{
			CourseQueryDetail x = (CourseQueryDetail)courses.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("CourseName", x.getCourseName());
			map.put("CourseCode",x.getCourseCode());
			map.put("CourseTeacherName", x.getCourseTeacherName());
			map.put("CourseType", x.getCourseType());
			map.put("CourseWeekDay", x.getCourseWeekDay());
			map.put("CourseTime", x.getCourseTime());
			map.put("CourseBeginEndWeek", x.getCourseBeginEndWeek());
			map.put("CourseRoom", x.getCourseRoom());
			listItem.add(map);
		}	
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_course_query_detail, menu);
		return true;
	}

}
