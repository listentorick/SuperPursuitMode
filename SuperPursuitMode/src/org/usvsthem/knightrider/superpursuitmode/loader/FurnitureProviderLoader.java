package org.usvsthem.knightrider.superpursuitmode.loader;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.CompositeSpriteFactory;
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.entity.ISpriteFactory;
import org.usvsthem.knightrider.superpursuitmode.entity.SpritePool;
import org.usvsthem.knightrider.superpursuitmode.furniture.IFurnitureProvider;
import org.usvsthem.knightrider.superpursuitmode.furniture.RandomlyPositionedFurnitureProvider;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.BaseFurnitureProviderConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.FurnitureConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.LevelConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.RandomlyPositionedFurnitureProviderConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.SpriteReferenceConfiguration;

public class FurnitureProviderLoader {

	private IFurnitureProvider furnitureProvider;
	private TextureRegionLibrary textureRegionLibrary;
	private Engine engine;
	private ILevel level;
	
	public FurnitureProviderLoader(ILevel level, Engine engine, TextureRegionLibrary textureRegionLibrary, LevelConfiguration levelConfiguration){
		this.engine = engine;
		this.level = level;
		this.textureRegionLibrary = textureRegionLibrary;
		
		BaseFurnitureProviderConfiguration bfpc = levelConfiguration.getFurnitureConfiguration();
		
		if(bfpc instanceof RandomlyPositionedFurnitureProviderConfiguration){
			furnitureProvider = loadRandomlyPositionedFurnitureProvider((RandomlyPositionedFurnitureProviderConfiguration)bfpc);
		}
		
	}
	
	public IFurnitureProvider getFurnitureProvider(){
		return furnitureProvider;
	}
	
	public IFurnitureProvider loadRandomlyPositionedFurnitureProvider(RandomlyPositionedFurnitureProviderConfiguration config){
		
		//fucking generics....
		List<SpriteReferenceConfiguration> src =  new ArrayList<SpriteReferenceConfiguration>();
		src.addAll(config.getFurnitureConfiguration());
		SpritePoolLoader spl  = new SpritePoolLoader(engine, textureRegionLibrary, src);
		
		SpritePool sp = spl.getSpritePool();
		
		//level cant be passed in here.. change the IFurnitureProvider interface
		RandomlyPositionedFurnitureProvider rfp = new RandomlyPositionedFurnitureProvider(level,sp);

		return rfp;
		
		
	}
	
}
