package org.usvsthem.knightrider.superpursuitmode.enemies;

import org.andengine.util.adt.pool.GenericPool;
import org.usvsthem.knightrider.superpursuitmode.entity.TerrainAlignedActor;

public class EnemyPool extends GenericPool<TerrainAlignedActor> {

	private IEnemyFactory enemyFactory;
	
	public EnemyPool(IEnemyFactory enemyFactory){
		super(0);
		this.enemyFactory = enemyFactory;
	}
	
	@Override
	protected TerrainAlignedActor onAllocatePoolItem() {
		return enemyFactory.createEnemy();
	}
	
	protected void onHandleObtainItem(TerrainAlignedActor pItem) {
		pItem.reset();
		pItem.setIgnoreUpdate(false);
	}

	protected void onHandleRecycleItem(TerrainAlignedActor pItem) {
		pItem.setIgnoreUpdate(true);
	}
	


}


