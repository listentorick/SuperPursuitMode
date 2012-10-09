package org.usvsthem.knightrider.superpursuitmode.loader;

import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.CompositeSpriteFactory;
import org.usvsthem.knightrider.superpursuitmode.entity.ISpriteFactory;
import org.usvsthem.knightrider.superpursuitmode.entity.SpritePool;
import org.usvsthem.knightrider.superpursuitmode.loader.configuration.SpriteReferenceConfiguration;

public class SpritePoolLoader {
	
	private TextureRegionLibrary textureRegionLibrary;
	private CompositeSpriteFactory compositeSpriteFactory;
	private Engine engine;
	private SpritePool spritePool;
	
	
	public SpritePoolLoader(Engine engine, TextureRegionLibrary textureRegionLibrary, List<SpriteReferenceConfiguration> spriteReferenceConfigurations){
		this.textureRegionLibrary = textureRegionLibrary;
		compositeSpriteFactory = new CompositeSpriteFactory();
		this.engine = engine;
		this.spritePool = new SpritePool(compositeSpriteFactory);
		
		for(SpriteReferenceConfiguration src: spriteReferenceConfigurations){
			this.addSpriteReference(src);
		}	
	}
	
	public SpritePool getSpritePool(){
		return spritePool;
		
	}
	
	private void addSpriteReference(SpriteReferenceConfiguration src){
		
		final TextureRegion textureRegion = textureRegionLibrary.get(src.getSpriteId());
		
		compositeSpriteFactory.addSpriteFactory(new ISpriteFactory() {
				
				@Override
				public Sprite create() {
					// TODO Auto-generated method stub
					return new Sprite(0,0,textureRegion.getWidth(),textureRegion.getHeight(), textureRegion , engine.getVertexBufferObjectManager());	
					
				}
			});
			
	}

}
