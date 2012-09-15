package org.usvsthem.knightrider.superpursuitmode.entity;


import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import com.badlogic.gdx.math.Vector2;

public class Terrain extends Entity {

	private Engine engine;
	private int numKeyPoints = 80;
	private ArrayList<Vector2> keyPoints;
	private float hillSegmentWidth = 10;
	protected float[] bufferData; //in this we store the vertices and the color data
	private Random r;
	private  int rangeDX = 80;
    private int rangeDY = 40;
    private float minDX = 160;
    private float minDY = 60;
	private Path path;
	
	public Terrain(Engine engine){
	
		r = new Random();
		this.engine = engine;
		this.engine.getCamera();
	
		keyPoints = this.generateTerrain(new ArrayList<Vector2>());
		bufferData = populateBuffer(keyPoints);
		
		path = new Path(0, 0, bufferData,engine.getVertexBufferObjectManager());
		path.setColor(1.0f,1.0f,0.1f);
		this.attachChild(path);
		
		
	}
	
	private float[] populateBuffer(ArrayList<Vector2> keyPoints){
		
		int bufferSize = numKeyPoints*3;
		float[] bufferData = new float[bufferSize];
		int i = 0;
		for(Vector2 v: keyPoints){
			
			bufferData[i] = v.x;
			i=i+1;
			bufferData[i]=v.y;
			i=i+2;
			
			if(i>=bufferSize-1){
				break;
			}
			
		}
		return bufferData;
	}

	private float sign =1;
	
	private float nextSign(){
	
		int index =  (int)(Math.random() * 2);
		if(index ==0) {
			return -1;
		}else {
			return 1;
		}
	}
	
	private ArrayList<Vector2> generateTerrain(ArrayList<Vector2> keyPoints ){

		if(keyPoints.size()==0){
			keyPoints.add(new Vector2(0,0));
		}
		
		//grab the last point
		Vector2 lastPoint = keyPoints.get(keyPoints.size()-1);
		Vector2 nextPoint;
		while(keyPoints.size()<numKeyPoints) {
			
			
			//we need another point
			//lastPoint =  generateTerrainVector(lastPoint,sign);
			nextPoint = generateTerrainVector(lastPoint,sign);
			
			keyPoints.addAll(generateSegments(lastPoint,nextPoint));
			///sign = sign *= -1;
			sign = nextSign();
			keyPoints.add(nextPoint);
			lastPoint = nextPoint;
		}

	    return keyPoints;
	}
	
	protected ArrayList<Vector2> generateSegments(Vector2 lastPoint, Vector2 nextPoint){
		ArrayList<Vector2> segments = new ArrayList<Vector2>(); 
		int hSegments = (int) Math.floor((nextPoint.x-lastPoint.x)/hillSegmentWidth);
		float dx = (nextPoint.x -lastPoint.x) / hSegments;
		float da = (float) (Math.PI / hSegments);
		float ymid = (lastPoint.y + nextPoint.y) / 2;
		float ampl = (lastPoint.y - nextPoint.y) / 2;
		
		Vector2 newPoint;
		for (int j = 0; j < hSegments+1; ++j) {
			newPoint = new Vector2();
			newPoint.x = lastPoint.x + j*dx;
			newPoint.y = (float) (ymid + ampl * Math.cos(da*j));  //cosf(da*j);
			segments.add(newPoint);
		}
		
		return segments; 
	}
	
	protected Vector2 generateTerrainVector(Vector2 previousVector, float sign){
		float x = previousVector.x + r.nextFloat()%rangeDX+minDX;
		float y = previousVector.y + ( r.nextFloat()%rangeDY+minDY)*sign;
		return new Vector2(x,y);
	}
	
	private float offsetX =0;
	private float offsetY =0;
	public void setOffset(float x, float y){
		offsetX = x;
		offsetY = y;
	}
	
	private void updateTerrain(){
		
	}
	
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		
		int minIndex = -1;
		
		//offset each item in the 
		for(Vector2 v: keyPoints){
			v.x+=offsetX;
			v.y+=offsetY;
			
			if(v.x<0) {
				minIndex = keyPoints.indexOf(v);
			} 
			
		}
		
		if(minIndex!=-1) {
			keyPoints.subList(0, minIndex).clear();
			
			keyPoints = generateTerrain(keyPoints);

			float[] vertices = populateBuffer(keyPoints);
			
			path.setBufferData(vertices);
		}
		
		
		
		super.onManagedUpdate(pSecondsElapsed);
	}


}
