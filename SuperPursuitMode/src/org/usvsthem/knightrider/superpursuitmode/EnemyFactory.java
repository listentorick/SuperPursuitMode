package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.entity.Direction;
import org.usvsthem.knightrider.superpursuitmode.entity.Karr;
import org.usvsthem.knightrider.superpursuitmode.entity.LevelScene;
import org.usvsthem.knightrider.superpursuitmode.entity.Terrain;
import org.usvsthem.knightrider.superpursuitmode.entity.TerrainAlignedActor;

public class EnemyFactory implements IEnemyFactory {

	private TextureRegionLibrary textureRegionLibrary;
	private Engine engine;
	private Terrain terrain;
	private PhysicsWorld physicsWorld;
	private LevelScene levelScene;
	
	public EnemyFactory(Engine engine,LevelScene levelScene, PhysicsWorld physicsWorld, Terrain terrain, TextureRegionLibrary textureRegionLibrary){
		this.engine = engine;
		this.textureRegionLibrary = textureRegionLibrary;
		this.physicsWorld = physicsWorld;
		this.terrain = terrain;
		this.levelScene =  levelScene;
	}
	
	private TerrainAlignedActor createKarr(){
		Karr karr = new Karr(-1000, -1000, Direction.RIGHT_TO_LEFT, engine, physicsWorld, terrain, levelScene, textureRegionLibrary);
		return karr;
	}

	@Override
	public TerrainAlignedActor createEnemy() {
		return createKarr();
	}


}	
