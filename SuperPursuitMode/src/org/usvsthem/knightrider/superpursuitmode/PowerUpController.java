package org.usvsthem.knightrider.superpursuitmode;

import java.util.ArrayList;
import java.util.HashMap;

import org.andengine.engine.handler.IUpdateHandler;
import org.usvsthem.knightrider.superpursuitmode.entity.BasePowerUp;

public class PowerUpController implements IUpdateHandler{
	
	ArrayList<IPowerUpLayoutStrategy> powerUpLayoutStrategies;
	private ArrayList<BasePowerUp> powerupsInScene;
	private ILevel level;
	private float minX;
	public ArrayList<BasePowerUp> powersUpToRemove;
	
	private HashMap<BasePowerUp,  IPowerUpLayoutStrategy> powerUpToStrategyMap;
	
	public PowerUpController(ILevel level){
		this.level = level;
		minX = level.getEngine().getCamera().getXMax();
		powerUpLayoutStrategies = new ArrayList<IPowerUpLayoutStrategy>();
		powerupsInScene = new ArrayList<BasePowerUp>();
		powerUpToStrategyMap = new HashMap<BasePowerUp, IPowerUpLayoutStrategy>();
		powersUpToRemove = new ArrayList<BasePowerUp>();
		powerupsInScene = new ArrayList<BasePowerUp>();
	}
	
	public void addPowerUpLayoutStrategy(IPowerUpLayoutStrategy powerupLayoutStrategy){
		powerUpLayoutStrategies.add(powerupLayoutStrategy);
	}
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		BasePowerUp powerUp;

		float cameraMinX = level.getEngine().getCamera().getXMax();
		
		if(minX<cameraMinX){
			minX = cameraMinX;
		}
			
		if(powerupsInScene.size()<20) {
			int powerUpLayoutStrategyIndex = (int) (Math.random() * powerUpLayoutStrategies.size());
			IPowerUpLayoutStrategy powerUpLayoutStrategy = powerUpLayoutStrategies.get(powerUpLayoutStrategyIndex);
		
			ArrayList<BasePowerUp> newPowerUps = powerUpLayoutStrategy.createAndlayout(minX);
			powerupsInScene.addAll(newPowerUps);
			
			//attach all these powerups to the level...
			for(int i=0; i<newPowerUps.size();i++) {
				
				powerUp = newPowerUps.get(i);
				powerUpToStrategyMap.put(powerUp, powerUpLayoutStrategy);
				level.addPowerUpToLevel(powerUp);
				if(minX<powerUp.getX()){
					minX = powerUp.getX() + powerUp.getWidth();
				}
			}

		}
		
		for(int i=0; i<powerupsInScene.size();i++) {
			powerUp = powerupsInScene.get(i);
			
			if(powerUp.getIsExecuted()==true || (powerUp.getX() + powerUp.getWidth())<level.getEngine().getCamera().getXMin()){	
				powersUpToRemove.add(powerUp);
			}
		}
		
		for(int i=0;i<powersUpToRemove.size();i++){
			powerUp = powersUpToRemove.get(i);
			powerupsInScene.remove(powerUp);
			powerUpToStrategyMap.get(powerUp).destroy(powerUp);
			powerUpToStrategyMap.remove(powerUp);

			level.removePowerUpFromLevel(powerUp);
		}		
		powersUpToRemove.clear();
	
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}


}
