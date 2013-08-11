package im.delight.soccer.andengine;

import im.delight.soccer.MyApp;
import im.delight.soccer.util.GameScreen;

import java.io.IOException;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import android.graphics.Color;

public class ResourcesManager {

	private static final String FONT_1 = "font1.ttf";
	private static final String FONT_DIR = "font/";
	private static final int FONT_SIZE_SMALL = 36;
	private static final int FONT_SIZE_BIG = 64;
	private static final String GRAPHICS_DIR = "gfx/";
	private static final String SOUND_DIR = "mfx/";
	private static final String GRAPHIC_FIELD_TILE = "field_tile.png";
	private static final String GRAPHIC_FIELD_LEFT = "field_left.png";
	private static final String GRAPHIC_FIELD_RIGHT = "field_right.png";
	private static final String GRAPHIC_FIELD_CENTER = "field_center.png";
	private static ResourcesManager mInstance;
	public Engine mEngine;
	public GameScreen mActivity;
	public BoundCamera mCamera;
	public VertexBufferObjectManager mVbom;
	// GENERAL BEGIN
	public ITexture mFontSmallTexture;
	public ITexture mFontBigTexture;
	public Font mFontSmall;
	public Font mFontBig;
	public ITexture mBackground_Texture;
	public TextureRegion mBackground_TextureRegion;
	public Music mAudio_Music;
	public Sound mAudio_Whistle;
	public Sound mAudio_Jump;
	public Sound mAudio_Applause;
	public Sound mAudio_Boo;
	public Sound mAudio_Kick;
	// GENERAL END
	// GAME SCREEN BEGIN
	public ITexture mPlayer1_Texture;
	public TiledTextureRegion mPlayer1_TextureRegion;
	public ITexture mPlayer2_Texture;
	public TiledTextureRegion mPlayer2_TextureRegion;
	public ITexture mBall_Texture;
	public TextureRegion mBall_TextureRegion;
	public ITexture mFieldLeft_Texture;
	public TextureRegion mFieldLeft_TextureRegion;
	public ITexture mFieldRight_Texture;
	public TextureRegion mFieldRight_TextureRegion;
	public ITexture mFieldCenter_Texture;
	public TextureRegion mFieldCenter_TextureRegion;
	// GAME SCREEN END
	
	private void loadSounds(int volumeMode) {
		try {
			if (volumeMode == MyApp.VOLUME_ALL) {
				mAudio_Music = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), mActivity, SOUND_DIR+"crowd.ogg");
				mAudio_Music.setLooping(true);
			}
			else {
				mAudio_Music = null;
			}
			if (volumeMode == MyApp.VOLUME_ALL || volumeMode == MyApp.VOLUME_SOUND) {
				mAudio_Whistle = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), mActivity, SOUND_DIR+"whistle.ogg");
				mAudio_Jump = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), mActivity, SOUND_DIR+"jump.ogg");
				mAudio_Applause = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), mActivity, SOUND_DIR+"applause.ogg");
				mAudio_Boo = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), mActivity, SOUND_DIR+"boo.ogg");
				mAudio_Kick = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), mActivity, SOUND_DIR+"kick.ogg");
			}
			else {
				mAudio_Whistle = null;
				mAudio_Jump = null;
				mAudio_Applause = null;
				mAudio_Boo = null;
				mAudio_Kick = null;
			}
		}
		catch (IOException e) { }
	}

	private void loadFonts() {
		FontFactory.setAssetBasePath(FONT_DIR);
		mFontSmallTexture = new BitmapTextureAtlas(mActivity.getTextureManager(), 512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mFontSmall = FontFactory.createStrokeFromAsset(mActivity.getFontManager(), mFontSmallTexture, mActivity.getAssets(), FONT_1, FONT_SIZE_SMALL, true, Color.WHITE, 2, Color.BLACK);
		mFontSmall.load();
		mFontBigTexture = new BitmapTextureAtlas(mActivity.getTextureManager(), 512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mFontBig = FontFactory.createStrokeFromAsset(mActivity.getFontManager(), mFontBigTexture, mActivity.getAssets(), FONT_1, FONT_SIZE_BIG, true, Color.WHITE, 3, Color.BLACK);
		mFontBig.load();
	}
	
	private void unloadFonts() {
		if (mFontSmallTexture != null) {
			mFontSmallTexture.unload();
			mFontSmallTexture = null;
		}
		if (mFontSmall != null) {
			mFontSmall.unload();
			mFontSmall = null;
		}
		if (mFontBigTexture != null) {
			mFontBigTexture.unload();
			mFontBigTexture = null;
		}
		if (mFontBig != null) {
			mFontBig.unload();
			mFontBig = null;
		}
	}
	
	public void GameScene_load(String player1Drawable, String player2Drawable, int volumeMode) {
		// GRAPHICS BEGIN
		try {
			mPlayer1_Texture = new AssetBitmapTexture(mActivity.getTextureManager(), mActivity.getAssets(), GRAPHICS_DIR+player1Drawable, TextureOptions.BILINEAR);
			mPlayer1_Texture.load();
			mPlayer1_TextureRegion = TextureRegionFactory.extractTiledFromTexture(mPlayer1_Texture, 2, 2);

			mPlayer2_Texture = new AssetBitmapTexture(mActivity.getTextureManager(), mActivity.getAssets(), GRAPHICS_DIR+player2Drawable, TextureOptions.BILINEAR);
			mPlayer2_Texture.load();
			mPlayer2_TextureRegion = TextureRegionFactory.extractTiledFromTexture(mPlayer2_Texture, 2, 2);
			
			mBall_Texture = new AssetBitmapTexture(mActivity.getTextureManager(), mActivity.getAssets(), GRAPHICS_DIR+"ball.png", TextureOptions.BILINEAR);
			mBall_Texture.load();
			mBall_TextureRegion = TextureRegionFactory.extractFromTexture(mBall_Texture);
			
			mBackground_Texture = new AssetBitmapTexture(mActivity.getTextureManager(), mActivity.getAssets(), GRAPHICS_DIR+GRAPHIC_FIELD_TILE, TextureOptions.REPEATING_BILINEAR);
			mBackground_Texture.load();
			mBackground_TextureRegion = TextureRegionFactory.extractFromTexture(mBackground_Texture);

			mFieldLeft_Texture = new AssetBitmapTexture(mActivity.getTextureManager(), mActivity.getAssets(), GRAPHICS_DIR+GRAPHIC_FIELD_LEFT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mFieldLeft_Texture.load();
			mFieldLeft_TextureRegion = TextureRegionFactory.extractFromTexture(mFieldLeft_Texture);

			mFieldRight_Texture = new AssetBitmapTexture(mActivity.getTextureManager(), mActivity.getAssets(), GRAPHICS_DIR+GRAPHIC_FIELD_RIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mFieldRight_Texture.load();
			mFieldRight_TextureRegion = TextureRegionFactory.extractFromTexture(mFieldRight_Texture);

			mFieldCenter_Texture = new AssetBitmapTexture(mActivity.getTextureManager(), mActivity.getAssets(), GRAPHICS_DIR+GRAPHIC_FIELD_CENTER, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mFieldCenter_Texture.load();
			mFieldCenter_TextureRegion = TextureRegionFactory.extractFromTexture(mFieldCenter_Texture);
		}
		catch (IOException e) { }
		// GRAPHICS END
		// OTHER BEGIN
		loadFonts();
		loadSounds(volumeMode);
		// OTHER END
	}
	
	public void GameScene_unload() {
		// GRAPHICS BEGIN
		if (mPlayer1_Texture != null) {
			mPlayer1_Texture.unload();
			mPlayer1_Texture = null;
		}
		if (mPlayer2_Texture != null) {
			mPlayer2_Texture.unload();
			mPlayer2_Texture = null;
		}
		if (mBall_Texture != null) {
			mBall_Texture.unload();
			mBall_Texture = null;
		}
		if (mBackground_Texture != null) {
			mBackground_Texture.unload();
			mBackground_Texture = null;
		}
		if (mFieldLeft_Texture != null) {
			mFieldLeft_Texture.unload();
			mFieldLeft_Texture = null;
		}
		if (mFieldRight_Texture != null) {
			mFieldRight_Texture.unload();
			mFieldRight_Texture = null;
		}
		if (mFieldCenter_Texture != null) {
			mFieldCenter_Texture.unload();
			mFieldCenter_Texture = null;
		}
		if (mCamera != null) {
			mCamera.setChaseEntity(null); // stop chasing the ball
		}
		// GRAPHICS END
		// OTHER BEGIN
		unloadFonts();
		// it seems there is no need to unload sounds manually
		// OTHER END
	}

	/**
	 * Use this method at beginning of game loading to prepare ResourcesManager properly and set all needed parameters so we can later access them from different classes (scenes etc.)
	 */
	public static void prepareManager(Engine engine, GameScreen activity, BoundCamera camera, VertexBufferObjectManager vbom) {
		getInstance().mEngine = engine;
		getInstance().mActivity = activity;
		getInstance().mCamera = camera;
		getInstance().mVbom = vbom;
	}
	
	public static ResourcesManager getInstance() {
		if (mInstance == null) {
			mInstance = new ResourcesManager();
		}
		return mInstance;
	}

}
