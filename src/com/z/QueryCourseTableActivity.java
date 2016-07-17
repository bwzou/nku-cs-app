package com.z;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.mail.handlers.text_html;
import com.z.po.CourseQueryDetail;
import com.z.po.CourseSelectedDetail;
import com.z.po.Global;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class QueryCourseTableActivity extends Activity {

	public EditText Query;
	public RadioGroup Radiog;
	public RadioButton rdinitialize;
	public RadioButton rdtemp;


	public RadioButton rdnumber;
	public RadioButton rdname;
	public RadioButton rdteacher;
	public RadioButton rdunit;


	public int RadioChoose=0;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_query_course_table);

		Query = (EditText)findViewById(R.id.qct_edit);
		Radiog = (RadioGroup)findViewById(R.id.query_course_table_radio);
		rdnumber = (RadioButton)findViewById(R.id.radioGroupButton0);
		rdname = (RadioButton)findViewById(R.id.radioGroupButton1);
		rdteacher = (RadioButton)findViewById(R.id.radioGroupButton2);
		rdunit = (RadioButton)findViewById(R.id.radioGroupButton3);


		Global.courseQueryDetails=new ArrayList<CourseQueryDetail>();

		int id = Radiog.getCheckedRadioButtonId();
		rdinitialize = (RadioButton)findViewById(id);
		RadioChoose = CheckRadio(rdinitialize.getText().toString());

		Radiog.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1 == rdnumber.getId())
					RadioChoose = 1;
				else if(arg1 == rdname.getId())
					RadioChoose = 2;
				else if(arg1 == rdteacher.getId())
					RadioChoose = 3;
				else if(arg1 == rdunit.getId())
					RadioChoose = 4;				
				//Toast.makeText(getApplicationContext(), RadioChoose+"", Toast.LENGTH_SHORT).show();			
			}
		});		

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2){
					Intent intent = new Intent(QueryCourseTableActivity.this,ShowCourseQueryDetailActivity.class);
					startActivity(intent);
				}
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("没有您要查找的记录，请确认搜索范围及方式");

			}
		};


	}


	private int CheckRadio(String s){
		if(s.equals("选课序号"))
			return 1;
		if(s.equals("课程名称"))
			return 2;
		if(s.equals("任课教师"))
			return 3;
		if(s.equals("开课单位"))
			return 4;
		return 0;
	}



	public void jumptonext(View v){
		//检查输入是否为空
		if(CheckData() != 0){
			Toast.makeText(getApplicationContext(), "请您输入具体的内容", Toast.LENGTH_SHORT).show();
			return;
		}	
		//将radiobutton的值记录  RadioChoose 是选中第几个的值。		
		new Thread() {
			public void run() {
				Looper.prepare(); 
				boolean res=getResults();
				if(res)              //可以成功获取内容
					handler.sendEmptyMessage(2);
				else 
					handler.sendEmptyMessage(-2);
				Looper.loop(); 
			};
		}.start();	

	}

	private int CheckData(){
		if(Query.getText().toString().equals(""))
			return 1;
		return 0;
	}

	//查找内容
	public boolean getResults(){

		try {
			URL url = new URL("http://jwc.nankai.edu.cn/apps/xksc/index.asp"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("POST"); //设定请求方式
			Global.conn.setDoOutput(true);
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			StringBuffer params = new StringBuffer();
			params.append("strsearch=").append(Query.getText().toString()).append("&radio=").append(RadioChoose).append("&Submit=%CC%E1%BD%BB");
			System.out.print(params.toString());
			byte[] bypes = params.toString().getBytes("gb2312");

			Global.conn.connect(); //建立到远程对象的实际连接

			OutputStream os=Global.conn.getOutputStream();
			os.write(bypes);   // 输入参数
			os.flush();
			os.close(); 

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.print(Global.conn.getResponseCode());
				System.out.println("网络错误异常！!!!");
				return  false;
			}else{				

				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","http://jwc.nankai.edu.cn/apps/xksc/index.asp");
				System.out.print(doc);
				Global.courseQueryDetails.clear();

				if(!doc.select("title:contains(课程查询)").text().equals("课程查询")){              //错误处理
					return false;
				}

				Elements form=doc.select("form").select("p");
				String string[]=form.last().text().split("/");
				Global.page_num=Integer.parseInt(string[1].trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("这是异常！");
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}	

		for(int k=1;k<= Global.page_num;k++){
			try {
				URL url = new URL("http://jwc.nankai.edu.cn/apps/xksc/index.asp"); //创建URL对象
				//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
				Global.conn = (HttpURLConnection) url.openConnection();
				Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
				Global.conn.setRequestMethod("POST"); //设定请求方式
				Global.conn.setDoOutput(true);
				Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				StringBuffer params = new StringBuffer();
				params.append("strsearch=").append(Query.getText().toString()).append("&radio=").append(RadioChoose).append("&Page=").append(k).append("&Submit=%CC%E1%BD%BB");
				System.out.print(params.toString());
				byte[] bypes = params.toString().getBytes("gb2312");

				Global.conn.connect(); //建立到远程对象的实际连接

				OutputStream os=Global.conn.getOutputStream();
				os.write(bypes);   // 输入参数
				os.flush();
				os.close(); 

				//判断是否正常响应数据 
				if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					System.out.print(Global.conn.getResponseCode());
					System.out.println("网络错误异常！!!!");
					return  false;
				}else{				

					Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","http://jwc.nankai.edu.cn/apps/xksc/index.asp");

					if(!doc.select("title:contains(课程查询)").text().equals("课程查询")){              //错误处理
						return false;
					}

					//我真的是天才啊啊
					CourseQueryDetail courseQueryDetail=null;
					Elements trs = doc.select("tbody tbody").select("tr");
					for(int i = 1;i<trs.size();i++){
						Elements tds = trs.get(i).select("td");
						courseQueryDetail=new CourseQueryDetail();
						for(int j = 0;j<tds.size();j++){
							String linkText = tds.get(j).text();
							switch (j) {
							case 0:
								courseQueryDetail.setCourseCode(linkText);
								break;
							case 1:
								courseQueryDetail.setCourseName(linkText);
								break;
							case 2:
								courseQueryDetail.setPlanPeopleNumber(linkText);
								break;
							case 3:
								courseQueryDetail.setRestrictedPeopleNumber(linkText);
								break;
							case 4:
								courseQueryDetail.setCourseTeacherName(linkText);
								break;
							case 5:
								courseQueryDetail.setCourseType(linkText);
								break;
							case 6:
								courseQueryDetail.setCourseWeekDay(linkText);
								break;
							case 7:
								courseQueryDetail.setCourseTime(linkText);
								break;
							case 8:
								courseQueryDetail.setCourseBeginEndWeek(linkText);
								break;
							case 9:
								courseQueryDetail.setCourseRoom(linkText);
								break;
							case 10:
								courseQueryDetail.setCourseUnit(linkText);
								break;
							default:
								break;
							}
						}
						Global.courseQueryDetails.add(courseQueryDetail);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("这是异常！");
				return false;
			} finally {
				if (Global.conn != null) {
					Global.conn.disconnect(); //中断连接
				}
			}	
		}

		return true;
	}

	
	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.query_course_table, menu);
		return true;
	}

}
