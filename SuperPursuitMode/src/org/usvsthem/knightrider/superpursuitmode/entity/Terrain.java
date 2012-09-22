package org.usvsthem.knightrider.superpursuitmode.entity;


import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Mesh;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import com.badlogic.gdx.math.Vector2;

public class Terrain extends Entity {

	private Engine engine;
	private int numPoints = 240;
	private int numHillVertices = numPoints * 4;
	private ArrayList<Vector2> keyPoints;
	private ArrayList<Vector2> borderPoints; //these are used to draw the line
	private ArrayList<Vector2> hillVertices; //these are used to draw the line
    private ArrayList<Vector2> hillTextCoords;
    
	private float hillSegmentWidth = 10;
	//protected float[] bufferData; //in this we store the vertices and the color data
	private Random r;
	private  int rangeDX = 80;
    private int rangeDY = 40;
    private float minDX = 400;
    private float minDY = 100;
	private Path borderPath;
	private PhysicsWorld physicsWorld;
	//private PhysicsFactory physicsFactory;
	private Body terrainBody;
	private Mesh hills;
	private float[] borderPointsBuffer;
	private float[] hillVerticesBuffer;
	
	public Terrain(Engine engine, PhysicsWorld physicsWorld){
	
		r = new Random();
		this.engine = engine;
		this.engine.getCamera();
		this.physicsWorld = physicsWorld;
		//physicsFactory = new PhysicsFactory();
		this.terrainBody = createTerrainBody();
		
		this.keyPoints = new ArrayList<Vector2>();
		this.borderPoints = new ArrayList<Vector2>();
		this.hillTextCoords = new ArrayList<Vector2>();
		this.hillVertices = new ArrayList<Vector2>();
		
		borderPointsBuffer = new float[numPoints * 3];
		hillVerticesBuffer = new float[numHillVertices * 3];
		
		this.generateTerrain();
		
		float[] borderBufferData = populateBuffer(borderPoints,borderPointsBuffer,numPoints);
		
		this.borderPath = new Path(0, 0, borderBufferData,engine.getVertexBufferObjectManager());
		this.borderPath.setColor(1.0f,1.0f,0.1f);
		
		float[] hillsBufferData = populateBuffer(hillVertices,hillVerticesBuffer,numHillVertices);
		
		this.hills = new Mesh(0f,0f, hillsBufferData, numHillVertices, DrawMode.TRIANGLE_STRIP,engine.getVertexBufferObjectManager());
		this.hills.setColor(1.0f,0.0f,0.0f);
		this.attachChild(hills);
		
		this.attachChild(borderPath);
		
	}
		
	public float getMinTerrainHeightInRange(float x1, float x2){
		//float minY = keyPoints.get(0).y;
		float minY  = 1000000;
		Vector2 point; 
		
		for(int i=0; i<borderPoints.size();i++){
			point = borderPoints.get(i);
			if(point.x >= x1  && point.x <= x2){
				if(borderPoints.get(i).y<minY) {;
					minY = borderPoints.get(i).y;
				}
			}
		}
		
		return minY;
	}
	
	public float getMaxTerrainHeightInRange(float x1, float x2){
		
		float maxY = 0;
		Vector2 point; 
		
		for(int i=0; i<borderPoints.size();i++){
			point = borderPoints.get(i);
			if(point.x >= x1  && point.x <= x2){
				if(borderPoints.get(i).y>maxY) {;
					maxY = borderPoints.get(i).y;
				}
			}
		}
		
		return maxY;
		
	}
	

	

	
	/*
	private float[] populateHillsBuffer(ArrayList<Vector2> vertices, float[] bufferData){
		int bufferSize = numPoints*3;
		//int i = 0;
		Vector2 v;
		
		for ( int i = 0, n = bufferSize; i < n; ++i )
		{
			
		   v = vertices.get( i );
		   bufferData[i] = v.x;
		   i=i+1;
		   bufferData[i]=v.y;
		   i=i+2;
			
		}
		
		/*
		for(Vector2 v: vertices){
			
			bufferData[i] = v.x;
			i=i+1;
			bufferData[i]=v.y;
			i=i+2;
			
			if(i>=bufferSize-1){
				break;
			}
			
		}
		return bufferData;
	}*/
	
	
	private float[] populateBuffer(ArrayList<Vector2> points, float[] bufferData, int bufferSize){		
		Vector2 v;
		int j =0;
		for ( int i = 0, n = bufferSize; i < n; ++i )
		{	
		   v = points.get( i );
		   bufferData[j] = v.x;
		   j=j+1;
		   bufferData[j]=v.y;
		   j=j+2;	
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

		
		
		Vector2 lastPoint;
		
		if(keyPoints.size()==0){
			lastPoint = new Vector2(0,200);
			keyPoints.add(lastPoint);
			borderPoints.add(lastPoint);
		
		} else {
			lastPoint = keyPoints.get(keyPoints.size()-1);
		}
		
		//grab the last KEY point
		//Vector2 
		Vector2 nextPoint;
		while(borderPoints.size()<(numPoints+1)) {
			
			
			//we need the next KEY point
			nextPoint = generateNextTerrainPoint(lastPoint,sign);

			createTriangulatedSegment(lastPoint,nextPoint,borderPoints, hillVertices, hillTextCoords);
			
			
			sign = nextSign();
			
			keyPoints.add(nextPoint);
			//borderPoints.add(nextPoint);
			
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
		 FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.6f, 1);
		 fixtureDef.shape = boxPoly;
		 return fixtureDef;
	}
	
	private void createTriangulatedSegment(Vector2 p0, Vector2 p1, ArrayList<Vector2> borderVertices,  ArrayList<Vector2> hillVertices,  ArrayList<Vector2> hillTexCoords){
		
		 float minY=1000;
		 int numHillVertices = 0;
		 int numBorderVertices = 0;
		 Vector2 pt0, pt1;

        // triangle strip between p0 and p1
        int hSegments = (int) Math.floor(((p1.x-p0.x)/hillSegmentWidth));
        float dx = (p1.x - p0.x) / hSegments;
        float da = (float) (Math.PI / hSegments);
        float ymid = (p0.y + p1.y) / 2;
        float ampl = (p0.y - p1.y) / 2;
        pt0 = p0;
       // borderVertices.add(pt0);
        for (int j=1; j<hSegments+1; j++) {
        	pt1 = new Vector2();
            pt1.x = p0.x + j*dx;
            pt1.y = (float) (ymid + ampl * Math.cos(da*j));
            borderVertices.add(pt1);
 
            createFixtureAndAddBody(pt0,pt1);
            
            hillVertices.add(new Vector2(pt0.x,minY)); 
           // hillTexCoords.add(new Vector2(pt0.x/512, 1.0f));
            hillVertices.add(new Vector2(pt1.x,minY));
            //hillTexCoords.add(new Vector2(pt1.x/512, 1.0f));
           
            
            hillVertices.add(new Vector2(pt0.x,pt0.y)); 
            //hillTexCoords.add(new Vector2(pt0.x/512, 0f));
            hillVertices.add(new Vector2(pt1.x,pt1.y)); 
            //hillTexCoords.add(new Vector2(pt1.x/512, 0f));
            
            pt0 = pt1;
        }
		 

		 
		   
	}
	
	private ArrayList<Fixture> fixtures;
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		
		int minIndex = -1;
		int minHillIndex = -1;
		float minX = this.engine.getCamera().getXMin();
		
		
		//offset each item in the 
		
		//avoid iterator!!
		for(Vector2 v: borderPoints){

			//remove any points we dont care about from the allPoints collection.
			if(v.x<minX) {
				minIndex = borderPoints.indexOf(v);
				 
			} else {
				break;
			}
			
		}

		 
		//avoid iterator!!
		
		int counter = 0;
		int numBoxes = hillVertices.size()/4;
		for(int i = 0; i<numBoxes; i++){
			counter = (4 * i) + 3;
			
			if(hillVertices.get(counter).x<minX){
				minHillIndex = counter;
			} else {
				break;
			}
			
		}

		if(minIndex!=-1) {
			borderPoints.subList(0, minIndex).clear();
			
			if(minHillIndex!=-1) {
				hillVertices.subList(0, minHillIndex+1).clear();
			}
			

			generateTerrain();
			
			if(minHillIndex!=-1) {
				
				populateBuffer(hillVertices, hillVerticesBuffer, numHillVertices);
			
				float[] hillBufferData = hills.getVertexBufferObject().getBufferData();
				System.arraycopy(hillVerticesBuffer, 0, hillBufferData, 0, hillBufferData.length);
				hills.getVertexBufferObject().setDirtyOnHardware();
				
			}
		 
			populateBuffer(borderPoints, borderPointsBuffer, numPoints);
				
			borderPath.setBufferData(borderPointsBuffer);

		}
	
		super.onManagedUpdate(pSecondsElapsed);		 		   
 	}
	
	
}
