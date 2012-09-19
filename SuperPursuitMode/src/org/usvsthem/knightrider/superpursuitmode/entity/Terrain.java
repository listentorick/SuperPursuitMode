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
	//private int numKeyPoints = 80;
	private int numPoints = 80;
	private ArrayList<Vector2> keyPoints;
	private ArrayList<Vector2> allPoints; //these are used to draw the line

	private float hillSegmentWidth = 10;
	//protected float[] bufferData; //in this we store the vertices and the color data
	private Random r;
	private  int rangeDX = 80;
    private int rangeDY = 40;
    private float minDX = 800;
    private float minDY = 50;
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
		
		this.keyPoints = new ArrayList<Vector2>();
		this.allPoints = new ArrayList<Vector2>();
		
		this.generateTerrain();
		
		float[] bufferData = populateBuffer(allPoints);
		
		this.path = new Path(0, 0, bufferData,engine.getVertexBufferObjectManager());
		this.path.setColor(1.0f,1.0f,0.1f);
		this.attachChild(path);
		
	}
	
	/*
	public ArrayList<Vector2> generateStartTerrain(ArrayList<Vector2> keyPoints){
		Vector2 previousPoint = new Vector2(0,200);
		keyPoints.add(previousPoint);
		Vector2 newPoint;
		for(int i=1; i< numKeyPoints;i++){
			newPoint = new Vector2(i*100,200);
			keyPoints.add(newPoint);
			createFixtureAndAddBody(previousPoint,newPoint);
			previousPoint = newPoint;
		}
		return keyPoints;
		
	}*/
	
	public float getMinTerrainHeightInRange(float x1, float x2){
		float minY = keyPoints.get(0).y;
		
		for(int i=0; i<keyPoints.size();i++){
			if(keyPoints.get(i).y<minY) {;
				minY = keyPoints.get(i).y;
			}
		}
		
		return minY;
	}
	
	public float getMaxTerrainHeightInRange(float x1, float x2){
		float maxY = 0;
		
		for(int i=0; i<keyPoints.size();i++){
			if(keyPoints.get(i).y>maxY) {;
				maxY = keyPoints.get(i).y;
			}
		}
		
		return maxY;	
		
	}
	
	public float getTerrainHeightAtX(float x){
		for(int i=0; i<keyPoints.size()-2;i++){
			float x1 = keyPoints.get(i).x;
			float x2 = keyPoints.get(i+1).x;
			if(x>x1 && x<x2){
				return keyPoints.get(i).y;
			}
		}
		return 0;
	}
	
	
	public Vector2 calculatePointPosition(float startX, float distance){
		
		float x1 = 0;
		float x2 = 0;
		
		//lets find the 2 keypoints x is between.
		
		Vector2 point1 = null;;
		Vector2 point2 = null;
		for(int i=0; i<keyPoints.size()-1;i++){
			point1 = keyPoints.get(i);
			point2 = keyPoints.get(i+1);
			
			x1 = point1.x;
			x2 = point2.x;
			if(startX>x1 && startX<x2){
				break;
			}
		}
		
		
		
		if(point1==null) {
			return null;
		}
		
		//distance travelled from point1
		float deltaX = startX - point1.x;
		

		
		
		
		//imagine the standard cosine graph where x axis is angle.
		
		//we know that between 2 key-points we travel through 180 degrees 
		float xPerAngle = (float) ((point2.x-point1.x)/Math.PI);
		float anglePerX = (float) (Math.PI/(point2.x-point1.x));
		//so the the angle we are at is...
		float startAngle = anglePerX * deltaX;
		
		
		
		//Calculate the radius of our hill
		float r = calculateRadius(point2,point1);
		
		//return null;
		
		
		
		//the length of the arc segment = distance.
		//the angle travelled through
		float deltaTheta = distance/r; //this is the equation of an arc
		
		if(startAngle+deltaTheta>Math.PI){
			//we've moved to the next point
			//distance = distance - Math.PI * r;
			return calculatePointPosition(point2.x,(float)(distance - (Math.PI * r)));
		} else {
			
			//now we can calculate the point!
			float x = distance + (deltaTheta * xPerAngle);
			float ymid =  calculateYOffset(point2, point1);
			//float r = calculateRadius(point2, point1);
			float y = (float) (ymid + r * Math.cos(startAngle + deltaTheta));
			return new Vector2(x,y);
		}
		
		//return null;
	}
	
	private float[] populateBuffer(ArrayList<Vector2> keyPoints){
		
		int bufferSize = numPoints*3;
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
	
	private void generateTerrain(){

		if(keyPoints.size()==0){
			Vector2 firstPoint = new Vector2(0,200);
			keyPoints.add(firstPoint);
			allPoints.add(firstPoint);
		
		}
		
		//grab the last KEY point
		Vector2 lastPoint = keyPoints.get(keyPoints.size()-1);
		Vector2 nextPoint;
		while(allPoints.size()<numPoints) {
			
			
			//we need the next KEY point
			nextPoint = generateNextTerrainPoint(lastPoint,sign);
			
			allPoints.addAll(generateSegments(lastPoint,nextPoint));
			sign = nextSign();
			
			keyPoints.add(nextPoint);
			allPoints.add(nextPoint);
			
			lastPoint = nextPoint;
		}
	}
	
	protected float calculateRadius(Vector2 lastPoint, Vector2 nextPoint){
		return (lastPoint.y - nextPoint.y) / 2;
	}
	
	protected float calculateYOffset(Vector2 lastPoint, Vector2 nextPoint){
		return (lastPoint.y + nextPoint.y) / 2;
	}
	
	
	
	protected ArrayList<Vector2> generateSegments(Vector2 lastPoint, Vector2 nextPoint){
		ArrayList<Vector2> segments = new ArrayList<Vector2>(); 
		int hSegments = (int) Math.floor((nextPoint.x-lastPoint.x)/hillSegmentWidth);
		float dx = (nextPoint.x -lastPoint.x) / hSegments;
		float da = (float) (Math.PI / hSegments); //split
		float ymid = calculateYOffset(lastPoint, nextPoint);
		float r = calculateRadius(lastPoint, nextPoint);
		
		//float r = calculateRadius(lastPoint, nextPoint);
		Vector2 newPoint;
		Vector2 previousPoint = lastPoint;
		for (int j = 0; j < hSegments+1; ++j) {
			newPoint = new Vector2();
			newPoint.x = lastPoint.x + j*dx;
			newPoint.y = (float) (ymid + r * Math.cos(da*j));  //cosf(da*j);
			segments.add(newPoint);
			
			createFixtureAndAddBody(previousPoint,newPoint);
			previousPoint = newPoint;
		}
		
		return segments; 
	}
	
	protected Vector2 generateNextTerrainPoint(Vector2 previousVector, float sign){
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
		 FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.9f, 1);
		 fixtureDef.shape = boxPoly;
		 return fixtureDef;
	}
	
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		
		int minIndex = -1;
		
		//offset each item in the 
		for(Vector2 v: allPoints){

			//remove any points we dont care about from the allPoints collection.
			if(v.x<this.engine.getCamera().getXMin()) {
				minIndex = allPoints.indexOf(v);
			} 
			
		}
		
		if(minIndex!=-1) {
			allPoints.subList(0, minIndex).clear();
			
			for(int i=0; i<=minIndex;i++){
				//remove fixtures we no longer care about
				//Fixture f = terrainBody.getFixtureList().get(i);
				//if(f!=null){
				//	terrainBody.destroyFixture(f);
				//}
				
			}
			
			generateTerrain();

			float[] vertices = populateBuffer(allPoints);
			
			path.setBufferData(vertices);
		}
		
		
		
		super.onManagedUpdate(pSecondsElapsed);
	}


}
