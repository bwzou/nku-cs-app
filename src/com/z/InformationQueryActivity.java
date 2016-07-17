package com.z;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.z.po.CourseDetail;
import com.z.po.Global;
import com.z.po.TestDetail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

public class InformationQueryActivity extends Activity {

	private Handler handler;


	private ListView listView;
	ArrayList<HashMap<String, Object>> listItem ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_third_server);


		listView =(ListView)findViewById(R.id.ThirdList);
		listItem = new ArrayList<HashMap<String, Object>>();  

		//存放考试信息
		Global.test_infor_details=new ArrayList<TestDetail>();



		AddDate();

		SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
				R.layout.activity_third,//ListItem的XML实现  
				//动态数组与ImageItem对应的子项          
				new String[] {"ItemImage"},   
				//ImageItem的XML文件里面的一个ImageView,两个TextView ID  
				new int[] {R.id.ThirdText}  
				); 


		listView.setAdapter(listItemAdapter);         
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub		
				Toast.makeText(getApplicationContext(), "item="+arg2, 0).show();
				switch(arg2)
				{
				case 0:
					break;
				case 1:
					new Thread() {
						public void run() {
							Looper.prepare(); 
							boolean res=getTestInf();
							if(res)                  //可以成功获取内容
								handler.sendEmptyMessage(2);
							else 
								handler.sendEmptyMessage(-2);
							Looper.loop(); 
						};
					}.start();
					break;
				case 2:
					break;
				default:
					break;
				}
			}
		});

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2)
					JumpTestInf();                   //进行页面跳转
				else if(msg.what==3){
				//	Intent intentP = new Intent(ThirdActivity.this,PreCourseTableActivity.class);
				//	startActivity(intentP);     跳转到外语考试信息界面
				}
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("获取考试安排失败！");
				else if(msg.what==-3)
					ShowErrorInf("获取外语考试报名失败");
			}
		};
		
	}
	

	public void JumpTestInf(){
		Intent intent = new Intent(InformationQueryActivity.this,TestManagementActivity.class);
		startActivity(intent);
	}
	
	//获取test信息
	public boolean getTestInf(){
		//获取页面
		try {
			URL url = new URL(Global.urlString+"/xxcx/stdexamarrange/listAction.do"); //创建URL对象
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
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xxcx/stdexamarrange/listAction.do");
				System.out.print(doc);
               
				Global.test_infor_details.clear();
				Elements NavText=doc.getElementsByClass("NavText");
				int num=0;
				int kknn=0;				
				TestDetail testDetail=null;         //获取课程详情
				for (Element link : NavText) {
					String linkText = link.text();
					if(num<10 && kknn!=0){          
						if(num==0){
							testDetail=new TestDetail();  //new 多一个不影响
						}
						switch (num) {
						case 0:
							testDetail.setTestNumber(linkText);
							break;
						case 1:
							testDetail.setCourseName(linkText);
							break;
						case 2:
							testDetail.setCourseType(linkText);
							break;
						case 3:
							testDetail.setTestWeek(linkText);
							break;
						case 4:
							testDetail.setTestWeekDay(linkText);
							break;
						case 5:
							testDetail.setTestBeginCourse(linkText);
							break;
						case 6:
							testDetail.setTestEndCourse(linkText);
							break;
						case 7:
							testDetail.setTestClassroom(linkText);
							break;
						case 8:
							testDetail.setTestBeginTime(linkText);
							break;
						case 9:
							testDetail.setTestEndTime(linkText);
							break;
						default:
							break;
						}
						num++; 
					}else if(num==10){
						num=0;
						if(kknn==0)
							kknn++;	
						else
						    Global.test_infor_details.add(testDetail);	   //每次添加一个						
					}else{
						num++;
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


	public void AddDate(){
		String[] names = new String[]{"教材信息","考试安排","教室资源","外语考试报名"};
		for(String s:names){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", s);//   
			listItem.add(map);
		}

	}

	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.information_query, menu);
		return true;
	}

}
