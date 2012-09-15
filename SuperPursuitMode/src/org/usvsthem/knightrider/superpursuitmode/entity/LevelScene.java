package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

public class LevelScene extends Scene implements IScrollDetectorListener{
	
	private Engine engine;
	private SurfaceScrollDetector scrollDetector;
	private Terrain terrain;
	public LevelScene(Engine engine){
		this.engine = engine;
		terrain = new Terrain(engine);
		this.attachChild(terrain);
		this.registerUpdateHandler(terrain);
		
		this.scrollDetector = new SurfaceScrollDetector(this);	
		scrollDetector.setTriggerScrollMinimumDistance(100);
		
	
		this.setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				// TODO Auto-generated method stub
				
				scrollDetector.onTouchEvent(pSceneTouchEvent);	
				
				return false;
			}
		});
		
		/*
		
		final Random rg = new Random();
		final float[] buffer = new float[12];
		buffer[0]=0;
		buffer[1]=0;
		
		buffer[3]=100;
		buffer[4]=100;
		
		buffer[6]=200;
		buffer[7]=400;
		
		buffer[9]=700;
		buffer[10]=400;
		
		final Path path = new Path(0, 0, buffer,engine.getVertexBufferObjectManager());
		path.setColor(1.0f,1.0f,0.1f);
		this.attachChild(path);
		
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				buffer[7]+=rg.nextFloat();
				path.setBufferData(buffer);
			}
		});*/
		
		
	}
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
		
	}
	


}
