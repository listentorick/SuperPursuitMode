package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;

public class LevelScene extends Scene{
	
	private Engine engine;
	
	public LevelScene(Engine engine){
		this.engine = engine;
		Terrain terrain = new Terrain(engine);
		this.attachChild(terrain);
		this.registerUpdateHandler(terrain);
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

}
