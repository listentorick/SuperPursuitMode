package org.usvsthem.knightrider.superpursuitmode.loader;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.furniture.IFurnitureProvider;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.BaseTerrainPolygonProviderConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.LayedOutTerrainPolygonProviderConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.LevelConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.RandomlyPositionedFurnitureProviderConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.TerrainPointConfiguration;
import org.usvsthem.knightrider.superpursuitmode.terrain.ITerrainPolygonProvider;
import org.usvsthem.knightrider.superpursuitmode.terrain.InfiniteRandomPolygonProvider;
import org.usvsthem.knightrider.superpursuitmode.terrain.LayedOutTerrainPolygonProvider;

import com.badlogic.gdx.math.Vector2;

public class TerrainPolygonProviderLoader {

	private ITerrainPolygonProvider terrainPolygonProvider;

	public TerrainPolygonProviderLoader(LevelConfiguration levelConfiguration){

		BaseTerrainPolygonProviderConfiguration btppc = levelConfiguration.getTerrainPolygonProviderConfiguration();
		
		if(btppc instanceof LayedOutTerrainPolygonProviderConfiguration){
			terrainPolygonProvider = loadLayedOutTerrainPolygonProvider((LayedOutTerrainPolygonProviderConfiguration)btppc);
		} else {
			terrainPolygonProvider = new InfiniteRandomPolygonProvider();
		}
	}
	
	public ITerrainPolygonProvider getTerrainPolygonProvider(){
		return terrainPolygonProvider;
	}
	
	private ITerrainPolygonProvider loadLayedOutTerrainPolygonProvider(LayedOutTerrainPolygonProviderConfiguration lotppc){
		
		List<TerrainPointConfiguration> terrainPointsConfig = lotppc.getTerrainPointsConfiguration();
		ArrayList<Vector2> terrainPoints = new ArrayList<Vector2>();
		for(TerrainPointConfiguration tpc: terrainPointsConfig) {
			terrainPoints.add(new Vector2(tpc.getX(), tpc.getY()));
		}
		return new LayedOutTerrainPolygonProvider(terrainPoints);
		
	}
	
}
