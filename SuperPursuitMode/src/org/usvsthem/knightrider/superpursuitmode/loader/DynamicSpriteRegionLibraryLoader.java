package org.usvsthem.knightrider.superpursuitmode.loader;

import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.LevelConfiguration;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.SpriteConfiguration;

import android.content.res.AssetManager;

public class DynamicSpriteRegionLibraryLoader {

	private AssetManager assetManager;
	private LevelConfiguration levelConfiguration;
	private TextureRegionLibrary textureRegionLibrary;
	private BuildableBitmapTextureAtlas textureAtlas;
	private TextureManager textureManager;
	
	public DynamicSpriteRegionLibraryLoader(TextureManager textureManager, AssetManager assetManager, LevelConfiguration levelConfiguration){
		this.assetManager = assetManager;
		this.levelConfiguration = levelConfiguration;
		this.textureRegionLibrary = new TextureRegionLibrary(0);
		this.textureManager = textureManager;
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");       
		textureAtlas = new BuildableBitmapTextureAtlas(textureManager, 2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	
		for(SpriteConfiguration sc : levelConfiguration.getSpriteConfiguration()){
			createTextureRegion(textureAtlas,sc);
		}
		
		try {
			textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,1,1));
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
		textureManager.loadTexture(textureAtlas);
	}
	
	public TextureRegionLibrary getTextureRegionLibrary(){
		return textureRegionLibrary;
	}
	
		
	//public TextureRegion get(int id) {
	//	return (TextureRegion) textureRegionLibrary.get(id);
	//}

	private void createTextureRegion(BuildableBitmapTextureAtlas textureAtlas, SpriteConfiguration spriteConfiguration){
		TextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, assetManager, spriteConfiguration.getSrc());
		textureRegionLibrary.put(spriteConfiguration.getId(), textureRegion);	
	}
	
}
