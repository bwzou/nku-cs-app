package com.z;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.z.po.Global;
import com.z.po.CourseDetail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StudiedActionActivity extends Activity {


	private ListView listView;
	private Handler handler;
	private ImageButton imageBtnNext;
	private ImageButton imageBtnPrev;

	public String page="next";
	//添加数据所准备。
	ArrayList<HashMap<String, Object>> listItem ;

	String[] from  = new String[]{"CourseName","CourseCode","CourseType","CourseGrade","CourseCredit","CourseRetake","CourseRetakeGrade"};
	int[] to = new int[]{};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_studied_action);

		//控件  数据的初始化。
		listView =(ListView)findViewById(R.id.StudiedActionList);
		imageBtnPrev=(ImageButton) findViewById(R.id.go_prev);
		imageBtnNext=(ImageButton) findViewById(R.id.go_next);

		listItem = new ArrayList<HashMap<String, Object>>();  

		//操作线程的handler
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2){
					Intent intent = new Intent(StudiedActionActivity.this,StudiedActionActivity.class);
					startActivity(intent);	
					finish();
				}
				else if(msg.what==3)
					ShowErrorInf("这已经是第一页了！");
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("获取成绩信息失败！");
				else if(msg.what==-3)
					ShowErrorInf("这已经是最后一页了！");
			}
		};

		//往listview里面添加数据
		AddData(Global.courses);

		SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
				R.layout.activity_third,//ListItem的XML实现  
				//动态数组与ImageItem对应的子项          
				new String[] {"CourseName"},   
				//ImageItem的XML文件里面的一个ImageView,两个TextView ID  
				new int[] {R.id.ThirdText}  
				);     

		listView.setAdapter(listItemAdapter);       
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub		
				JumpToShowGrade(arg2);           //跳转到对应的课程位置
			}
		});

		imageBtnPrev.setOnClickListener(new OnClickListener() {
			// TODO Auto-generated method stub
			public void onClick(View v){
				page="prev";			
				if(Global.course_page>1){			
					new Thread() {
						public void run() {
							Looper.prepare(); 
							boolean res=getCourses();
							if(res) {
								Global.course_page--;//可以成功获取内容
								handler.sendEmptyMessage(2);
							}						
							else 
								handler.sendEmptyMessage(-2);
							Looper.loop(); 
						};
					}.start();
					
				}else{			
					handler.sendEmptyMessage(3);
				}
			}  
		});

		imageBtnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				page="next";			
				if(Global.course_page<4){
					new Thread() {
						public void run() {
							Looper.prepare(); 
							boolean res=getCourses();
							if(res)   
							{//可以成功获取内容
								Global.course_page++;
								handler.sendEmptyMessage(2);
							}
							else 
								handler.sendEmptyMessage(-2);
							Looper.loop(); 
						};
					}.start();
					
				}
				else{
					handler.sendEmptyMessage(-3);
				}
			}
		});

	}

	//留下的一个函数
	private void AddCourseDetail()
	{
		CourseDetail x = new CourseDetail();
		x.setCourseName("毛泽东思想与邓小平理论概述");
		x.setCourseCode("123456789");
		x.setCourseCredit("2");
		x.setCourseGrade("88");
		x.setCourseRetake("未重修");
		x.setCourseRetakeGrade("无");   
		Global.courses.add(x);

	}


	private void AddData(ArrayList<CourseDetail> courses)
	{	
		for(int i = 0;i<courses.size();i++)
		{
			CourseDetail x = (CourseDetail)courses.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("CourseName", x.getCourseName());
			listItem.add(map);
		}	
	}


	public void JumpToShowGrade(int num)
	{
		Global.coursedetail = Global.courses.get(num);           //这是每一回咬跳转的值
		Global.item=num;
		Intent intent = new Intent(StudiedActionActivity.this,ShowGradeActivity.class);
		startActivity(intent);
	}

	//从网页获取课程内容
	public boolean getCourses() {
		//获取页面
		try {
			URL url = new URL(Global.urlString+"/xsxk/studiedPageAction.do"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("POST"); //设定请求方式
			Global.conn.setDoOutput(true);
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Cookie", Global.cookie);

			StringBuffer params = new StringBuffer();
			// 表单参数与get形式一样
			params.append("page=").append(page);
			System.out.print(params.toString());
			byte[] bypes = params.toString().getBytes("gb2312");

			Global.conn.connect(); //建立到远程对象的实际连接

			OutputStream os=Global.conn.getOutputStream();
			os.write(bypes);   // 输入参数
			os.flush();
			os.close(); 

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				ShowErrorInf("网络错误异常！!!!");
				handler.sendEmptyMessage(-1);
				return  false;
			}else{				
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xsxk/studiedAction.do");
				System.out.print(doc);

				Global.courses.clear();       //先清空一下数据
				Elements NavText=doc.getElementsByClass("NavText");
				int num=0;
				int kknn=0;				
				CourseDetail courseDetail=null;         //获取课程详情
				for (Element link : NavText) {
					String linkText = link.text();
					if(num<8 && kknn!=0){
						if(num==1){
							courseDetail=new CourseDetail();
						}
						switch (num) {
						case 1:
							courseDetail.setCourseCode(linkText);
							break;
						case 2:
							courseDetail.setCourseName(linkText);
							break;
						case 3:
							courseDetail.setCourseType(linkText);
							break;
						case 4:
							courseDetail.setCourseGrade(linkText);
							break;
						case 5:
							courseDetail.setCourseCredit(linkText);
							break;
						case 6:
							courseDetail.setCourseRetake(linkText);
							break;
						case 7:
							courseDetail.setCourseRetakeGrade(linkText);
							break;
						default:
							break;
						}
						num++; 
					}else if(num==8){
						num=1;
						if(kknn==0)
							kknn++;
						else if(kknn<13){							
							Global.courses.add(courseDetail);	
							kknn++;
						}else{
							break;
						}
					}else{
						num++;
					}
				}
				return true;				
			}
		} catch (Exception e) {
			e.printStackTrace();
			ShowErrorInf("获取课程异常！");
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}

		return false;
	}


	//异常提示
	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.studied_action, menu);
		return true;
	}

}
