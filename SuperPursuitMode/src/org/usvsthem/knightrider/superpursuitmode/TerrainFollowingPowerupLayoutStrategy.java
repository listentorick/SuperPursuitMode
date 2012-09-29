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
	private int minNumRows;
	private int minNumColumns;
	private int maxNumRows;
	private int maxNumColumns;
	
	public TerrainFollowingPowerupLayoutStrategy(ILevel level, PowerUpPool powerUpPool){
		this(level,powerUpPool, 1,5,1,10);
	}
	
	public TerrainFollowingPowerupLayoutStrategy(ILevel level, PowerUpPool powerUpPool, int minNumRows, int maxNumRows, int minNumColumns, int maxNumColumns){
		this.level = level;
		this.powerUpPool = powerUpPool;
		this.minNumRows = minNumRows;
		this.maxNumRows = maxNumRows;
		this.minNumColumns = minNumColumns;
		this.maxNumColumns = maxNumColumns;
		
	}
	
	public ArrayList<BasePowerUp> createAndlayout(float startX) {
		return starsFollowLine(startX,minNumRows + Math.floor(Math.random() * (maxNumRows-minNumRows)),minNumColumns + Math.floor(Math.random() * (maxNumColumns-minNumColumns)));
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
				y = level.getTerrain().getYAt(x) - ((actor.getHeight()) * (i + 1)) - POWERUP_PADDING;
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
