package org.usvsthem.knightrider.superpursuitmode;

import java.util.ArrayList;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.usvsthem.knightrider.superpursuitmode.entity.BasePowerUp;
import org.usvsthem.knightrider.superpursuitmode.entity.PowerUpPool;

import android.util.Log;

public class TerrainFollowingPowerupLayoutStrategy implements IPowerUpLayoutStrategy {

	private ILevel level;
	
	private float POWERUP_PADDING = 5;
	private PowerUpPool powerUpPool;
	
	public TerrainFollowingPowerupLayoutStrategy(ILevel level, PowerUpPool powerUpPool){
		this.level = level;
		this.powerUpPool = powerUpPool;
	}
	
	public ArrayList<BasePowerUp> createAndlayout(float startX) {
		return starsFollowLine(startX,Math.floor(Math.random() * 5),Math.floor(Math.random() * 10));
	}
	
	private ArrayList<BasePowerUp> starsFollowLine(float startX, double d, double e){
		
		
		
		ArrayList<BasePowerUp> powerups = new ArrayList<BasePowerUp>();
		BasePowerUp actor;
		float x = startX;
		float y = 0;
		
		for(int i=0; i<d;i++){	
			
			for(int j=0;j<e;j++){
				actor = powerUpPool.obtainPoolItem();
				powerups.add(actor);
				y = level.getTerrain().getYAt(x) - ((actor.getHeight()) * (i + 1));
				actor.setPosition(x,y);
				x = x + actor.getWidth() + POWERUP_PADDING; 
			} 
			
			x = startX;
		}
		
		Log.d("POWERUP", "CREATED" + powerups.size());
		
		return powerups;
		
		
	}

	@Override
	public void destroy(BasePowerUp basePowerUp) {
		powerUpPool.recyclePoolItem(basePowerUp);
		
	}


	
}
