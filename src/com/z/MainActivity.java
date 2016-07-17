package com.z;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.PatternSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.z.po.*;


public class MainActivity extends Activity {

	public EditText UserName;
	public EditText PassWD;
	public EditText ValidCode;
	public CheckBox Remember;
	public ImageView imageView;

	private String isMemory = "";
	private String File = "saveUserNamePwd";
	private SharedPreferences sp = null;
	private Handler handler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		UserName  = (EditText)findViewById(R.id.page1_login_user_edit);
		PassWD    = (EditText)findViewById(R.id.page1_login_passwd_edit);
		ValidCode = (EditText)findViewById(R.id.page1_login_valid_edit);
		Remember  = (CheckBox)findViewById(R.id.page1_checbox1);
		imageView = (ImageView) findViewById(R.id.page1_valid);
		sp = getSharedPreferences(File,MODE_PRIVATE);
		isMemory = sp.getString("isMemory", "NO");

		if(isMemory.equals("YES"))
		{
			UserName.setText(sp.getString("name", ""));
			PassWD.setText(sp.getString("password",""));
			if(sp.getString("isChecked", "").equals("YES"))
				Remember.setChecked(true);
			else 
				Remember.setChecked(false);
		}
		Editor edit = sp.edit();
		edit.putString("name", UserName.getText().toString());
		edit.putString("password", PassWD.getText().toString());
		edit.commit();

		//操作线程的handler
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2){
					RememberService();
					Intent intent = new Intent(MainActivity.this, SecondActivity.class);
					startActivity(intent);
					finish();
				}
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("登陆失败");
			}
		};
		
		new Thread() {
			public void run() {
				Looper.prepare(); 
				getCookie();
				handler.sendEmptyMessage(1);
				Looper.loop(); 
			};
		}.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//登陆界面
	public void GotoSecond(View v)
	{
		int i;
		//进行一下检查，检查数据是否完整
		if( ( i =DataCheck()) !=0)
		{
			ErrorHandle(i);
			return ;
		}

		Global.PassWord=PassWD.getText().toString();
		Global.UserName=UserName.getText().toString();
		Global.checkcode_text=ValidCode.getText().toString();
		new Thread() {
			public void run() {
				Looper.prepare();
				boolean res=getLogin();				
				if(res)			
					handler.sendEmptyMessage(2);
				else 
					handler.sendEmptyMessage(-2);
				Looper.loop();
			};
		}.start();
	}

	//登录检查
	private int DataCheck()
	{
		if(UserName.getText().toString().equals(""))
			return 1;
		if(PassWD.getText().toString().equals(""))
			return 2;
		if(ValidCode.getText().toString().equals(""))
			return 3;
		return 0;
	}

	public void ErrorHandle(int i)
	{
		switch(i)
		{
		case 1:
			Toast.makeText(getApplicationContext(), "账号未输入", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			Toast.makeText(getApplicationContext(), "密码未输入", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			Toast.makeText(getApplicationContext(), "验证码未输入", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public void ShowErrorInf(String s)
	{
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	//清空数据
	public void ClearData(View v)
	{
		//把三个框的数据清空。
		UserName.setText("");
		PassWD.setText("");
		ValidCode.setText("");		
	}	

	//记住密码
	public void RememberOrNot(View v)
	{
		RememberService();
	}
	public void RememberService()
	{
		if(Remember.isChecked())
		{
			if(sp == null)
			{
				sp = getSharedPreferences(File, MODE_PRIVATE);
			}
			Editor edit = sp.edit();
			edit.putString("name", UserName.getText().toString());
			edit.putString("password", PassWD.getText().toString());
			edit.putString("isMemory", "YES");
			edit.putString("isChecked", "YES");
			edit.commit();
		}
		else if(!Remember.isChecked())
		{
			if(sp == null)
			{
				sp = getSharedPreferences(File, MODE_PRIVATE);
			}
			Editor edit = sp.edit();
			edit.putString("isMemory", "NO");
			edit.putString("isChecked", "NO");
			edit.commit();
		}

	}

	
	//请求网页Cookie和Validcode
	public boolean getCookie() {
		try {
			URL url = new URL(Global.urlString); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(5000); //设置连接超时为5秒
			Global.conn.setRequestMethod("GET"); //设定请求方式
			Global.conn.connect(); //建立到远程对象的实际连接

			//返回打开连接读取的输入流
			DataInputStream dis = new DataInputStream(Global.conn.getInputStream());  
			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				ShowErrorInf("网络错误异常！!!!");
				return false;
			}else{
				//获取cookie 
				Global.cookie=Global.conn.getHeaderField("Set-Cookie"); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			ShowErrorInf("这是异常！");
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}
		
		if(getValicate()){     //请求验证码
			return true;
		}else{
			return false;
		}
	}

	
	//获取验证码
	public boolean getValicate(){
		//获取验证码
		try {
			URL url = new URL(Global.urlString+"/ValidateCode"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("GET"); //设定请求方式
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Connection", "Keep-Alive");
			Global.conn.setRequestProperty("Cookie", Global.cookie);
			Global.conn.connect(); //建立到远程对象的实际连接

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				ShowErrorInf("网络错误异常！!!!");
				return  false;
			}else{				
				//获取验证码
				InputStream inStream=Global.conn.getInputStream();
				BitmapFactory.Options opt = new BitmapFactory.Options(); 
				//获取资源图片  
				Bitmap bitmap = BitmapFactory.decodeStream(inStream,null,opt); 
				//调整大小
			    Matrix matrix = new Matrix(); 
				matrix.postScale(4.0f,4.0f); //长和宽放大缩小的比例
				Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
				 
				imageView.setImageBitmap(resizeBmp);//显示图片
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}
		return false;
	}
	

	//登录
	public boolean getLogin() {
		
		int result=0;
		//提交post
		try {
			URL url = new URL(Global.urlString+"/stdloginAction.do"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("POST"); //设定请求方式
			Global.conn.setDoOutput(true);
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Cookie", Global.cookie);

			StringBuffer params = new StringBuffer();
			// 表单参数与get形式一样
			params.append("operation=").append("&usercode_text=").append(Global.UserName).append("&userpwd_text=")
			.append(Encrypt(Global.PassWord)).append("&checkcode_text=").append(Global.checkcode_text).append("&submittype=%C8%B7+%C8%CF");
			System.out.print(params.toString());
			byte[] bypes = params.toString().getBytes("gb2312");

			Global.conn.connect(); //建立到远程对象的实际连接
			//返回打开连接读取的输入流
			OutputStream os=Global.conn.getOutputStream();
			os.write(bypes);   // 输入参数
			os.flush();
			os.close();  

			//判断是否正常响应数据 
			if (Global.conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				ShowErrorInf("登录失败！!!!");
				return  false;
			}else{			
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/xsxk/studiedAction.do");
				System.out.print(doc);
				//登陆失败后返回值
				if(doc.select("div:contains(密码错误)").text().equals("用户不存在或密码错误！")){
					result=-1;								
					ShowErrorInf("用户不存在或密码错误！");
					
				}else if(doc.select("div:contains(正确的验证码)").text().equals("请输入正确的验证码！")){
					result=-1;
					ShowErrorInf("请输入正确的验证码！");
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
			ShowErrorInf("登录异常！");
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}
		
		if(result==0)
			return true;
		else{		
			getValicate();
		    return false;
		}
	}


	//rsa加密算法
	public static String Encrypt(String password) throws IOException, ClassNotFoundException{

		//publicKey;		
		Global.publicKey=new BigInteger(Global.publicKeyString, 16);
		System.out.print(new BigInteger(Global.publicKeyString, 16).toString()+"\n");

		//reverse
		String pwdString="";
		for(int i=0;i<password.length();i++){
			pwdString+=password.charAt(password.length()-i-1);		
		}

		//change to Ascii code
		byte[] bypes=pwdString.getBytes("gb2312");

		//encrypt
		BigInteger m = new BigInteger(bypes);
		BigInteger n=new BigInteger("65537",10);		
		BigInteger c = m.modPow(n,Global.publicKey);

		//change to hex
		BigInteger hex=new BigInteger("16",10);	
		String csString="";
		BigInteger zores=new BigInteger("0",10);
		while(!c.equals(zores)){
			BigInteger hee=c.mod(hex);
			BigInteger c1=c.divide(hex);
			//赋值是否成功
			c=c1;      			
			String ch=hee.toString();
			Integer integer=Integer.parseInt(ch);		
			if(integer.intValue()>=10){
				int a=(integer-10)+'a';
				csString=(char)a+csString;
			}else{
				csString=ch+csString;
			}						
		}		
		System.out.println("密文是："+csString);						
		return csString;    //返回密文
	}
    
}
