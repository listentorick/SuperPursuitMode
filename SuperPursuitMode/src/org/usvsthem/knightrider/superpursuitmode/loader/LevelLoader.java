package org.usvsthem.knightrider.superpursuitmode.loader;

import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.entity.LevelScene;
import org.usvsthem.knightrider.superpursuitmode.entity.SpriteMultiPool;
import org.usvsthem.knightrider.superpursuitmode.furniture.FurnitureController;
import org.usvsthem.knightrider.superpursuitmode.furniture.RandomlyPositionedFurnitureProvider;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.LevelConfiguration;

import android.content.Context;
import android.content.res.AssetManager;

public class LevelLoader {
	
	private Engine engine;
	private TextureRegionLibrary textureRegionLibrary;
	private Context context;
	
	public LevelLoader(Context context, Engine engine, TextureRegionLibrary textureRegionLibrary){
		this.textureRegionLibrary = textureRegionLibrary;
		this.engine = engine;
		this.context = context;
	}
	
	public ILevel loadLevel(int level) throws Exception{
		
	 	AssetManager assetManager = this.context.getAssets();
	    StringBuilder stringBuilder = new StringBuilder("levels/");
	    stringBuilder.append(level);
	    stringBuilder.append(".xml"); 
	    InputStream stream = assetManager.open(stringBuilder.toString());
	    return loadLevelFromStream(level, stream);
	}
	
	public ILevel loadLevelFromStream(int level,  InputStream paramInputStream) throws Exception{
		Serializer serializer = new Persister();
		LevelConfiguration levelConfiguration = serializer.read(LevelConfiguration.class, paramInputStream);
		return loadLevelFromConfiguration(level,levelConfiguration);
	}
	 
	public ILevel loadLevelFromConfiguration( int level, LevelConfiguration levelConfiguration){
		
		//This internally contains a library with all the dynamic sprites int
		DynamicSpriteRegionLibraryLoader dsrl = new DynamicSpriteRegionLibraryLoader(engine.getTextureManager(), context.getAssets(),levelConfiguration);
		
		LevelScene levelScene = new LevelScene(engine, textureRegionLibrary);
			
		//Lets create 
		FurnitureProviderLoader  fpl = new FurnitureProviderLoader(levelScene, engine, dsrl.getTextureRegionLibrary(), levelConfiguration);
	
		levelScene.setFurnitureProvider(fpl.getFurnitureProvider());
		
		levelScene.build();
		
		return levelScene;
	}

}
