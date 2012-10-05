package org.usvsthem.knightrider.superpursuitmode.powerUps;

import org.andengine.util.adt.pool.GenericPool;

public class PowerUpPool extends GenericPool<BasePowerUp> {

	private IPowerUpFactory factory;
	
	public PowerUpPool(IPowerUpFactory factory){
		super(0);
		this.factory = factory;
	}
	
	@Override
	protected BasePowerUp onAllocatePoolItem() {
		return factory.create();
	}
	
	protected void onHandleObtainItem(BasePowerUp pItem) {
		pItem.reset();
	}

	protected void onHandleRecycleItem(BasePowerUp pItem) {
		pItem.setVisible(false);
		pItem.setIgnoreUpdate(true);
	}
	


}

