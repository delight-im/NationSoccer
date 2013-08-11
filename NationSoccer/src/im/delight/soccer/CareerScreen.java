package im.delight.soccer;

import im.delight.soccer.R;
import im.delight.soccer.util.GameScreen;
import im.delight.soccer.util.Match;
import im.delight.soccer.util.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CareerScreen extends SherlockActivity {

	public static final String EXTRA_MATCH = "match";
	public static final String EXTRA_PLAYER_SELF = "playerSelf";
	private static final int REQUEST_CODE_PLAY_GAME = 1;
	private static final String PREFERENCE_CURRENT_LEVEL = "career_saved_level";
	private static final String PREFERENCE_SAVED_PLAYER = "career_saved_player";
	private static final int LEVEL_NOT_STARTED_YET = -1;
	private static final int LEVEL_FIRST_MATCH = 0;
	private static final int LEVEL_FINISHED = 23; // 0 = introduction, 1-22 = 23 matches, 23 = the end
	private Player mPlayerSelf;
	private MyApp mApp;
	private SharedPreferences mPrefs;
	private int mCurrentLevel;
	// DATA THAT NEEDS TO BE UPDATED THROUGHOUT THE CAREER BEGIN
	private Match mCurrentMatch;
	private Button mActionButton;
	private TextView mDescription;
	private ImageView mFlagSelf;
	private ImageView mPlayerIcon;
	private ImageView mFlagOpponent;
	// DATA THAT NEEDS TO BE UPDATED THROUGHOUT THE CAREER END
	// CAREER DATA SOURCES BEGIN
	private List<Player> mPlayerList;
	private String[] mCareerSteps;
	// CAREER DATA SOURCES END
	private AlertDialog mAlertDialog;
	private View.OnClickListener mActionButtonClick_ChooseTeam = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
        	mApp.setMusicIsContinuing(true);
    		startActivityForResult(new Intent(CareerScreen.this, PlayerSelection.class), PlayerSelection.REQUEST_CODE_GET_CAREER_TEAM);
		}
	};
	private View.OnClickListener mActionButtonClick_Restart = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mCurrentLevel = LEVEL_NOT_STARTED_YET;
			updateCareer();
		}
	};
	private View.OnClickListener mActionButtonClick_PlayMatch = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent gameIntent = new Intent(CareerScreen.this, GameScreenSingle.class);
			if (mCurrentMatch.getPlayerHome().equals(mPlayerSelf)) { // if own team is the home team
    			gameIntent.putExtra(GameScreenSingle.EXTRA_PLAYER_1, mCurrentMatch.getPlayerHome());
    			gameIntent.putExtra(GameScreenSingle.EXTRA_PLAYER_2, mCurrentMatch.getPlayerGuest());
    			gameIntent.putExtra(GameScreenSingle.EXTRA_SELF_IS_HOME, true);
			}
			else { // if own team is the guest team
    			gameIntent.putExtra(GameScreenSingle.EXTRA_PLAYER_1, mCurrentMatch.getPlayerGuest());
    			gameIntent.putExtra(GameScreenSingle.EXTRA_PLAYER_2, mCurrentMatch.getPlayerHome());
    			gameIntent.putExtra(GameScreenSingle.EXTRA_SELF_IS_HOME, false);
			}
			gameIntent.putExtra(GameScreenSingle.EXTRA_IS_TOURNAMENT_MATCH, true);
        	mApp.setMusicIsContinuing(false);
			startActivityForResult(gameIntent, REQUEST_CODE_PLAY_GAME);
		}
	};
	
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
	    intent.putExtra(MyApp.EXTRA_REQUEST_CODE, requestCode);
	    super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.career);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mApp = MyApp.getInstance();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Player[] allPlayers = MyApp.getPlayerList();
        mPlayerList = new ArrayList<Player>(allPlayers.length);
        for (Player player : allPlayers) {
        	mPlayerList.add(player);
        }
        mCareerSteps = getResources().getStringArray(R.array.career_steps);
        mCurrentLevel = mPrefs.getInt(PREFERENCE_CURRENT_LEVEL, LEVEL_NOT_STARTED_YET);
        final String savedPlayerString = mPrefs.getString(PREFERENCE_SAVED_PLAYER, null);
        try {
			mPlayerSelf = new Player(savedPlayerString);
		} 
        catch (Exception e) {
			mPlayerSelf = null;
			mCurrentLevel = LEVEL_NOT_STARTED_YET;
		}
        // SET UP VIEWS BEGIN
        mActionButton = (Button) findViewById(R.id.button_action);
        mDescription = (TextView) findViewById(R.id.description);
        mDescription.setMovementMethod(new ScrollingMovementMethod()); // enable scrolling
        mFlagSelf = (ImageView) findViewById(R.id.flag_self);
        mPlayerIcon = (ImageView) findViewById(R.id.player_icon);
        mFlagOpponent = (ImageView) findViewById(R.id.flag_opponent);
        // SETUP UP VIEWS END
        updateCareer();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mAlertDialog != null) {
    		if (mAlertDialog.isShowing()) {
    			mAlertDialog.dismiss();
    		}
    		mAlertDialog = null;
    	}
    }
    
    private void increasePlayerStrength() {
    	AlertDialog.Builder chooseSkill = new AlertDialog.Builder(this);
    	chooseSkill.setTitle(R.string.congratulations);
    	chooseSkill.setCancelable(false);
    	chooseSkill.setMessage(R.string.choose_skill_to_improve);
    	chooseSkill.setPositiveButton(R.string.skill_power, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				increasePlayerStrength(Player.SKILL_POWER);
			}
		});
    	chooseSkill.setNeutralButton(R.string.skill_jump, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				increasePlayerStrength(Player.SKILL_JUMP);
			}
		});
    	chooseSkill.setNegativeButton(R.string.skill_speed, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				increasePlayerStrength(Player.SKILL_SPEED);
			}
		});
    	mAlertDialog = chooseSkill.show();
    }
    
    private void increasePlayerStrength(final int skill) {
    	final float improvement = (4 + (new Random()).nextInt(3)) * 0.01f; // between 0.04 and 0.06
    	final int messageResourceID;
    	if (skill == Player.SKILL_SPEED) {
    		messageResourceID = R.string.skill_improved_speed;
    		mPlayerSelf.increaseSpeed(improvement);
    	}
    	else if (skill == Player.SKILL_JUMP) {
    		messageResourceID = R.string.skill_improved_jump;
    		mPlayerSelf.increaseJump(improvement);
    	}
    	else if (skill == Player.SKILL_POWER) {
    		messageResourceID = R.string.skill_improved_power;
    		mPlayerSelf.increasePower(improvement);
    	}
    	else {
    		messageResourceID = 0;
    	}
    	if (messageResourceID > 0) {
    		Toast.makeText(this, getString(messageResourceID, (int) (improvement*100)), Toast.LENGTH_SHORT).show();
    	}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == REQUEST_CODE_PLAY_GAME) {
    		if (resultCode == RESULT_OK) {
    			Match resultMatch = data.getParcelableExtra(GameScreen.EXTRA_MATCH);
    			if (resultMatch != null && resultMatch.isReady() && resultMatch.isFinished()) {
    				if (resultMatch.getGoalsHome() > resultMatch.getGoalsGuest()) { // match has been won
    					increasePlayerStrength(); // increase the player strength slightly due to more experience
						mCurrentLevel++; // proceed to next opponent and career step
    				}
    				else {
    					final String[] gameLostMessages = getResources().getStringArray(R.array.career_match_lost);
    					final int whichMessage = new Random().nextInt(gameLostMessages.length);
    					AlertDialog.Builder gameLost = new AlertDialog.Builder(this);
    					gameLost.setTitle(R.string.pity);
    					gameLost.setMessage(gameLostMessages[whichMessage]);
    					gameLost.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) { }
						});
    					mAlertDialog = gameLost.show();
    				}
    				updateCareer();
    			}
    		}
    	}
    	else if (requestCode == PlayerSelection.REQUEST_CODE_GET_CAREER_TEAM) {
    		if (resultCode == RESULT_OK) {
    			mPlayerSelf = data.getParcelableExtra(PlayerSelection.EXTRA_PLAYER);
    			mCurrentLevel = LEVEL_FIRST_MATCH;
    			updateCareer();
    		}
    	}
    }
    
    @SuppressLint("CommitPrefEdits")
	@Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREFERENCE_CURRENT_LEVEL, mCurrentLevel);
        if (mPlayerSelf != null) {
	        editor.putString(PREFERENCE_SAVED_PLAYER, mPlayerSelf.toString());
        }
        MyApp.savePreferences(editor);
        mApp.setMusicEnabled(false);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mApp.setMusicEnabled(mApp.getVolumeMode() == MyApp.VOLUME_ALL);
    }
    
    private void updateCareer() {
		if (mCurrentLevel <= LEVEL_NOT_STARTED_YET) { // career not started yet
			mCurrentMatch = null;
			mDescription.setText(mCareerSteps[LEVEL_NOT_STARTED_YET+1]);
			mActionButton.setText(R.string.career_choose_team);
			mActionButton.setOnClickListener(mActionButtonClick_ChooseTeam);
			setImageOrHidden(mPlayerIcon, mPlayerList.get(mPlayerList.size()-1).getDrawableID());
			setImageOrHidden(mFlagSelf, 0);
			setImageOrHidden(mFlagOpponent, 0);
		}
		else { // career already started
			if (mCurrentLevel >= LEVEL_FINISHED) { // career successfully completed
				mCurrentMatch = null;
				mDescription.setText(mCareerSteps[LEVEL_FINISHED+1]);
				mActionButton.setText(R.string.career_restart);
				mActionButton.setOnClickListener(mActionButtonClick_Restart);
				setImageOrHidden(mPlayerIcon, mPlayerSelf.getDrawableID());
				setImageOrHidden(mFlagSelf, 0);
				setImageOrHidden(mFlagOpponent, 0);
			}
			else { // still playing the career right now
				mPlayerList.remove(mPlayerSelf); // make sure own player is not in the list of opponents
				final Player opponent = mPlayerList.get(mCurrentLevel);
				mCurrentMatch = new Match(mPlayerSelf, opponent);
				mDescription.setText(String.format(mCareerSteps[mCurrentLevel+1], opponent.getLongName(this)));
				mActionButton.setText(R.string.play_now);
				mActionButton.setOnClickListener(mActionButtonClick_PlayMatch);
				setImageOrHidden(mPlayerIcon, 0);
				setImageOrHidden(mFlagSelf, mCurrentMatch.getPlayerHome().getFlagDrawableID());
				setImageOrHidden(mFlagOpponent, mCurrentMatch.getPlayerGuest().getFlagDrawableID());
			}
		}
    }
	
	private void setImageOrHidden(final ImageView view, final int resourceID) {
		if (resourceID == 0) {
			view.setVisibility(View.GONE);
			view.setImageResource(0);
		}
		else {
			view.setImageResource(resourceID);
			view.setVisibility(View.VISIBLE);
		}
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	mApp.setMusicIsContinuing(true);
    	finish();
		return true;
	}
    
    @Override
    public void onBackPressed() {
    	mApp.setMusicIsContinuing(true);
    	finish();
    }
    
}