package org.usvsthem.knightrider.superpursuitmode.furniture;

import org.andengine.entity.sprite.Sprite;

public interface IFurnitureProvider {
	
	Sprite[] obtainFurniture(float minX, float maxX);
	void furnitureRemoved(Sprite sprite);
	

}
