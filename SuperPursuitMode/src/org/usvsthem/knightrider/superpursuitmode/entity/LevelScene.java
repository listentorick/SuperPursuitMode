package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.DesertFurnitureFactory;
import org.usvsthem.knightrider.superpursuitmode.EnemyFactory;
import org.usvsthem.knightrider.superpursuitmode.Textures;
import org.usvsthem.knightrider.superpursuitmode.Theme;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class LevelScene extends Scene {
	
	public static int ACTOR_INDEX = 0;
	
	private Engine engine;
	//private SurfaceScrollDetector scrollDetector;
	private Terrain terrain;
	private PhysicsWorld physicsWorld;
	private static final Vector2 GRAVITY = new Vector2(0.0F, SensorManager.GRAVITY_EARTH);
	private PlayerActor playerActor;
	private TextureRegionLibrary textureRegionLibrary;
	
	private float minZoom = (1f/2f);
	private float maxViewHeight;
	private float HERO_X_OFFSET  = 100;
	private float CAMERA_Y_PADDING  = 100;
	private SmoothCamera camera;
	
	private FurnitureMultiPool furniturePool;
	private int NUM_FURNITURE = 10;
	
	private ArrayList<Sprite> furnitureInScene;
	private Theme theme;
	private EnemyPool enemyPool; 

	public LevelScene(final Engine engine, TextureRegionLibrary textureRegionLibrary){
	
		this.engine = engine;
		this.camera = (SmoothCamera) engine.getCamera();
		maxViewHeight = engine.getCamera().getHeightRaw() / minZoom;
		
		theme = Theme.DESERT;
		
		SpriteBackground bg = new SpriteBackground(new Sprite(0, 0,800,400, textureRegionLibrary.get(Textures.SKY),engine.getVertexBufferObjectManager()));
		bg.setColor(1f,1f,1f);
		this.setBackground(bg);
		
		this.textureRegionLibrary = textureRegionLibrary;
		
		createPhysicsWorld();
		createTerrain();
		
		this.furnitureInScene = new ArrayList<Sprite>();
		
		this.playerActor = createPlayer(0, 10);
		
	//	Karr karr = new Karr(1600, terrain.getYAt(1600) - 100, Direction.RIGHT_TO_LEFT, engine, physicsWorld, terrain, this, textureRegionLibrary);
		//this.registerUpdateHandler(karr);
		playerActor.wake();
		
		configureFurniture();
		configureEnemies();
		configureCamera();
		
	}
	
	public void createTerrain(){
		
		terrain = new Terrain(engine, this.physicsWorld);
		this.attachChild(terrain);
		this.registerUpdateHandler(terrain);
		
		this.setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				
				
				
				//turbo boost on the rhs 
				if((pSceneTouchEvent.getX()- camera.getXMin())>camera.getWidth()/2){
				
					if(pSceneTouchEvent.isActionUp()) {
						playerActor.stopChargingTurboBoost();
					}
					if(pSceneTouchEvent.isActionDown()){
						playerActor.startChargingTurboBoost();
					}
				}
				
				if((pSceneTouchEvent.getX()- camera.getXMin())<camera.getWidth()/2){
					
					if(pSceneTouchEvent.isActionUp()) {
						playerActor.stopAccelerating();
					}
					if(pSceneTouchEvent.isActionDown()){
						playerActor.startAccelerating();
					}
				}
					
				return false;
			}
		});
		
	
	}
	
	private void configureFurniture() {
		furniturePool = new FurnitureMultiPool();

		FurniturePool desertFurniturePool =  new FurniturePool(new DesertFurnitureFactory(engine, textureRegionLibrary));
		desertFurniturePool.batchAllocatePoolItems(NUM_FURNITURE);
		furniturePool.registerPool(Theme.DESERT.ordinal(),desertFurniturePool);

		
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				
				manageFurniture();
			}
		});
	}

	private void manageFurniture(){
		Sprite furniture;
		for(int i=0; i<furnitureInScene.size();i++) {
			furniture = furnitureInScene.get(i);
			if((furniture.getX() + furniture.getWidth())<camera.getXMin()){
				this.removeFurnitureFromSceneToPool(furniture);
			}
		}
		
		for(int i=0; i<furniturePool.getAvailableItemCount(theme.ordinal());i++){
			//grabs a random item from the pool and adds it to the scene.
			this.addFurnitureFromPoolToScene(theme);
		}
	}
	
	private ArrayList<TerrainAlignedActor> enemies = new ArrayList<TerrainAlignedActor>();
	
	private void configureEnemies(){
		
		
		enemyPool = new EnemyPool(new EnemyFactory(engine,this, physicsWorld,terrain,textureRegionLibrary));
		enemyPool.batchAllocatePoolItems(1);
		
		
		this.registerUpdateHandler(new TimerHandler(10,true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// TODO Auto-generated method stub
				
				Log.d("ENEMY", "Adding enemy");
				if(enemyPool.getAvailableItemCount()>0) {
					TerrainAlignedActor actor = enemyPool.obtainPoolItem();
					float x = camera.getXMax();
					float y = terrain.getYAt(x);
					actor.setPosition(x, y);
					
					Log.d("ENEMY", "positioning enemy");
					registerUpdateHandler(actor);
					enemies.add(actor);
				}
			}
		}));
		
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				int numEnemies = enemies.size();
				TerrainAlignedActor enemy;
				for(int i=0; i<numEnemies;i++){
					enemy = enemies.get(i);
					if(enemy.getX() < camera.getXMin()){
						enemyPool.recyclePoolItem(enemy);
						unregisterUpdateHandler(enemy);
						enemies.remove(i);
						break;
					}
				}

			}
		});
		
	}

	/*
	 * Adds (positions) a furniture item from the pool to the scene
	 */
	private void addFurnitureFromPoolToScene(Theme theme) {
		Sprite furniture = furniturePool.obtainPoolItem(theme.ordinal());
		this.attachChild(furniture);
		positionTerrainFuniture(furniture);
		furnitureInScene.add(furniture);
	}
	
	private void removeFurnitureFromSceneToPool(Sprite furniture) {
		furnitureInScene.remove(furniture);
		furniturePool.recyclePoolItem(furniture);
		this.detachChild(furniture);
	}
	
	private void positionTerrainFuniture(Sprite furniture){
		float xPos = camera.getXMax() + ((float)Math.random()* camera.getWidth());
		float yPos = terrain.getYAt(xPos);
		furniture.setPosition(xPos, yPos - furniture.getHeight());
	}
	
	public void createPhysicsWorld(){
		this.physicsWorld = new FixedStepPhysicsWorld(60,new Vector2(0,9.8f), true);

		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				
				physicsWorld.onUpdate(pSecondsElapsed);
			}
		});
	}
	
	public void configureCamera(){
		
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			private int NUM_PREV_MAXY  = 15;//35;
			double[] weightedMaxY = new double[NUM_PREV_MAXY];
			
			int _nextMaxY = 0;
			
			
			@Override
			public void onUpdate(float pSecondsElapsed) {

				float maxY =  terrain.getMaxTerrainHeightInRange(camera.getXMin(), camera.getXMax());
				float minY = playerActor.getY();
				
				minY-=CAMERA_Y_PADDING; //add some loverly padding
				maxY+=CAMERA_Y_PADDING;
				
				//Due to the discrete nature of our terrain generation
				//we use a weighted maxY when positioning/zooming the camera.
				//this avoids jerky camera motion
				float averageMaxY = 0;
				for(int i = 0; i < NUM_PREV_MAXY; ++i) { 
					averageMaxY+=weightedMaxY[i];
				}
				
				averageMaxY = averageMaxY/NUM_PREV_MAXY;
				      
				weightedMaxY[_nextMaxY++] = maxY;
				
				if (_nextMaxY >= NUM_PREV_MAXY) _nextMaxY = 0;
				
				//lets calculate the zoom we need - based upon terrain and player
				float zoom = camera.getHeightRaw()/(averageMaxY-minY);
							
				float cameraY = 0;
				
				if(zoom>1f){
					zoom = 1f;
				} 
				
				if(Float.compare(zoom,minZoom)<0){
					zoom = minZoom;
					
					//if we're at the max zoom anchor to the player
					cameraY = minY + (maxViewHeight/2.0f);
				} else {
					//anchor to the lowest hill.
					cameraY = averageMaxY - (camera.getHeight()/2.0f);
				}
				
				float cameraX = playerActor.getX()+(camera.getWidth()/2 - (HERO_X_OFFSET / zoom));
				
				camera.setCenter(cameraX , cameraY);
				camera.setZoomFactor(zoom);
			}
			
			
		});
	
	}
	
	public PlayerActor createPlayer(float x, float y){
		PlayerActor playerActor = new PlayerActor(x, y, engine, physicsWorld, terrain ,this, textureRegionLibrary);
		this.registerUpdateHandler(playerActor);
		
		
		return playerActor;
	}
	
	public void createTestBall(){
		
	}
	
}
