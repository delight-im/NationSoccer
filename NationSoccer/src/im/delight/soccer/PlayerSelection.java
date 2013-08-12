package im.delight.soccer;

import im.delight.soccer.util.Player;
import im.delight.soccer.util.PlayerSelectionHandler;
import java.util.ArrayList;
import java.util.List;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

public class PlayerSelection extends SherlockFragmentActivity implements PlayerSelectionHandler {

	public static final String EXTRA_FIRST_PLAYER = "firstSelectedPlayer";
	public static final String EXTRA_PLAYER = "newSelectedPlayer";
	public static final int REQUEST_CODE_GET_TEAM_SELF = 1;
	public static final int REQUEST_CODE_GET_TEAM_OPPONENT = 2;
	public static final int REQUEST_CODE_GET_TOURNAMENT_TEAM = 3;
	public static final int REQUEST_CODE_GET_CAREER_TEAM = 4;
	private static final String PREFERENCE_LAST_SELECTION_INDEX_SELF = "default_last_index_self";
	private static final String PREFERENCE_LAST_SELECTION_INDEX_OPPONENT = "default_last_index_opponent";
	private int mPageCount;
	private ViewPager mPager; // pager object that handles swiping and animations
	private PagerAdapter mPagerAdapter; // adapter that holds all pages and their content
	private int mRequestCode;
	private SharedPreferences mPrefs;
	private int mLastSelectionIndexSelf;
	private int mLastSelectionIndexOpponent;
    
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		
		private List<Player> mItems;
		private int mRequestCode;

		public ScreenSlidePagerAdapter(final FragmentManager fm, final List<Player> items, final int requestCode) {
            super(fm);
            mItems = items;
            mPageCount = mItems.size();
            mRequestCode = requestCode;
        }

        @Override
        public Fragment getItem(final int position) {
        	return PlayerSelectionFragment.create(position, mPageCount, mItems.get(position), mRequestCode);
        }

        @Override
        public int getCount() {
            return mPageCount;
        }

    }
    
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) { // if user is in the first step handle the back key normally
        	finish();
        }
        else { // otherwise select the previous step
        	mPager.setCurrentItem(mPager.getCurrentItem()-1);
        }
    }
    
    public void navigateTo(int index) {
    	if (mPager != null && index >= 0 && index < mPageCount) {
    		mPager.setCurrentItem(index);
    	}
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	finish();
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.player_selection);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    	mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	try {
    		mLastSelectionIndexSelf = mPrefs.getInt(PREFERENCE_LAST_SELECTION_INDEX_SELF, 0);
    		mLastSelectionIndexOpponent = mPrefs.getInt(PREFERENCE_LAST_SELECTION_INDEX_OPPONENT, 0);
    	}
    	catch (Exception e) {
    		mLastSelectionIndexSelf = 0;
    		mLastSelectionIndexOpponent = 0;
    	}

    	mRequestCode = getIntent().getIntExtra(MyApp.EXTRA_REQUEST_CODE, 0);
    	
    	Player firstSelectedPlayer = getIntent().getParcelableExtra(EXTRA_FIRST_PLAYER);

    	final Player[] allPlayers = MyApp.getPlayerList();
    	final List<Player> playerList = new ArrayList<Player>(firstSelectedPlayer == null ? Player.PLAYERS_TOTAL : Player.PLAYERS_TOTAL-1);
    	Player player;
    	for (int p = 0; p < allPlayers.length; p++) {
    		if (!allPlayers[p].equals(firstSelectedPlayer)) {
    			player = new Player(allPlayers[p]);
    			if (mRequestCode == REQUEST_CODE_GET_CAREER_TEAM) { // in career mode
    				player.setSpeed(0.3f); // all teams start equally weak
    				player.setJump(0.3f);
    				player.setPower(0.3f);
    			}
    			playerList.add(player);
    		}
    	}

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), playerList, mRequestCode);
        mPager.setAdapter(mPagerAdapter);
        if (mRequestCode == PlayerSelection.REQUEST_CODE_GET_TEAM_OPPONENT) {
            navigateTo(mLastSelectionIndexOpponent);
        }
        else {
            navigateTo(mLastSelectionIndexSelf);
        }
	}
	
	@SuppressLint("CommitPrefEdits")
	private void updateIntPreference(final String name, final int value) {
		if (mPrefs != null) {
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putInt(name, value);
			MyApp.savePreferences(editor);
		}
	}

	@Override
	public void onSelectPlayer(final int requestCode, final int index, final Player player) {
		if (requestCode == PlayerSelection.REQUEST_CODE_GET_TEAM_OPPONENT) {
			mLastSelectionIndexOpponent = index;
			updateIntPreference(PREFERENCE_LAST_SELECTION_INDEX_OPPONENT, index);
		}
		else {
			mLastSelectionIndexSelf = index;
			updateIntPreference(PREFERENCE_LAST_SELECTION_INDEX_SELF, index);
		}
		Intent returnIntent = new Intent();
		returnIntent.putExtra(EXTRA_PLAYER, player);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

}
