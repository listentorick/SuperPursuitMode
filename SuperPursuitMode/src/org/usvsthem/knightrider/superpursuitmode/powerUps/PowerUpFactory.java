package org.usvsthem.knightrider.superpursuitmode.powerUps;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.Textures;

public class PowerUpFactory implements IPowerUpFactory{

	private ILevel level;
	
	public PowerUpFactory(ILevel level){
		this.level = level;
	}

	@Override
	public BasePowerUp create() {
		
		int index = (int) Math.round(Math.random()*1);
		if(index == 0) {
			return createJump();
		} else {
			return createPower();
		}
	}
	
	public BasePowerUp createJump() {
		ITextureRegion backgroundRegion = level.getTextureRegionLibrary().get(Textures.STAR_BACKGROUND);
		ITextureRegion midgroundRegion = level.getTextureRegionLibrary().get(Textures.POWERUP_BACKGROUND_BLUE);
		ITextureRegion foregroundRegion = level.getTextureRegionLibrary().get(Textures.POWERUP_JUMP);

		ThrobbingPowerUp powerUp = new ThrobbingPowerUp(0,0, backgroundRegion, midgroundRegion,foregroundRegion, level.getEngine().getVertexBufferObjectManager(), new IPowerUpExecutor() {
			
			@Override
			public boolean shouldExecutePowerup(BasePowerUp basePowerUp) {
				// TODO Auto-generated method stub
				return basePowerUp.collidesWith(level.getPlayerActor().getPrincipleShape());
				//return false;
			}
			
			@Override
			public void executePowerup(BasePowerUp powerup) {
				// TODO Auto-generated method stub
				level.addTurboBoostPower(20);
			}
		});
		
		return powerUp;

	}
	
	public BasePowerUp createPower() {
		ITextureRegion backgroundRegion = level.getTextureRegionLibrary().get(Textures.STAR_BACKGROUND);
		ITextureRegion midgroundRegion = level.getTextureRegionLibrary().get(Textures.POWERUP_BACKGROUND_YELLOW);
		ITextureRegion foregroundRegion = level.getTextureRegionLibrary().get(Textures.POWERUP_POWER);

		ThrobbingPowerUp powerUp = new ThrobbingPowerUp(0,0, backgroundRegion, midgroundRegion,foregroundRegion, level.getEngine().getVertexBufferObjectManager(), new IPowerUpExecutor() {
			
			@Override
			public boolean shouldExecutePowerup(BasePowerUp basePowerUp) {
				// TODO Auto-generated method stub
				//return false;
				return basePowerUp.collidesWith(level.getPlayerActor().getPrincipleShape());
			}
			
			@Override
			public void executePowerup(BasePowerUp powerup) {
				// TODO Auto-generated method stu
				level.addEnginePower(20);
			}
		});
		
		return powerUp;

	}
	
}
