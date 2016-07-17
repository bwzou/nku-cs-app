package com.z;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.z.po.Global;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePWActivity extends Activity {

	private Handler handler;
	public EditText oldPasswd;
	public EditText newPasswd1;
	public EditText newPasswd2;

	public String oldPwd="";
	public String newPwd1="";
	public String newPwd2="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_pw);

		oldPasswd=(EditText) findViewById(R.id.change_old_passwd);
		newPasswd1=(EditText) findViewById(R.id.new_passwd);
		newPasswd2=(EditText) findViewById(R.id.new_passwd_valid_edit);
		
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					return;
				else if(msg.what==2){
					ShowErrorInf("修改密码成功！");                   //进行页面跳转
				}
				else if (msg.what == -1)
					ShowErrorInf("服务器请求异常");
				else if (msg.what == -2)
					ShowErrorInf("修改密码失败，原密码错误！");
			}
		};
	}

	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	public void ChangePassWD(View v) {
		oldPwd=oldPasswd.getText().toString();
		newPwd1=newPasswd1.getText().toString();
		newPwd2=newPasswd2.getText().toString();
		if(oldPwd.equals("")){
			ShowErrorInf("原密码不能为空");
			return;
		}else if(newPwd1.equals("")){
			ShowErrorInf("新密码不能为空");
			return;
		}else if(newPwd2.equals("")){
			ShowErrorInf("请再次输入新密码");
			return;
		}else if(newPasswd1.getText().toString().length() < 8){
			ShowErrorInf("现密码的长度至少为8位");
			return;
		}else if(!newPasswd1.getText().toString().equals(newPasswd2.getText().toString())){
			ShowErrorInf("两次输入的密码不一致");
			return;
		}
		
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare(); 
				boolean res=changePwd();
				if(res)                             //可以成功获取内容
					handler.sendEmptyMessage(2);
				else 
					handler.sendEmptyMessage(-2);
				Looper.loop(); 
			}
		}.start();
	}

	public void ClearData(View v){
		oldPasswd.setText("");
		newPasswd1.setText("");
		newPasswd2.setText("");
	}
	
	//change password
	public boolean changePwd(){
		//提交post
		try {
			URL url = new URL(Global.urlString+"/stdchangepwAction.do"); //创建URL对象
			//返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			Global.conn = (HttpURLConnection) url.openConnection();
			Global.conn.setConnectTimeout(50000); //设置连接超时为5秒
			Global.conn.setRequestMethod("POST"); //设定请求方式
			Global.conn.setDoOutput(true);
			Global.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			Global.conn.setRequestProperty("Cookie", Global.cookie);

			StringBuffer params = new StringBuffer();
			// 表单参数与get形式一样
			params.append("operation=").append("&olduserpwd_text=").append(Encrypt(oldPwd)).append("&newuserpwd1_text=")
			.append(Encrypt(newPwd1)).append("&newuserpwd2_text=").append(Encrypt(newPwd2));
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
				Document doc = Jsoup.parse(Global.conn.getInputStream(),"gb2312","/stdchangepwAction.do");
				System.out.print(doc);
				//登陆失败后返回值
				if(doc.select("div:contains(密码修改)").text().equals("密码修改成功！")){						
					return true;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			ShowErrorInf("网络异常！");
		} finally {
			if (Global.conn != null) {
				Global.conn.disconnect(); //中断连接
			}
		}
		return false;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_pw, menu);
		return true;
	}

}
