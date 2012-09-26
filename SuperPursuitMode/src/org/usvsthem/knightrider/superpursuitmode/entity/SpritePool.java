package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.pool.GenericPool;
import org.usvsthem.knightrider.superpursuitmode.ISpriteFactory;

public class SpritePool extends GenericPool<Sprite> {

	private ISpriteFactory factory;
	
	public SpritePool(ISpriteFactory factory){
		super(0);
		this.factory = factory;
	}
	
	@Override
	protected Sprite onAllocatePoolItem() {
		return factory.create();
	}
	
	protected void onHandleObtainItem(Sprite pItem) {
		pItem.reset();
	}

	protected void onHandleRecycleItem(Sprite pItem) {
		pItem.setVisible(false);
		pItem.setIgnoreUpdate(true);
	}
	


}

