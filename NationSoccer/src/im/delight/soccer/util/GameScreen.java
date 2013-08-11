package im.delight.soccer.util;

import org.andengine.ui.activity.BaseGameActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import im.delight.soccer.R;

public abstract class GameScreen extends BaseGameActivity {
	
	public static final String EXTRA_MATCH = "match";
	public static final int PHRASE_GOLDEN_GOAL = 1;
	public static final int PHRASE_GOAL = 2;
	public static final int PHRASE_OUT = 3;
	public static final int PHRASE_TAP_TO_LEAVE = 4;
	public static final int PHRASE_YOU_LOST = 5;
	public static final int PHRASE_YOU_WON = 6;
	public static final int PHRASE_GAME_TIME_DEFAULT = 7;
	public static final int PHRASE_GAME_GOALS_DEFAULT = 8;
	public static final int PHRASE_GAME_END_DEFAULT = 9;
	public static final String PREFERENCE_GAME_END = "setting_game_end";
	public static final String PREFERENCE_GAME_TIME = "setting_game_time";
	public static final String PREFERENCE_GAME_GOALS = "setting_game_goals";
	public static final int FIELD_WIDTH = 3584;
	public static final int FIELD_HEIGHT = 512;
	public static final int CAMERA_WIDTH = 854;
	public static final int CAMERA_HEIGHT = 512;
	public static final int FRAMES_PER_SECOND = 40;
	protected boolean mGameFinished;
	protected SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public String getPhrase(final int phraseID) {
		switch (phraseID) {
			case PHRASE_GOLDEN_GOAL: return getString(R.string.golden_goal);
			case PHRASE_GOAL: return getString(R.string.goal_capital);
			case PHRASE_OUT: return getString(R.string.out_capital);
			case PHRASE_TAP_TO_LEAVE: return getString(R.string.tap_to_leave);
			case PHRASE_YOU_WON: return getString(R.string.you_won);
			case PHRASE_YOU_LOST: return getString(R.string.you_lost);
			case PHRASE_GAME_TIME_DEFAULT: return getString(R.string.setting_game_time_default);
			case PHRASE_GAME_GOALS_DEFAULT: return getString(R.string.setting_game_goals_default);
			case PHRASE_GAME_END_DEFAULT: return getString(R.string.setting_game_end_default);
			default: return "";
		}
	}
	
	public String getPreference(final String preferenceName, final String defaultValue) {
		return mPrefs.getString(preferenceName, defaultValue);
	}
	
	public void setGameFinished() {
		mGameFinished = true;
	}

}
