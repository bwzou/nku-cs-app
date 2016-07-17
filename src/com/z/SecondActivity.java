package com.z;



import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.z.po.EmailUtils;
import com.z.po.Global;
import com.z.view.MyGridLayout;
import com.z.view.MyGridLayout.GridAdatper;
import com.z.view.MyGridLayout.OnItemClickListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends Activity {

	private Handler handler;
	MyGridLayout grid;
	int[] srcs = { R.drawable.actions_booktag, R.drawable.actions_comment,
			R.drawable.actions_order, R.drawable.actions_account,
			R.drawable.actions_cent, R.drawable.actions_weibo,
			R.drawable.actions_feedback, R.drawable.actions_about };
	String titles[] = { "退出登录", "培养计划", "信息查询", "学生选课", "学生评教", "修改密码", "反馈", "关于我们" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_second);
		grid = (MyGridLayout) findViewById(R.id.list);
		
		grid.setGridAdapter(new GridAdatper() {

			@Override
			public View getView(int index) {
				View view = getLayoutInflater().inflate(R.layout.actions_item,
						null);
				ImageView iv = (ImageView) view.findViewById(R.id.iv);
				TextView tv = (TextView) view.findViewById(R.id.tv);
				iv.setImageResource(srcs[index]);
				tv.setText(titles[index]);
				return view;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return titles.length;
			}
		});
		
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2){                   //进行页面跳转
					Intent intentM = new Intent(SecondActivity.this, MainActivity.class);
					startActivity(intentM);
					finish();
				}
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("退出失败");
			}
		};
		
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View v, int index) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "item="+index, 0).show();
				switch(index){
				case 0:
					GoBack();
					break;
				case 1:
					Intent intentD =new Intent(SecondActivity.this, DevelopPlanActivity.class);
					startActivity(intentD);
					break;
				case 2:
					Intent intentI = new Intent(SecondActivity.this, InformationQueryActivity.class);
					startActivity(intentI);
					break;
				case 3:
					Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
					startActivity(intent);
					break;

				case 4:
					Intent intentC = new Intent(SecondActivity.this, CommentActivity.class);
					startActivity(intentC);
					break;
				case 5:
					Intent intentCH = new Intent(SecondActivity.this, ChangePWActivity.class);
					startActivity(intentCH);
					break;
				case 6:                         //留言系统
					Intent intentS = new Intent(SecondActivity.this, SendEmailActivity.class);
            		startActivity(intentS);
            		break;                      
				case 7:
					Intent intentAd = new Intent(SecondActivity.this, AboutUsActivity.class);
					startActivity(intentAd);
					break;        		
				default:
					break;
				}
			}
		});
		
	}

	//logout
	private void GoBack()
	{
		new Thread() {
			public void run() {
				Looper.prepare();
				boolean res=goEixt();				
				if(res)			
					handler.sendEmptyMessage(2);
				else 
					handler.sendEmptyMessage(-2);
				Looper.loop();
			};
		}.start();
	}

	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}
	
	//退出请求
	public boolean goEixt(){
		//获取页面
		try {
			URL url = new URL(Global.urlString+"/exitAction.do"); //创建URL对象
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
				return false;
			}else{								
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/exitAction.do");
				System.out.print(doc);
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
}
