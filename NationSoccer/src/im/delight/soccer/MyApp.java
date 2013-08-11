package im.delight.soccer;

import im.delight.soccer.R;
import im.delight.soccer.util.Player;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.preference.PreferenceManager;

public class MyApp extends Application {

	public static final int VOLUME_NONE = 0;
	public static final int VOLUME_SOUND = 1;
	public static final int VOLUME_ALL = 2;
	public static final String PREFERENCE_VOLUME = "setting_sound";
	public static final String EXTRA_REQUEST_CODE = "requestCode";
	public static final String PACKAGE_NAME = "im.delight.soccer";
	/** Instance reference for singleton pattern */
	private static MyApp mInstance;
	private static Player[] mPlayerList;
	/** Manages all sound files and can be used to play single sounds */
	private SoundPool mSoundPool;
	private boolean mMusicIsContinuing;
	private AudioManager mAudioManager;
	private boolean mLastMusicPlayFailed;
	private SharedPreferences mPrefs;
	private int mSoundID_Music;
	private int mStreamID_Music;
	
	@Override
	public void onCreate() {
		mInstance = this;
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		loadSounds();
	}
	
    @SuppressLint("NewApi")
	public static void savePreferences(SharedPreferences.Editor editor) {
    	if (editor != null) {
	    	if (Build.VERSION.SDK_INT < 9) {
	    		editor.commit();
	    	}
	    	else {
	    		editor.apply();
	    	}
    	}
    }
	
	public static Player[] getPlayerList() {
		if (mPlayerList == null) {
	    	mPlayerList = new Player[Player.PLAYERS_TOTAL];
	    	mPlayerList[0] = new Player(Player.COUNTRY_CHINA, 0.35f, 0.35f, 0.35f);
	    	mPlayerList[1] = new Player(Player.COUNTRY_CAMEROON, 0.75f, 0.15f, 0.25f);
	    	mPlayerList[2] = new Player(Player.COUNTRY_JAMAICA, 0.85f, 0.15f, 0.25f);
	    	mPlayerList[3] = new Player(Player.COUNTRY_RUSSIA, 0.55f, 0.45f, 0.35f);
	    	mPlayerList[4] = new Player(Player.COUNTRY_USA, 0.45f, 0.55f, 0.45f);
	    	mPlayerList[5] = new Player(Player.COUNTRY_ECUADOR, 0.85f, 0.35f, 0.45f);
	    	mPlayerList[6] = new Player(Player.COUNTRY_TURKEY, 0.55f, 0.55f, 0.55f);
	    	mPlayerList[7] = new Player(Player.COUNTRY_SOUTH_KOREA, 0.65f, 0.45f, 0.55f);
	    	mPlayerList[8] = new Player(Player.COUNTRY_BELGIUM, 0.75f, 0.55f, 0.45f);
	    	mPlayerList[9] = new Player(Player.COUNTRY_NIGERIA, 0.75f, 0.45f, 0.55f);
	    	mPlayerList[10] = new Player(Player.COUNTRY_GREECE, 0.55f, 0.45f, 0.75f);
	    	mPlayerList[11] = new Player(Player.COUNTRY_URUGUAY, 0.65f, 0.55f, 0.55f);
	    	mPlayerList[12] = new Player(Player.COUNTRY_UK, 0.55f, 0.75f, 0.45f);
	    	mPlayerList[13] = new Player(Player.COUNTRY_MEXICO, 0.75f, 0.55f, 0.55f);
	    	mPlayerList[14] = new Player(Player.COUNTRY_JAPAN, 0.65f, 0.65f, 0.65f);
	    	mPlayerList[15] = new Player(Player.COUNTRY_PORTUGAL, 0.65f, 0.55f, 0.75f);
	    	mPlayerList[16] = new Player(Player.COUNTRY_ARGENTINA, 0.75f, 0.65f, 0.65f);
	    	mPlayerList[17] = new Player(Player.COUNTRY_NETHERLANDS, 0.75f, 0.65f, 0.75f);
	    	mPlayerList[18] = new Player(Player.COUNTRY_FRANCE, 0.65f, 0.75f, 0.75f);
	    	mPlayerList[19] = new Player(Player.COUNTRY_IVORY_COAST, 0.85f, 0.65f, 0.75f);
	    	mPlayerList[20] = new Player(Player.COUNTRY_ITALY, 0.75f, 0.75f, 0.75f);
	    	mPlayerList[21] = new Player(Player.COUNTRY_BRAZIL, 0.85f, 0.75f, 0.75f);
	    	mPlayerList[22] = new Player(Player.COUNTRY_GERMANY, 0.75f, 0.85f, 0.85f);
	    	mPlayerList[23] = new Player(Player.COUNTRY_SPAIN, 0.85f, 0.85f, 0.85f);
		}
		return mPlayerList;
	}
	
	public void setMusicIsContinuing(boolean enabled) {
		mMusicIsContinuing = getVolumeMode() == MyApp.VOLUME_ALL && enabled;
	}
	
    public int getVolumeMode() {
    	if (mPrefs == null) {
    		return VOLUME_ALL;
    	}
    	else {
    		try {
    			return Integer.parseInt(mPrefs.getString(PREFERENCE_VOLUME, getString(R.string.setting_sound_default)));
    		}
    		catch (Exception e) {
    			return VOLUME_ALL;
    		}
    	}
    }
    
    public void switchVolumeMode(int oldValue) {
    	if (mPrefs != null) {
    		int newValue = (oldValue+2) % 3;
    		if (newValue == VOLUME_ALL) {
    			setMusicEnabled(true);
    		}
    		else {
    			setMusicEnabled(false);
    		}
			SharedPreferences.Editor prefsEdit = mPrefs.edit();
			prefsEdit.putString(PREFERENCE_VOLUME, String.valueOf(newValue));
			prefsEdit.commit();
    	}
    }
	
	public void setMusicEnabled(boolean enabled) {
		synchronized (this) {
			if (enabled) {
				if (!mMusicIsContinuing || mStreamID_Music == 0) {
					playMusic();
				}
				mMusicIsContinuing = false;
			}
			else {
				if (mStreamID_Music != 0) {
					if (!mMusicIsContinuing) {
						mSoundPool.stop(mStreamID_Music);
						mStreamID_Music = 0;
					}
				}
			}
			mLastMusicPlayFailed = false;
		}
	}
	
	private void loadSounds() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				if (status == 0) { // if successful
					if (soundPool != null && mLastMusicPlayFailed && sampleId == mSoundID_Music) {
						playMusic();
					}
				}
			}
        });
        new Thread(new Runnable() {
			public void run() {
				synchronized (mSoundPool) {
					try {
						mSoundID_Music = mSoundPool.load(MyApp.this, R.raw.music_latin, 1);
					}
					catch (Exception e) { }
				}
			}
        }).start();
	}
	
	public void playMusic() {
		new Thread() {
			public void run() {
				try {
					if (mSoundID_Music != 0) {
						float actualVolume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
						float maxVolume = (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
						float volume = actualVolume / maxVolume;
						if (mSoundPool != null) {
							int streamID = mSoundPool.play(mSoundID_Music, volume, volume, 0, -1, 1.0f);
							synchronized (this) {
								mStreamID_Music = streamID;
								if (streamID == 0) {
									mLastMusicPlayFailed = true;
								}
							}
						}
					}
				}
				catch (Exception e) { }
			}
		}.start();
	}
	
	public static MyApp getInstance() {
		return mInstance;
	}

}
