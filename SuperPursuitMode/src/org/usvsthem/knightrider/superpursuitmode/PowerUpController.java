package org.usvsthem.knightrider.superpursuitmode;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.usvsthem.knightrider.superpursuitmode.entity.BasePowerUp;
import org.usvsthem.knightrider.superpursuitmode.entity.IPowerUpListener;
import org.usvsthem.knightrider.superpursuitmode.entity.PowerUpPool;

public class PowerUpController implements IUpdateHandler{
	
	ArrayList<IPowerUpLayoutStrategy> powerUpLayoutStrategies;
	ArrayList<PowerUpPool> powerUpPools;
	private ArrayList<BasePowerUp> powerupsInScene = new ArrayList<BasePowerUp>();
	private IPowerUpLayoutStrategy lastPowerUpLayoutStrategy;
	private ILevel level;
	
	
	public PowerUpController(ILevel level){
		this.level = level;
		powerUpLayoutStrategies = new ArrayList<IPowerUpLayoutStrategy>();
		powerUpPools = new ArrayList<PowerUpPool>();
	}
	
	public void addPowerUpLayoutStrategy(IPowerUpLayoutStrategy powerupLayoutStrategy){
		powerUpLayoutStrategies.add(powerupLayoutStrategy);
	}
	
	public void addPowerUpPool(PowerUpPool powerUpPool){
		powerUpPools.add(powerUpPool);
	}
	
	
	public ArrayList<BasePowerUp> powersUpToRemove = new ArrayList<BasePowerUp>();
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		BasePowerUp powerUp;
		
		if(powerupsInScene.size()==0) {
			
			//pick a random strategy
			int powerUpLayoutStrategyIndex = (int) (Math.random() * powerUpLayoutStrategies.size());
			lastPowerUpLayoutStrategy = powerUpLayoutStrategies.get(powerUpLayoutStrategyIndex);
		
			powerupsInScene = lastPowerUpLayoutStrategy.createAndlayout(level.getEngine().getCamera().getXMax());
			
			//attach all these powerups to the level...
			for(int i=0; i<powerupsInScene.size();i++) {
				powerUp = powerupsInScene.get(i);
				level.addPowerUpToLevel(powerUp);
			}
		} else {
			
			boolean remove = false;
			for(int i=0; i<powerupsInScene.size();i++) {
				powerUp = powerupsInScene.get(i);
				
				if(powerUp.getIsExecuted()==true || (powerUp.getX() + powerUp.getWidth())<level.getEngine().getCamera().getXMin()){	
					powersUpToRemove.add(powerUp);
				}
			}
			
			for(int i=0;i<powersUpToRemove.size();i++){
				powerUp = powersUpToRemove.get(i);
				powerupsInScene.remove(powerUp);
				lastPowerUpLayoutStrategy.destroy(powerUp);
				level.removePowerUpFromLevel(powerUp);
			}		
			powersUpToRemove.clear();
	
		}
		
		
	
		
		
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	

}
