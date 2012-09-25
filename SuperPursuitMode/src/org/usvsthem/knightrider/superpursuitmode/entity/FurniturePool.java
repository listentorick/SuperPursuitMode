package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.pool.GenericPool;
import org.usvsthem.knightrider.superpursuitmode.IFurnitureFactory;

public class FurniturePool extends GenericPool<Sprite> {

	private IFurnitureFactory furnitureFactory;
	
	public FurniturePool(IFurnitureFactory furnitureFactory){
		super(0);
		this.furnitureFactory = furnitureFactory;
	}
	
	@Override
	protected Sprite onAllocatePoolItem() {
		return furnitureFactory.createFurniture();
	}
	
	protected void onHandleObtainItem(Sprite pItem) {
		pItem.reset();
	}

	protected void onHandleRecycleItem(Sprite pItem) {
		pItem.setVisible(false);
		pItem.setIgnoreUpdate(true);
	}
	


}

