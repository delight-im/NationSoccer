package im.delight.soccer.andengine;

import im.delight.soccer.util.Match;

import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager {

	/** Singleton holder for a global instance of this class */
	private static SceneManager mInstance;
	private BaseScene mGameScene;

	public static SceneManager getInstance() {
		if (mInstance == null) {
			mInstance = new SceneManager();
		}
		return mInstance;
	}
	
	public void GameScene_create(final Match match, int volumeMode, OnCreateSceneCallback pOnCreateSceneCallback) {
	    ResourcesManager.getInstance().GameScene_load(match.getPlayerHome().getDrawableString(), match.getPlayerGuest().getDrawableString(), volumeMode);
		mGameScene = new GameScene();
		pOnCreateSceneCallback.onCreateSceneFinished(mGameScene);
	}
	
	public void GameScene_dispose() {
	    ResourcesManager.getInstance().GameScene_unload();
	    if (mGameScene != null) {
	    	mGameScene.disposeScene();
	    	mGameScene = null;
	    }
	}

}
