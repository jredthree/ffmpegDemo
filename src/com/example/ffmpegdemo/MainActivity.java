package com.example.ffmpegdemo;

import cn.dennishucd.FFmpegNative;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


@SuppressLint("NewApi") public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  

		setContentView(R.layout.activity_main);  

		TextView tv = (TextView)this.findViewById(R.id.textview_hello);  

		FFmpegNative ffmpeg = new FFmpegNative();  
		int codecID = 28; //28 is the H264 Codec ID  

		int res = ffmpeg.avcodec_find_decoder(codecID);  

		if(res ==0) {  
			tv.setText("Success!");  
		}  
		else{  
			tv.setText("Failed!");  
		}  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
