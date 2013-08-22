package im.delight.soccer;

import im.delight.apprater.AppRater;
import im.delight.soccer.R;
import im.delight.soccer.util.Player;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class StartScreen extends Activity {

	private Player mPlayerSelf;
	private Player mPlayerOpponent;
	private MyApp mApp;
	private ImageView ivSoundOnOff;
	private AlertDialog mAlertDialog;
	private View.OnClickListener soundOnOffClick = new View.OnClickListener() {
		public void onClick(View v) {
			int oldValue = mApp.getVolumeMode();
			mApp.switchVolumeMode(oldValue);
			showSoundStatus();
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.main);
        mApp = MyApp.getInstance();
        findViewById(R.id.button_quick_match).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		startActivityForResult(new Intent(StartScreen.this, PlayerSelection.class), PlayerSelection.REQUEST_CODE_GET_TEAM_SELF);
        	}
        });
        findViewById(R.id.button_tournament).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		startActivityForResult(new Intent(StartScreen.this, PlayerSelection.class), PlayerSelection.REQUEST_CODE_GET_TOURNAMENT_TEAM);
        	}
        });
        findViewById(R.id.button_career).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		startActivity(new Intent(StartScreen.this, CareerScreen.class));
        	}
        });
        findViewById(R.id.button_bluetooth).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		startActivity(new Intent(StartScreen.this, BluetoothScreen.class));
        	}
        });
        ivSoundOnOff = (ImageView) findViewById(R.id.sound_mode);
        ivSoundOnOff.setOnClickListener(soundOnOffClick);
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
        		startActivity(new Intent(StartScreen.this, im.delight.soccer.settings.Main.class));
			}
		});

        AppRater appRater = new AppRater(this, MyApp.PACKAGE_NAME);
        appRater.setDaysBeforePrompt(3);
        appRater.setLaunchesBeforePrompt(7);
        appRater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now, R.string.rate_later, R.string.rate_never);
        mAlertDialog = appRater.show();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	closeDialog(mAlertDialog);
    }
    
    private void closeDialog(AlertDialog dialog) {
    	if (dialog != null) {
    		if (dialog.isShowing()) {
    			dialog.dismiss();
    		}
    		dialog = null;
    	}
    }

    private void showSoundStatus() {
    	if (mApp.getVolumeMode() == MyApp.VOLUME_OFF) {
    		ivSoundOnOff.setImageResource(R.drawable.volume_off);
    	}
    	else {
    		ivSoundOnOff.setImageResource(R.drawable.volume_on);
    	}
    }
    
    protected void onResume() {
    	super.onResume();
    	showSoundStatus();
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
	    intent.putExtra(MyApp.EXTRA_REQUEST_CODE, requestCode);
	    super.startActivityForResult(intent, requestCode);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == PlayerSelection.REQUEST_CODE_GET_TEAM_SELF) {
    		if (resultCode == RESULT_OK) {
    			mPlayerSelf = data.getParcelableExtra(PlayerSelection.EXTRA_PLAYER);
    			Intent chooseSecondPlayer = new Intent(StartScreen.this, PlayerSelection.class);
    			chooseSecondPlayer.putExtra(PlayerSelection.EXTRA_FIRST_PLAYER, mPlayerSelf);
    			startActivityForResult(chooseSecondPlayer, PlayerSelection.REQUEST_CODE_GET_TEAM_OPPONENT);
    		}
    		else {
    			mPlayerSelf = null;
    			mPlayerOpponent = null;
    		}
    	}
    	else if (requestCode == PlayerSelection.REQUEST_CODE_GET_TEAM_OPPONENT) {
    		if (resultCode == RESULT_OK) {
    			mPlayerOpponent = data.getParcelableExtra(PlayerSelection.EXTRA_PLAYER);
    			if (mPlayerSelf != null && mPlayerOpponent != null) {
	    			Intent gameIntent = new Intent(StartScreen.this, GameScreenSingle.class);
	    			gameIntent.putExtra(GameScreenSingle.EXTRA_PLAYER_1, mPlayerSelf);
	    			gameIntent.putExtra(GameScreenSingle.EXTRA_PLAYER_2, mPlayerOpponent);
	    			startActivity(gameIntent);
    			}
    		}
    		else {
    			mPlayerSelf = null;
    			mPlayerOpponent = null;
    		}
    	}
    	else if (requestCode == PlayerSelection.REQUEST_CODE_GET_TOURNAMENT_TEAM) {
    		if (resultCode == RESULT_OK) {
    			mPlayerSelf = data.getParcelableExtra(PlayerSelection.EXTRA_PLAYER);
    			if (mPlayerSelf != null) {
	    			Intent tournamentIntent = new Intent(StartScreen.this, TournamentScreen.class);
	    			tournamentIntent.putExtra(TournamentScreen.EXTRA_PLAYER_SELF, mPlayerSelf);
	    			startActivity(tournamentIntent);
    			}
    		}
    		else {
    			mPlayerSelf = null;
    			mPlayerOpponent = null;
    		}
    	}
    }
    
}
