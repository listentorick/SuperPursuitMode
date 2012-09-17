package org.usvsthem.knightrider.superpursuitmode.entity;


import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

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
    private float minDX = 600;
    private float minDY = 100;
	private Path path;
	private PhysicsWorld physicsWorld;
	//private PhysicsFactory physicsFactory;
	private Body terrainBody;
	
	public Terrain(Engine engine, PhysicsWorld physicsWorld){
	
		r = new Random();
		this.engine = engine;
		this.engine.getCamera();
		this.physicsWorld = physicsWorld;
		//physicsFactory = new PhysicsFactory();
		this.terrainBody = createTerrainBody();
		
		this.keyPoints = this.generateTerrain(new ArrayList<Vector2>());
		this.bufferData = populateBuffer(keyPoints);
		
		this.path = new Path(0, 0, bufferData,engine.getVertexBufferObjectManager());
		this.path.setColor(1.0f,1.0f,0.1f);
		this.attachChild(path);
		
	}
	
	public float getMinTerrainHeightInRange(float x1, float x2){
		float minY = keyPoints.get(0).y;
		
		for(int i=0; i<numKeyPoints;i++){
			if(keyPoints.get(i).y<minY) {;
				minY = keyPoints.get(i).y;
			}
		}
		
		return minY;
	}
	
	public float getMaxTerrainHeightInRange(float x1, float x2){
		float maxY = 0;
		
		for(int i=0; i<numKeyPoints;i++){
			if(keyPoints.get(i).y>maxY) {;
				maxY = keyPoints.get(i).y;
			}
		}
		
		return maxY;	
		
	}
	
	public float getTerrainHeightAtX(float x){
		for(int i=0; i<numKeyPoints-2;i++){
			float x1 = keyPoints.get(i).x;
			float x2 = keyPoints.get(i+1).x;
			if(x>x1 && x<x2){
				return keyPoints.get(i).y;
			}
		}
		return 0;
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
			keyPoints.add(new Vector2(0,200));
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
		Vector2 previousPoint = lastPoint;
		for (int j = 0; j < hSegments+1; ++j) {
			newPoint = new Vector2();
			newPoint.x = lastPoint.x + j*dx;
			newPoint.y = (float) (ymid + ampl * Math.cos(da*j));  //cosf(da*j);
			segments.add(newPoint);
			
			createFixtureAndAddBody(previousPoint,newPoint);
			previousPoint = newPoint;
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
	
	private Body createTerrainBody(){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.x = 0;
		bodyDef.position.y = 0;
		Body body = physicsWorld.createBody(bodyDef);
		return body;
	}
	
	private void createFixtureAndAddBody(Vector2 point1, Vector2 point2){
		FixtureDef fixtureDef = createFixture(point1,point2);
		terrainBody.createFixture(fixtureDef);
	}
	
	private FixtureDef createFixture(Vector2 point1, Vector2 point2){
		
		 PolygonShape boxPoly = new PolygonShape();
		 boxPoly.setAsEdge(new Vector2(point1.x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, point1.y/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT), new Vector2(point2.x/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,point2.y/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
		 FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 1, 1);
		 fixtureDef.shape = boxPoly;
		 return fixtureDef;
	}
	
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		
		int minIndex = -1;
		
		//offset each item in the 
		for(Vector2 v: keyPoints){
			//v.x+=offsetX;
			//v.y+=offsetY;
			
			if(v.x<this.engine.getCamera().getXMin()) {
				minIndex = keyPoints.indexOf(v);
			} 
			
		}
		
		if(minIndex!=-1) {
			keyPoints.subList(0, minIndex).clear();
			
			for(int i=0; i<=minIndex;i++){
				//remove fixtures we no longer care about
				//Fixture f = terrainBody.getFixtureList().get(i);
				//if(f!=null){
				//	terrainBody.destroyFixture(f);
				//}
				
			}
			
			keyPoints = generateTerrain(keyPoints);

			float[] vertices = populateBuffer(keyPoints);
			
			path.setBufferData(vertices);
		}
		
		
		
		super.onManagedUpdate(pSecondsElapsed);
	}


}
