package com.z;

import com.z.po.CourseDetail;
import com.z.po.Global;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ShowGradeActivity extends Activity {

	public TextView CName;
	public TextView CCode;
	public TextView CGrade;
	public TextView CCredit;
	public TextView CType;
	public TextView CRetake;
	public TextView CRetakeGrade;
	public ImageButton imageBtnPrev;
	public ImageButton imageBtnNext;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_grade);

		//控件初始化
		CName = (TextView)findViewById(R.id.CourseName);
		CCode = (TextView)findViewById(R.id.CourseCode);
		CGrade = (TextView)findViewById(R.id.CourseGrade);
		CCredit = (TextView)findViewById(R.id.CourseCredit);
		CType = (TextView)findViewById(R.id.CourseType);
		CRetake = (TextView)findViewById(R.id.CourseRetake);
		CRetakeGrade = (TextView)findViewById(R.id.CourseRetakeGrade);
		imageBtnPrev=(ImageButton) findViewById(R.id.show_grade_before);
		imageBtnNext=(ImageButton)findViewById(R.id.show_grade_next);

		imageBtnPrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Global.item>0){	
					Global.item--;
					Global.coursedetail = Global.courses.get(Global.item);           //这是每一回咬跳转的值
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
				if(Global.course_page==Global.course_total_page){  //已经是最后一页了
					if(Global.item<(Global.course_total%12-1)){   
						Global.item++;
						Global.coursedetail=Global.courses.get(Global.item);
						setCourseDetail();					
					}else{
						ShowErrorInf("这已经是最后一条了");
					}					
				}else{
					if(Global.item<11){		
						Global.item++;
						Global.coursedetail=Global.courses.get(Global.item);
						setCourseDetail();					
					}else{
						ShowErrorInf("这已经是最后一条了");
					}
				}
				
			}
		});
		setCourseDetail() ;
	}

	
	public void setCourseDetail() {
		//初始化赋值。
		CourseDetail coursedetail = Global.coursedetail;

		if(coursedetail == null)
		{
			Toast.makeText(getApplicationContext(), "此处为空值", Toast.LENGTH_SHORT).show();
			return;
		}
		else 
		{
			CName.setText(coursedetail.getCourseName());
			CCode.setText(coursedetail.getCourseCode());
			CGrade.setText(coursedetail.getCourseGrade());
			CCredit.setText(coursedetail.getCourseCredit());
			CType.setText(coursedetail.getCourseType());
			CRetake.setText(coursedetail.getCourseRetake());
			CRetakeGrade.setText(coursedetail.getCourseRetakeGrade());		
		}
	}

	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_grade, menu);
		return true;
	}

}
