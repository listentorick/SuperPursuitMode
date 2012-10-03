package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.usvsthem.knightrider.superpursuitmode.entity.PlayerActor;
import org.usvsthem.knightrider.superpursuitmode.entity.Terrain;

public class CameraPositionManager implements IUpdateHandler{
	
	private int NUM_PREV_MAXY  = 15;
	double[] weightedMaxY = new double[NUM_PREV_MAXY];
	int _nextMaxY = 0;
	private float averageMaxY = 0;
	private ZoomCamera camera;
	private Terrain terrain;
	private PlayerActor playerActor;
	private float minZoom = (1f/2f);
	private float maxViewHeight;
	private float HERO_X_OFFSET  = 100f;
	private float HERO_PADDING  = 100f;
	public CameraPositionManager( Engine engine,  ZoomCamera camera,  Terrain terrain, PlayerActor playerActor){
		
		this.camera = camera;
		this.terrain = terrain;
		this.playerActor = playerActor;
		this.maxViewHeight = engine.getCamera().getHeightRaw() / minZoom;
		calculateMaxY();
		//terrainTimer = new TimerHandler(0.1f,true,this);
	}
	


	@Override
	public void onUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		//terrainTimer.onUpdate(pSecondsElapsed);
		
		
		calculateMaxY();
		
		float minY = playerActor.getY() - HERO_PADDING;
		
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
		

		
	    //float cameraX = camera.getCenterX()+5f;
		
		camera.setCenter(cameraX , cameraY);
		//camera.setZoomFactor(zoom);
		
		
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	
	private void calculateMaxY(){
		//Due to the discrete nature of our terrain generation
		//we use a weighted maxY when positioning/zooming the camera.
		//this avoids jerky camera motion
		float maxY = terrain.getMaxTerrainHeightInRange(camera.getXMin(), camera.getXMax()) + HERO_PADDING;
		
		averageMaxY = 0;
		for(int i = 0; i < NUM_PREV_MAXY; ++i) { 
			averageMaxY+=weightedMaxY[i];
		}
		
		averageMaxY = averageMaxY/NUM_PREV_MAXY;
		      
		weightedMaxY[_nextMaxY++] = maxY;
		
		if (_nextMaxY >= NUM_PREV_MAXY) _nextMaxY = 0;
	}

}
