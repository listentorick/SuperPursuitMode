package org.usvsthem.knightrider.superpursuitmode.terrain;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;


public class LayedOutTerrainPolygonProvider extends BaseTerrainPolygonProvider implements ITerrainPolygonProvider{
	
	ArrayList<Vector2> points;
	
	public  LayedOutTerrainPolygonProvider(ArrayList<Vector2> points){
		this.points = points;
	}

	@Override
	public void populateWithNPoints(Vector2 lastPoint, int numberOfPoints,
			ArrayList<Vector2> borderVertices, ArrayList<Vector2> hillVertices,
			ArrayList<Vector2> hillTexCoords) {
	
		Vector2 p0 = lastPoint;
		Vector2 p1 = null;
		
		while(borderVertices.size()<numberOfPoints) {
			p1 = points.remove(0);
			createTriangulatedSegment(p0,p1,borderVertices, hillVertices,hillTexCoords);
			p0 = p1;
		}
		
		
		//ArrayList<Vector2> pointsToManipulate = new ArrayList<Vector2>(points.subList(0, numberOfPoints));
		//int numPoints = pointsToManipulate.size();
		//for(int i=0; i<numPoints-1;i++){
		//	createTriangulatedSegment(pointsToManipulate.get(i),pointsToManipulate.get(i+1),borderVertices, hillVertices,hillTexCoords);
		//}
		//points.removeAll(pointsToManipulate);
		
	}

}
