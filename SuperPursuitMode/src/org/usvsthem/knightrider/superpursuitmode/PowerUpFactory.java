package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.usvsthem.knightrider.superpursuitmode.entity.BasePowerUp;
import org.usvsthem.knightrider.superpursuitmode.entity.IPowerUpExecutor;
import org.usvsthem.knightrider.superpursuitmode.entity.ThrobbingPowerUp;

public class PowerUpFactory implements IPowerUpFactory{
	

	private ILevel level;
	public static float STAR_HEIGHT = 68;
	public static float STAR_WIDTH = 62;
	
	public PowerUpFactory(ILevel level){
		this.level = level;
	}

	@Override
	public BasePowerUp create() {
		
		ITextureRegion backgroundRegion = level.getTextureRegionLibrary().get(Textures.STAR_BACKGROUND);
		ITextureRegion midgroundRegion = level.getTextureRegionLibrary().get(Textures.POWERUP_BACKGROUND_BLUE);
		ITextureRegion foregroundRegion = level.getTextureRegionLibrary().get(Textures.POWERUP_JUMP);

		ThrobbingPowerUp powerUp = new ThrobbingPowerUp(0,0, backgroundRegion, midgroundRegion,foregroundRegion, level.getEngine().getVertexBufferObjectManager(), new IPowerUpExecutor() {
			
			@Override
			public boolean shouldExecutePowerup(BasePowerUp basePowerUp) {
				// TODO Auto-generated method stub
				return basePowerUp.collidesWith(level.getPlayerActor().getPrincipleShape());
			}
			
			@Override
			public void executePowerup(BasePowerUp powerup) {
				// TODO Auto-generated method stub
				
			}
		});

		return powerUp;
	}
	
	

}
