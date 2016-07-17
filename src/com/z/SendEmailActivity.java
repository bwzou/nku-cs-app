package com.z;



import com.z.po.EmailUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendEmailActivity extends Activity {
	
	public EditText EmailContent;
	public Button Ack;
	public Button CleanData;
	public Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_email);
		
		//EmailAddress = (EditText)findViewById(R.id.send_email_address);
		EmailContent = (EditText)findViewById(R.id.send_email_content);
		Ack          = (Button)findViewById(R.id.send_email_ack);
		CleanData    = (Button)findViewById(R.id.send_email_clean);
		
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					ShowErrorInf("请填写内容！");
				else if(msg.what==2)
					ShowErrorInf("发送成功，感谢您的反馈！");
				else if (msg.what == -1)
					ShowErrorInf("提交反馈失败，请检查网路");
				else if (msg.what == -2)
					ShowErrorInf("提交反馈失败，请检查网路");
			}
		};
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_email, menu);
		return true;
	}
	

	public void Clean(View v){
		EmailContent.setText("");
	}
	
	public void SendEmail(View v){
		if(EmailContent.getText().toString().equals("")){
			handler.sendEmptyMessage(1);
			return;
		}		
		new Thread(){
			  public void run() {
				  boolean res=EmailUtils.SendEmail(EmailContent.getText().toString());
				  if(res)
					  handler.sendEmptyMessage(2);
				  else {
					  handler.sendEmptyMessage(-2);
				}
			  };
		  }.start();
	}

	public void ShowErrorInf(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}
	
}

