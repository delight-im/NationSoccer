package im.delight.soccer.andengine;

import im.delight.soccer.util.GameScreen;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class BaseScene extends Scene {

	public static final int TEXT_HALIGN_LEFT = 1;
	public static final int TEXT_HALIGN_CENTER = 2;
	public static final int TEXT_HALIGN_RIGHT = 3;
	public static final int TEXT_VALIGN_TOP = 1;
	public static final int TEXT_VALIGN_CENTER = 2;
	public static final int TEXT_VALIGN_BOTTOM = 3;
	protected Engine mEngine;
	protected GameScreen mActivity;
	protected ResourcesManager mResourcesManager;
	protected VertexBufferObjectManager mVertexManager;
	protected BoundCamera mCamera;

	public BaseScene() {
		mResourcesManager = ResourcesManager.getInstance();
		mEngine = mResourcesManager.mEngine;
		mActivity = mResourcesManager.mActivity;
		mVertexManager = mResourcesManager.mVbom;
		mCamera = mResourcesManager.mCamera;
		createScene();
	}
	
	public void updateText(Text view, String text, float x, float y, int horizontalAlign, int verticalAlign) {
		if (text != null) {
			view.setText(text);
		}
		if (horizontalAlign == TEXT_HALIGN_LEFT) {
			if (verticalAlign == TEXT_VALIGN_TOP) {
				view.setPosition(x+view.getWidth()/2, y-view.getHeight()/2);
			}
			else if (verticalAlign == TEXT_VALIGN_BOTTOM) {
				view.setPosition(x+view.getWidth()/2, y+view.getHeight()/2);
			}
			else {
				view.setPosition(x+view.getWidth()/2, y);
			}
		}
		else if (horizontalAlign == TEXT_HALIGN_RIGHT) {
			if (verticalAlign == TEXT_VALIGN_TOP) {
				view.setPosition(x-view.getWidth()/2, y-view.getHeight()/2);
			}
			else if (verticalAlign == TEXT_VALIGN_BOTTOM) {
				view.setPosition(x-view.getWidth()/2, y+view.getHeight()/2);
			}
			else {
				view.setPosition(x-view.getWidth()/2, y);
			}
		}
		else {
			if (verticalAlign == TEXT_VALIGN_TOP) {
				view.setPosition(x, y-view.getHeight()/2);
			}
			else if (verticalAlign == TEXT_VALIGN_BOTTOM) {
				view.setPosition(x, y+view.getHeight()/2);
			}
			else {
				view.setPosition(x, y);
			}
		}
	}

	public abstract void createScene();

	public abstract void disposeScene();

}
