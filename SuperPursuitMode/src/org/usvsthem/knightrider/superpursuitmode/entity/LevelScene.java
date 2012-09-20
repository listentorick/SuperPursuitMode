package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.Textures;
import org.usvsthem.knightrider.superpursuitmode.util.Box2dDebugRenderer;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LevelScene extends Scene {
	
	public static int ACTOR_INDEX = 0;
	
	private Engine engine;
	//private SurfaceScrollDetector scrollDetector;
	private Terrain terrain;
	private PhysicsWorld physicsWorld;
	private static final Vector2 GRAVITY = new Vector2(0.0F, SensorManager.GRAVITY_EARTH);
	private PlayerActor playerActor;
	private TextureRegionLibrary textureRegionLibrary;
	
	public LevelScene(final Engine engine, TextureRegionLibrary textureRegionLibrary){
	
		this.engine = engine;
		this.textureRegionLibrary = textureRegionLibrary;
		
		
		this.setBackground(new Background(1f,1f,1f));
		
		createPhysicsWorld();
		createTerrain();
		
		
		this.playerActor = createPlayer(0, 10);
		
		playerActor.wake();
		
		configureCamera();
		
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				//
				//terrain.setOffset(engine.getCamera().getCenterX(), engine.getCamera().getCenterY());
				//terrain.setPosition(engine.getCamera().getXMin(), engine.getCamera().getYMin());
			}
		});
		
		//createPlayer(200,0);
		
		//Box2dDebugRenderer renderer = new Box2dDebugRenderer(physicsWorld, engine.getVertexBufferObjectManager());
		//this.attachChild(renderer);
		
		
		
	}
	
	public void createTerrain(){
		
		terrain = new Terrain(engine, this.physicsWorld);
		this.attachChild(terrain);
		this.registerUpdateHandler(terrain);
		
		//this.scrollDetector = new SurfaceScrollDetector(this);	
		//scrollDetector.setTriggerScrollMinimumDistance(100);
		
		this.setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				
				if(pSceneTouchEvent.isActionUp()) {
					//playerActor.setPursuitMode(false);
					playerActor.stopChargingTurboBoost();
				}
				if(pSceneTouchEvent.isActionDown()){
					playerActor.startChargingTurboBoost();
				}
					
				return false;
			}
		});
		
		
		//Vector2 position = terrain.calculatePointPosition(100,100);
		
		
		//Sprite chasisShape = new Sprite(position.x,position.y,147,55, textureRegionLibrary.get(Textures.PlayerChasis), engine.getVertexBufferObjectManager());
		//this.attachChild(chasisShape);
	
		
	}
	
	
	
	
	public void createPhysicsWorld(){
		this.physicsWorld = new FixedStepPhysicsWorld(60,new Vector2(0,9.8f), true);
		//this.physicsWorld = new PhysicsWorld(new Vector2(0,9.8f), true);
		//this.physicsWorld.setContinuousPhysics(false);
		
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
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				SmoothCamera camera = (SmoothCamera) engine.getCamera();
				
				float cameraX = playerActor.getX()+(camera.getWidth()/2 -200);
				
				//float y1 = terrain.getTerrainHeightAtX(cameraX-camera.getWidth()/2);
				//float y2 = terrain.getTerrainHeightAtX(cameraX+ camera.getWidth()/2);
				
				float minY = terrain.getMinTerrainHeightInRange(camera.getXMin(), camera.getXMax());
				float maxY =  terrain.getMaxTerrainHeightInRange(camera.getXMin(), camera.getXMax());
				float playerY = playerActor.getY();
				
				//remember 0,0 is at top left of the screen!
				if(playerY<minY) {
					minY = playerY;
				}
				
				Log.d("MinY", String.valueOf(minY));
				Log.d("MaxY", String.valueOf(maxY));
				
				
				minY-=100; //add some loverly padding
				maxY+=100;
				
				//lets calculate the zoom we need - based upon terrain and player

				float zoom = camera.getHeightRaw()/(maxY-minY);
				Log.d("Zoom", String.valueOf(minY) + " " + String.valueOf(maxY) + " " + String.valueOf(zoom));
				if(zoom>1){
					zoom = 1;
				}
				float cameraY = minY + (maxY-minY)/2.0f;
				
				camera.setCenter(cameraX , cameraY);
				camera.setZoomFactor(zoom);
			}
			
			
		});
		
		//engine.getCamera().setChaseEntity(playerActor.getPrincipleEntity());
		
	}
	
	public PlayerActor createPlayer(float x, float y){
		PlayerActor playerActor = new PlayerActor(x, y, engine, physicsWorld, this, textureRegionLibrary);
		this.registerUpdateHandler(playerActor);
		
		
		return playerActor;
	}
	
	public void createTestBall(){
		
	}
	 
	/*
	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
		
		
	}
	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		terrain.setOffset(pDistanceX, pDistanceY);
		
	}
	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		terrain.setOffset(0, 0);
		
	}*/
	


}
