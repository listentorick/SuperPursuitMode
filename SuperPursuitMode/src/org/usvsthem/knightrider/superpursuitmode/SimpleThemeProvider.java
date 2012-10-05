package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.entity.IThemeProvider;
import org.usvsthem.knightrider.superpursuitmode.entity.ParallaxBackground2d;
import org.usvsthem.knightrider.superpursuitmode.entity.SpriteMultiPool;
import org.usvsthem.knightrider.superpursuitmode.entity.SpritePool;
import org.usvsthem.knightrider.superpursuitmode.furniture.DesertFurnitureFactory;

public class SimpleThemeProvider implements IThemeProvider {

	private Engine engine;
	private TextureRegionLibrary textureRegionLibrary;
	
	public SimpleThemeProvider(Engine engine, TextureRegionLibrary textureRegionLibrary){
		this.textureRegionLibrary = textureRegionLibrary;
		this.engine = engine;
	}
	@Override
	public ParallaxBackground2d createBackground() {
		ParallaxBackground2d background = new ParallaxBackground2d(1f,1f,1f);
		
		Sprite backgroundSprite =  new Sprite(0,0, 800, 480,textureRegionLibrary.get(Textures.SKY),engine.getVertexBufferObjectManager());
		background.attachParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(0,0,backgroundSprite,false,false,false));
		
		Sprite mountainsSprite =  new Sprite(0,244, 800, 236,textureRegionLibrary.get(Textures.MOUNTAINS),engine.getVertexBufferObjectManager());
		background.attachParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.2f,-0.05f,mountainsSprite,true,false,false));

		return background;
	}

	@Override
	public SpriteMultiPool createFurniturePool() {

		SpriteMultiPool furniturePool = new SpriteMultiPool();
		
		SpritePool desertFurniturePool =  new SpritePool(new DesertFurnitureFactory(engine, textureRegionLibrary));
		desertFurniturePool.batchAllocatePoolItems(10);
		furniturePool.registerPool(Theme.DESERT.ordinal(),desertFurniturePool);

		return furniturePool;
		
	}

}
