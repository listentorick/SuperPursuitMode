package org.usvsthem.knightrider.superpursuitmode.powerUps;


public interface IPowerUpExecutor {

	boolean shouldExecutePowerup(BasePowerUp basePowerUp);
	void executePowerup(BasePowerUp powerup);
	
}
