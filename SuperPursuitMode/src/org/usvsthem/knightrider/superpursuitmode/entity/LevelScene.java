package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
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
import org.usvsthem.knightrider.superpursuitmode.CameraPositionManager;
import org.usvsthem.knightrider.superpursuitmode.DesertFurnitureFactory;
import org.usvsthem.knightrider.superpursuitmode.EnemyFactory;
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.PowerUpController;
import org.usvsthem.knightrider.superpursuitmode.PowerUpFactory;
import org.usvsthem.knightrider.superpursuitmode.RandomPositionPowerUpLayoutStrategy;
import org.usvsthem.knightrider.superpursuitmode.StarPowerUpFactory;
import org.usvsthem.knightrider.superpursuitmode.TerrainFollowingPowerupLayoutStrategy;
import org.usvsthem.knightrider.superpursuitmode.Textures;
import org.usvsthem.knightrider.superpursuitmode.Theme;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class LevelScene extends Scene implements ILevel {
	
	public static int ACTOR_INDEX = 0;
	
	private Engine engine;
	private Terrain terrain;
	private PhysicsWorld physicsWorld;
	private PlayerActor playerActor;
	private TextureRegionLibrary textureRegionLibrary;
	
	private ZoomCamera camera;
	
	private SpriteMultiPool furniturePool;
	private int NUM_FURNITURE = 10;
	
	private ArrayList<Sprite> furnitureInScene;
	private Theme theme;
	private EnemyPool enemyPool; 

	private PowerUpController powerupController; 
	
	private PowerBar enginePowerBar;
	private PowerBar turboBoostPowerBar;
	
	private float PLAYER_START_X  = 100;

	public LevelScene(final Engine engine, TextureRegionLibrary textureRegionLibrary){
	
		this.engine = engine;
		this.camera = (ZoomCamera) engine.getCamera();
		this.textureRegionLibrary = textureRegionLibrary;
		
		theme = Theme.DESERT;
		
		createBackground();
		createPhysicsWorld();
		createTerrain();
		
		createPlayer();

		configureFurniture();
		//configureStars();
		//configureEnemies();
		configurePowerups();
		configureCamera();
		
		enginePowerBar = new PowerBar(10, 10, 10, 10, 10, playerActor.MAX_ENGINE_POWER, this.getEngine().getVertexBufferObjectManager(), textureRegionLibrary);
		turboBoostPowerBar = new PowerBar(400, 10, 10, 10, 10, playerActor.MAX_TURBO_BOOST_POWER, this.getEngine().getVertexBufferObjectManager(), textureRegionLibrary);
		
		HUD hud = new HUD();
		hud.attachChild(enginePowerBar); 
		hud.attachChild(turboBoostPowerBar);
		 
		camera.setHUD(hud);
	
	}
	
	public PowerBar getPowerBar(){
		return enginePowerBar;
	}
	
	public void addEnginePower(float power){
		playerActor.addEnginePower(power);	
	}
	
	public void addTurboBoostPower(float power){
		playerActor.addTurboBoostPower(power);
	}
	
	public Terrain getTerrain(){
		return this.terrain;
	}
	
	public PhysicsWorld getPhysicsWorld(){
		return this.physicsWorld;
	}
	
	public TextureRegionLibrary getTextureRegionLibrary(){
		return this.textureRegionLibrary;
	}
	
	public Engine getEngine(){
		return this.engine;
	}
	
	public PlayerActor getPlayerActor(){
		return this.playerActor;
	}
	
	public void createBackground() {
		SpriteBackground bg = new SpriteBackground(new Sprite(0, 0,800,480, textureRegionLibrary.get(Textures.SKY),engine.getVertexBufferObjectManager()));
		bg.setColor(1f,1f,1f);
		
		this.setBackground(bg);
	}
	
	public void createPlayer(){
		this.playerActor = createPlayer(PLAYER_START_X, terrain.getYAt(PLAYER_START_X) - 100);
		
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				enginePowerBar.setLevel(playerActor.getEnginePower());	
				turboBoostPowerBar.setLevel(playerActor.getTurboBoostPower());	
			}
		});
		
		
		
		playerActor.wake();
	}
	
	public void createTerrain(){
		
		terrain = new Terrain(engine, this.physicsWorld);
		this.attachChild(terrain);
		this.registerUpdateHandler(terrain);
		
		this.setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				
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
	

	
	
	private float minFurnitureX = 0;
	
	private void configureFurniture() { 
		furniturePool = new SpriteMultiPool();
		furnitureInScene = new ArrayList<Sprite>();
		SpritePool desertFurniturePool =  new SpritePool(new DesertFurnitureFactory(engine, textureRegionLibrary));
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
	
	
	public void configurePowerups(){
		
		powerupController = new PowerUpController(this);
		PowerUpPool powerUpPool = new PowerUpPool(new PowerUpFactory(this)); 
		PowerUpPool starPowerUpPool = new PowerUpPool(new StarPowerUpFactory(this)); 
		RandomPositionPowerUpLayoutStrategy randomPowerUpLayoutStrategy = new RandomPositionPowerUpLayoutStrategy(this, powerUpPool);
		TerrainFollowingPowerupLayoutStrategy starPowerupLayoutStrategy = new TerrainFollowingPowerupLayoutStrategy(this, starPowerUpPool);
		
		
		powerupController.addPowerUpLayoutStrategy(randomPowerUpLayoutStrategy);
		powerupController.addPowerUpLayoutStrategy(starPowerupLayoutStrategy);
		
		this.registerUpdateHandler(powerupController);
		
		//bum since some power ups should never be arranged in a particular way, the layout strategy and the poools need to be linked..
		//perhaps IPowerUpLayoutStrategy should have create, layout, destroy methods? create 
	
	}
	
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
					float y = terrain.getYAt(x) - actor.getHeight();
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
					if(enemy.getX() < camera.getXMin() - 200){
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
	 * 
	 * 
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
		if(minFurnitureX < camera.getXMax()) {
			minFurnitureX = camera.getXMax();
		}
		minFurnitureX+= furniture.getWidth() + ((float)Math.random()*200);
		
		float xPos = minFurnitureX;
		
		float yPos = terrain.getYAt(xPos);
		float yPos2 = terrain.getYAt(xPos+ furniture.getWidth());
		if(yPos2> yPos) yPos = yPos2; 
		furniture.setPosition(xPos, yPos - furniture.getHeight());
	}
	
	public void createPhysicsWorld(){
		//this.physicsWorld = new FixedStepPhysicsWorld(30,new Vector2(0,9.8f), true);

		this.physicsWorld = new PhysicsWorld(new Vector2(0,9.8f), true);

		
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
		
		CameraPositionManager cpm = new CameraPositionManager(engine,camera,terrain,playerActor);
		this.registerUpdateHandler(cpm);
	}
	
	public PlayerActor createPlayer(float x, float y){
		PlayerActor playerActor = new PlayerActor(x, y, engine, physicsWorld, terrain ,this, textureRegionLibrary);
		this.registerUpdateHandler(playerActor);
		
		
		return playerActor;
	}
	
	public void createTestBall(){
		
	}

	@Override
	public void addPowerUpToLevel(BasePowerUp powerup) {
		// TODO Auto-generated method stub
		this.attachChild(powerup);
		
	}

	@Override
	public void removePowerUpFromLevel(BasePowerUp powerup) {
		// TODO Auto-generated method stub
		this.detachChild(powerup);
	}
	
}
