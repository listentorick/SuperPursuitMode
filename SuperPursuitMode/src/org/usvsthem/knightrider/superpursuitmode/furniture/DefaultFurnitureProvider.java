package org.usvsthem.knightrider.superpursuitmode.furniture;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.entity.SpriteMultiPool;

public class DefaultFurnitureProvider implements IFurnitureProvider {
	
	SpriteMultiPool spriteMultiPool;
	private ILevel level;
	private float minFurnitureX = 0;
	private Camera camera;
	
	public DefaultFurnitureProvider(ILevel level, SpriteMultiPool spriteMultiPool){
		this.spriteMultiPool = spriteMultiPool;
		this.level=level;
		this.camera = level.getEngine().getCamera();
	}

	@Override
	public Sprite[] obtainFurniture(float minX, float maxX) {
		Sprite[] sprites = null;
		int numItems = spriteMultiPool.getAvailableItemCount(0);
		if(numItems>0){
			sprites = new Sprite[numItems];
			for(int i=0;i<numItems;i++) {
				sprites[i] = spriteMultiPool.obtainPoolItem(0);
				positionTerrainFuniture(sprites[i]);
			}
		}
		
		return sprites;
		
	}
	
	private void positionTerrainFuniture(Sprite furniture){
		float cameraXMax = camera.getXMax();
		if(minFurnitureX < cameraXMax) {
			minFurnitureX = cameraXMax;
		}
		minFurnitureX+= furniture.getWidth() + ((float)Math.random()*200);
		
		float xPos = minFurnitureX;
		
		float yPos = level.getTerrain().getYAt(xPos);
		float yPos2 = level.getTerrain().getYAt(xPos+ furniture.getWidth());
		if(yPos2> yPos) yPos = yPos2; 
		furniture.setPosition(xPos, yPos - furniture.getHeight());
	}

	@Override
	public void furnitureRemoved(Sprite sprite) {
		spriteMultiPool.recyclePoolItem(sprite);
	}

}
