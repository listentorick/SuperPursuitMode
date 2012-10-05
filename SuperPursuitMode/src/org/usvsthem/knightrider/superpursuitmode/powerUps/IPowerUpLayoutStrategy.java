package org.usvsthem.knightrider.superpursuitmode.powerUps;

import java.util.ArrayList;


public interface IPowerUpLayoutStrategy {
	
	public ArrayList<BasePowerUp> createAndlayout(float startX);
	
	public void destroy(BasePowerUp basePowerUp);

}
