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
	
	private float minZoom = (1f/2f);
	private float maxViewHeight;
	private float HERO_X_OFFSET  = 100;
	private float CAMERA_Y_PADDING  = 100;
	private SmoothCamera camera;
	
	public LevelScene(final Engine engine, TextureRegionLibrary textureRegionLibrary){
	
		this.engine = engine;
		this.camera = (SmoothCamera) engine.getCamera();
		maxViewHeight = engine.getCamera().getHeightRaw() / minZoom;
		
		this.textureRegionLibrary = textureRegionLibrary;
		
		
		this.setBackground(new Background(1f,1f,1f));
		
		createPhysicsWorld();
		createTerrain();
		
		
		this.playerActor = createPlayer(0, 10);
		
		playerActor.wake();
		
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
				if((pSceneTouchEvent.getX()- camera.getXMin())>400){
				
					if(pSceneTouchEvent.isActionUp()) {
						playerActor.stopChargingTurboBoost();
					}
					if(pSceneTouchEvent.isActionDown()){
						playerActor.startChargingTurboBoost();
					}
				}
				
				if((pSceneTouchEvent.getX()- camera.getXMin())<400){
					
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
