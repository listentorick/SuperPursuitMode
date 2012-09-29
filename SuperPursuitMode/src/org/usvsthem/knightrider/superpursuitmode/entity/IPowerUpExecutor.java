package org.usvsthem.knightrider.superpursuitmode.entity;

public interface IPowerUpExecutor {

	boolean shouldExecutePowerup(BasePowerUp basePowerUp);
	void executePowerup(BasePowerUp powerup);
	
}
