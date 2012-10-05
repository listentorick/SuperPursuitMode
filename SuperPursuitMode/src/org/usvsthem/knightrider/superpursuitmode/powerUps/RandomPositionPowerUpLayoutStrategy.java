package org.usvsthem.knightrider.superpursuitmode.powerUps;

import java.util.ArrayList;

import org.usvsthem.knightrider.superpursuitmode.ILevel;

import android.util.Log;

public class RandomPositionPowerUpLayoutStrategy implements IPowerUpLayoutStrategy {

	private ILevel level;
	private PowerUpPool powerUpPool;
	private int numPowerUps;
	private float deltaX;
	private float deltaY;

	public RandomPositionPowerUpLayoutStrategy(ILevel level, PowerUpPool powerUpPool){
		this(level,powerUpPool,5, 30,200);
	}
	
	public RandomPositionPowerUpLayoutStrategy(ILevel level, PowerUpPool powerUpPool, int numPowerUps, float deltaX, float deltaY){
		this.level = level;
		this.powerUpPool = powerUpPool;
		this.numPowerUps = numPowerUps;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}
	
	
	@Override
	public ArrayList<BasePowerUp> createAndlayout(float startX) {
		
		ArrayList<BasePowerUp> powerups = new ArrayList<BasePowerUp>();
		BasePowerUp actor;
		float x = startX;
		float y = 0;
		
		for(int i=0; i<numPowerUps;i++){	
			
			actor = powerUpPool.obtainPoolItem();
			powerups.add(actor);
			
			x += (float)(Math.random() * deltaX) + actor.getWidth() ;
			y = level.getTerrain().getYAt(x) -  actor.getHeight()  - (float) (Math.random() * deltaY);
			actor.setPosition(x,y);
				
		}
		
		return powerups;

	}

	@Override
	public void destroy(BasePowerUp basePowerUp) {
		powerUpPool.recyclePoolItem(basePowerUp);
	}

}
