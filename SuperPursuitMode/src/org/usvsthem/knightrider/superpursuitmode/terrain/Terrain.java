package org.usvsthem.knightrider.superpursuitmode.terrain;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Mesh;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.usvsthem.knightrider.superpursuitmode.entity.Path;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import com.badlogic.gdx.math.Vector2;

public class Terrain extends Entity implements ITerrain {

	private Engine engine;
	private int numPoints = 240;
	private int numHillVertices = numPoints * 4;
	private ArrayList<Vector2> keyPoints;
	private ArrayList<Vector2> borderPoints; //these are used to draw the line
	private ArrayList<Vector2> hillVertices; //these are used to draw the line
    private ArrayList<Vector2> hillTextCoords;
    
	private Random r;
	private PhysicsWorld physicsWorld;
	private Body terrainBody;
	private Mesh hills;
	private float[] hillVerticesBuffer;
	private ITerrainPolygonProvider terrainPolygonProvider;
	
	public Terrain(Engine engine, PhysicsWorld physicsWorld, ITerrainPolygonProvider terrainPolygonProvider){
	
		r = new Random();
		this.engine = engine;
		this.engine.getCamera();
		this.physicsWorld = physicsWorld;
		this.terrainPolygonProvider = terrainPolygonProvider;
		
		this.terrainBody = createTerrainBody();
		
		this.keyPoints = new ArrayList<Vector2>();
		this.borderPoints = new ArrayList<Vector2>();
		this.hillTextCoords = new ArrayList<Vector2>();
		this.hillVertices = new ArrayList<Vector2>();
		
		hillVerticesBuffer = new float[numHillVertices * 3];
		
		this.generateTerrain();
	
		float[] hillsBufferData = populateBuffer(hillVertices,hillVerticesBuffer,numHillVertices);
		
		this.hills = new Mesh(0f,0f, hillsBufferData, numHillVertices, DrawMode.TRIANGLE_STRIP,engine.getVertexBufferObjectManager());
		this.hills.setColor(0.0f,0.0f,0.0f);
	
		this.attachChild(hills);
		
	}
		
	public float getMinTerrainHeightInRange(float x1, float x2){

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
	
	
	public Vector2 getVectorAtX(float x){
		
		Vector2 point1; 
		Vector2 point2; 
		Vector2 vector = null;
		
		for(int i=0; i<borderPoints.size()-1;i++){
			point1 = borderPoints.get(i);
			point2 = borderPoints.get(i+1);

			if(point1.x <= x  && point2.x >= x){
				return point2.cpy().sub(point1);
			}
		}
		
		return vector;
		
	}
	
	public float getYAt(float x){
		
		Vector2 point1; 
		Vector2 point2; 
		float y = 0;
		
		for(int i=0; i<borderPoints.size()-1;i++){
			point1 = borderPoints.get(i);
			point2 = borderPoints.get(i+1);

			if(point1.x <= x  && point2.x >= x){
				return point1.y + (point2.y-point1.y)/2;
			}
		}
		
		return y;
	}

	

	public float getMaxTerrainHeightInRange(float x1, float x2){
		
		float maxY = -100000;
		Vector2 point; 
		int numPoints =  borderPoints.size();
		
		for(int i=0; i<numPoints;i++){
			point = borderPoints.get(i);
			if(point.x >= x1  && point.x <= x2){
				if(borderPoints.get(i).y>maxY) {;
					maxY = borderPoints.get(i).y;
				}
			} else if(point.x>x2) {
				return maxY;
			}
		}
		
		return maxY;
		
	}
	
	
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

	private void generateTerrain(){
		
		Vector2 lastPoint;
		
		if(borderPoints.size()==0){
			lastPoint = new Vector2(0,200);
			borderPoints.add(lastPoint);
		
		} else {
			lastPoint = borderPoints.get(borderPoints.size()-1);
		}
		
		int numRequiredPoints = (numPoints+100) - borderPoints.size();
		
		if(numRequiredPoints>0){
			
			ArrayList<Vector2> newBorderPoints = new ArrayList<Vector2>();
			ArrayList<Vector2> newHillVertices = new ArrayList<Vector2>();
			ArrayList<Vector2> newHillTextCoords = new ArrayList<Vector2>();
			
			this.terrainPolygonProvider.populateWithNPoints(lastPoint, numRequiredPoints, newBorderPoints, newHillVertices, newHillTextCoords);
			
			borderPoints.addAll(newBorderPoints);
			hillVertices.addAll(newHillVertices);
			hillTextCoords.addAll(newHillTextCoords);
			
			newBorderPoints.add(0,lastPoint); //we need the last point also
			int numNewBorderPoints = newBorderPoints.size(); 
			for(int i=0; i<numNewBorderPoints-1;i++) {
				 createFixtureAndAddBody(newBorderPoints.get(i),newBorderPoints.get(i+1));
			}
		
		}

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
		 //FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.6f, 1);
		 FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.3f, 1);
		 fixtureDef.shape = boxPoly;
		 return fixtureDef;
	}
	
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		
		
		int minIndex = -1;
		int minHillIndex = -1;
		float minX = this.engine.getCamera().getXMin();
		
		
		//offset each item in the 
		
		//avoid iterator!!
		
		for(int j=0;j<borderPoints.size();j++) {

			//remove any points we dont care about from the allPoints collection.
			if(borderPoints.get(j).x<minX) {
				minIndex = j;		 
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
			List<Fixture> fixturesToRemove = new ArrayList<Fixture>();
			//List<Fixture> fixturesToRemove = terrainBody.getFixtureList().subList(0,  (minIndex));
			for(int j =0 ; j <minIndex;j++){
				fixturesToRemove.add(terrainBody.getFixtureList().get(j));
			}
			
			for(int s =0; s<fixturesToRemove.size();s++){
				terrainBody.destroyFixture(fixturesToRemove.get(s));
			}
			
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
		 
		}
	
		super.onManagedUpdate(pSecondsElapsed);		 		   
 	}
	
	
}
