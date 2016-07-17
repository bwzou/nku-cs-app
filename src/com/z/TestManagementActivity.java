package com.z;

import java.util.ArrayList;
import java.util.HashMap;

import com.z.po.CourseDetail;
import com.z.po.Global;
import com.z.po.TestDetail;

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

public class TestManagementActivity extends Activity {
	
	
	private ListView listView;
	
	//添加数据所准备。
	ArrayList<HashMap<String, Object>> listItem ;
	
	String[] from  = new String[]{"CourseName","TestClassRoom","TestWeek","TestWeekDay","TestBeginCourse","TestEndCourse","TestBeginTime","TestEndTime"};
	int[] to = new int[]{};
	
	SimpleAdapter listItemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_test_management);
		
		
		listView =(ListView)findViewById(R.id.test_course_list);
		
	    listItem = new ArrayList<HashMap<String, Object>>();  
	    
	    AddData(Global.test_infor_details);
	     
	   
	    listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
	            R.layout.test_management_serve,//ListItem的XML实现  
	            //动态数组与ImageItem对应的子项          
	            new String[] {"CourseName","TestClassRoom","TestWeek","TestWeekDay","TestBeginCourse","TestEndCourse","TestBeginTime","TestEndTime"},   
	            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
	            new int[] {R.id.test_course_name,R.id.test_calss_room,R.id.test_week,R.id.test_week_day,R.id.test_begin_course,R.id.test_end_course,R.id.test_begin_time,R.id.test_end_time}  
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
	
	
	private void AddCourseDetail()
	{
		TestDetail x = new TestDetail();
		x.setCourseName("大学语文");
		x.setCourseType("主讲");
		x.setTestBeginCourse("3");
		x.setTestBeginTime("2016-01-07 14:00:00.0");
		x.setTestClassroom("JN5B101");
		x.setTestEndCourse("4");
		x.setTestEndTime("2016-01-07 14:00:00.0");
		x.setTestNumber("1");
		x.setTestWeek("17");
		x.setTestWeekDay("4");
	    //courses.add(x);
		
	}
	
	
	private void AddData(ArrayList<TestDetail> courses)
	{	
		for(int i = 0;i<courses.size();i++)
		{
			TestDetail x = (TestDetail)courses.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("CourseName", x.getCourseName());
			map.put("TestClassRoom",x.getTestClassroom());
			map.put("TestWeek", x.getTestWeek());
			map.put("TestWeekDay", x.getTestWeekDay());
			map.put("TestBeginCourse", x.getTestBeginCourse());
			map.put("TestEndCourse", x.getTestEndCourse());
			map.put("TestBeginTime", x.getTestBeginTime());
			map.put("TestEndTime", x.getTestEndTime());
			listItem.add(map);
		}	
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_management, menu);
		return true;
	}

}
