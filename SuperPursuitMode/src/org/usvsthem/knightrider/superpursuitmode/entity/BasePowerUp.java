package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class BasePowerUp extends Rectangle {

	private IPowerUpListener powerupListener;
	private IPowerUpExecutor powerUpExecutor;
		
	public BasePowerUp(float pX, float pY,
			ITextureRegion backgroundTextureRegion, ITextureRegion midgroundTextureRegion, ITextureRegion foregroundTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, IPowerUpExecutor powerupExecutor) {
		super(pX, pY, backgroundTextureRegion.getWidth(), backgroundTextureRegion.getWidth(), vertexBufferObjectManager);
		this.powerUpExecutor = powerupExecutor;
		
		this.setAlpha(0f);
		
		construct(backgroundTextureRegion,midgroundTextureRegion,foregroundTextureRegion);
	
	}

		
	public void setPowerUpListener(IPowerUpListener powerUpListener){
		this.powerupListener = powerUpListener;
	}
	
	
	public void setPowerUpExecutor(IPowerUpExecutor powerUpExecutor){
		this.powerUpExecutor = powerUpExecutor;
	}
	
	private boolean isExecuted = false;
	
	public boolean getIsExecuted(){
		return isExecuted;
	}
	
	protected abstract void construct(ITextureRegion backgroundTextureRegion, ITextureRegion midgroundTextureRegion, ITextureRegion foregroundTextureRegion);
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if(powerUpExecutor.shouldExecutePowerup(this)){
			powerUpExecutor.executePowerup(this);
			if(powerupListener!=null){
				powerupListener.powerUpExecuted(this);
				
			}
			isExecuted = true;
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		isExecuted = false;
	}
	
	
}


