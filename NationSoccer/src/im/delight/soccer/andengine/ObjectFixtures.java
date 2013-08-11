package im.delight.soccer.andengine;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class ObjectFixtures {
	
	private static FixtureDef mPlayerFixture;
	private static FixtureDef mBallFixture;
	private static FixtureDef mWallFixture;
	
	public static FixtureDef getPlayer() {
		if (mPlayerFixture == null) {
			mPlayerFixture = PhysicsFactory.createFixtureDef(9.2f, 0.36f, 0.32f); // mass - bounce factor - friction
		}
		return mPlayerFixture;
	}
	
	public static FixtureDef getBall() {
		if (mBallFixture == null) {
			mBallFixture = PhysicsFactory.createFixtureDef(2.2f, 0.95f, 0.35f); // mass - bounce factor - friction
		}
		return mBallFixture;
	}
	
	public static FixtureDef getWall() {
		if (mWallFixture == null) {
			mWallFixture = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.5f); // mass - bounce factor - friction
		}
		return mWallFixture;
	}

}
