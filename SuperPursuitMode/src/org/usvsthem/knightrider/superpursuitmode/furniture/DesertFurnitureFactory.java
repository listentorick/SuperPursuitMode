package org.usvsthem.knightrider.superpursuitmode.furniture;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.CompositeSpriteFactory;
import org.usvsthem.knightrider.superpursuitmode.Textures;
import org.usvsthem.knightrider.superpursuitmode.entity.ISpriteFactory;

public class DesertFurnitureFactory extends CompositeSpriteFactory {

	public DesertFurnitureFactory(final Engine engine, final TextureRegionLibrary textureRegionLibrary){
		super();
		this.addSpriteFactory(new ISpriteFactory() {
			@Override
			public Sprite create() {
				return new Sprite(0,0,20,51, textureRegionLibrary.get(Textures.CACTUS_1), engine.getVertexBufferObjectManager());	
			}
		});
		
		this.addSpriteFactory(new ISpriteFactory() {
			@Override
			public Sprite create() {
				return new Sprite(0,0,16,50, textureRegionLibrary.get(Textures.CACTUS_2), engine.getVertexBufferObjectManager());	
			}
		});
		
		this.addSpriteFactory(new ISpriteFactory() {
			@Override
			public Sprite create() {
				return new Sprite(0,0,30,50, textureRegionLibrary.get(Textures.CACTUS_3), engine.getVertexBufferObjectManager());	
			}
		});
		
		this.addSpriteFactory(new ISpriteFactory() {
			@Override
			public Sprite create() {
				return new Sprite(0,0,30,50, textureRegionLibrary.get(Textures.CACTUS_3), engine.getVertexBufferObjectManager());	
			}
		});
	}
	
}
