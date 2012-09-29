package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ThrobbingPowerUp extends BasePowerUp {

	private Sprite background;
	private Sprite middle;
	private Sprite foreground; 
	
	public ThrobbingPowerUp(float pX, float pY, ITextureRegion backgroundTextureRegion,
			ITextureRegion midgroundTextureRegion,
			ITextureRegion foregroundTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager,
			IPowerUpExecutor powerUpExecutor) {
		super(pX, pY, backgroundTextureRegion, midgroundTextureRegion,
				foregroundTextureRegion, vertexBufferObjectManager, powerUpExecutor);
	}
	
	protected void construct(ITextureRegion backgroundTextureRegion, ITextureRegion midgroundTextureRegion, ITextureRegion foregroundTextureRegion){
		
		float halfWidth = this.getWidth()/2;
		float halfHeight = this.getHeight()/2;
		
		float backgroundX = halfWidth - (backgroundTextureRegion.getWidth()/2);
		float backgroundY = halfHeight - (backgroundTextureRegion.getHeight()/2);
			
		float middleX = halfWidth - (midgroundTextureRegion.getWidth()/2);
		float middleY = halfHeight - (midgroundTextureRegion.getHeight()/2);
	
		float foregroundX = halfWidth - (foregroundTextureRegion.getWidth()/2);
		float foregroundY = halfHeight - (foregroundTextureRegion.getHeight()/2);
	
		background = new Sprite(backgroundX,backgroundY,backgroundTextureRegion.getWidth(),backgroundTextureRegion.getHeight(), backgroundTextureRegion, this.getVertexBufferObjectManager());
		this.attachChild(background);

		middle = new Sprite(middleX, middleY,midgroundTextureRegion.getWidth(),midgroundTextureRegion.getHeight(), midgroundTextureRegion, this.getVertexBufferObjectManager());
		this.attachChild(middle);
		
		LoopEntityModifier scaleLoop = new LoopEntityModifier(  new SequenceEntityModifier( new ScaleModifier(1,0.75f,1f),new ScaleModifier(1,1f,0.75f)));
		middle.registerEntityModifier(scaleLoop);
		
		foreground = new Sprite(foregroundX, foregroundY, foregroundTextureRegion.getWidth(),foregroundTextureRegion.getHeight(), foregroundTextureRegion, this.getVertexBufferObjectManager());
		this.attachChild(foreground);
		
	}
		
}


