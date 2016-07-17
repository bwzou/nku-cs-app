package com.z;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DevelopPlanActivity extends Activity {

	private ListView listView;
	ArrayList<HashMap<String, Object>> listItem ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_third_server);
		
		
		listView =(ListView)findViewById(R.id.ThirdList);
		
		listItem = new ArrayList<HashMap<String, Object>>();  
	    
	       
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
					case 2:
						break;
					default:
						break;
				}
			}
		});
	}
	public void AddDate()
	{
		 
		 String[] names = new String[]{"院系设置","专业设置","课程设置","教学计划","毕业条件","外校学习计划课程","外校课程学分认定"};
		 for(String s:names)
		 {
			 HashMap<String, Object> map = new HashMap<String, Object>();
			 map.put("ItemImage", s);//   
		      listItem.add(map);
		 }
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.develop_plan, menu);
		return true;
	}

}
