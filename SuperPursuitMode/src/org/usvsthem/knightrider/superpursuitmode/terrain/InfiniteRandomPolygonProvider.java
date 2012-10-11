package org.usvsthem.knightrider.superpursuitmode.terrain;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class InfiniteRandomPolygonProvider extends BaseTerrainPolygonProvider implements ITerrainPolygonProvider {

	private Random r;
	private  int rangeDX = 80;
    private int rangeDY = 40;
    private float minDX = 400;
    private float minDY = 100;
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
	
}
