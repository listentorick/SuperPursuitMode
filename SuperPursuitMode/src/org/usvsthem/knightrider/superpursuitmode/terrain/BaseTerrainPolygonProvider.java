package org.usvsthem.knightrider.superpursuitmode.terrain;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class BaseTerrainPolygonProvider {
	
	private float hillSegmentWidth = 10;
	  
	protected void createTriangulatedSegment(Vector2 p0, Vector2 p1, ArrayList<Vector2> borderVertices,  ArrayList<Vector2> hillVertices,  ArrayList<Vector2> hillTexCoords){
		 
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
