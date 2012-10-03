package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.ArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.usvsthem.knightrider.superpursuitmode.Textures;

public class PowerBar extends Rectangle {

	private float level;
	private ArrayList<Sprite> sprites;
	private float maxPower;
	private int index;
	private float factor; 
	
	public PowerBar(float pX, float pY, int numRed, int numYellow, int numGreen, float maxPower,
			VertexBufferObjectManager vertexBufferObjectManager, TextureRegionLibrary textureRegionLibrary) {
		super(pX, pY, 0, 0, vertexBufferObjectManager );

		this.maxPower = maxPower;
		this.sprites = new ArrayList<Sprite>();
		for(int i = 0;i<numRed;i++){
			createAndConfigureSprite(textureRegionLibrary.get(Textures.POWERBAR_RED));
		}	
		
		for(int i = 0;i<numYellow;i++){
			createAndConfigureSprite(textureRegionLibrary.get(Textures.POWERBAR_YELLOW));
		}	
		
		for(int i = 0;i<numGreen;i++){
			createAndConfigureSprite(textureRegionLibrary.get(Textures.POWERBAR_GREEN));
		}	
		this.factor = sprites.size()/maxPower;
		
		this.setLevel(maxPower);
		
	}
	
	private void createAndConfigureSprite(ITextureRegion textureRegion){
		Sprite sprite = new Sprite(0,0,textureRegion.getWidth(),textureRegion.getHeight(),textureRegion,  this.getVertexBufferObjectManager());
		this.attachChild(sprite);
		this.sprites.add(sprite);
		
	}
	private int SPRITE_PADDING = 5;
	
	public void setLevel(float level){
		if(level>maxPower) {
			level = maxPower;
		}
		
		this.level = level;
		//float factor = sprites.size()/maxPower; 
		int index = Math.round(factor * level);
		if(index==this.index){
			return;
		}
		this.index = index;
		
		float x = 0;
		Sprite sprite;
		boolean isVisible = false;
		//iterate over
		for(int i=0; i<sprites.size();i++){
			sprite = sprites.get(i);
		
			isVisible = i <=index;
			
			sprite.setVisible(isVisible);
			sprite.setPosition(x, 0);
			x += sprite.getWidth() + SPRITE_PADDING;
			
		}
	}

}
