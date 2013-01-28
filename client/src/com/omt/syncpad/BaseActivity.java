package com.omt.syncpad;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class BaseActivity extends DemoKitActivity {

	private InputController mInputController;
	private SoundManager mSoundManager;
	private Button mButton;

	public BaseActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if (mAccessory != null) {
		showControls();
	    } else {
		hideControls();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Simulate");
		menu.add("Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == "Simulate") {
			showControls();
		} else if (item.getTitle() == "Quit") {
			finish();
			System.exit(0);
		}
		return true;
	}

	@Override
	protected void enableControls(boolean enable) {
		if (enable) {
			showControls();
		} else {
			hideControls();
		}
	}

	protected void hideControls() {
		setContentView(R.layout.no_device);
		mInputController = null;
	}

	protected void showControls() {
		setContentView(R.layout.main);

		mInputController = new InputController(this);
		mInputController.accessoryAttached();

		mButton = (Button)findViewById(R.id.button1);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    new Thread(backgroundThread).start();
			}});
		
		mSoundManager = new SoundManager();
		SoundManager.initSound(this);
	}

	@Override
	protected void handleKickMessage(KickMsg k) {
		if (mInputController != null) {
			mInputController.setKickValue(k.getKick());
		}
		if (mSoundManager != null) {
			SoundManager.playSound(MESSAGE_KICK, k.getKick());
		}
	}

	@Override
	protected void handleSnareMessage(SnareMsg s) {
		if (mInputController != null) {
			mInputController.setSnareValue(s.getSnare());
		}
		if (mSoundManager != null) {
			SoundManager.playSound(MESSAGE_SNARE, s.getSnare());
		}
	}

	@Override
	protected void handleTomMessage(TomMsg t) {
		if (mInputController != null) {
			mInputController.setTomValue(t.getTom());
		}
		if (mSoundManager != null) {
			SoundManager.playSound(MESSAGE_TOM, t.getTom());
		}
	}

	@Override
	protected void handleHihatMessage(HihatMsg h) {
		if (mInputController != null) {
			mInputController.setHihatValue(h.getHihat());
		}
		if (mSoundManager != null) {
			SoundManager.playSound(MESSAGE_HIHAT, h.getHihat());
		}
	}


}