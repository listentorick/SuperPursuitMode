package org.usvsthem.knightrider.superpursuitmode.terrain;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public interface ITerrainPolygonProvider {

	void populateWithNPoints(Vector2 lastPoint, int numberOfPoints, ArrayList<Vector2> borderVertices,  ArrayList<Vector2> hillVertices,  ArrayList<Vector2> hillTexCoords);

}
