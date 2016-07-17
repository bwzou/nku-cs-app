package com.z;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.z.po.ChooseCourse;
import com.z.po.CourseDetail;
import com.z.po.CourseQueryDetail;
import com.z.po.CourseSelectedDetail;
import com.z.po.Global;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ThirdActivity extends Activity {

	private ListView listView;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_third_server);

		//get the courses
		Global.courses = new ArrayList<CourseDetail>();
		Global.selected_courses=new ArrayList<CourseSelectedDetail>();
		Global.choose_courses=new ArrayList<ChooseCourse>();


		listView =(ListView)findViewById(R.id.ThirdList);

		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
		HashMap<String, Object> map = new HashMap<String, Object>();  
		map.put("ItemImage", "选课退课");//   
		listItem.add(map);
		HashMap<String, Object> map1 = new HashMap<String, Object>(); 
		map1.put("ItemImage", "已选课程详情");//    
		listItem.add(map1); 
		HashMap<String, Object> map2 = new HashMap<String, Object>(); 
		map2.put("ItemImage", "已修课程");//   
		listItem.add(map2);
		HashMap<String, Object> map3 = new HashMap<String, Object>(); 
		map3.put("ItemImage", "成绩预警信息");//   
		listItem.add(map3);
		HashMap<String, Object> map4 = new HashMap<String, Object>(); 
		map4.put("ItemImage", "选课指南");// 
		listItem.add(map4);  


		SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
				R.layout.activity_third,//ListItem的XML实现  
				//动态数组与ImageItem对应的子项          
				new String[] {"ItemImage"},   
				//ImageItem的XML文件里面的一个ImageView,两个TextView ID  
				new int[] {R.id.ThirdText}  
				); 

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2)
					JumpStudiedAction();                   //进行页面跳转
				else if(msg.what==3){
					Intent intentP = new Intent(ThirdActivity.this,PreCourseTableActivity.class);
					startActivity(intentP);
				}
				else if(msg.what==4){
					Intent intentC = new Intent(ThirdActivity.this,ChooseCourseActivity.class);
					startActivity(intentC);
				}
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("获取成绩信息失败！");
				else if(msg.what==-3)
					ShowErrorInf("获取已选课程信息失败");
				else if(msg.what==-4)
					ShowErrorInf("选课系统已关闭");
			}
		};


		listView.setAdapter(listItemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub		
				// TODO Auto-generated method stub		
				//Toast.makeText(getApplicationContext(), "item="+arg2, 0).show();
				switch(arg2)
				{
				case 0:
					new Thread() {
						public void run() {
							Looper.prepare(); 
							boolean res=getChoosedCourses();
							if(res)              //可以成功获取内容
								handler.sendEmptyMessage(4);
							else 
								handler.sendEmptyMessage(-4);
							Looper.loop(); 
						};
					}.start();		
					break;
				case 1:
					new Thread() {
						public void run() {
							Looper.prepare(); 
							boolean res=getSelectedCourses();
							if(res)              //可以成功获取内容
								handler.sendEmptyMessage(3);
							else 
								handler.sendEmptyMessage(-3);
							Looper.loop(); 
						};
					}.start();			
					break;
				case 2:
					new Thread() {
						public void run() {
							Looper.prepare(); 
							boolean res=getCourses();
							if(res)              //可以成功获取内容
								handler.sendEmptyMessage(2);
							else 
								handler.sendEmptyMessage(-2);
							Looper.loop(); 
						};
					}.start();				
					break;
				default:
					break;
				}
			}
		});

	}

	//页面跳转
	public void JumpStudiedAction(){
		Global.course_page=1;             //第一页
		Intent intent = new Intent(ThirdActivity.this,StudiedActionActivity.class);
		startActivity(intent);
	}

	//获取以修课程	
	public boolean getCourses() {
		//获取页面
		try {
			URL url = new URL(Global.urlString+"/xsxk/studiedAction.do"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("GET"); //设定请求方式
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Cookie", Global.cookie);
			Global.conn.connect(); //建立到远程对象的实际连接

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("网络错误异常！!!!");
				return  false;
			}else{				
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xsxk/studiedAction.do");
				System.out.print(doc);
                Global.courses.clear();
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

				//获取已修课程总数和总页数
				if(doc.select("td:contains(条记录)").text().equals("")){
					return false;
				}else{
					Global.course_total=Integer.parseInt(doc.select("td:contains(条记录)").text().substring(2, 4));
					if(Global.course_total%12>0){
						Global.course_total_page=Global.course_total/12+1;
					}else {
						Global.course_total_page=Global.course_total/12;
					}		
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

	//获取已选课程详情
	public boolean getSelectedCourses(){
		//获取已选课程总数
		try {
			URL url = new URL(Global.urlString+"/xsxk/selectedAction.do"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(5000); //设置连接超时为5秒
			Global.conn.setRequestMethod("POST"); //设定请求方式
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Cookie", Global.cookie);
			Global.conn.connect();

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.print(Global.conn.getResponseCode());
				System.out.println("网络错误异常！!!!");
				return  false;
			}else{				

				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xsxk/selectedAction.do");
				Global.course_selected_total=Integer.parseInt(doc.select("td:contains(条记录)").text().substring(2, 3));
				if(Global.course_selected_total%12>0){
					Global.course_selected_page=Global.course_selected_total/12+1;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("这是异常！");
			return false;
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}

		Global.selected_courses.clear();       //这样清空还是可能会有问题的
		//获取所有的课程
		for(int i=0;i<Global.course_selected_total;i++){
			//获取页面
			try {
				URL url1 = new URL(Global.urlString+"/xsxk/selectedAllAction.do"); //创建URL对象
				//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
				Global.conn = (HttpURLConnection) url1.openConnection();
				Global.conn.setConnectTimeout(5000); //设置连接超时为5秒
				Global.conn.setRequestMethod("POST"); //设定请求方式
				Global.conn.setDoOutput(true);
				Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				Global.conn.setRequestProperty("Cookie", Global.cookie);

				StringBuffer params = new StringBuffer();
				// 表单参数与get形式一样
				params.append("ifkebiao=no&select=").append(i);
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
					return false;
				}else{				
					Document doc1= Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xsxk/selectedAllAction.do");

					//NavText 
					Elements NavText=doc1.getElementsByClass("NavText");
					CourseSelectedDetail courseSelectedDetail=new CourseSelectedDetail();
					int num=0;
					for (Element link : NavText) {
						String linkText = link.text();
						switch (num) {
						case 0:
							courseSelectedDetail.setCourseCode(linkText);
							break;
						case 1:
							courseSelectedDetail.setCourseCodeString(linkText);
							break;
						case 2:
							courseSelectedDetail.setCourseName(linkText);
							break;
						case 3:
							courseSelectedDetail.setCourseWeekDay(linkText);
							break;
						case 4:
							courseSelectedDetail.setCourseWeekDoubleOrNot(linkText);
							break;
						case 5:
							courseSelectedDetail.setCourseClassRoom(linkText);
							break;
						case 6:
							courseSelectedDetail.setCourseTeacherCodeString(linkText);
							break;
						case 7:
							courseSelectedDetail.setCourseTeacherName(linkText);
							break;
						case 8:
							courseSelectedDetail.setCourseTeachingClassCodeString(linkText);
							break;
						case 9:
							courseSelectedDetail.setCourseType(linkText);
							break;
						case 10:
							courseSelectedDetail.setCourseBeginWeek(linkText);
							break;
						case 11:
							courseSelectedDetail.setCourseEndWeek(linkText);
						default:
							break;
						}
						num++;

						System.out.print(linkText+"\t");
					}
					Global.selected_courses.add(courseSelectedDetail);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (Global.conn != null) {
					Global.conn.disconnect(); //中断连接
				}
			}
		}

		return true;
	}

	//获取选课退课内容
	public boolean getChoosedCourses(){
		//获取页面
		try {
			URL url = new URL(Global.urlString+"/xsxk/selectMianInitAction.do"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("GET"); //设定请求方式
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Cookie", Global.cookie);
			Global.conn.connect(); //建立到远程对象的实际连接

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("网络错误异常！!!!");
				return  false;
			}else{				
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xsxk/selectMianInitAction.do");
				System.out.print(doc);
				Global.choose_courses.clear();     //每次先清空
				
				if(!doc.select("td:contains(课程名称)").text().equals("课程名称")){
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
		getMenuInflater().inflate(R.menu.third, menu);
		return true;
	}

}
