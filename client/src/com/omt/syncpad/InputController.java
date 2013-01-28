package com.omt.syncpad;

import java.text.DecimalFormat;
//import java.util.ArrayList;

//import android.graphics.drawable.Drawable;
//import android.widget.ImageView;
import android.widget.TextView;

public class InputController extends AccessoryController {
	private TextView mTextView;
	private final DecimalFormat mKickValueFormatter = new DecimalFormat("##.#");
	
	InputController(DemoKitActivity hostActivity) {
		super(hostActivity);
		mTextView = (TextView) findViewById(R.id.textbox);
	}

	@Override
	protected void onAccesssoryAttached() {
		
	}

	public void setKickValue(int kickValueFromArduino) {
		mTextView.append(mKickValueFormatter.format(
				100.0 * kickValueFromArduino / 1024.0));
		
	}
	
	public void setSnareValue(int s) {
	}
	
	public void setTomValue(int s) {
	}
	
	public void setHihatValue(int s) {
	}

	public void onKickChange(int kickValue) {
		setKickValue(kickValue);
	}

}
