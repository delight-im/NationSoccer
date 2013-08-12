package im.delight.soccer;

import im.delight.soccer.R;
import im.delight.soccer.util.GameScreen;
import im.delight.soccer.util.Match;
import im.delight.soccer.util.Player;
import im.delight.soccer.util.ResizableImageView;
import java.util.Random;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TournamentScreen extends SherlockActivity {

	public static final int REQUEST_CODE_PLAY_GAME = 1;
	public static final String EXTRA_PLAYER_SELF = "playerSelf";
	private Tournament mTournament;
	private Player mPlayerSelf;
	private LinearLayout[] mRoundViews;
	private View[] mCaptions;
	private Button mPlayNow;
	private Button mFinish;
	private Match mCurrentMatch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.tournament);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPlayerSelf = getIntent().getParcelableExtra(EXTRA_PLAYER_SELF);
        mTournament = new Tournament(16, MyApp.getPlayerList(), mPlayerSelf);
        mRoundViews = new LinearLayout[4];
        mRoundViews[0] = (LinearLayout) findViewById(R.id.round_1);
        mRoundViews[1] = (LinearLayout) findViewById(R.id.round_2);
        mRoundViews[2] = (LinearLayout) findViewById(R.id.round_3);
        mRoundViews[3] = (LinearLayout) findViewById(R.id.round_4);
        mCaptions = new View[4];
        mCaptions[0] = findViewById(R.id.round_1_caption);
        mCaptions[1] = findViewById(R.id.round_2_caption);
        mCaptions[2] = findViewById(R.id.round_3_caption);
        mCaptions[3] = findViewById(R.id.round_4_caption);
        mPlayNow = (Button) findViewById(R.id.button_start);
        mPlayNow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
    			Intent gameIntent = new Intent(TournamentScreen.this, GameScreenSingle.class);
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
    			startActivityForResult(gameIntent, REQUEST_CODE_PLAY_GAME);
			}
		});
        mFinish = (Button) findViewById(R.id.button_finish);
        mFinish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        updateTournamentList();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == REQUEST_CODE_PLAY_GAME) {
    		if (resultCode == RESULT_OK) {
    			Match resultMatch = data.getParcelableExtra(GameScreen.EXTRA_MATCH);
    			if (resultMatch != null && resultMatch.isReady() && resultMatch.isFinished()) {
    				updateTournamentList(resultMatch);
    			}
    		}
    	}
    }
    
    private void updateTournamentList() {
    	updateTournamentList(null);
    }

	private void updateTournamentList(Match matchToUpdate) {
    	Random random = new Random();
    	Player playerHome, playerGuest;
    	LinearLayout.LayoutParams viewParams;
    	Match latestMatchSelf = null;

    	for (int r = 0; r < mRoundViews.length; r++) {
    		mRoundViews[r].removeAllViews(); // add any other (old) entries that may have existed
    		Match[] matches = mTournament.getMatches(r); // get all matches for this round
	    	int textViewPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
	    	boolean hasMatchesReady = false;
	    	for (Match match : matches) {
	    		if (match != null && match.isReady()) {
	    		
		    		playerHome = match.getPlayerHome();
		    		playerGuest = match.getPlayerGuest();
		
		            LinearLayout matchContainer = new LinearLayout(this);
		            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		            matchContainer.setOrientation(LinearLayout.VERTICAL);
		            matchContainer.setLayoutParams(layoutParams);
		
		            ImageView teamHome = new ResizableImageView(this);
		            teamHome.setAdjustViewBounds(true);
		            teamHome.setScaleType(ImageView.ScaleType.CENTER_CROP);
		            teamHome.setImageResource(playerHome.getFlagDrawableID());
		            viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		            teamHome.setLayoutParams(viewParams);
		            matchContainer.addView(teamHome);
		            
		            TextView score = new TextView(this);
		            score.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		            score.setGravity(Gravity.CENTER);
		            viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		            viewParams.setMargins(textViewPadding*2, textViewPadding, textViewPadding*2, textViewPadding);
		            score.setTextColor(Color.WHITE);
		            score.setPadding(textViewPadding, textViewPadding/2, textViewPadding, textViewPadding/2);
		            score.setLayoutParams(viewParams);
		            if (playerHome.equals(mPlayerSelf) || playerGuest.equals(mPlayerSelf)) { // game with own team
		            	score.setBackgroundColor(getResources().getColor(R.color.blue_dark_50)); // mark this entry
		            	if (match.isPending()) { // if match still has to be played
		            		if (matchToUpdate != null && match.equals(matchToUpdate)) {
		            			match.setResult(matchToUpdate.getGoalsHome(), matchToUpdate.getGoalsGuest());
		            			matchToUpdate = null; // mark match result as used
		            		}
		            		else {
		            			latestMatchSelf = match;
		            		}
		            	}
		            }
		            else { // game between two other teams
		            	score.setBackgroundColor(getResources().getColor(R.color.grey_dark_50)); // normal entry color
		            	if (match.isPending()) { // if match still has to be played
			            	// GENERATE RANDOM RESULT BEGIN
			            	int goals1 = 0;
			            	int goals2 = 0;
			            	do {
			            		goals1 = random.nextInt(6); // between 0 and 5 goals
			            		goals2 = random.nextInt(6); // between 0 and 5 goals
			            	}
			            	while (goals1 == goals2);
			            	// GENERATE RANDOM RESULT END
			            	match.setResult(goals1, goals2); // set the auto-generated result for this computer game
		            	}
		            }
		            score.setText(match.isFinished() ? match.getResultString() : "-:-");
		            matchContainer.addView(score);
		            
		            ImageView teamGuest = new ResizableImageView(this);
		            teamGuest.setAdjustViewBounds(true);
		            teamGuest.setScaleType(ImageView.ScaleType.CENTER_CROP);
		            teamGuest.setImageResource(playerGuest.getFlagDrawableID());
		            viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		            teamGuest.setLayoutParams(viewParams);
		            matchContainer.addView(teamGuest);

		            mRoundViews[r].addView(matchContainer); // add match entry to round view
		            hasMatchesReady = true;
	            
	    		}
	    	}
    		if (hasMatchesReady) {
    			mCaptions[r].setVisibility(View.VISIBLE);
    			mRoundViews[r].setVisibility(View.VISIBLE);
    		}
    		else {
    			mCaptions[r].setVisibility(View.GONE);
    			mRoundViews[r].setVisibility(View.GONE);
    		}
    		mTournament.propagateWinnersToNextRounds(r); // now that round has been processed propagate winners to the next round if necessary
    	}
    	if (latestMatchSelf == null) {
    		mCurrentMatch = null;
    		mPlayNow.setVisibility(View.GONE);
    		mFinish.setVisibility(View.VISIBLE);
    	}
    	else {
    		mCurrentMatch = latestMatchSelf;
    		mPlayNow.setVisibility(View.VISIBLE);
    		mFinish.setVisibility(View.GONE);
    	}
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	finish();
		return true;
	}
    
}