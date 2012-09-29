package org.usvsthem.knightrider.superpursuitmode;

import java.util.ArrayList;

import org.usvsthem.knightrider.superpursuitmode.entity.BasePowerUp;
import org.usvsthem.knightrider.superpursuitmode.entity.PowerUpPool;

public interface IPowerUpLayoutStrategy {
	
	public ArrayList<BasePowerUp> createAndlayout(float startX);
	
	public void destroy(BasePowerUp basePowerUp);

}
