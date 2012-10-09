package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="level")
public class LevelConfiguration {
	
	@ElementList(required=true,name="spritePool")
	private List<SpriteConfiguration> spriteConfiguration;
		
	public List<SpriteConfiguration> getSpriteConfiguration(){
		return spriteConfiguration;
	}
	
	@Element(name="furnitureProvider")
	private BaseFurnitureProviderConfiguration furnitureProviderConfiguration;
	
	public BaseFurnitureProviderConfiguration getFurnitureConfiguration(){
		return furnitureProviderConfiguration;
	}
	
}
