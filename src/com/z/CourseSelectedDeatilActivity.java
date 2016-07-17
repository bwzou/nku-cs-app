package com.z;

import com.z.po.CourseDetail;
import com.z.po.CourseSelectedDetail;
import com.z.po.Global;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CourseSelectedDeatilActivity extends Activity {

	public TextView CCode;
	public TextView CName;
	public TextView CCodeString;
	public TextView CWeekDay;
	public TextView CWeekDoub;
	public TextView CClassRoom;
	public TextView CTeacherCode;
	public TextView CTeacherName;
	public TextView CTeacherClass;
	public TextView CType;
	public TextView CBeginWeek;
	public TextView CEndWeek;

	public ImageButton imageBtnPrev;
	public ImageButton imageBtnNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course_selected_deatil);

		//控件初始化
		CCode = (TextView)findViewById(R.id.course_selected_coursecode);
		CName = (TextView)findViewById(R.id.course_selected_name);
		CCodeString = (TextView)findViewById(R.id.course_selected_code_string);
		CWeekDay = (TextView)findViewById(R.id.course_selected_week_day);
		CWeekDoub = (TextView)findViewById(R.id.course_selected_week_double_or_not);
		CClassRoom = (TextView)findViewById(R.id.course_selected_class_room);
		CTeacherCode = (TextView)findViewById(R.id.course_selected_teacher_code_string);
		CTeacherName = (TextView)findViewById(R.id.course_selected_teacher_name);
		CTeacherClass = (TextView)findViewById(R.id.course_selected_teaching_class);
		CType = (TextView)findViewById(R.id.course_selected_type);
		CBeginWeek = (TextView)findViewById(R.id.course_selected_begin_week);
		CEndWeek = (TextView)findViewById(R.id.course_selected_end_week);

		imageBtnPrev=(ImageButton) findViewById(R.id.course_selected_before);
		imageBtnNext=(ImageButton) findViewById(R.id.course_selected_next);
		
		imageBtnPrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Global.course_item>0){	
					Global.course_item--;
					Global.course_selected_detail = Global.selected_courses.get(Global.course_item);           //这是每一回咬跳转的值
					setCourseDetail();				
				}else{
					ShowErrorInf("这已经是第一条了！");
				}
			}
		});


		imageBtnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {                      //经调试发现，如过网络过慢还是会出现问题
				// TODO Auto-generated method stub

				if(Global.course_item<Global.course_selected_total-1){
					Global.course_item++;
					Global.course_selected_detail = Global.selected_courses.get(Global.course_item);           //这是每一回咬跳转的值
					setCourseDetail();
				}else{
					ShowErrorInf("这已经是最后一条了");
				}
			}

		});
		setCourseDetail() ;
	}

	public void setCourseDetail() {
		//初始化赋值。
		CourseSelectedDetail coursedetail = Global.course_selected_detail;

		if(coursedetail == null)
		{
			Toast.makeText(getApplicationContext(), "此处为空值", Toast.LENGTH_SHORT).show();
			return;
		}
		else 
		{
			CName.setText(coursedetail.getCourseName());
			CCode.setText(coursedetail.getCourseCode());
			CCodeString.setText(coursedetail.getCourseCodeString());
			CWeekDay.setText(coursedetail.getCourseWeekDay());
			CType.setText(coursedetail.getCourseType());
			CWeekDoub.setText(coursedetail.getCourseWeekDoubleOrNot());
			CClassRoom.setText(coursedetail.getCourseClassRoom());
			CTeacherCode.setText(coursedetail.getCourseTeacherCodeString());

			CTeacherClass.setText(coursedetail.getCourseTeachingClassCodeString());
			CTeacherName.setText(coursedetail.getCourseTeacherName());
			CBeginWeek.setText(coursedetail.getCourseBeginWeek());
			CEndWeek.setText(coursedetail.getCourseEndWeek());
		}
	}

	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_selected_deatil, menu);
		return true;
	}

}
