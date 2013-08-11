package im.delight.soccer.settings;

import im.delight.soccer.PreferencesActivity;
import im.delight.soccer.R;

public class Main extends PreferencesActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void initFieldsAndXML() {
	    addPreferencesFromResource(R.xml.preferences_main);
	    mAutoSummaryFields = new String[] { "setting_game_end", "setting_game_time", "setting_game_goals", "setting_sound" };
	}

}
