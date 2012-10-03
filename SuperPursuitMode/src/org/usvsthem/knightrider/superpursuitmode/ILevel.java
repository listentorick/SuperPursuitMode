package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.Engine;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.entity.BasePowerUp;
import org.usvsthem.knightrider.superpursuitmode.entity.PlayerActor;
import org.usvsthem.knightrider.superpursuitmode.entity.PowerBar;
import org.usvsthem.knightrider.superpursuitmode.entity.Terrain;

public interface ILevel {
	
	
	Terrain getTerrain();
	PhysicsWorld getPhysicsWorld();
	Engine getEngine();
	TextureRegionLibrary getTextureRegionLibrary();
	PlayerActor getPlayerActor();
	void addPowerUpToLevel(BasePowerUp powerup);
	void removePowerUpFromLevel(BasePowerUp powerup);
	PowerBar getPowerBar();
	
	void addEnginePower(float power);
	
	void addTurboBoostPower(float power);

}
