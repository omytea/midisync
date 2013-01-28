/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omt.syncpad;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
//import android.widget.SeekBar;
//import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class DemoKitActivity extends Activity implements Runnable {
	private static final String TAG = "MidiSync";
	private static final String uploadFile = "/mnt/emmc/Music/timestretched.mid";
	private static final String fileName = "timestretched.mid";
	private static final String extName = "mid";
	private static final String url = "http://omychatsubo.appspot.com/upload";

	private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;

	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;

	protected static final int MESSAGE_KICK = 1;
	protected static final int MESSAGE_SNARE = 2;
	protected static final int MESSAGE_TOM = 3;
	protected static final int MESSAGE_HIHAT = 4;
	

	protected class KickMsg {
		private int kick;
		public KickMsg(int kick) {
			this.kick = kick;
		}
		public int getKick() {
			return kick;
		}
	}
	
	protected class SnareMsg {
		private int snare;
		public SnareMsg(int snare) {
			this.snare = snare;
		}
		public int getSnare() {
			return snare;
		}
	}
	
	protected class TomMsg {
		private int tom;
		public TomMsg(int tom) {
			this.tom = tom;
		}
		public int getTom() {
			return tom;
		}
	}
	
	protected class HihatMsg {
		private int hihat;
		public HihatMsg(int hihat) {
			this.hihat = hihat;
		}
		public int getHihat() {
			return hihat;
		}
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(mAccessory);
		}

		setContentView(R.layout.main);

		enableControls(false);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		Intent intent = getIntent();
		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}

	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "DemoKit");
			thread.start();
			Log.d(TAG, "accessory opened");
			enableControls(true);
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		enableControls(false);

		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}

	protected void enableControls(boolean enable) {
	}
	
	private int composeInt(byte hi, byte lo) {
		int val = hi & 0xff;
		val *= 256;
		val += lo & 0xff;
		return val;
	}


	@Override
	public void run() {
		int ret = 0;
		byte[] buffer = new byte[16384];
		int i;

		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}

			i = 0;
			while (i < ret) {
				int len = ret - i;

				switch (buffer[i]) {
				case 0x1:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_KICK);
						m.obj = new KickMsg(composeInt(buffer[i + 1],
								buffer[i + 2]));
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
				case 0x2:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_SNARE);
						m.obj = new SnareMsg(composeInt(buffer[i + 1],
								buffer[i + 2]));
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
				case 0x3:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_TOM);
						m.obj = new TomMsg(composeInt(buffer[i + 1],
								buffer[i + 2]));
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
				case 0x4:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_HIHAT);
						m.obj = new HihatMsg(composeInt(buffer[i + 1],
								buffer[i + 2]));
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
					
				default:
					Log.d(TAG, "unknown msg: " + buffer[i]);
					i = len;
					break;
				}
			}

		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_KICK:
				KickMsg k = (KickMsg) msg.obj;
				handleKickMessage(k);
				break;
			case MESSAGE_SNARE:
				SnareMsg s = (SnareMsg) msg.obj;
				handleSnareMessage(s);
				break;
			case MESSAGE_TOM:
				TomMsg t = (TomMsg) msg.obj;
				handleTomMessage(t);
				break;
			case MESSAGE_HIHAT:
				HihatMsg h = (HihatMsg) msg.obj;
				handleHihatMessage(h);
				break;

			}
		}
	};

	public void sendCommand(byte command, byte target, int value) {
		byte[] buffer = new byte[3];
		if (value > 255)
			value = 255;

		buffer[0] = command;
		buffer[1] = target;
		buffer[2] = (byte) value;
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
	}

	protected void handleKickMessage(KickMsg l) {
	}
	protected void handleSnareMessage(SnareMsg l) {
	}
	protected void handleTomMessage(TomMsg l) {
	}
	protected void handleHihatMessage(HihatMsg l) {
	}

    Runnable backgroundThread = new Runnable(){
	    @Override
		public void run() {
		postFile();
	    }};

	protected void postFile() {
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
		    //HttpResponse response = httpclient.execute(httppost);
		    //Do something with response...
		    Log.i(TAG, "File posted");

		} catch (Exception e) {
		    // show error
		    e.printStackTrace();
		    return;
		}
	}

}
