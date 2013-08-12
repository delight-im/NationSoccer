package im.delight.soccer.settings;

import im.delight.soccer.R;
import im.delight.soccer.util.PreferencesActivity;

public class Main extends PreferencesActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void initFieldsAndXML() {
	    addPreferencesFromResource(R.xml.preferences_main);
	    mAutoSummaryFields = new String[] { "setting_game_end", "setting_game_time", "setting_game_goals", "setting_sound" };
	}

}
