package com.omt.syncpad;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DemoKitLaunch extends Activity {
	static final String TAG = "DemoKitLaunch";

	static Intent createIntent(Activity activity) {
		
		Intent intent;
		Log.i(TAG, "starting phone ui");
		intent = new Intent(activity, BaseActivity.class);
		
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = createIntent(this);

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "unable to start DemoKit activity", e);
		}
		finish();
	}
}
