package org.usvsthem.knightrider.superpursuitmode.furniture;

import org.andengine.entity.sprite.Sprite;

public class XMLFurnitureProvider  implements IFurnitureProvider {

	
	public XMLFurnitureProvider(){
		//read the configuration and use a compositeSpriteFactory to build the types of furniture
		//pass this to a sprite pool
	}
	@Override
	public Sprite[] obtainFurniture(float minX, float maxX) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void furnitureRemoved(Sprite sprite) {
		// TODO Auto-generated method stub
		
	}

}
