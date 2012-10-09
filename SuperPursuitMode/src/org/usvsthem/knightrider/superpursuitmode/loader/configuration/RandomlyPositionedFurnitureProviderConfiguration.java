package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import java.util.List;

import org.simpleframework.xml.ElementList;

public class RandomlyPositionedFurnitureProviderConfiguration extends BaseFurnitureProviderConfiguration{

	@ElementList(required=true,name="furniturePool")
	private List<FurnitureConfiguration> furnitureConfiguration;
	
	public List<FurnitureConfiguration> getFurnitureConfiguration(){
		return furnitureConfiguration;
	}
}
