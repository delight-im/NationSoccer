package im.delight.soccer;

import im.delight.soccer.R;
import im.delight.soccer.andengine.GameScene;
import im.delight.soccer.andengine.ResourcesManager;
import im.delight.soccer.andengine.SceneManager;
import im.delight.soccer.util.GameScreen;
import im.delight.soccer.util.Match;
import im.delight.soccer.util.Player;
import java.io.IOException;
import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;

public class GameScreenSingle extends GameScreen {
	
	public static final String EXTRA_PLAYER_1 = "player1";
	public static final String EXTRA_PLAYER_2 = "player2";
	public static final String EXTRA_IS_TOURNAMENT_MATCH = "isTournamentMatch";
	public static final String EXTRA_SELF_IS_HOME = "selfIsHome";
	public static final int ALERT_REALLY_EXIT = 1;
	private BoundCamera mCamera;
	private Match mMatch;
	private Player mPlayer1;
	private Player mPlayer2;
	private boolean mIsTournamentMatch;
	private MyApp mApp;
	private int mVolumeMode;
	private boolean mSelfIsHome;
	/** Holds the AlertDialog that is currently showing in this Activity */
	private AlertDialog mAlertDialog;
	
	public void setCurrentResult(int goals1, int goals2) {
		mMatch.setResult(goals1, goals2);
	}
	
	public Match getMatch() {
		return mMatch;
	}
	
	public void showAlert(int alertID) {
		if (alertID == ALERT_REALLY_EXIT) {
			if (mEngine != null) {
				mEngine.stop();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.exit_game);
			builder.setMessage(R.string.exit_game_sure);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null) {
						dialog.dismiss();
					}
					finish();
				}
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null) {
						dialog.dismiss();
					}
					if (mEngine != null) {
						mEngine.start();
					}
				}
			});
			mAlertDialog = builder.show();
		}
	}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mAlertDialog != null) {
    		mAlertDialog.dismiss();
    	}
    	SceneManager.getInstance().GameScene_dispose();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    @Override
    public void onBackPressed() {
    	showAlert(ALERT_REALLY_EXIT);
    }
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        Intent intent = getIntent();
        mPlayer1 = intent.getParcelableExtra(EXTRA_PLAYER_1);
        mPlayer2 = intent.getParcelableExtra(EXTRA_PLAYER_2);
        mMatch = new Match(mPlayer1, mPlayer2, 0, 0);
        GameScene.setMatch(mMatch);
        mIsTournamentMatch = intent.getBooleanExtra(EXTRA_IS_TOURNAMENT_MATCH, false);
        mSelfIsHome = intent.getBooleanExtra(EXTRA_SELF_IS_HOME, true);

        mApp = MyApp.getInstance();
        mVolumeMode = mApp.getVolumeMode();
		mCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		engineOptions.getRenderOptions().setDithering(true); // enable dithering for this engine
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true); // enable sound and music
		engineOptions.getTouchOptions().setNeedsMultiTouch(true); // enable multi-touch for left/right and up button
		return engineOptions;
	}
	
    @Override
    public void finish() {
    	if (mIsTournamentMatch && mGameFinished) {
    		if (!mSelfIsHome) {
    			mMatch.swapTeams();
    		}
			sendResult(mMatch);
    	}
		super.finish();
    }
    
	public void sendResult(Match match) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(EXTRA_MATCH, match);
		setResult(RESULT_OK, returnIntent);
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, FRAMES_PER_SECOND);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
		ResourcesManager.prepareManager(mEngine, this, mCamera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
		SceneManager.getInstance().GameScene_create(mMatch, mVolumeMode, pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
    
}