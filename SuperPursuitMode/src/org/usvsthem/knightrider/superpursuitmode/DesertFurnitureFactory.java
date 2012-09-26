package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegionLibrary;

public class DesertFurnitureFactory implements ISpriteFactory {

	private TextureRegionLibrary textureRegionLibrary;
	private Engine engine;
	
	public DesertFurnitureFactory(Engine engine, TextureRegionLibrary textureRegionLibrary){
		this.engine = engine;
		this.textureRegionLibrary = textureRegionLibrary;
	}
	
	private Sprite createCactus(){
		Sprite cactus = new Sprite(0,0,20,51, textureRegionLibrary.get(Textures.CACTUS_1), engine.getVertexBufferObjectManager());
		return cactus;
	}
	
	private Sprite createCactus2(){
		Sprite cactus = new Sprite(0,0,12,36, textureRegionLibrary.get(Textures.CACTUS_2), engine.getVertexBufferObjectManager());
		return cactus;
	}
	
	private Sprite createCactus3(){
		Sprite cactus = new Sprite(0,0,30,50, textureRegionLibrary.get(Textures.CACTUS_3), engine.getVertexBufferObjectManager());
		return cactus;
	}
	

	@Override
	public Sprite create() {
		int index = (int) Math.round(Math.random()*2);
		if(index == 0) {
			return createCactus();
		} else if (index==1) {
			return createCactus2();
		} else {
			return createCactus3();
		}
	}

}
