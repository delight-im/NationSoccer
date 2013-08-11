package im.delight.soccer;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.WindowManager;

/**
 * Base class for PreferenceScreens with auto-updating summaries
 * <p>
 * Only onCreate() must be overriden to specify the XML resource and initialize mAutoSummaryFields and mPreferenceEntries
 */
public abstract class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	protected String[] mAutoSummaryFields;
	protected Preference[] mPreferenceEntries;
	protected MyApp mApp;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    initFieldsAndXML();
	    mPreferenceEntries = new Preference[mAutoSummaryFields.length];
	    for (int i = 0; i < mAutoSummaryFields.length; i++) {
	    	mPreferenceEntries[i] = getPreferenceScreen().findPreference(mAutoSummaryFields[i]);
	    }
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    mApp = MyApp.getInstance();
	}
	
	/**
	 * Must load the correct XML preferences resource
	 * <p>
	 * Has to initialize mAutoSummaryFields
	 */
	protected abstract void initFieldsAndXML();
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
        super.onResume();
	    for (int i = 0; i < mAutoSummaryFields.length; i++) {
	    	updateSummary(mAutoSummaryFields[i]); // initialization
	    }
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this); // register change listener
        mApp.setMusicEnabled(mApp.getVolumeMode() == MyApp.VOLUME_ALL);
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); // unregister change listener
        mApp.setMusicEnabled(false);
    }
    
    private void updateSummary(String key) {
	    for (int i = 0; i < mAutoSummaryFields.length; i++) {
	    	if (key.equals(mAutoSummaryFields[i])) {
	    		if (mPreferenceEntries[i] instanceof EditTextPreference) {
	    			final EditTextPreference currentPreference = (EditTextPreference) mPreferenceEntries[i];
	    			mPreferenceEntries[i].setSummary(currentPreference.getText());
	    		}
	    		else if (mPreferenceEntries[i] instanceof ListPreference) {
	    			final ListPreference currentPreference = (ListPreference) mPreferenceEntries[i];
	    			mPreferenceEntries[i].setSummary(currentPreference.getEntry());
	    		}
	    		break;
	    	}
	    }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	updateSummary(key);
    }
    
    @Override
    public void onBackPressed() {
    	mApp.setMusicIsContinuing(true);
    	finish();
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	mApp.setMusicIsContinuing(true);
    	finish();
		return true;
	}
	
}
