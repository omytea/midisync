package com.omt.syncpad;

import java.io.File;
//import java.io.IOException;

//import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

import android.app.Activity;
//import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.TextView;

public class MainActivity extends Activity {

	
	private static final String TAG = "MidiSync";

	private static final String uploadFile = "/mnt/emmc/Music/timestretched.mid";
	private static final String fileName = "timestretched.mid";
	private static final String extName = "mid";
	
	private static final String url = "http://omychatsubo.appspot.com/upload";
	//private TextView mTextView;
	private Button mButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//mTextView = (TextView) findViewById(R.id.textView1);
		mButton = (Button)findViewById(R.id.button1);
		mButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new Thread(backgroundThread).start();
			}
		});
	}

	Runnable backgroundThread = new Runnable(){
		public void run() {
			postFile();
		}
	};
		public void postFile() {
			Log.i(TAG, "Posting file");
			try {
				HttpPost httppost = new HttpPost(url);
				File file = new File(uploadFile);
				MultipartEntity entity = new MultipartEntity();
				entity.addPart("filename", new StringBody(fileName));
				entity.addPart("fileext", new StringBody(extName));
				entity.addPart("upfile", new FileBody(file));
				HttpClient httpclient = new DefaultHttpClient();
				httppost.setEntity(entity);
				httpclient.execute(httppost);
				//		    HttpResponse response = httpclient.execute(httppost);
				//Do something with response...
				Log.i(TAG, "File posted");

			} catch (Exception e) {
				// 	show error
				e.printStackTrace();
				return;
			}
		}
	
	}

