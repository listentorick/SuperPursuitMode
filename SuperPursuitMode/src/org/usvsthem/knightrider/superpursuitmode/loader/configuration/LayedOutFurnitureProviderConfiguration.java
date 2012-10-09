package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import java.util.List;

import org.simpleframework.xml.ElementList;

public class LayedOutFurnitureProviderConfiguration extends BaseFurnitureProviderConfiguration {

	@ElementList(required=true,name="furniturePool")
	private List<LayedOutFurnitureConfiguration> furnitureConfiguration;
	
	public List<LayedOutFurnitureConfiguration> getFurnitureConfiguration(){
		return furnitureConfiguration;
	}
}
