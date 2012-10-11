package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import java.util.List;

import org.simpleframework.xml.ElementList;

public class LayedOutTerrainPolygonProviderConfiguration extends BaseTerrainPolygonProviderConfiguration{

	@ElementList(required=true,name="terrainPoints")
	private List<TerrainPointConfiguration> terrainPointsConfiguration;
	
	public List<TerrainPointConfiguration> getTerrainPointsConfiguration(){
		return terrainPointsConfiguration;
	}
}
