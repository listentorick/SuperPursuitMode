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
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.SimpleThemeProvider;
import org.usvsthem.knightrider.superpursuitmode.Textures;
import org.usvsthem.knightrider.superpursuitmode.Theme;
import org.usvsthem.knightrider.superpursuitmode.enemies.EnemyFactory;
import org.usvsthem.knightrider.superpursuitmode.enemies.EnemyPool;
import org.usvsthem.knightrider.superpursuitmode.furniture.DesertFurnitureFactory;
import org.usvsthem.knightrider.superpursuitmode.furniture.FurnitureController;
import org.usvsthem.knightrider.superpursuitmode.powerUps.BasePowerUp;
import org.usvsthem.knightrider.superpursuitmode.powerUps.PowerUpController;
import org.usvsthem.knightrider.superpursuitmode.powerUps.PowerUpFactory;
import org.usvsthem.knightrider.superpursuitmode.powerUps.PowerUpPool;
import org.usvsthem.knightrider.superpursuitmode.powerUps.RandomPositionPowerUpLayoutStrategy;
import org.usvsthem.knightrider.superpursuitmode.powerUps.StarPowerUpFactory;
import org.usvsthem.knightrider.superpursuitmode.powerUps.TerrainFollowingPowerupLayoutStrategy;
import org.usvsthem.knightrider.superpursuitmode.terrain.ITerrain;
import org.usvsthem.knightrider.superpursuitmode.terrain.Terrain;
import org.usvsthem.knightrider.superpursuitmode.ui.PowerBar;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class LevelScene extends Scene implements ILevel {
	
	public static int ACTOR_INDEX = 0;
	
	private Engine engine;
	private ITerrain terrain;
	private PhysicsWorld physicsWorld;
	private PlayerActor playerActor;
	private TextureRegionLibrary textureRegionLibrary;
	
	private ZoomCamera camera;
		
	private EnemyPool enemyPool; 

	private PowerUpController powerupController; 
	private FurnitureController furnitureController;
	
	private PowerBar enginePowerBar;
	private PowerBar turboBoostPowerBar;
	
	private float PLAYER_START_X  = 100;
	private IThemeProvider themeProvider;

	public LevelScene(final Engine engine, TextureRegionLibrary textureRegionLibrary){
	
		this.engine = engine;
		this.camera = (ZoomCamera) engine.getCamera();
		this.textureRegionLibrary = textureRegionLibrary;

		themeProvider = new SimpleThemeProvider(engine, textureRegionLibrary);
		
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
	
	public ITerrain getTerrain(){
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
	
	
	private ParallaxBackground2d background;
	
	public void createBackground() {
		
		background = this.themeProvider.createBackground();
		
		this.setBackground(background);
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				background.setParallaxValue(camera.getCenterX(), camera.getCenterY());
			}
		});
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
		
		
		//here we could load a xml doc with the terrain details in it here...
		
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
	

	private void configureFurniture() { 
		SpriteMultiPool furniturePool = this.themeProvider.createFurniturePool();
		furnitureController = new FurnitureController(this,furniturePool);
		this.registerUpdateHandler(furnitureController);
	}

	private ArrayList<TerrainAlignedActor> enemies = new ArrayList<TerrainAlignedActor>();
	
	
	public void configurePowerups(){
		
		//here we might load a different controller that reads xml
		
		powerupController = new PowerUpController(this);
		PowerUpPool powerUpPool = new PowerUpPool(new PowerUpFactory(this)); 
		PowerUpPool starPowerUpPool = new PowerUpPool(new StarPowerUpFactory(this)); 
		RandomPositionPowerUpLayoutStrategy randomPowerUpLayoutStrategy = new RandomPositionPowerUpLayoutStrategy(this, powerUpPool);
		TerrainFollowingPowerupLayoutStrategy starPowerupLayoutStrategy = new TerrainFollowingPowerupLayoutStrategy(this, starPowerUpPool);
		
		
		powerupController.addPowerUpLayoutStrategy(randomPowerUpLayoutStrategy);
		powerupController.addPowerUpLayoutStrategy(starPowerupLayoutStrategy);
		
		this.registerUpdateHandler(powerupController);
		
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

	@Override
	public void removeFurniture(Sprite furniture) {
		// TODO Auto-generated method stub
		this.attachChild(furniture);
		
	}

	@Override
	public void addFurniture(Sprite furniture) {
		// TODO Auto-generated method stub
		this.detachChild(furniture);
	}
	
	
	
}
