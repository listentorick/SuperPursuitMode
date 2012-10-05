package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.engine.Engine;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.Textures;
import org.usvsthem.knightrider.superpursuitmode.terrain.ITerrain;

public class Karr extends TerrainAlignedActor{

	
	private static float FRONT_WHEEL_X_OFFSET = 7f;
	private static float FRONT_WHEEL_Y_OFFSET = 15f;
	private static float FRONT_WHEEL_HANGING_Y_OFFSET = 17f;
	
	private static float REAR_WHEEL_X_OFFSET = 41f;
	private static float REAR_WHEEL_Y_OFFSET = 14f;
	private static float REAR_WHEEL_HANGING_Y_OFFSET = 18f;
	
	private static float SCANNER_X_OFFSET = -9f;
	private static float SCANNER_Y_OFFSET = 7f;
	private static float SCANNER_RADIUS = 12;
	
	public Karr(float x, float y, Direction direction, Engine engine, PhysicsWorld physicsWorld,
			ITerrain terrain, LevelScene leveScene,
			TextureRegionLibrary textureRegionLibrary) {
		super(x, y, direction, engine, physicsWorld, terrain, leveScene, textureRegionLibrary);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IAreaShape constructShape(
			TextureRegionLibrary textureRegionLibrary) {
		
		Sprite chasis = new Sprite(0,0,64,24, textureRegionLibrary.get(Textures.KARR_CHASIS), engine.getVertexBufferObjectManager());
		
		Sprite scannerShape = new Sprite(SCANNER_X_OFFSET,SCANNER_Y_OFFSET,SCANNER_RADIUS*2,SCANNER_RADIUS*2, textureRegionLibrary.get(Textures.KARR_SCANNER), engine.getVertexBufferObjectManager());
		chasis.attachChild(scannerShape);
		scannerShape.setZIndex(-10);
		
		Sprite frontWheelShape = new Sprite(FRONT_WHEEL_X_OFFSET,FRONT_WHEEL_Y_OFFSET,14,14, textureRegionLibrary.get(Textures.PlayerFrontWheel), engine.getVertexBufferObjectManager());
		chasis.attachChild(frontWheelShape);
		
		Sprite rearWheelShape = new Sprite(REAR_WHEEL_X_OFFSET,REAR_WHEEL_Y_OFFSET,15,15, textureRegionLibrary.get(Textures.PlayerRearWheel), engine.getVertexBufferObjectManager());
		chasis.attachChild(rearWheelShape);

		return chasis;
	}

}
