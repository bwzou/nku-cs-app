package com.z;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.z.po.ChooseCourse;
import com.z.po.Global;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseCourseActivity extends Activity {

	private ListView listView;
	private CheckBox resel;
	private CheckBox fuxiu;
	private EditText coursecode;
	public String resel1="";
	public String fuxiu1="";
	public String xuantuike="";
	
	ArrayList<HashMap<String, Object>> listItem ;
	SimpleAdapter listItemAdapter;
	
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_course);

		listView =(ListView)findViewById(R.id.choosecourse_list);
		resel = (CheckBox)findViewById(R.id.choose_course_c_resel);
		fuxiu = (CheckBox)findViewById(R.id.choose_course_c_fuxiu);
    	coursecode=(EditText) findViewById(R.id.choose_course_course_code);

		
		//  resel  是重修              fuxiu  就是  辅修   直接用resel.isChecked().判断是否被选中

		listItem = new ArrayList<HashMap<String, Object>>();  

		AddData(Global.choose_courses);

		listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
				R.layout.choose_course_server_a,//ListItem的XML实现  
				//动态数组与ImageItem对应的子项          
				new String[] {"CourseNumber","MainCourseNumber","CourseCodeString","CourseCredit","CourseName"},   
				//ImageItem的XML文件里面的一个ImageView,两个TextView ID  
				new int[] {R.id.ccsa_choose,R.id.ccsa_main_number,R.id.ccsa_course_code,R.id.ccsa_credit,R.id.ccsa_course_name}  
				); 

		listView.setAdapter(listItemAdapter);          
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub		
				Toast.makeText(getApplicationContext(), "item="+arg2, 0).show();

			}
		});
		
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2){
					Intent intent = new Intent(ChooseCourseActivity.this,ChooseCourseActivity.class);
					startActivity(intent);
					finish();
				}
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("所选课与已选课程上课时间冲突)选课操作失败(或不在指定的选课年级)！");
				
			}
		};
	}

	/*
	private void AddCourseDetail(){
		ChooseCourse x = new ChooseCourse();
	    x.setCourseName("大学物理");
	    x.setMainCourseNumber("2068");
	    x.setCourseCodeString("1290010710");
	    x.setCourseCredit("2");
	    x.setCourseNumber("2068");	     
	    courses.add(x);	
	}*/

	private void AddData(ArrayList<ChooseCourse> courses){	
		for(int i = 0;i<courses.size();i++)
		{
			ChooseCourse x = (ChooseCourse)courses.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("CourseName", x.getCourseName());
			map.put("MainCourseNumber", x.getMainCourseNumber());
			map.put("CourseCodeString", x.getCourseCodeString());
			map.put("CourseCredit", x.getCourseCredit());
			map.put("CourseNumber", x.getCourseNumber());
			listItem.add(map);
		}	
	}

	//查询界面
	public void gotoquery(View v){
		Intent intent = new Intent(ChooseCourseActivity.this,QueryCourseTableActivity.class);
		startActivity(intent);

	}

	//选课界面
	public void gotoselect(View v){
		xuantuike="xuanke";
		if(resel.isChecked()){
			resel1="&xkxh5=selected";
		}
		if(fuxiu.isChecked()){
			fuxiu1="&xkxh6=selected";
		}
		if(coursecode.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请您输入具体的内容", Toast.LENGTH_SHORT).show();
			return;
		}
		new Thread() {
			public void run() {
				Looper.prepare(); 
				boolean res=getSelectCourses();
				if(res)              //可以成功获取内容
					handler.sendEmptyMessage(2);
				else 
					handler.sendEmptyMessage(-2);
				Looper.loop(); 
			};
		}.start();	

	}

	//抢课
	public void gotograb(View v){
		Intent intent = new Intent(ChooseCourseActivity.this,GrabCourseActivity.class);
		startActivity(intent);
	}

	
	//退课界面
	public void gotocancel(View v){
		xuantuike="tuike";
		if(resel.isChecked()){
			resel1="&xkxh5=selected";
		}
		if(fuxiu.isChecked()){
			fuxiu1="&xkxh6=selected";
		}
		if(coursecode.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请您输入具体的内容", Toast.LENGTH_SHORT).show();
			return;
		}
		new Thread() {
			public void run() {
				Looper.prepare(); 
				boolean res=getSelectCourses();
				if(res)              //可以成功获取内容
					handler.sendEmptyMessage(2);
				else 
					handler.sendEmptyMessage(-2);
				Looper.loop(); 
			};
		}.start();	
	}


	//获取选课退课内容
	public boolean getSelectCourses(){
		//获取页面
		try {
			URL url = new URL(Global.urlString+"/xsxk/swichAction.do"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("POST"); //设定请求方式
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Cookie", Global.cookie);
						
			StringBuffer params = new StringBuffer();
			params.append("operation=").append(xuantuike).append("&index=&xkxh1=").append(coursecode.getText().toString())
			.append("&xkxh2=&xkxh3=&xkxh4=").append(resel1).append(fuxiu1).append("&courseindex=");
			System.out.print(params.toString());
			byte[] bypes = params.toString().getBytes("gb2312");
			Global.conn.connect(); //建立到远程对象的实际连接

			OutputStream os=Global.conn.getOutputStream();
			os.write(bypes);   // 输入参数
			os.flush();
			os.close(); 

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("网络错误异常！!!!");
				return  false;
			}else{				
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xsxk/swichAction.do");
				System.out.print(doc);
				Global.choose_courses.clear();     //每次先清空

				if(!doc.select("p:contains(选课操作失败)").text().equals("")){    //这里应该判断一下能否选上课
					return false;
				}

				Elements table = doc.select("body:has(form)").select("table");
				Elements trs = table.get(2).select("tr");

				ChooseCourse chooseCourse=null;
				for(int i = 1;i<trs.size();i++){
					Elements tds = trs.get(i).select("td");
					chooseCourse=new ChooseCourse();
					for(int j = 0;j<tds.size();j++){
						String linkText = tds.get(j).text();
						switch (j) {
						case 0:
							chooseCourse.setCourseType(linkText);
							break;
						case 1:
							chooseCourse.setCourseNumber(linkText);
							break;
						case 2:
							chooseCourse.setMainCourseNumber(linkText);
							break;
						case 3:
							chooseCourse.setCourseCodeString(linkText);
							break;
						case 4:
							chooseCourse.setCourseName(linkText);
							break;
						case 5:
							chooseCourse.setCourseCredit(linkText);
							break;
						default:
							break;
						}
					}
					Global.choose_courses.add(chooseCourse);
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("这是异常！");
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}

		return false;
	}

	
	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_course, menu);
		return true;
	}

}
