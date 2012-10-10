package org.usvsthem.knightrider.superpursuitmode.terrain;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class InfiniteRandomPolygonProvider implements ITerrainPolygonProvider {

	private Random r;
	private  int rangeDX = 80;
    private int rangeDY = 40;
    private float minDX = 400;
    private float minDY = 100;
    private float hillSegmentWidth = 10;
    private float sign =1;

    
	public InfiniteRandomPolygonProvider(){
		r = new Random();
	}
	
	private float nextSign(){
		
		int index =  (int)(Math.random() * 2);
		if(index ==0) {
			return -1;
		}else {
			return 1;
		}
	}
	
	
	@Override
	public void populateWithNPoints(Vector2 lastPoint, int numberOfPoints, ArrayList<Vector2> borderPoints,  ArrayList<Vector2> hillVertices,  ArrayList<Vector2> hillTextCoords) {

		Vector2 nextPoint;
		while(borderPoints.size()<numberOfPoints) {
			nextPoint = generateNextTerrainPoint(lastPoint,sign);
			createTriangulatedSegment(lastPoint,nextPoint,borderPoints, hillVertices, hillTextCoords);
			sign = nextSign();
			lastPoint = nextPoint;
		}
	}
	
	
	protected Vector2 generateNextTerrainPoint(Vector2 previousVector, float sign){
		float x = previousVector.x + r.nextFloat()%rangeDX+minDX;
		float y = previousVector.y + ( r.nextFloat()%rangeDY+minDY)*sign;
		//float y = 0;
		return new Vector2(x,y);
	}
	
	private void createTriangulatedSegment(Vector2 p0, Vector2 p1, ArrayList<Vector2> borderVertices,  ArrayList<Vector2> hillVertices,  ArrayList<Vector2> hillTexCoords){
	 
		//float minY=engine.getCamera().getYMax() + 1000;
	   float minY =100000;
		
		Vector2 pt0, pt1;

       // triangle strip between p0 and p1
       int hSegments = (int) Math.floor(((p1.x-p0.x)/hillSegmentWidth));
       float dx = (p1.x - p0.x) / hSegments;
       float da = (float) (Math.PI / hSegments);
       float ymid = (p0.y + p1.y) / 2;
       float ampl = (p0.y - p1.y) / 2;
       pt0 = p0;
    
       for (int j=1; j<hSegments+1; j++) {
       	pt1 = new Vector2();
           pt1.x = p0.x + j*dx;
           pt1.y = (float) (ymid + ampl * Math.cos(da*j));
          borderVertices.add(pt1);

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
	

}
