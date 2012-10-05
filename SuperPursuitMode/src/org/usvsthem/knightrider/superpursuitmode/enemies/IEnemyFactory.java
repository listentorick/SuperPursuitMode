package org.usvsthem.knightrider.superpursuitmode.enemies;

import org.andengine.entity.sprite.Sprite;
import org.usvsthem.knightrider.superpursuitmode.entity.TerrainAlignedActor;

public interface IEnemyFactory {
	
	TerrainAlignedActor createEnemy();

}
