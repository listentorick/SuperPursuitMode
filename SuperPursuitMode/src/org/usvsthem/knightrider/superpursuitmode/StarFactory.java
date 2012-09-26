package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.Engine;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegionLibrary;

public class StarFactory implements ISpriteFactory{

	private TextureRegionLibrary textureRegionLibrary;
	private Engine engine;
	
	public static float STAR_HEIGHT = 68;
	public static float STAR_WIDTH = 62;
	
	public StarFactory(Engine engine, TextureRegionLibrary textureRegionLibrary){
		this.engine = engine;
		this.textureRegionLibrary = textureRegionLibrary;
	}
	
	
	@Override
	public Sprite create() {
	
		Sprite star_background = new Sprite(0,0,43,38, textureRegionLibrary.get(Textures.STAR_BACKGROUND), engine.getVertexBufferObjectManager());
		
		Sprite star_middle = new Sprite(-10,-15,STAR_WIDTH,STAR_HEIGHT, textureRegionLibrary.get(Textures.STAR_MIDDLE), engine.getVertexBufferObjectManager());
		
		star_background.attachChild(star_middle);
		
		LoopEntityModifier loop = new LoopEntityModifier( new RotationModifier(1,0,360));
		
		
		star_middle.registerEntityModifier(loop);
		
		Sprite star_foreground = new Sprite(10,9,21,20, textureRegionLibrary.get(Textures.STAR_FOREGROUND), engine.getVertexBufferObjectManager());
		star_background.attachChild(star_foreground);
		
		return star_background;
		
	}

}
