package com.z;

import java.util.ArrayList;
import java.util.HashMap;

import com.z.po.CourseDetail;
import com.z.po.CourseSelectedDetail;
import com.z.po.Global;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PreCourseTableActivity extends Activity {
	
	private ListView listView;
	
	ArrayList<HashMap<String, Object>> listItem ;
	
	SimpleAdapter listItemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pre_course_table);
		
		listView =(ListView)findViewById(R.id.pre_course_table_list);
	    listItem = new ArrayList<HashMap<String, Object>>();   
	    AddData(Global.selected_courses);
	   
	    listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
	            R.layout.choose_course_server,//ListItem的XML实现  
	            //动态数组与ImageItem对应的子项          
	            new String[] {"CourseName","choose_course_teachername"},   
	            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
	            new int[] {R.id.choose_course_title,R.id.choose_course_teachername}  
	        ); 	    
		
        listView.setAdapter(listItemAdapter);               
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub		
				JumpToShowCourse(arg2);
				
				
			}
		});
	}
	
	//暂时没有用到
	private void AddCourseDetail()
	{
		CourseDetail x = new CourseDetail();
	    x.setCourseName("大学物理");
	    x.setCourseCode("123456789");
	    x.setCourseCredit("2");
	    x.setCourseGrade("88");
	    x.setCourseRetake("未重修");
	    x.setCourseRetakeGrade("无");   
		
	}
	
	private void AddData(ArrayList<CourseSelectedDetail> courses)
	{	
		for(int i = 0;i<courses.size();i++)
		{
			CourseSelectedDetail x = (CourseSelectedDetail)courses.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("CourseName", x.getCourseName());
			map.put("choose_course_teachername", x.getCourseTeacherName());		
			listItem.add(map);
		}	
	}

	public void JumpToShowCourse(int num)
	{
		Global.course_selected_detail = Global.selected_courses.get(num);           //这是每一回咬跳转的值
		Global.course_item=num;
		Intent intent = new Intent(PreCourseTableActivity.this,CourseSelectedDeatilActivity.class);
		startActivity(intent);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pre_course_table, menu);
		return true;
	}

}
