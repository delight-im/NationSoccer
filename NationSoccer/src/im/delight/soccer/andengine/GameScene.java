package im.delight.soccer.andengine;

import im.delight.soccer.util.GameScreen;
import im.delight.soccer.util.Match;
import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameScene extends BaseScene implements ContactListener, IOnSceneTouchListener {

	public static final String BODY_TYPE_PLAYER_1 = "body_type_player_1";
	public static final String BODY_TYPE_PLAYER_2 = "body_type_player_2";
	public static final String BODY_TYPE_BALL = "body_type_ball";
	public static final String BODY_TYPE_WALL_LEFT = "body_type_wall_left";
	public static final String BODY_TYPE_WALL_RIGHT = "body_type_wall_right";
	public static final String BODY_TYPE_WALL_GROUND = "body_type_wall_ground";
	/** Players' x-acceleration when manually opted to move (movement is continuously triggered in game loop) */
	public static final float PLAYER_ACCELERATION_X = 1900.0f;
	/** Players' y-acceleration when manually opted to move (much larger than normal because jump is triggered in a single time step only) */
	public static final float PLAYER_ACCELERATION_Y = 280000.0f;
	public static final float PLAYER_MAX_SPEED_X = 14.0f;
	public static final float PLAYER_MAX_SPEED_Y = 14.5f;
	public static final float BALL_MAX_SPEED_Y = 22.0f;
	/** Factor affecting the players' x-speed when up in the air */
	public static final float PLAYER_AIR_BONUS_X = 1.4f;
	public static final float BALL_KICK_ACCELERATION_Y = 2800.0f;
	public static final int SPRITE_PLAYER_1 = 1;
	public static final int SPRITE_PLAYER_2 = 2;
	public static final int SPRITE_BALL = 3;
	public static final int GOALKICK_NONE = 0;
	public static final int GOALKICK_LEFT = 1;
	public static final int GOALKICK_RIGHT = 2;
	/** When debug mode is set to this constant, any debug functions will be disabled */
	public static final int DEBUG_NONE = 0;
	/** When debug mode is set to this constant, sprite borders will be drawn to screen for physics verification etc. */
	public static final int DEBUG_RENDER = 1;
	/** When debug mode is set to this constant, players will stop moving after the first goal so that you can capture the screen */
	//public static final int DEBUG_SCREENSHOT = 2; // DEBUG_SCREENSHOT_MODE (enable all lines with this comment)
	public static final float FIELD_BASELINE_Y = GameScreen.FIELD_HEIGHT * 0.25f;
	public static final float FIELD_GOALLINE_Y = GameScreen.FIELD_HEIGHT * 0.60f;
	public static final float FIELD_CENTER_START = GameScreen.FIELD_WIDTH * 0.1f;
	public static final float FIELD_CENTER_END = GameScreen.FIELD_WIDTH * 0.9f;
	public static final float WALL_THICKNESS = 50; // thickness of the invisible walls that are the world's bounding box
	public static final float GOAL_WIDTH = 122.0f;
	public static final float PADDING_TEXTS_OUTTER = 16.0f;
	/** Format that is used to print the score line (Player 1, Player 2, Goals 1, Goals 2) **/
	private static final String SCORE_FORMAT = "%1$3s %3$02d\n%2$3s %4$02d";
	private static final String TIME_FORMAT = "%1$02d:%2$02d";
	private static final int STATE_RUNNING = 1;
	private static final int STATE_ENDED = 2;
	private static final int STATE_EXTRA_TIME = 3;
	private static final int STATE_WAITING = 4;
	private static final int DIRECTION_LEFT = 1; // used for bitmask
	private static final int DIRECTION_RIGHT = 2; // used for bitmask
	private static final int DIRECTION_UP = 4; // used for bitmask
	private static final long EXCLAMATION_DISPLAY_DURATION = 1500;
	private static final int EXCLAMATION_NONE = 0;
	private static final int EXCLAMATION_GOAL = 1;
	private static final int EXCLAMATION_OUT = 2;
	/** The gravity for this scene */
	private static final float GRAVITY = -28.0f;
	/** When debug is enabled, sprite borders will be drawn to the screen and players will stop moving after first goal */
	private static final int DEBUG = DEBUG_NONE;
	/** Number of time steps that an update of scores and time occurs after */
	private static final int TIME_STEP_INTERVAL = 30;
	private static final int CONTROL_SPACING = 12;
	private static final float CONTROL_ALPHA_NORMAL = 1.0f;
	private static final float CONTROL_ALPHA_PRESSED = 0.6f;
	/** Singleton holder for the match to simulate */
	private volatile static Match mMatch;
	private volatile static Vector2[] mPlayerVertices;
	private HUD mHUD;
	private HUD mTutorial;
	private Text mTimeText;
	private Text mExclamation;
	private Text mScoreText;
	private Text mBottomText;
	private Text mIntro_Text;
	private String mTimeText_Text;
	private PhysicsWorld mPhysicsWorld;
	private RepeatingSpriteBackground mBackground_Sprite;
	private Sprite mFieldLeft_Sprite;
	private Sprite mFieldRight_Sprite;
	private Sprite mFieldCenter_Sprite;
	private TiledSprite mPlayer1_Sprite;
	private TiledSprite mPlayer2_Sprite;
	private Sprite mBall_Sprite;
	private volatile Body mPlayer1_Body;
	private volatile Body mPlayer2_Body;
	private volatile Body mBall_Body;
	/** Timestamp (in milliseconds) when the game (that has already ended) may be closed by tapping on the screen */
	private long mStartTime;
	private int mGoalLimit;
	/** Positive number if game is limited by time (in seconds) or -1 if game is limited by goal count */
	private int mGameDuration;
	/** Whether the game is running, in extra time or has already ended */
	private volatile int mState;
	private volatile boolean mPendingKickoff;
	private volatile int mPendingGoalkick;
	private volatile int mTimestepCounter;
	private volatile Fixture mCollisionFixture1;
	private volatile Fixture mCollisionFixture2;
	private volatile int mExclamationText;
	private volatile long mExclamationExpires;
	//private volatile boolean mPlayersMayMove = true; // DEBUG_SCREENSHOT_MODE (enable all lines with this comment)
	
	private static class AIMoves {
		
		private static volatile boolean mHuman_closeLeft_blockingBall;
		private static volatile boolean mHuman_closeRight_blockingBall;
		private static volatile boolean mBall_closeAndBelow;
		private static volatile boolean mBall_shortlyAbove;
		private static volatile float mComputer_x;
		private static volatile float mComputer_y;
		private static volatile float mBall_x;
		private static volatile float mBall_y;
		private static volatile float mHuman_x;

	}
	
	private static class Phrases {

		private static final int FIELD_SCORE = 1;
		private static final int FIELD_TIME = 2;
		private static final int FIELD_EXCLAMATION = 3;
		private static final int FIELD_TAP_TO_LEAVE = 4;
		private static final int FIELD_TAP_TO_START = 5;
		private static final String mEmpty = "";
		private static String mGoal;
		private static String mOut;
		private static String mYouWon;
		private static String mYouLost;
		private static String mTapToLeave;
		private static String mTapToStart;
		
		private static String getPossibleCharacters(final int fieldID) {
			switch (fieldID) {
				case FIELD_SCORE: return "ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789";
				case FIELD_TIME: return "0123456789:";
				case FIELD_EXCLAMATION: return mGoal+mOut+mYouWon+mYouLost;
				case FIELD_TAP_TO_LEAVE: return mTapToLeave;
				case FIELD_TAP_TO_START: return mTapToStart;
				default: return "";
			}
		}

	}
	
	private static class PlayerData {

		private static volatile float mPlayer1_factorSpeed;
		private static volatile float mPlayer1_factorJump;
		private static volatile float mPlayer1_factorPower;
		private static volatile boolean mPlayer1_isLookingLeft;
		private static volatile boolean mPlayer1_isJumping;
		private static volatile boolean mPlayer1_isBoing;
		private static volatile Vector2 mPlayer1_Movement = new Vector2();
		private static volatile Vector2 mPlayer1_Speed = new Vector2();
		private static volatile float mPlayer1_currentMaxSpeed_X;

		private static volatile float mPlayer2_factorSpeed;
		private static volatile float mPlayer2_factorJump;
		private static volatile float mPlayer2_factorPower;
		private static volatile boolean mPlayer2_isLookingLeft;
		private static volatile boolean mPlayer2_isJumping;
		private static volatile boolean mPlayer2_isBoing;
		private static volatile Vector2 mPlayer2_Movement = new Vector2();
		private static volatile Vector2 mPlayer2_Speed = new Vector2();
		private static volatile float mPlayer2_currentMaxSpeed_X;

	}
	
	private static class BallData {
		
		private static volatile Vector2 mMovement = new Vector2();
		private static volatile Vector2 mSpeed = new Vector2();

	}
	
	public static final int getDebug() {
		return DEBUG;
	}
	
	public static void setMatch(Match match) {
		mMatch = match;
	}
	
	private void createPlayerData() {
		PlayerData.mPlayer1_factorSpeed = mMatch.getPlayerHome().getSpeedFactor();
		PlayerData.mPlayer1_factorJump = mMatch.getPlayerHome().getJumpFactor();
		PlayerData.mPlayer1_factorPower = mMatch.getPlayerHome().getPowerFactor();
		
		PlayerData.mPlayer2_factorSpeed = mMatch.getPlayerGuest().getSpeedFactor();
		PlayerData.mPlayer2_factorJump = mMatch.getPlayerGuest().getJumpFactor();
		PlayerData.mPlayer2_factorPower = mMatch.getPlayerGuest().getPowerFactor();
	}
	
	private void createPhrases() {
		Phrases.mGoal = mActivity.getPhrase(GameScreen.PHRASE_GOAL);
		Phrases.mOut = mActivity.getPhrase(GameScreen.PHRASE_OUT);
		Phrases.mTapToLeave = mActivity.getPhrase(GameScreen.PHRASE_TAP_TO_LEAVE);
		Phrases.mYouLost = mActivity.getPhrase(GameScreen.PHRASE_YOU_LOST);
		Phrases.mYouWon = mActivity.getPhrase(GameScreen.PHRASE_YOU_WON);
		Phrases.mTapToStart = mActivity.getPhrase(GameScreen.PHRASE_TAP_TO_START);
	}

	@Override
	public void createScene() {
		if (mActivity.getPreference(GameScreen.PREFERENCE_GAME_END, mActivity.getPhrase(GameScreen.PHRASE_GAME_END_DEFAULT)).equals("time")) { // game is set to end after a certain period of time
			mGoalLimit = -1; // do not a goal limit
			try {
				mGameDuration = Integer.parseInt(mActivity.getPreference(GameScreen.PREFERENCE_GAME_TIME, mActivity.getPhrase(GameScreen.PHRASE_GAME_TIME_DEFAULT))); // use the user-defined game length in seconds
			}
			catch (Exception e) {
				mGameDuration = 120;
			}
		}
		else { // game is set to end after one team has scored a certain number of goals
			try {
				mGoalLimit = Integer.parseInt(mActivity.getPreference(GameScreen.PREFERENCE_GAME_GOALS, mActivity.getPhrase(GameScreen.PHRASE_GAME_GOALS_DEFAULT))); // use the user-defined goal limit
			}
			catch (Exception e) {
				mGoalLimit = 5;
			}
			mGameDuration = -1; // do not use a time limit (this is not a count-down game)
		}
		mState = STATE_WAITING;
		createBackground();
		createPlayerData();
		createPhrases();
		createTutorial();

		playMusic(mResourcesManager.mAudio_Music);
	}
	
	private synchronized void makeHumanMoves() {
		if (HumanMoves.isTouchDirection(DIRECTION_LEFT)) { // horizontal == left
			if (HumanMoves.isTouchDirection(DIRECTION_UP)) { // vertical == up
				applyAcceleration(SPRITE_PLAYER_1, -PLAYER_ACCELERATION_X, PLAYER_ACCELERATION_Y);
			}
			else { // vertical == none
				applyAcceleration(SPRITE_PLAYER_1, -PLAYER_ACCELERATION_X, 0);
			}
		}
		else if (HumanMoves.isTouchDirection(DIRECTION_RIGHT)) { // horizontal == right
			if (HumanMoves.isTouchDirection(DIRECTION_UP)) { // vertical == up
				applyAcceleration(SPRITE_PLAYER_1, PLAYER_ACCELERATION_X, PLAYER_ACCELERATION_Y);
			}
			else { // vertical == none
				applyAcceleration(SPRITE_PLAYER_1, PLAYER_ACCELERATION_X, 0);
			}
		}
		else { // horizontal == none
			if (HumanMoves.isTouchDirection(DIRECTION_UP)) { // vertical == up
				applyAcceleration(SPRITE_PLAYER_1, 0, PLAYER_ACCELERATION_Y);
			}
		}
	}
	
	private synchronized void makeAIMoves() {
		//if (getDebug() == DEBUG_SCREENSHOT && !mPlayersMayMove) { return; } // DEBUG_SCREENSHOT_MODE (enable all lines with this comment)
    	if (PlayerData.mPlayer2_isJumping) { // when in the air (jumping)
    		applyAcceleration(SPRITE_PLAYER_2, PlayerData.mPlayer2_isLookingLeft ? -PLAYER_ACCELERATION_X : PLAYER_ACCELERATION_X, 0.0f); // continue to accelerate in same direction
    	}
    	else { // when on the ground (running)
    		AIMoves.mBall_x = mBall_Body.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
    		AIMoves.mBall_y = mBall_Body.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
    		AIMoves.mComputer_x = mPlayer2_Body.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
    		AIMoves.mComputer_y = mPlayer2_Body.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
    		AIMoves.mHuman_x = mPlayer1_Body.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

	    	AIMoves.mHuman_closeLeft_blockingBall = AIMoves.mHuman_x < AIMoves.mComputer_x && AIMoves.mHuman_x > (AIMoves.mComputer_x - 160.0f) && AIMoves.mHuman_x > AIMoves.mBall_x;
	    	AIMoves.mHuman_closeRight_blockingBall = AIMoves.mHuman_x > AIMoves.mComputer_x && AIMoves.mHuman_x < (AIMoves.mComputer_x + 160.0f) && AIMoves.mHuman_x < AIMoves.mBall_x;
	    	AIMoves.mBall_closeAndBelow = Math.abs(AIMoves.mBall_x - AIMoves.mComputer_x) < 170.0f && AIMoves.mBall_y < AIMoves.mComputer_y && mBall_Body.getLinearVelocity().y < 0;
	    	AIMoves.mBall_shortlyAbove = Math.abs(AIMoves.mBall_x - AIMoves.mComputer_x) < 90.0f && AIMoves.mBall_y > AIMoves.mComputer_y && mBall_Body.getLinearVelocity().y < 0;

			if (AIMoves.mBall_x > AIMoves.mComputer_x) { // ball is on player's right (towards own goal)
				applyAcceleration(SPRITE_PLAYER_2, PLAYER_ACCELERATION_X, ((AIMoves.mBall_closeAndBelow || AIMoves.mHuman_closeRight_blockingBall) ? PLAYER_ACCELERATION_Y : 0.0f));
	    	}
			else { // ball is on player's left (towards opponent's goal)
				applyAcceleration(SPRITE_PLAYER_2, (AIMoves.mBall_shortlyAbove ? 0.0f : -PLAYER_ACCELERATION_X), (AIMoves.mHuman_closeLeft_blockingBall ? PLAYER_ACCELERATION_Y : 0.0f)); // do never jump when between ball and own goal
	    	}
    	}
    }
    
    private void playSound(Sound sound) {
		if (sound != null && sound.isLoaded() && !sound.isReleased()) {
			sound.play();
		}
    }
    
    private void playMusic(Music music) {
    	if (music != null && !music.isReleased()) {
    		music.play();
    	}
    }
	
	private synchronized void applyAcceleration(int spriteID, float accX, float accY) {
		if (spriteID == SPRITE_PLAYER_1) {
			if (PlayerData.mPlayer1_isJumping) {
				accY = 0.0f;
				accX *= PLAYER_AIR_BONUS_X;
			}
			accX *= PlayerData.mPlayer1_factorPower;
			accY *= PlayerData.mPlayer1_factorPower;
			mPlayer1_Body.applyForce(PlayerData.mPlayer1_Movement.set(accX, accY * PlayerData.mPlayer1_factorJump), mPlayer1_Body.getWorldCenter());
			if (accY > 0) {
				PlayerData.mPlayer1_isJumping = true;
				playSound(mResourcesManager.mAudio_Jump);
			}
			PlayerData.mPlayer1_currentMaxSpeed_X = PLAYER_MAX_SPEED_X * PlayerData.mPlayer1_factorSpeed;
			if (PlayerData.mPlayer1_isJumping) {
				PlayerData.mPlayer1_currentMaxSpeed_X *= (2+PLAYER_AIR_BONUS_X) / 3;
			}
			synchronized (PlayerData.mPlayer1_Speed) {
				PlayerData.mPlayer1_Speed = mPlayer1_Body.getLinearVelocity();
				if (PlayerData.mPlayer1_Speed.x > PlayerData.mPlayer1_currentMaxSpeed_X) {
					PlayerData.mPlayer1_Speed.set(PlayerData.mPlayer1_currentMaxSpeed_X, PlayerData.mPlayer1_Speed.y);
				}
				else if (PlayerData.mPlayer1_Speed.x < -PlayerData.mPlayer1_currentMaxSpeed_X) {
					PlayerData.mPlayer1_Speed.set(-PlayerData.mPlayer1_currentMaxSpeed_X, PlayerData.mPlayer1_Speed.y);
				}
				if (PlayerData.mPlayer1_Speed.y > (PLAYER_MAX_SPEED_Y * PlayerData.mPlayer1_factorJump)) {
					PlayerData.mPlayer1_Speed.set(PlayerData.mPlayer1_Speed.x, (PLAYER_MAX_SPEED_Y * PlayerData.mPlayer1_factorJump));
				}
				mPlayer1_Body.setLinearVelocity(PlayerData.mPlayer1_Speed);
			}
		}
		else if (spriteID == SPRITE_PLAYER_2) {
			if (PlayerData.mPlayer2_isJumping) {
				accY = 0.0f;
				accX *= PLAYER_AIR_BONUS_X;
			}
			accX *= PlayerData.mPlayer2_factorPower;
			accY *= PlayerData.mPlayer2_factorPower;
			mPlayer2_Body.applyForce(PlayerData.mPlayer2_Movement.set(accX, accY * PlayerData.mPlayer2_factorJump), mPlayer2_Body.getWorldCenter());
			if (accY > 0) {
				PlayerData.mPlayer2_isJumping = true;
				playSound(mResourcesManager.mAudio_Jump);
			}
			PlayerData.mPlayer2_currentMaxSpeed_X = PLAYER_MAX_SPEED_X * PlayerData.mPlayer2_factorSpeed;
			if (PlayerData.mPlayer2_isJumping) {
				PlayerData.mPlayer2_currentMaxSpeed_X *= (2+PLAYER_AIR_BONUS_X) / 3;
			}
			synchronized (PlayerData.mPlayer2_Speed) {
				PlayerData.mPlayer2_Speed = mPlayer2_Body.getLinearVelocity();
				if (PlayerData.mPlayer2_Speed.x > PlayerData.mPlayer2_currentMaxSpeed_X) {
					PlayerData.mPlayer2_Speed.set(PlayerData.mPlayer2_currentMaxSpeed_X, PlayerData.mPlayer2_Speed.y);
				}
				else if (PlayerData.mPlayer2_Speed.x < -PlayerData.mPlayer2_currentMaxSpeed_X) {
					PlayerData.mPlayer2_Speed.set(-PlayerData.mPlayer2_currentMaxSpeed_X, PlayerData.mPlayer2_Speed.y);
				}
				if (PlayerData.mPlayer2_Speed.y > (PLAYER_MAX_SPEED_Y * PlayerData.mPlayer2_factorJump)) {
					PlayerData.mPlayer2_Speed.set(PlayerData.mPlayer2_Speed.x, (PLAYER_MAX_SPEED_Y * PlayerData.mPlayer2_factorJump));
				}
				mPlayer2_Body.setLinearVelocity(PlayerData.mPlayer2_Speed);
			}
		}
		if (accX > 1.0f) { // 1.0 is arbitrary comparison for positive/negative
			setDirection(spriteID, DIRECTION_RIGHT);
		}
		else if (accX < -1.0f) { // 1.0 is arbitrary comparison for positive/negative
			setDirection(spriteID, DIRECTION_LEFT);
		}
	}
	
	private void updateScore() {
		updateScore(getGameTime(), isLimitedByGoals());
	}

	@Override
	public void disposeScene() {
		if (mPhysicsWorld != null) {
			unregisterUpdateHandler(mPhysicsWorld);
			mPhysicsWorld.dispose();
			mPhysicsWorld = null;
		}
		
		Sprite[] allSprites = new Sprite[] { mPlayer1_Sprite, mPlayer2_Sprite, mBall_Sprite, mFieldLeft_Sprite, mFieldCenter_Sprite, mFieldRight_Sprite, Controller.left, Controller.right, Controller.up };

		for (Sprite sprite : allSprites) {
			if (sprite != null) {
				sprite.detachSelf();
				sprite.dispose();
			}
		}
		
		try {
			detachSelf();
			dispose();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createBackground() {
		mBackground_Sprite = new RepeatingSpriteBackground(GameScreen.CAMERA_WIDTH, GameScreen.CAMERA_HEIGHT, mResourcesManager.mBackground_TextureRegion, mVertexManager);
		setBackground(mBackground_Sprite);
		
		// create field marks that will be attached to the scene as soon as the game starts
		mFieldLeft_Sprite = new Sprite(256, GameScreen.CAMERA_HEIGHT/2, mResourcesManager.mFieldLeft_TextureRegion, mVertexManager);
		mFieldLeft_Sprite.setCullingEnabled(true); // don't render this sprite if it is not visible on the screen
		mFieldRight_Sprite = new Sprite(GameScreen.FIELD_WIDTH-256, GameScreen.CAMERA_HEIGHT/2, mResourcesManager.mFieldRight_TextureRegion, mVertexManager);
		mFieldRight_Sprite.setCullingEnabled(true); // don't render this sprite if it is not visible on the screen
		mFieldCenter_Sprite = new Sprite(GameScreen.FIELD_WIDTH/2, GameScreen.CAMERA_HEIGHT/2, mResourcesManager.mFieldCenter_TextureRegion, mVertexManager);
		mFieldCenter_Sprite.setCullingEnabled(true); // don't render this sprite if it is not visible on the screen
	}
	
	/** Create three walls (left, ground, right) as the field's boundaries (open at the top) */
	private void createWorldBoundaries() {
		Body body;
		final Rectangle wall_ground = new Rectangle(GameScreen.FIELD_WIDTH/2, FIELD_BASELINE_Y-WALL_THICKNESS/2, GameScreen.FIELD_WIDTH, WALL_THICKNESS, mVertexManager);
		final Rectangle wall_left = new Rectangle(GOAL_WIDTH-WALL_THICKNESS/2, GameScreen.FIELD_HEIGHT*2, WALL_THICKNESS, GameScreen.FIELD_HEIGHT*4, mVertexManager); // make left and right wall 4x-high so that the ball can never cross these walls
		final Rectangle wall_right = new Rectangle(GameScreen.FIELD_WIDTH-GOAL_WIDTH+WALL_THICKNESS/2, GameScreen.FIELD_HEIGHT*2, WALL_THICKNESS, GameScreen.FIELD_HEIGHT*4, mVertexManager); // make left and right wall 4x-high so that the ball can never cross these walls

		body = PhysicsFactory.createBoxBody(mPhysicsWorld, wall_ground, BodyType.StaticBody, ObjectFixtures.getWall());
		body.setUserData(BODY_TYPE_WALL_GROUND); // set the identifier for this body (e.g. for collision detection later)
		wall_ground.setVisible(false); // walls are not visible
		wall_ground.setUserData(body); // attach the body (shape) to the wall

		body = PhysicsFactory.createBoxBody(mPhysicsWorld, wall_left, BodyType.StaticBody, ObjectFixtures.getWall());
		body.setUserData(BODY_TYPE_WALL_LEFT); // set the identifier for this body (e.g. for collision detection later)
		wall_left.setVisible(false); // walls are not visible
		wall_left.setUserData(body); // attach the body (shape) to the wall

		body = PhysicsFactory.createBoxBody(mPhysicsWorld, wall_right, BodyType.StaticBody, ObjectFixtures.getWall());
		body.setUserData(BODY_TYPE_WALL_RIGHT); // set the identifier for this body (e.g. for collision detection later)
		wall_right.setVisible(false); // walls are not visible
		wall_right.setUserData(body); // attach the body (shape) to the wall

		attachChild(wall_ground); // attach the wall to the scene
		attachChild(wall_left); // attach the wall to the scene
		attachChild(wall_right); // attach the wall to the scene
	}
	
	private void createSprites() {
		// attach field marks that have been created in createBackground()
		attachChild(mFieldLeft_Sprite);
		attachChild(mFieldRight_Sprite);
		attachChild(mFieldCenter_Sprite);

		float[] startingPosition;

		// CREATE PLAYER 1 BEGIN
		startingPosition = getKickoffPosition(SPRITE_PLAYER_1);
		mPlayer1_Sprite = new TiledSprite(startingPosition[0], startingPosition[1], mResourcesManager.mPlayer1_TextureRegion, mVertexManager);
		mPlayer1_Body = createPlayerBody(mPhysicsWorld, mPlayer1_Sprite, BodyType.DynamicBody, ObjectFixtures.getPlayer()); // physics world - object shape - body type - fixture definition
		mPlayer1_Body.setFixedRotation(true); // this body may not rotate
		mPlayer1_Body.setUserData(BODY_TYPE_PLAYER_1);
		mPlayer1_Body.setBullet(true);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mPlayer1_Sprite, mPlayer1_Body, true, false)); // object shape - body - updates of position - updates of rotation
		mPlayer1_Sprite.setUserData(mPlayer1_Body);
		mPlayer1_Sprite.setCurrentTileIndex(0);
		mPlayer1_Sprite.setCullingEnabled(true); // don't render this sprite if it is not visible on the screen
		PlayerData.mPlayer1_isLookingLeft = false;
		// CREATE PLAYER 1 END
		
		// CREATE PLAYER 2 BEGIN
		startingPosition = getKickoffPosition(SPRITE_PLAYER_2);
		mPlayer2_Sprite = new TiledSprite(startingPosition[0], startingPosition[1], mResourcesManager.mPlayer2_TextureRegion, mVertexManager);
		mPlayer2_Body = createPlayerBody(mPhysicsWorld, mPlayer2_Sprite, BodyType.DynamicBody, ObjectFixtures.getPlayer()); // physics world - object shape - body type - fixture definition
		mPlayer2_Body.setFixedRotation(true); // this body may not rotate
		mPlayer2_Body.setUserData(BODY_TYPE_PLAYER_2);
		mPlayer2_Body.setBullet(true);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mPlayer2_Sprite, mPlayer2_Body, true, false)); // object shape - body - updates of position - updates of rotation
		mPlayer2_Sprite.setUserData(mPlayer2_Body);
		mPlayer2_Sprite.setCurrentTileIndex(1);
		mPlayer2_Sprite.setCullingEnabled(true); // don't render this sprite if it is not visible on the screen
		PlayerData.mPlayer2_isLookingLeft = true;
		// CREATE PLAYER 2 END
		
		// CREATE THE BALL BEGIN
		startingPosition = getKickoffPosition(SPRITE_BALL);
		mBall_Sprite = new Sprite(startingPosition[0], startingPosition[1], mResourcesManager.mBall_TextureRegion, mVertexManager);
		mBall_Body = PhysicsFactory.createCircleBody(mPhysicsWorld, mBall_Sprite, BodyType.DynamicBody, ObjectFixtures.getBall()); // physics world - object shape - body type - fixture definition
		mBall_Body.setUserData(BODY_TYPE_BALL);
		mBall_Body.setLinearDamping(0.35f); // slows the ball down gradually
		mBall_Body.setBullet(true);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mBall_Sprite, mBall_Body, true, true)); // object shape - body - updates of position - updates of rotation
		mBall_Sprite.setUserData(mBall_Body);
		// CREATE THE BALL END

		attachChild(mPlayer1_Sprite);
		attachChild(mPlayer2_Sprite);
		attachChild(mBall_Sprite);
		
		if (getDebug() == DEBUG_RENDER) {
			DebugRenderer debugger = new DebugRenderer(mPhysicsWorld, mVertexManager);
			attachChild(debugger);
		}
	}
	
	private static class Controller {

		private static Sprite left;
		private static Sprite right;
		private static Sprite up;

	}
	
	private void createController() {
		final float controllerWidthHor = mResourcesManager.mControlLeft_TextureRegion.getWidth();
		final float controllerWidthVer = mResourcesManager.mControlUp_TextureRegion.getWidth();
		final float controllerHeight = mResourcesManager.mControlLeft_TextureRegion.getHeight();

		Controller.left = new Sprite(controllerWidthHor*0.5f, controllerHeight*0.5f, mResourcesManager.mControlLeft_TextureRegion, mVertexManager) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown() || touchEvent.isActionMove()) {
					HumanMoves.unsetTouchDirection(DIRECTION_RIGHT);
					HumanMoves.setTouchDirection(DIRECTION_LEFT);
					this.setAlpha(CONTROL_ALPHA_PRESSED);
				}
				else {
					HumanMoves.unsetTouchDirection(DIRECTION_LEFT);
					this.setAlpha(CONTROL_ALPHA_NORMAL);
				}
				return true;
			};
		};
		mHUD.registerTouchArea(Controller.left);
		mHUD.attachChild(Controller.left);
		
		Controller.right = new Sprite(controllerWidthHor*1.5f+CONTROL_SPACING, controllerHeight*0.5f, mResourcesManager.mControlRight_TextureRegion, mVertexManager) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown() || touchEvent.isActionMove()) {
					HumanMoves.unsetTouchDirection(DIRECTION_LEFT);
					HumanMoves.setTouchDirection(DIRECTION_RIGHT);
					this.setAlpha(CONTROL_ALPHA_PRESSED);
				}
				else {
					HumanMoves.unsetTouchDirection(DIRECTION_RIGHT);
					this.setAlpha(CONTROL_ALPHA_NORMAL);
				}
				return true;
			};
		};
		mHUD.registerTouchArea(Controller.right);
		mHUD.attachChild(Controller.right);
		
		Controller.up = new Sprite(GameScreen.CAMERA_WIDTH-controllerWidthVer/2, controllerHeight/2, mResourcesManager.mControlUp_TextureRegion, mVertexManager) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown() || touchEvent.isActionMove()) {
					HumanMoves.setTouchDirection(DIRECTION_UP);
					this.setAlpha(CONTROL_ALPHA_PRESSED);
				}
				else {
					HumanMoves.unsetTouchDirection(DIRECTION_UP);
					this.setAlpha(CONTROL_ALPHA_NORMAL);
				}
				return true;
			};
		};
		mHUD.registerTouchArea(Controller.up);
		mHUD.attachChild(Controller.up);

		mHUD.setTouchAreaBindingOnActionDownEnabled(true);
		mHUD.setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	private float[] getKickoffPosition(int spriteID) {
		switch (spriteID) {
			case SPRITE_PLAYER_1: return new float[] { GameScreen.FIELD_WIDTH/2-190, GameScreen.FIELD_HEIGHT*0.5f };
			case SPRITE_PLAYER_2: return new float[] { GameScreen.FIELD_WIDTH/2+190, GameScreen.FIELD_HEIGHT*0.5f };
			case SPRITE_BALL: return new float[] { GameScreen.FIELD_WIDTH/2, GameScreen.FIELD_HEIGHT*0.85f };
			default: return new float[] { 0.0f, 0.0f };
		}
	}
	
	private float[] getGoalkickPosition(int spriteID, int side) {
		switch (spriteID) {
			case SPRITE_PLAYER_1: return new float[] { GOAL_WIDTH*2, GameScreen.FIELD_HEIGHT*0.5f };
			case SPRITE_PLAYER_2: return new float[] { GameScreen.FIELD_WIDTH-GOAL_WIDTH*2, GameScreen.FIELD_HEIGHT*0.5f };
			case SPRITE_BALL: return new float[] { (side == GOALKICK_LEFT ? GOAL_WIDTH*3.5f : GameScreen.FIELD_WIDTH-GOAL_WIDTH*3.5f), GameScreen.FIELD_HEIGHT*0.85f };
			default: return new float[] { 0.0f, 0.0f };
		}
	}
	
	private void resetSpeeds() {
		mPlayer1_Body.setLinearVelocity(0.0f, 0.0f);
		mPlayer1_Sprite.setCurrentTileIndex(0);
		PlayerData.mPlayer1_isLookingLeft = false;
		
		mPlayer2_Body.setLinearVelocity(0.0f, 0.0f);
		mPlayer2_Sprite.setCurrentTileIndex(1);
		PlayerData.mPlayer2_isLookingLeft = true;

		mBall_Body.setLinearVelocity(0.0f, 0.0f);
		mBall_Body.setAngularVelocity(0.0f);		
	}
	
	private void initKickoff() {
		//mPlayersMayMove = false; // DEBUG_SCREENSHOT_MODE (enable all lines with this comment)
		float[] startingPosition;
		resetSpeeds();

		startingPosition = getKickoffPosition(SPRITE_PLAYER_1);
		mPlayer1_Body.setTransform(startingPosition[0]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, startingPosition[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0.0f);

		startingPosition = getKickoffPosition(SPRITE_PLAYER_2);
		mPlayer2_Body.setTransform(startingPosition[0]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, startingPosition[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0.0f);

		startingPosition = getKickoffPosition(SPRITE_BALL);
		mBall_Body.setTransform(startingPosition[0]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, startingPosition[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0.0f);
		
		playSound(mResourcesManager.mAudio_Whistle);
	}
	
	private void initGoalkick(int side) {
		float[] startingPosition;
		resetSpeeds();

		startingPosition = getGoalkickPosition(SPRITE_PLAYER_1, side);
		mPlayer1_Body.setTransform(startingPosition[0]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, startingPosition[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0.0f);

		startingPosition = getGoalkickPosition(SPRITE_PLAYER_2, side);
		mPlayer2_Body.setTransform(startingPosition[0]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, startingPosition[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0.0f);

		startingPosition = getGoalkickPosition(SPRITE_BALL, side);
		mBall_Body.setTransform(startingPosition[0]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, startingPosition[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0.0f);
		
		playSound(mResourcesManager.mAudio_Whistle);
	}
	
	private static Body createPlayerBody(final PhysicsWorld pPhysicsWorld, final IEntity pEntity, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		final float width = pEntity.getWidth() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT; // because box2d's Body coordinates are in meters
		final float height = pEntity.getHeight() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT; // because box2d's Body coordinates are in meters
		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pEntity, getPlayerVertices(width, height), pBodyType, pFixtureDef);
	}
	
	/**
	 * Returns a singleton instance of the vertex array for players (created with free tool AndEngine VertexHelper)
	 * 
	 * @param width the width of the player sprite to create vertices for
	 * @param height the height of the player sprite to create vertices for
	 */
	private static Vector2[] getPlayerVertices(float width, float height) {
		if (mPlayerVertices == null) {
			mPlayerVertices = new Vector2[] {
				new Vector2(-0.44792f*width, -0.02344f*height),
				new Vector2(+0.01562f*width, -0.49219f*height),
				new Vector2(+0.47396f*width, -0.02344f*height),
				new Vector2(+0.39062f*width, +0.32031f*height),
				new Vector2(-0.01562f*width, +0.48828f*height),
				new Vector2(-0.45833f*width, +0.29297f*height)
			};
		}
		return mPlayerVertices;
	}
	
	private void createPhysics() {
		mPhysicsWorld = new FixedStepPhysicsWorld(GameScreen.FRAMES_PER_SECOND, new Vector2(0, GRAVITY), false, 8, 4); // create world physics (steps per second - gravity - allow sleep - velocity iterations - position iterations)
		mPhysicsWorld.setContinuousPhysics(true);
		registerUpdateHandler(mPhysicsWorld);
		mPhysicsWorld.setContactListener(this);
	}
	
	private void closeTutorial() {
		setOnSceneTouchListener(null);
		mCamera.setHUD(null);
		mTutorial.setVisible(false);
		if (mIntro_Text != null) {
			mIntro_Text.detachSelf();
			mIntro_Text.dispose();
		}
		mTutorial.detachSelf();
		mTutorial.dispose();
		mTutorial = null;
	}
	
	private void createTutorial() {
		mTutorial = new HUD(); // create fixed HUD for tutorial display
		
		mIntro_Text = new Text(0, 0, mResourcesManager.mFontSmall, Phrases.getPossibleCharacters(Phrases.FIELD_TAP_TO_START), new TextOptions(HorizontalAlign.CENTER), mVertexManager); // prepare memory with all possible chars
		mIntro_Text.setColor(1.0f, 1.0f, 1.0f);
		updateText(mIntro_Text, Phrases.mTapToStart, GameScreen.CAMERA_WIDTH/2, GameScreen.CAMERA_HEIGHT/3, TEXT_HALIGN_CENTER, TEXT_VALIGN_TOP);
		mTutorial.attachChild(mIntro_Text);
		
		mCamera.setHUD(mTutorial);
		setOnSceneTouchListener(this);
	}
	
	private void createHUD() {
		mHUD = new HUD(); // create fixed HUD for static text display
		
		mTimeText = new Text(0, 0, mResourcesManager.mFontSmall, Phrases.getPossibleCharacters(Phrases.FIELD_TIME), new TextOptions(HorizontalAlign.LEFT), mVertexManager); // prepare memory with all possible chars
		mExclamation = new Text(0, 0, mResourcesManager.mFontBig, Phrases.getPossibleCharacters(Phrases.FIELD_EXCLAMATION), new TextOptions(HorizontalAlign.CENTER), mVertexManager); // prepare memory with all possible chars
		mScoreText = new Text(0, 0, mResourcesManager.mFontSmall, Phrases.getPossibleCharacters(Phrases.FIELD_SCORE), new TextOptions(HorizontalAlign.RIGHT), mVertexManager); // prepare memory with all possible chars
		mBottomText = new Text(0, 0, mResourcesManager.mFontSmall, Phrases.getPossibleCharacters(Phrases.FIELD_TAP_TO_LEAVE), new TextOptions(HorizontalAlign.CENTER), mVertexManager); // prepare memory with all possible chars
		updateScore();
		updateExclamation();
		updateText(mBottomText, Phrases.mEmpty, GameScreen.CAMERA_WIDTH/2, GameScreen.CAMERA_HEIGHT/3, TEXT_HALIGN_CENTER, TEXT_VALIGN_TOP);
		
		mHUD.attachChild(mTimeText);
		mHUD.attachChild(mExclamation);
		mHUD.attachChild(mScoreText);
		mHUD.attachChild(mBottomText);
		mCamera.setHUD(mHUD);
	}
	
	private long getGameTime() {
		return isLimitedByGoals() ? (System.currentTimeMillis()-mStartTime)/1000 : (mGameDuration-(System.currentTimeMillis()-mStartTime)/1000);
	}
	
	private boolean isLimitedByGoals() {
		return mGameDuration == -1;
	}
	
	private void updateExclamation() {
		if (mState != STATE_ENDED) {
			updateText(mExclamation, mExclamationText == EXCLAMATION_GOAL ? Phrases.mGoal : (mExclamationText == EXCLAMATION_OUT ? Phrases.mOut : Phrases.mEmpty), GameScreen.CAMERA_WIDTH/2, GameScreen.CAMERA_HEIGHT-PADDING_TEXTS_OUTTER, TEXT_HALIGN_CENTER, TEXT_VALIGN_TOP);
		}
	}
	
    /**
     * Displays the game time and score line and finishes the game in case of a Golden Goal or goal-limit game
     * @param gameTime game time (in seconds) to be displayed
     * @param isLimitedByGoals whether the game is limited by goals (true) or by time (false)
     */
    public void updateScore(long gameTime, boolean isLimitedByGoals) {
    	if (mState != STATE_ENDED) {
    		updateText(mScoreText, String.format(SCORE_FORMAT, mMatch.getPlayerHome().getShortName(), mMatch.getPlayerGuest().getShortName(), mMatch.getGoalsHome(), mMatch.getGoalsGuest()), GameScreen.CAMERA_WIDTH-PADDING_TEXTS_OUTTER, GameScreen.CAMERA_HEIGHT-PADDING_TEXTS_OUTTER, TEXT_HALIGN_RIGHT, TEXT_VALIGN_TOP);
    	}
    	if (gameTime >= 0) {
    		mTimeText_Text = String.format(TIME_FORMAT, (int) gameTime/60, gameTime % 60);
    	}
    	else {
    		if (mMatch.getGoalsHome() == mMatch.getGoalsGuest()) {
        		mTimeText_Text = mActivity.getPhrase(GameScreen.PHRASE_GOLDEN_GOAL);
    			mState = STATE_EXTRA_TIME;
    		}
    		else {
        		mTimeText_Text = String.format(TIME_FORMAT, 0, 0);
    			setGameEnded();
    		}
    	}
    	if (mState != STATE_ENDED) {
    		updateText(mTimeText, String.valueOf(mTimeText_Text), PADDING_TEXTS_OUTTER, GameScreen.CAMERA_HEIGHT-PADDING_TEXTS_OUTTER, TEXT_HALIGN_LEFT, TEXT_VALIGN_TOP);
    	}
    	if (isLimitedByGoals && mMatch.getGoalsHome() != mMatch.getGoalsGuest() && (mMatch.getGoalsHome() >= mGoalLimit || mMatch.getGoalsGuest() >= mGoalLimit)) {
    		setGameEnded();
    	}
    }
    
	private void setGameEnded() {
		if (mState != STATE_ENDED) {
			mState = STATE_ENDED; // Golden Goal lets the game end
			//playSound(MyApp.SOUND_WHISTLE);
			if (mMatch.getGoalsHome() > mMatch.getGoalsGuest()) {
				//playSound(MyApp.SOUND_APPLAUSE);
			}
			else if (mMatch.getGoalsGuest() > mMatch.getGoalsHome()) {
				//playSound(MyApp.SOUND_BOO);
			}
			if (mActivity != null) {
				mActivity.setGameFinished();
			}
			showEndingScreen();
		}
	}
	
	private void showEndingScreen() {
		mPlayer1_Sprite.setVisible(false);
		mPlayer2_Sprite.setVisible(false);
		mBall_Sprite.setVisible(false);
		mFieldLeft_Sprite.setVisible(false);
		mFieldCenter_Sprite.setVisible(false);
		mFieldRight_Sprite.setVisible(false);

		mTimeText.setVisible(false);
		mScoreText.setVisible(false);
		synchronized (mExclamation) {
			if (mMatch.getGoalsHome() > mMatch.getGoalsGuest()) { // player (is always home team) has won
				playSound(mResourcesManager.mAudio_Applause);
				showWonLostMessage(true);
			}
			else { // opponent (is always guest team) has won
				playSound(mResourcesManager.mAudio_Boo);
				showWonLostMessage(false);
			}
		}
		updateText(mBottomText, Phrases.mTapToLeave, GameScreen.CAMERA_WIDTH/2, GameScreen.CAMERA_HEIGHT/3, TEXT_HALIGN_CENTER, TEXT_VALIGN_TOP);
		
		setOnSceneTouchListener(this);
		setIgnoreUpdate(true);
	}
	
	private void showWonLostMessage(boolean won) {
		updateText(mExclamation, won ? Phrases.mYouWon : Phrases.mYouLost, GameScreen.CAMERA_WIDTH/2, GameScreen.CAMERA_HEIGHT*2/3, TEXT_HALIGN_CENTER, TEXT_VALIGN_BOTTOM);
	}
	
	private void setDirection(int spriteID, int directionID) {
		if (spriteID == SPRITE_PLAYER_1) {
			if (PlayerData.mPlayer1_isLookingLeft && directionID == DIRECTION_RIGHT) { // turning from left to right
				mPlayer1_Sprite.setCurrentTileIndex(0);
				PlayerData.mPlayer1_isLookingLeft = false;
			}
			else if (!PlayerData.mPlayer1_isLookingLeft && directionID == DIRECTION_LEFT) { // turning from right to left
				mPlayer1_Sprite.setCurrentTileIndex(1);
				PlayerData.mPlayer1_isLookingLeft = true;
			}
		}
		else if (spriteID == SPRITE_PLAYER_2) {
			if (PlayerData.mPlayer2_isLookingLeft && directionID == DIRECTION_RIGHT) { // turning from left to right
				mPlayer2_Sprite.setCurrentTileIndex(0);
				PlayerData.mPlayer2_isLookingLeft = false;
			}
			else if (!PlayerData.mPlayer2_isLookingLeft && directionID == DIRECTION_LEFT) { // turning from right to left
				mPlayer2_Sprite.setCurrentTileIndex(1);
				PlayerData.mPlayer2_isLookingLeft = true;
			}
		}
	}
	
	private static class HumanMoves {

		/** Bitmask indicating which directions the touch control was used for (may be a combination of left/right and top/bottom) */
		private volatile static int mTouchDirection;
		
		private synchronized static void setTouchDirection(final int direction) {
			mTouchDirection |= direction;
		}
		
		private synchronized static void unsetTouchDirection(final int direction) {
			mTouchDirection &= ~direction;
		}
		
		private synchronized static boolean isTouchDirection(final int direction) {
			return (HumanMoves.mTouchDirection & direction) == direction;
		}

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (mState == STATE_WAITING) {
			mState = STATE_RUNNING;
			closeTutorial();
			mStartTime = System.currentTimeMillis();

			createHUD();
			createPhysics();
			createWorldBoundaries();
			createSprites();
			createController();
			
			registerUpdateHandler(new IUpdateHandler() {
				@Override
				public void onUpdate(float pSecondsElapsed) {
					if (isPendingKickoff()) {
						initKickoff();
						setPendingKickoff(false);
					}
					else if (getPendingGoalkick() != GOALKICK_NONE) {
						initGoalkick(getPendingGoalkick());
						setPendingGoalkick(GOALKICK_NONE);
					}
					else {
						synchronized (BallData.mMovement) {
							if (BallData.mMovement.y != 0.0f) {
								mBall_Body.applyForce(BallData.mMovement, mBall_Body.getWorldCenter());
								BallData.mMovement.set(0.0f, 0.0f);
								synchronized (BallData.mSpeed) {
									BallData.mSpeed = mBall_Body.getLinearVelocity();
									if (BallData.mSpeed.y > BALL_MAX_SPEED_Y) {
										BallData.mSpeed.set(BallData.mSpeed.x, BALL_MAX_SPEED_Y);
										mBall_Body.setLinearVelocity(BallData.mSpeed);
									}
								}
							}
							else {
								makeHumanMoves();
								makeAIMoves();
							}
						}
					}
					if (mTimestepCounter == 0) {
						updateScore();
					}
					mTimestepCounter = (mTimestepCounter+1) % TIME_STEP_INTERVAL;
					if (mExclamationExpires == Long.MAX_VALUE) {
						mExclamationExpires = System.currentTimeMillis()+EXCLAMATION_DISPLAY_DURATION;
						updateExclamation();
					}
					else if (mExclamationExpires > 0 && mExclamationExpires < System.currentTimeMillis()) {
						mExclamationText = EXCLAMATION_NONE;
						updateExclamation();
					}
				}
				@Override
				public void reset() { }
			});

			mCamera.setBounds(0, 0, GameScreen.FIELD_WIDTH, GameScreen.FIELD_HEIGHT);
			mCamera.setBoundsEnabled(true);
			mCamera.setChaseEntity(mBall_Sprite);
			
			playSound(mResourcesManager.mAudio_Whistle); // referee announces kick-off
		}
		else if (mState == STATE_ENDED) {
			if (mActivity != null && pSceneTouchEvent.isActionDown()) {
				mActivity.finish();
			}
		}
		return true; // event has been consumed
	}
	
	private synchronized void setPendingKickoff(boolean status) {
		mPendingKickoff = status;
	}
	
	private synchronized boolean isPendingKickoff() {
		return mPendingKickoff;
	}
	
	private synchronized void setPendingGoalkick(int side) {
		mPendingGoalkick = side;
	}
	
	private synchronized int getPendingGoalkick() {
		return mPendingGoalkick;
	}

	private synchronized boolean isCollision(final Fixture x1, final Fixture x2, final String type1, final String type2) {
		return (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) && ((x1.getBody().getUserData().equals(type1) && x2.getBody().getUserData().equals(type2)) || (x1.getBody().getUserData().equals(type2) && x2.getBody().getUserData().equals(type1)));
	}

	@Override
	public void beginContact(Contact contact) { // box2d locks the world inside all ContactListeners so you can only set flags here and move or modify game objects in the main game loop
		mCollisionFixture1 = contact.getFixtureA();
		mCollisionFixture2 = contact.getFixtureB();
		if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_PLAYER_1, BODY_TYPE_WALL_GROUND)) {
			PlayerData.mPlayer1_isJumping = false;
		}
		else if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_PLAYER_2, BODY_TYPE_WALL_GROUND)) {
			PlayerData.mPlayer2_isJumping = false;
		}
		else if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_PLAYER_1, BODY_TYPE_PLAYER_2)) {
			if (mPlayer1_Sprite.getY() > (mPlayer2_Sprite.getY()+mPlayer2_Sprite.getHeight()/2)) {
				mPlayer2_Sprite.setCurrentTileIndex(PlayerData.mPlayer2_isLookingLeft ? 3 : 2);
				PlayerData.mPlayer2_isBoing = true;
			}
			else if (mPlayer2_Sprite.getY() > (mPlayer1_Sprite.getY()+mPlayer1_Sprite.getHeight()/2)) {
				mPlayer1_Sprite.setCurrentTileIndex(PlayerData.mPlayer1_isLookingLeft ? 3 : 2);
				PlayerData.mPlayer1_isBoing = true;
			}
		}
		else if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_BALL, BODY_TYPE_WALL_LEFT)) { // ball did cross one goal line (either left or right)
			if (mBall_Sprite.getY() < FIELD_GOALLINE_Y) {
				mMatch.addGoals(0, 1);
				setPendingKickoff(true);
				setExclamation(EXCLAMATION_GOAL);
			}
			else {
				setPendingGoalkick(GOALKICK_LEFT);
				setExclamation(EXCLAMATION_OUT);
			}
		}
		else if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_BALL, BODY_TYPE_WALL_RIGHT)) { // ball did cross one goal line (either left or right)
			if (mBall_Sprite.getY() < FIELD_GOALLINE_Y) {
				mMatch.addGoals(1, 0);
				setPendingKickoff(true);
				setExclamation(EXCLAMATION_GOAL);
			}
			else {
				setPendingGoalkick(GOALKICK_RIGHT);
				setExclamation(EXCLAMATION_OUT);
			}
		}
	}
	
	private void setExclamation(int exclamationID) {
		mExclamationText = exclamationID;
		mExclamationExpires = Long.MAX_VALUE;
	}

	@Override
	public void endContact(Contact contact) { // box2d locks the world inside all ContactListeners so you can only set flags here and move or modify game objects in the main game loop
		mCollisionFixture1 = contact.getFixtureA();
		mCollisionFixture2 = contact.getFixtureB();
		if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_PLAYER_1, BODY_TYPE_PLAYER_2)) { // players did collide
			if (PlayerData.mPlayer1_isBoing) { // player 1 was BOINGed by player 2
				mPlayer1_Sprite.setCurrentTileIndex(PlayerData.mPlayer1_isLookingLeft ? 1 : 0); // show the normal player graphic again
				PlayerData.mPlayer1_isBoing = false;
			}
			if (PlayerData.mPlayer2_isBoing) { // player 2 was BOINGed by player 1
				mPlayer2_Sprite.setCurrentTileIndex(PlayerData.mPlayer2_isLookingLeft ? 1 : 0); // show the normal player graphic again
				PlayerData.mPlayer2_isBoing = false;
			}
		}
		else {
			if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_BALL, BODY_TYPE_PLAYER_1)) { // player 1 kicked the ball
				if (!PlayerData.mPlayer1_isJumping) {
					BallData.mMovement.set(0.0f, mBall_Body.getWorldCenter().y < 6.65f ? BALL_KICK_ACCELERATION_Y : BALL_KICK_ACCELERATION_Y*0.35f); // only accelerate normal kicks that much and if the ball is higher (head kick) just a little bit (y-value by experience)
				}
				playSound(mResourcesManager.mAudio_Kick);
			}
			else if (isCollision(mCollisionFixture1, mCollisionFixture2, BODY_TYPE_BALL, BODY_TYPE_PLAYER_2)) { // player 2 kicked the ball
				if (!PlayerData.mPlayer2_isJumping) {
					BallData.mMovement.set(0.0f, mBall_Body.getWorldCenter().y < 6.65f ? BALL_KICK_ACCELERATION_Y : BALL_KICK_ACCELERATION_Y*0.35f); // only accelerate normal kicks that much and if the ball is higher (head kick) just a little bit (y-value by experience)
				}
				playSound(mResourcesManager.mAudio_Kick);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) { }

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) { }

}
