package com.omt.syncpad;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;



public class SoundManager {
	private static SoundPool soundPool;
	private static HashMap<Integer, Integer> soundPoolMap;
	private static AudioManager  audioManager;	
	boolean SOUND_PLAY = false;
	
	SoundManager() {}
	
	public static void initSound(Context context) {
	    soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0); // max stream settato a 10
	    soundPoolMap = new HashMap<Integer, Integer>(); 
	    audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE); 	    
		soundPoolMap.put(1, soundPool.load(context, R.raw.kick, 1));
		soundPoolMap.put(2, soundPool.load(context, R.raw.snare, 1));
		soundPoolMap.put(3, soundPool.load(context, R.raw.tom, 1));
		soundPoolMap.put(4, soundPool.load(context, R.raw.hihat, 1));
	}
	
	public static void playSound(int index, int vol) {
	     float streamVolume = vol / 1024;
	     soundPool.play(soundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1); 

	}
	
	public static void stopSound(int index) {
		soundPool.stop(soundPoolMap.get(index));
	}

	public static void cleanup()
	{
		soundPool.release();
		soundPool = null;
	    soundPoolMap.clear();
	    audioManager.unloadSoundEffects();
	    //_instance = null;
	    
	}

}
